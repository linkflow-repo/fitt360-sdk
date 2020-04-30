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

import com.android.mediacodec.VideoDecoder;
import com.linkflow.fitt360sdk.R;

import app.library.linkflow.manager.NeckbandRestApiClient;
import app.library.linkflow.manager.item.RecordSetItem;
import app.library.linkflow.manager.neckband.NotifyManage;
import app.library.linkflow.rtsp.RTSPStreamManager;

public class PreviewActivity extends BaseActivity implements SurfaceHolder.Callback {
    private final String TAG = getClass().getSimpleName();
    private static final int MSG_NOT_START_RTSP = 10;
    private RTSPStreamManager mRTSPStreamManager;

    private Handler mRTSPChecker;
    private Button mMuteBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        hideHeader();
        super.onCreate(savedInstanceState);
        setBodyView(R.layout.activity_preview);

        SurfaceView surfaceView = findViewById(R.id.surface);
        surfaceView.getHolder().addCallback(this);

        mMuteBtn = findViewById(R.id.audio);
        mMuteBtn.setOnClickListener(this);

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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surface created");
        mRTSPChecker.removeMessages(MSG_NOT_START_RTSP);
        mRTSPChecker.sendEmptyMessageDelayed(MSG_NOT_START_RTSP, 6000);
        RecordSetItem recordSetItem = mNeckbandManager.getSetManage().getRecordSetItem();
        mRTSPStreamManager = new RTSPStreamManager(recordSetItem.getWidth(), recordSetItem.getHeight(), new VideoDecoder.FrameCallback() {
            @Override
            public void hasFrame() {
                if (mRTSPChecker.hasMessages(MSG_NOT_START_RTSP)) {
                    mNeckbandManager.setPreviewState(true);
                    mRTSPChecker.removeMessages(MSG_NOT_START_RTSP);
                }
            }
        }, null);
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
