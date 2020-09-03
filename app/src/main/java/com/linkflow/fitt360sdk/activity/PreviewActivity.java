package com.linkflow.fitt360sdk.activity;

import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.mediacodec.AVPacket;
import com.android.mediacodec.Packet;
import com.android.mediacodec.VideoDecoder;
import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.texture.MD360VideoTexture;
import com.linkflow.fitt360sdk.R;

import java.nio.ByteBuffer;

import app.library.linkflow.Constant;
import app.library.linkflow.manager.NeckbandRestApiClient;
import app.library.linkflow.manager.item.RecordSetItem;
import app.library.linkflow.manager.model.StitchingModel;
import app.library.linkflow.manager.neckband.NotifyManage;
import app.library.linkflow.rtsp.AudioDecoderThread;
import app.library.linkflow.rtsp.RTSPStreamManager;
import app.library.linkflow.rtsp.VideoDecodeThread;

public class PreviewActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();
    private static final int MSG_NOT_START_RTSP = 10;
    private RTSPStreamManager mRTSPStreamManager;

    private GLSurfaceView mGLSurfaceView;
    private MDVRLibrary mVRLibrary;
    private Handler mRTSPChecker;
    private Button mMuteBtn, mStableBtn;
    private boolean mIsStable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        hideHeader();
        super.onCreate(savedInstanceState);
        setBodyView(R.layout.activity_preview);

        mGLSurfaceView = findViewById(R.id.surface);

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

        initRenderer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVRLibrary.onResume(this);
    }

    private void initRenderer() {
        mVRLibrary = MDVRLibrary.with(this)
                .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION | MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH)
                .asVideo(new MDVRLibrary.IOnSurfaceReadyCallback() {
                    @Override
                    public void onSurfaceReady(Surface surface) {
                        Log.e("preview", "on surface ready");
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
                        mRTSPStreamManager.setSurface(surface);
                        mRTSPStreamManager.start();
                    }

                }).listenGesture(new MDVRLibrary.IGestureListener() {
                    @Override
                    public void onClick(MotionEvent e) {

                    }
                })
                .build(mGLSurfaceView);
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
        mVRLibrary.onDestroy();
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
    public void alertRTSP(String type) {
        if (type.equals(NotifyManage.RTSP_TYPE_EXIT)) {
            mRTSPStreamManager.stop();
            mNeckbandManager.getPreviewModel().activateRTSP(mNeckbandManager.getAccessToken(), false);
            finish();
        }
    }
}
