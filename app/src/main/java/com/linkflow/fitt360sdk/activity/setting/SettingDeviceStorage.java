package com.linkflow.fitt360sdk.activity.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.activity.BaseActivity;

import app.library.linkflow.manager.model.FormatModel;

public class SettingDeviceStorage extends BaseActivity implements FormatModel.Listener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(R.string.storage_management);
        setBodyView(R.layout.activity_setting_device_storage);

        Button internalDeleteBtn = findViewById(R.id.internal_delete);
        Button externalFormatBtn = findViewById(R.id.external_format);
        internalDeleteBtn.setOnClickListener(this);
        externalFormatBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (!mNeckbandManager.getConnectStateManage().isConnected()) {
            Toast.makeText(this, R.string.try_after_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        if (view.getId() == R.id.internal_delete) {
            mNeckbandManager.format(mNeckbandManager.getAccessToken(), true, this);
        } else if (view.getId() == R.id.external_format) {
            mNeckbandManager.format(mNeckbandManager.getAccessToken(), false, this);
        }
    }

    @Override
    public void completedFormat(boolean success, FormatModel.TYPE type) {
        if (type == FormatModel.TYPE.TYPE_INTERNAL) {
            Toast.makeText(this, success ? R.string.deleted : R.string.try_again_later, Toast.LENGTH_SHORT).show();
        } else if (type == FormatModel.TYPE.TYPE_EXTERNAL) {
            Toast.makeText(this, success ? R.string.formatted : R.string.formatted_alert, Toast.LENGTH_SHORT).show();
        }
    }
}
