package com.linkflow.fitt360sdk.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.mediacodec.AVPacket;
import com.android.mediacodec.Packet;
import com.android.mediacodec.VideoDecoder;
import com.linkflow.fitt360sdk.R;

import app.library.linkflow.manager.NeckbandRestApiClient;
import app.library.linkflow.manager.item.RecordSetItem;
import app.library.linkflow.manager.model.StitchingModel;
import app.library.linkflow.manager.neckband.NotifyManage;
import app.library.linkflow.rtsp.AudioDecoderThread;
import app.library.linkflow.rtsp.RTSPStreamManager;

public class PreviewActivity extends BaseActivity implements SurfaceHolder.Callback {
    private final String TAG = getClass().getSimpleName();
    private static final int MSG_NOT_START_RTSP = 10;
    private RTSPStreamManager mRTSPStreamManager;

    private Handler mRTSPChecker;
    private Button mMuteBtn, mStableBtn;
    private boolean mIsStable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        hideHeader();
        super.onCreate(savedInstanceState);
        setBodyView(R.layout.activity_preview);

        SurfaceView surfaceView = findViewById(R.id.surface);
        surfaceView.getHolder().addCallback(this);

        mMuteBtn = findViewById(R.id.audio);
        mMuteBtn.setOnClickListener(this);

        mStableBtn = findViewById(R.id.stable);
        mStableBtn.setOnClickListener(this);
        mIsStable = mNeckbandManager.getSetManage().enabledStabilization();

        mStableBtn.setText(mIsStable ? R.string.stable_on : R.string.stable_off);

        mNeckbandManager.getPreviewModel().activateRTSP(mNeckbandManager.getAccessToken(), !mNeckbandManager.isPreviewing());
        mRTSPChecker = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG_NOT_START_RTSP) {
                    Log.d(TAG, "no started rtsp so do start after 6 sec");
                    mNeckbandManager.getPreviewModel().activateRTSP(mNeckbandManager.getAccessToken(), !mNeckbandManager.isPreviewing());
                    mRTSPChecker.sendEmptyMessageDelayed(MSG_NOT_START_RTSP, 6000);
                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.base_dialog_disagree) {
            if (mNeckbandManager.isRecording()) {
                mNeckbandManager.getRecordModel().actionRecord(mNeckbandManager.getAccessToken(), false);
                mNeckbandManager.setRecordState(false);
            }
            if (mRTSPStreamManager != null) {
                mRTSPStreamManager.stop();
            }
        }  else if (view.getId() == R.id.audio) {
            boolean isAudioDisabled = !mRTSPStreamManager.isAudioDisabled();
            mRTSPStreamManager.setAudioDisable(isAudioDisabled);
            mMuteBtn.setText(isAudioDisabled ? R.string.audio_disable : R.string.audio_enable);
        } else if (view.getId() == R.id.stable) {
            setStable(mIsStable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRTSPStreamManager != null) {
            mRTSPStreamManager.stop();
        }
        mNeckbandManager.setPreviewState(false);
        mNeckbandManager.getPreviewModel().activateRTSP(mNeckbandManager.getAccessToken(), false);
    }

    private void setStable(boolean enable) {
        mNeckbandManager.getSetManage().getStitchingModel().setStabilizationState(mNeckbandManager.getAccessToken(), !enable, new StitchingModel.StabilizationListener() {
            @Override
            public void completedGetStabilizationState(boolean success, boolean enabled) {

            }

            @Override
            public void completedSetStabilizationState(boolean success) {
                if (success) {
                    mIsStable = !mIsStable;
                    mStableBtn.setText(mIsStable ? R.string.stable_on : R.string.stable_off);
                }
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surface created");
        mRTSPChecker.removeMessages(MSG_NOT_START_RTSP);
        mRTSPChecker.sendEmptyMessageDelayed(MSG_NOT_START_RTSP, 6000);
        RecordSetItem recordSetItem = mNeckbandManager.getSetManage().getRecordSetItem();
        mRTSPStreamManager = RTSPStreamManager.builder().setAsyncDataListener(new VideoDecoder.AsyncDataListener() {
            @Override
            public void asyncData(int type, Packet packet) {
                // packet has all information about stream. you can use it.
                if (type == AVPacket.PT_VIDEO) {

                } else if (type == AVPacket.PT_AUDIO) {

                }
            }

            @Override
            public boolean disablePreview() {
                return false;
            }
        }).setFrameCallback(new VideoDecoder.FrameCallback() {
            @Override
            public void hasFrame() {
                if (mRTSPChecker.hasMessages(MSG_NOT_START_RTSP)) {
                    mNeckbandManager.setPreviewState(true);
                    mRTSPChecker.removeMessages(MSG_NOT_START_RTSP);
                }
            }
        }).setAudioDecodedListener(new AudioDecoderThread.DecodedListener() {
            @Override
            public void decodedAudio(byte[] audioData, int offset, int length) {

            }
        }).setResolution(recordSetItem.getWidth(), recordSetItem.getHeight()).build();
        mRTSPStreamManager.setUrl(NeckbandRestApiClient.getRTSPUrl());
        mRTSPStreamManager.setSurface(holder.getSurface());
        mRTSPStreamManager.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surface changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surface destroyed");
    }

    @Override
    public void alertRTSP(String type) {
        if (type.equals(NotifyManage.RTSP_TYPE_EXIT)) {
            mRTSPStreamManager.stop();
            mNeckbandManager.getPreviewModel().activateRTSP(mNeckbandManager.getAccessToken(), false);
            finish();
        }
    }
}
