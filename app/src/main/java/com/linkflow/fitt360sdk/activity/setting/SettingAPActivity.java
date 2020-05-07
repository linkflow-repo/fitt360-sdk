package com.linkflow.fitt360sdk.activity.setting;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.activity.BaseActivity;

import app.library.linkflow.manager.item.WifiAPItem;
import app.library.linkflow.manager.model.WifiAPModel;
import app.library.linkflow.manager.neckband.SetManage;

public class SettingAPActivity extends BaseActivity implements SetManage.Listener, WifiAPModel.Listener {
    private EditText mAPSSIDEt, mAPPasswordEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(R.string.setting_ap);
        setBodyView(R.layout.activity_setting_ap);

        Button applyBtn = findViewById(R.id.apply);
        applyBtn.setOnClickListener(this);

        mAPSSIDEt = findViewById(R.id.ssid_et);
        mAPPasswordEt = findViewById(R.id.password_et);

        mNeckbandManager.getSetManage().getWifiAPModel().setListener(this);
        mNeckbandManager.getSetManage().setListener(this);

        initWifiAPInfo(mNeckbandManager.getSetManage().getWifiAPItem());
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.apply) {
            apply();
        }
    }

    private void initWifiAPInfo(WifiAPItem item) {
        if (item != null) {
            mAPSSIDEt.setText(item.mWifiSSID);
            mAPPasswordEt.setText(item.mPassword);
        }
    }

    private void apply() {
        String ssid = mAPSSIDEt.getText().toString();
        String password = mAPPasswordEt.getText().toString();
        if (ssid.trim().length() > 2 && password.trim().length() > 2) {
            WifiAPItem item = new WifiAPItem(ssid, password);
            mNeckbandManager.getSetManage().getWifiAPModel().setApInfo(mNeckbandManager.getAccessToken(), item);
        }
    }

    @Override
    public void completedCallSetApi(boolean success, boolean isSet) {
        if (success && isSet) {
            Toast.makeText(this, R.string.applied, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void completedGetStoredApInfo(boolean success, WifiAPItem item) {
        if (success) {
            initWifiAPInfo(item);
        }
    }

    @Override
    public void completedSetApInfo(boolean success) {
        if (success) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mNeckbandManager.getSetManage().getWifiAPModel().getStoredApInfo(mNeckbandManager.getAccessToken());
                }
            }, 1000);
        }
    }
}
