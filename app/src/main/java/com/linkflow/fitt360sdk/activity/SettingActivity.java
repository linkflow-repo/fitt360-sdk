package com.linkflow.fitt360sdk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.activity.setting.SettingA2DPActivity;
import com.linkflow.fitt360sdk.activity.setting.SettingAPActivity;
import com.linkflow.fitt360sdk.activity.setting.SettingDeviceStorage;
import com.linkflow.fitt360sdk.activity.setting.SettingGPSActivity;
import com.linkflow.fitt360sdk.activity.setting.SettingLanguageActivity;
import com.linkflow.fitt360sdk.activity.setting.SettingOthersActivity;
import com.linkflow.fitt360sdk.activity.setting.SettingPhotoActivity;
import com.linkflow.fitt360sdk.activity.setting.SettingRecordActivity;
import com.linkflow.fitt360sdk.adapter.SettingRecyclerAdapter;
import com.linkflow.fitt360sdk.item.SettingItem;

import app.library.linkflow.manager.model.SupportCheckModel;

import static com.linkflow.fitt360sdk.activity.BTListActivity.KEY_CALLED_BY;
import static com.linkflow.fitt360sdk.adapter.SettingRecyclerAdapter.ID.ID_LANGUAGE;
import static com.linkflow.fitt360sdk.adapter.SettingRecyclerAdapter.ID.ID_MANAGEMENT_STORAGE;
import static com.linkflow.fitt360sdk.adapter.SettingRecyclerAdapter.ID.ID_SPEC;

public class SettingActivity extends BaseActivity implements SettingRecyclerAdapter.ItemClickedListener {
    private SettingRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(R.string.setting);
        setBodyView(R.layout.activity_setting);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mAdapter = new SettingRecyclerAdapter(this, mNeckbandManager.isSupport(SupportCheckModel.Type.A2DP), this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.init(mNeckbandManager.isSupport(SupportCheckModel.Type.A2DP));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.header_close) {
            finish();
        }
    }

    @Override
    public void clickedItem(int position) {
        SettingItem item = mAdapter.getItem(position);
        if (!mNeckbandManager.getConnectStateManage().isConnected()) {
            if (item.getId() == ID_SPEC || item.getId() == ID_MANAGEMENT_STORAGE || item.getId() == ID_LANGUAGE) {
                Toast.makeText(this, R.string.try_after_connected, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Class selectedClass = null;
        switch (item.getId()) {
            case ID_CONNECT: selectedClass = BTListActivity.class; break;
            case ID_SPEC: selectedClass = SpecActivity.class; break;
            case ID_MANAGEMENT_STORAGE: selectedClass = SettingDeviceStorage.class; break;
            case ID_LANGUAGE: selectedClass = SettingLanguageActivity.class; break;
            case ID_SETTING_PHOTO: selectedClass = SettingPhotoActivity.class; break;
            case ID_SETTING_RECORD: selectedClass = SettingRecordActivity.class; break;
            case ID_SETTING_OTHERS: selectedClass = SettingOthersActivity.class; break;
            case ID_GPS_PHONE: selectedClass = SettingGPSActivity.class; break;
            case ID_A2DP: selectedClass = SettingA2DPActivity.class; break;
            case ID_AP: selectedClass = SettingAPActivity.class; break;
            default:
        }
        if (selectedClass != null) {
            Intent intent = new Intent(this, selectedClass);
            if (item.getId() == SettingRecyclerAdapter.ID.ID_CONNECT) {
                intent.putExtra(KEY_CALLED_BY, BTListActivity.CALLED_BY_SETTING_ACTIVITY);
            }
            startActivity(intent);
        }
    }

    @Override
    public void recordState(boolean isRecording) {
        super.recordState(isRecording);
    }
}
