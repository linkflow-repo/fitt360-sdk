package com.linkflow.fitt360sdk.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.dialog.TemperLimitAlertDialog;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import app.library.linkflow.manager.item.StatusItem;
import app.library.linkflow.manager.item.VolumeItem;
import app.library.linkflow.manager.model.InfoModel;
import app.library.linkflow.manager.model.NotifyModel;
import app.library.linkflow.manager.model.RecordModel;
import app.library.linkflow.manager.neckband.ConnectStateManage;
import app.library.linkflow.manager.neckband.NeckbandManager;
import app.library.linkflow.manager.neckband.NotifyManage;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener,
        ConnectStateManage.StateListener, NotifyManage.Listener, RecordModel.Listener, NotifyModel.AgreementListener {
    private String TAG = getClass().getSimpleName();
    private RelativeLayout mHeaderLayout, mBodyLayout;
    private LinearLayout mShadowLayout;
    private ViewGroup mViewGroup;
    private TextView mHeaderTitleTv;
    protected TextView mSubmitTv;
    protected ImageButton mCloseBtn;
    private boolean mBaseHeaderDisable;

    protected NeckbandManager mNeckbandManager;
    protected NotifyManage mNotifyManage;
    protected BatteryAndStorageChecker mBatteryAndStorageChecker = BatteryAndStorageChecker.getInstance();
    protected TemperLimitAlertDialog mTemperLimitAlertDialog = new TemperLimitAlertDialog();

    protected static int mBeforeBatteryLevel = 0;

    private boolean mAlertEnable = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mHeaderLayout = findViewById(R.id.base_header);
        mBodyLayout = findViewById(R.id.base_body);
        mShadowLayout = findViewById(R.id.base_shadow);
        mViewGroup = (ViewGroup) ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);

        if (!mBaseHeaderDisable) {
            setHeaderView(R.layout.header_base);
        }

        mNeckbandManager = NeckbandManager.getInstance().init(getApplicationContext());
        mNotifyManage = mNeckbandManager.getNotifyManage();

        mTemperLimitAlertDialog.setClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.header_close) {
            finish();
        } else if (view.getId() == R.id.base_dialog_agree) {
            mNeckbandManager.getNotifyManage().getNotifyModel().agreementTemperLimit(mNeckbandManager.getAccessToken(), true, this);
        } else if (view.getId() == R.id.base_dialog_disagree) {
            mTemperLimitAlertDialog.dismiss();
            if (mNeckbandManager.isRecording()) {
                mNeckbandManager.getRecordModel().actionRecord(mNeckbandManager.getAccessToken(), false);
                mNeckbandManager.setRecordState(false);
            }
            mTemperLimitAlertDialog.dismiss();
        }
    }

    protected void setHeaderView(int resource) {
        mHeaderLayout.removeAllViews();
        View view = LayoutInflater.from(this).inflate(resource, mViewGroup, false);
        mHeaderLayout.addView(view);
        mHeaderTitleTv = view.findViewById(R.id.header_title);
        mSubmitTv = view.findViewById(R.id.header_submit);
        if (mSubmitTv != null) {
            mSubmitTv.setOnClickListener(this);
        }
        mCloseBtn = view.findViewById(R.id.header_close);
        if (mCloseBtn != null) {
            mCloseBtn.setOnClickListener(this);
        }
    }

    protected void setBodyView(int resource) {
        View view = LayoutInflater.from(this).inflate(resource, mViewGroup, false);
        mBodyLayout.addView(view);
    }

    protected void clearBodyBackground() {
        mBodyLayout.setBackgroundColor(0);
    }

    protected void shadow(boolean enable) {
        mShadowLayout.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    protected void hideHeader() {
        mBaseHeaderDisable = true;
    }

    protected void setHeaderTitle(int resource) {
        mHeaderTitleTv.setText(resource);
    }

    protected void setHeaderTitle(String title) {
        mHeaderTitleTv.setText(title);
    }

    protected void setSubmitText(int resource) {
        mSubmitTv.setText(resource);
    }

    protected void enableAlert(boolean enable) {
        mAlertEnable = enable;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNeckbandManager.getConnectStateManage().setStateListener(this);
        mNotifyManage.setListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onConnectState(ConnectStateManage.STATE state) {
        Log.d(TAG, "on connect state - " + state);
        if (state == ConnectStateManage.STATE.STATE_DONE) {
            mNeckbandManager.getRecordModel(this).getRecordState(mNeckbandManager.getAccessToken());
            mBatteryAndStorageChecker.setListener(new BatteryAndStorageCheckerListener() {
                @Override
                public void takeBatteryAndStorageLevel() {
                    mNeckbandManager.getInfoManage().getInfoModel().getStatus(mNeckbandManager.getAccessToken(), new InfoModel.StatusListener() {
                        @Override
                        public void completedGetStatus(boolean success, StatusItem statusItem, VolumeItem volumeItem) {
                            if (success) {
                                if (mBeforeBatteryLevel != statusItem.mBatteryLevel && statusItem.mBatteryLevel <= 30) {
                                    mBeforeBatteryLevel = statusItem.mBatteryLevel;
                                    Toast.makeText(BaseActivity.this, mBeforeBatteryLevel + "% Battery low", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            });
        } else if (state == ConnectStateManage.STATE.STATE_NONE) {
            mNeckbandManager.setRecordState(false);
        }
    }

    @Override
    public void onConnectAlert(ConnectStateManage.ALERT type) {
        if (mAlertEnable) {
            if (type == ConnectStateManage.ALERT.ALERT_GPS_DISABLED) {
                if (!mNeckbandManager.getConnectStateManage().isConnected()) {

                }
            } else if (type == ConnectStateManage.ALERT.ALERT_GPS_ENABLED || type == ConnectStateManage.ALERT.ALERT_DISABLE) {

            }
        }
    }

    @Override
    public void completedGetRecordState(boolean success, boolean isRecording) {
        if (success) {
            mNeckbandManager.setRecordState(isRecording);
        }
    }

    @Override
    public void completedActionRecord(boolean success, boolean isRecording, String filename) {
        if (success) {
            mNeckbandManager.setRecordState(isRecording);
        }
    }

    @Override
    public void recordState(boolean isRecording) {
        mNeckbandManager.setRecordState(isRecording);
    }

    @Override
    public void alertOnPreview(String type) {

    }

    @Override
    public void alertHighTemperature(String type) {

    }

    @Override
    public void alertStorageFull(String type) {
        if (type.equals("external")) {
            Toast.makeText(this, "external storage full", Toast.LENGTH_SHORT).show();
        } else if (type.equals("internal")) {
            Toast.makeText(this, "internal storage full", Toast.LENGTH_SHORT).show();
        } else if (type.equals("both")) {
            Toast.makeText(this, "internal and external storage full", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void alertSdcardChanged(boolean isMounted) {
        mNeckbandManager.getInfoManage().getSpecItem(mNeckbandManager.getAccessToken(), true, null);
    }

    @Override
    public void alertBatteryLow(int level) {

    }

    @Override
    public void completedAgreement(boolean success) {
        if (success) {
            if (mTemperLimitAlertDialog.isAdded()) {
                mTemperLimitAlertDialog.dismiss();
            }
        }
    }

    @Override
    public void alertTemperLimit(String type, int temper) {
        String message = null;
        switch (type) {
            case NotifyManage.TEMPER_ALERT_FIRST: message = "The neckband temperature has risen."; break;
            case NotifyManage.TEMPER_ALERT_SECOND:
                if (!mTemperLimitAlertDialog.isAdded()) {
                    mTemperLimitAlertDialog.showWithMessage(getSupportFragmentManager(),
                            "For your safety, we will exit the " + getWorkingState() + " after 5 minutes.\nDo you want to continue?");
                }
                break;
            case NotifyManage.TEMPER_ALERT_THIRD:
                message = "Exit the " + getWorkingState()  + " for your safety.\\nPlease rest for a while";
                break;
        }
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private String getWorkingState() {
        String workingState = "";
        if (mNeckbandManager.isPreviewing()) {
            workingState = "preview";
        }
        if (mNeckbandManager.isRecording()) {
            workingState += (workingState.length() > 1 ? ", " : "") + "video";
        }
        return workingState;
    }

    @Override
    public void alertRTSP(String type) {

    }

    @Override
    public void connectedRndis(String rndisIp) {

    }

    protected boolean isUSBTetheringActive(){
        try{
            for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
                NetworkInterface intf=en.nextElement();
                for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
                    enumIpAddr.nextElement();
                    if(!intf.isLoopback()){
                        if(intf.getName().contains("rndis")){
                            return true;
                        }
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static class BatteryAndStorageChecker extends AsyncTask<Void, Integer, Void> {
        private static BatteryAndStorageChecker mInstance;
        private boolean mStop, mIsWorking;
        private BatteryAndStorageCheckerListener mListener;
        private int mLevel = -1;

        public static BatteryAndStorageChecker getInstance() {
            if (mInstance == null) {
                mInstance = new BatteryAndStorageChecker();
            }
            return mInstance;
        }

        public BatteryAndStorageChecker() {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        public void setListener(BatteryAndStorageCheckerListener listener) {
            mListener = listener;
        }

        public void start() {
            mIsWorking = true;
            mStop = false;
        }

        public void terminate() {
            mIsWorking = false;
            mStop = true;
        }

        public boolean isWorking() {
            return mIsWorking;
        }

        public void setLevel(int level) {
            mLevel = level;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int cnt = 3;
            while(!mStop) {
                mIsWorking = true;
                if (mListener != null) {
                    mListener.takeBatteryAndStorageLevel();
                }
                if (mLevel != -1) {
                    if (cnt-- > 0) {
                        mIsWorking = false;
                        mStop = true;
                        break;
                    }
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public interface BatteryAndStorageCheckerListener {
        void takeBatteryAndStorageLevel();
    }
}
