package com.linkflow.fitt360sdk.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.linkflow.fitt360sdk.activity.MainActivity;
import com.linkflow.fitt360sdk.helper.NotificationHelper;
import com.linkflow.fitt360sdk.helper.TimerHelper;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import app.library.linkflow.StoreDataHelper;
import app.library.linkflow.manager.NeckbandRestApiClient;
import app.library.linkflow.manager.StoredDataManager;
import app.library.linkflow.manager.item.RecordSetItem;
import app.library.linkflow.manager.model.PreviewModel;
import app.library.linkflow.manager.neckband.NeckbandManager;
import app.library.linkflow.rtmp.RTSPToRTMPConverter;

import static app.library.linkflow.rtmp.RTSPToRTMPConverter.RTMP_STATUS.CONNECTED;
import static app.library.linkflow.rtmp.RTSPToRTMPConverter.RTMP_STATUS.RECONNECTING;

public class RTMPStreamService extends Service implements RTSPToRTMPConverter.Listener, TimerHelper.Listener {
    private String TAG = this.getClass().getSimpleName();
    public static final String ACTION_START_RTMP_STREAM = "linkflow.app.fitt360.rtmp_stream_start";
    public static final String ACTION_CANCEL_RTMP_STREAM = "linkflow.app.fitt360.rtmp_stream_cancel";
    private static final int MSG_RTSP_CONFIGURE_CHANGED = 10, MSG_RTSP_CHECK_STARTED = 11, MSG_RTSP_CONNECT = 12, MSG_RTSP_RECONNECT = 13;

    private int ADAPTIVE_RTMP_QUEUE_SIZE_CRITERIA = 180;

    private RTSPToRTMPConverter mConverter;
    private PreviewModel mPreviewModel;
    private TimerHelper mTimerHelper;
    private String currentStatusMsg;
    private RTSPToRTMPConverter.RTMP_STATUS currentStatus;
    private NeckbandManager mNeckbandManager;
    private NotificationHelper mNotifyManager;
    private StoredDataManager mSDM = StoredDataManager.getInstance();

    private DecimalFormat mDecimalFormat = new DecimalFormat("0.#");
    private String mRTMPUrl;
    private boolean mEnableBitrateAuto;
    private Handler mRTSPCheckHandler;
    private Handler mRTSPMsgHandler;

    private Timer adaptiveStreamingTimer = null;
    private boolean pauseBitrateControl = false;
    private boolean isRTSPConnected = false;

    private long lastBitrateChangeExecutionTime = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        mNeckbandManager = NeckbandManager.getInstance().init(getApplicationContext());
        mNotifyManager = NotificationHelper.getInstance().init(this);
        mTimerHelper = TimerHelper.getInstance().init(getMainLooper(), this);
        mPreviewModel = mNeckbandManager.getPreviewModel();
        mPreviewModel = new PreviewModel(new PreviewModel.Listener() {
            @Override
            public void completedControlRTSP(boolean success, boolean activated) {
                if(success) {
                    if(activated) {
                        Log.d(TAG, "completedControlRTSP success. startRTSPDecoder");
                        String version = mNeckbandManager.getInfoManage().getSoftwareItem().mVersion;
                        mConverter.startRTSPDecoder(NeckbandRestApiClient.getRTSPUrl(), false, version);
                    } else
                        Log.d(TAG, "completedControlRTSP success. not activated");
                } else {
                    Log.d(TAG, "completedControlRTSP failed");
                }
            }

            @Override
            public void completedGetMuteState(boolean success, boolean enable) {

            }

            @Override
            public void completedSetMuteState(boolean success) {

            }
        });
        mConverter = RTSPToRTMPConverter.getInstance().init(getMainLooper(), this);
        mRTSPCheckHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG_RTSP_CONFIGURE_CHANGED) {
                    mPreviewModel.activateRTSP(mNeckbandManager.getAccessToken(), false);
                    isRTSPConnected = false;

                    mRTSPCheckHandler.sendEmptyMessageDelayed(MSG_RTSP_CONNECT, 2000);

                    String str = msg.getData().getString("code");
                    int currentBitrate = msg.getData().getInt("before");
                    int nextBitrate = msg.getData().getInt("next");
                    Toast.makeText(RTMPStreamService.this, "Bitrate " + str + "\n"
                            + currentBitrate + "mbps -> " + nextBitrate + "mbps", Toast.LENGTH_LONG).show();
                } else if (msg.what == MSG_RTSP_CHECK_STARTED) {
                    Log.d(TAG, "called MSG_RTSP_CHECK_STARTED");
                    if(!isRTSPConnected) {
                        Log.d(TAG, "start activateRTSP");
                        mPreviewModel.activateRTSP(mNeckbandManager.getAccessToken(), true);
                    }
                } else if (msg.what == MSG_RTSP_CONNECT) {
                    mConverter.closeRTSPDecoder();

                    mRTSPCheckHandler.sendEmptyMessageDelayed(MSG_RTSP_CHECK_STARTED, 4000);
                } else if (msg.what == MSG_RTSP_RECONNECT) {
                    mPreviewModel.activateRTSP(mNeckbandManager.getAccessToken(), false);
                    isRTSPConnected = false;

                    mRTSPCheckHandler.sendEmptyMessageDelayed(MSG_RTSP_CONNECT, 2000);
                }
            }
        };

        mRTSPMsgHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                mPreviewModel.activateRTSP(mNeckbandManager.getAccessToken(), true);
            }
        };
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null) {
            Notification notification = mNotifyManager.makeRTMPStreamStatus(RTMPStreamService.this, "Live Streaming Disconnected", Color.BLACK, "00:00:00", "0 Mb/s");
            startForeground(NotificationHelper.RTMP_STREAM_NOTIFY_ID, notification);
            switch (intent.getAction()) {
                case ACTION_START_RTMP_STREAM:
                    mRTMPUrl = intent.getStringExtra("rtmp_url");
                    mEnableBitrateAuto = intent.getBooleanExtra("rtmp_bitrate_auto", false);
                    if (mRTMPUrl != null) {
                        RecordSetItem recordSetItem = mNeckbandManager.getSetManage().getRecordSetItem();
                        if (mConverter.startRTMP(mRTMPUrl, recordSetItem.getWidth(), recordSetItem.getHeight(), recordSetItem.mFPS)) {
                            // backup original bitrate value and set temporary start bitrate to device
                            mSDM.setData(this, "bitrateStartTime", mNeckbandManager.getSetManage().getBitrate());
                            if (mEnableBitrateAuto) {
                                startAutoBitrateChanger();
                            }
                            mConverter.setEnableAutoBitrate(mEnableBitrateAuto);

                            // Add a little delay to have enough time to device setting applied
                            mRTSPMsgHandler.sendEmptyMessageDelayed(0, 100);

                            mTimerHelper.start();
                            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MainActivity.ACTION_START_RTMP));
                        } else {
                            stopForeground(true);
                            stopSelf();
                        }
                    }
                    break;
                case ACTION_CANCEL_RTMP_STREAM:
                    Intent closeIntent = new Intent(MainActivity.ACTION_STOP_RTMP);
                    closeIntent.putExtra("close", intent.getIntExtra("close", -1));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(closeIntent);

                    stopForeground(true);
                    stopSelf();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if(adaptiveStreamingTimer != null) {
            adaptiveStreamingTimer.cancel();
            adaptiveStreamingTimer = null;
        }

        mRTSPCheckHandler.removeCallbacksAndMessages(null);

        mPreviewModel.activateRTSP(mNeckbandManager.getAccessToken(), false);
        isRTSPConnected = false;

        // restore the device bitrate
        if (mNeckbandManager != null) {
            mNeckbandManager.getSetManage().setBitrate(mNeckbandManager.getAccessToken(), (Integer) mSDM.getData(this, "bitrateStartTime", StoredDataManager.TYPE.TYPE_INT, 5));
        }

        mConverter.stop();
        mTimerHelper.stop();

        Intent intent = new Intent(MainActivity.ACTION_STOP_RTMP);
        intent.putExtra("close", 10);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onFrame(boolean isFirst) {
        if (isFirst) {
            pauseBitrateControl = false;
            isRTSPConnected = true;
        }
    }

    @Override
    public void onStatusChange(RTSPToRTMPConverter.RTMP_STATUS status) {
        String value = "";
        switch (status) {
            case CONNECTED:
                value = "Live Streaming Connected";
                break;
            case RECONNECTING:
                value = "Live Streaming Reconnecting (3 mins) ...";
                break;
            case DISCONNECTED:
                value = "Live Streaming Disconnected";
                break;
        }
        Toast.makeText(this, value, Toast.LENGTH_LONG).show();
        currentStatus = status;
        currentStatusMsg = value;
    }

    @Override
    public void onError(RTSPToRTMPConverter.ErrorCode code) {
        String str;
        switch (code) {
            case RTSP_NODATA:
                // Skip rtsp no data during bitrate change. This may cause unexpected rtsp connection error
                if(System.currentTimeMillis() < lastBitrateChangeExecutionTime + 10000/*10 secs*/) {
                    Log.e(TAG, "No data from FITT360, but skip on this time since the device is during bitrate changing");
                    return;
                }
                str = "No data from FITT360\nApp is now trying to re-open streaming";
                Log.e(TAG, str);
                Toast.makeText(this, str, Toast.LENGTH_LONG).show();
                mRTSPCheckHandler.sendEmptyMessageDelayed(MSG_RTSP_RECONNECT, 0);
                break;
            case RTMP_NODATA:
                // Do nothing, just information
                // there is risk to handle rtmp flow here due to thread race condition
                // The error handler(try reconnect when caught socket broken) itself is enough
                str = "No data output to server\nCheck network or server";
                Log.e(TAG, str);
                Toast.makeText(this, str, Toast.LENGTH_LONG).show();
                break;
            case RTSP_INTERNAL_ERROR:
                str = "Stop stream\nNetwork internal error happened during data transmission";
                Log.e(TAG, str);
                Toast.makeText(this, str, Toast.LENGTH_LONG).show();
                closeService();
                break;
            case RECONNECT_FAILURE:
                str = "Reconnection expired\nTerminate live streaming";
                Log.e(TAG, str);
                Toast.makeText(this, str, Toast.LENGTH_LONG).show();
                closeService();
                break;
        }
    }

    @Override
    public void updateTime(String time) {
        // set text color for status display on service dialog
        int statusColor;
        if(currentStatus == CONNECTED) {
            statusColor = Color.RED;
        } else if (currentStatus == RECONNECTING) {
            statusColor = Color.BLUE;
        } else {
            statusColor = Color.BLACK;
        }

        Notification notification = mNotifyManager.makeRTMPStreamStatus(this, currentStatusMsg, statusColor, time, mDecimalFormat.format((mConverter.getSentByteAmount() / 1000000f) * 8) + " Mb/s");
        startForeground(NotificationHelper.RTMP_STREAM_NOTIFY_ID, notification);

    }

    private void closeService() {
        Intent intent = new Intent(MainActivity.ACTION_STOP_RTMP);
        intent.putExtra("close", 10);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        stopForeground(true);
        stopSelf();
    }

    private void startAutoBitrateChanger() {
        Log.d(TAG, "Initial bitrate: " + NeckbandManager.getInstance().getSetManage().getRecordSetItem().mBitrate);
        adaptiveStreamingTimer = new Timer();
        adaptiveStreamingTimer.schedule(new TimerTask() {
            public int previousFrameCount;
            public int frameQueueIncreased;
            private boolean bitrateInitiallyStabilized = false;

            public boolean nonZeroFrameStarted = false;
            public int sumQueue = 0;
            public Queue<Integer> queue = new LinkedList<>();

            public int t = 0;
//            public int period = 10;
//            public int firstCount = 0;
//            public int lastCount = 0;

            @Override
            public void run() {
                // pause this when bitrate control change, rtsp restart by other process, rtmp is disconnected
                if(pauseBitrateControl || !isRTSPConnected || currentStatus != CONNECTED) {
                    return;
                }

                boolean forceBitrateDown = false;

                int frameCountInQueue = (int)mConverter.getFrameCountInQueue();
                Log.d(TAG, "frameCountInQueue : " + frameCountInQueue);
                if (frameCountInQueue > previousFrameCount) {
                    frameQueueIncreased++;
                }
                else if(frameCountInQueue < previousFrameCount){
                    frameQueueIncreased--;
                }
                if(frameQueueIncreased < 0)
                    frameQueueIncreased = 0;

                previousFrameCount = frameCountInQueue;

                if(!nonZeroFrameStarted && frameCountInQueue > 0)
                    nonZeroFrameStarted = true;

                if(nonZeroFrameStarted) {
                    if(queue.size() == ADAPTIVE_RTMP_QUEUE_SIZE_CRITERIA) {
                        sumQueue -= queue.poll();
                    }

                    queue.offer(frameCountInQueue);
                    sumQueue += frameCountInQueue;
                }

                if(frameQueueIncreased > 12)
                    // normal criteria
                    forceBitrateDown = true;
                else if(queue.size() > 5 && (sumQueue / queue.size() > 1350 /*30fps * 5 secs = 150 frame margin*/))
                    // when reaches to overflow
                    forceBitrateDown = true;
                else if(queue.size() == ADAPTIVE_RTMP_QUEUE_SIZE_CRITERIA && (sumQueue / queue.size()) > 200)
                    // when high buffer state last long
                    forceBitrateDown = true;

                // Calculate increse ratio during specific period
//                if(t % period == 0)
//                    firstCount = frameCountInQueue;
//                else if(t % period == period - 1) {
//                    lastCount = frameCountInQueue;
//
//                    if((lastCount - firstCount) / period > 10)
//                        forceChangeDown = true;
//
//                    lastCount = 0;
//                    firstCount = 0;
//                }
                t++;

                if (forceBitrateDown) {
                    //decrease bitrate
                    int currentBitrate = NeckbandManager.getInstance().getSetManage().getRecordSetItem().mBitrate;
                    int downFactor = 5;
                    if(bitrateInitiallyStabilized)
                        downFactor = 3;
                    int newBitrate = Math.max(currentBitrate - downFactor, 5);
                    if(currentBitrate > newBitrate) {
                        mNeckbandManager.getSetManage().setBitrate(mNeckbandManager.getAccessToken(), newBitrate);
                        pauseBitrateControl = true;
                        lastBitrateChangeExecutionTime = System.currentTimeMillis();

                        Message msg = new Message();
                        msg.what = MSG_RTSP_CONFIGURE_CHANGED;
                        Bundle bundle = new Bundle();
                        bundle.putString("code", "DOWN");
                        bundle.putInt("before", currentBitrate);
                        bundle.putInt("next", newBitrate);
                        msg.setData(bundle);

                        mRTSPCheckHandler.sendMessageDelayed(msg, 500);
                        frameQueueIncreased = 0;
                        nonZeroFrameStarted = false;
                        queue.clear();
                        sumQueue = 0;
                        t = 0;
                        Log.d(TAG, "Current Bitrate: " + currentBitrate + " Decreased bitrate to : " + newBitrate);
                    }
                } else if ((queue.size() == ADAPTIVE_RTMP_QUEUE_SIZE_CRITERIA && (sumQueue / queue.size()) < 4)) {
                    //increase bitrate
                    int currentBitrate = NeckbandManager.getInstance().getSetManage().getRecordSetItem().mBitrate;
                    int newBitrate = Math.min(currentBitrate + 1, 30);
                    if(currentBitrate < newBitrate) {
                        mNeckbandManager.getSetManage().setBitrate(mNeckbandManager.getAccessToken(), newBitrate);
                        pauseBitrateControl = true;
                        lastBitrateChangeExecutionTime = System.currentTimeMillis();

                        Message msg = new Message();
                        msg.what = MSG_RTSP_CONFIGURE_CHANGED;
                        Bundle bundle = new Bundle();
                        bundle.putString("code", "UP");
                        bundle.putInt("before", currentBitrate);
                        bundle.putInt("next", newBitrate);
                        msg.setData(bundle);

                        mRTSPCheckHandler.sendMessageDelayed(msg, 500);
                        frameQueueIncreased = 0;
                        nonZeroFrameStarted = false;
                        queue.clear();
                        sumQueue = 0;
                        t = 0;
                        bitrateInitiallyStabilized = true;
                        Log.d(TAG, "Current Bitrate: " + currentBitrate + " Increased bitrate to : " + newBitrate);
                    }
                }
            }
        }, 0, 1000);
    }
}
