package com.linkflow.fitt360sdk.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.item.TitleAndSubItem;

import java.util.ArrayList;

import app.library.linkflow.manager.item.RecordExtendSetItem;
import app.library.linkflow.manager.item.RecordSetItem;
import app.library.linkflow.manager.model.SupportCheckModel;
import app.library.linkflow.manager.neckband.ConnectStateManage;

import static com.linkflow.fitt360sdk.activity.setting.SettingRecordSelectActivity.TIME_LAPSE_RATE;
import static com.linkflow.fitt360sdk.activity.setting.SettingRecordSelectActivity.TIME_LAPSE_RATE_TITLE;

public class SettingRecordActivity extends SettingBaseRPActivity  {
    private SparseIntArray mBitrateMap = new SparseIntArray();
    {
        mBitrateMap.put(10, R.string.bitrate_low);
        mBitrateMap.put(20, R.string.bitrate_middle);
        mBitrateMap.put(30, R.string.bitrate_high);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(R.string.setting_record);
    }

    @Override
    protected ArrayList<TitleAndSubItem> initItems() {
        ArrayList<TitleAndSubItem> items = new ArrayList<>();
        RecordSetItem recordSetItem = mNeckbandManager.getSetManage().getRecordSetItem();
        RecordExtendSetItem recordExtendSetItem = mNeckbandManager.getSetManage().getRecordExtendSetItem();
        items.add(new TitleAndSubItem(ID.ID_CAMERA_MODE, getString(R.string.camera_mode), recordSetItem.mViewMode));
        if (recordSetItem.mViewMode.equals("single")) {
            items.add(new TitleAndSubItem(ID.ID_CAMERA_POSITION, getString(R.string.camera_position),getString(CAMERA_POSITION[recordSetItem.mSingle - 1])));
        }
        items.add(new TitleAndSubItem(ID.ID_RESOLUTION, getString(R.string.resolution), recordSetItem.getWidth() + "x" + recordSetItem.getHeight()));
        items.add(new TitleAndSubItem(ID.ID_FPS, getString(R.string.fps), recordSetItem.mFPS + " FPS"));
        items.add(new TitleAndSubItem(ID.ID_BITRATE, getString(R.string.bitrate), getString(findCorrectBitrateStatus(recordSetItem.mBitrate))));
        items.add(new TitleAndSubItem(ID.ID_CODEC, getString(R.string.codec), recordExtendSetItem.mCodec));
        items.add(new TitleAndSubItem(ID.ID_DELAY, getString(R.string.delay), (float)recordExtendSetItem.mDelay / 1000.0f + " " + getString(R.string.second)));
        if (mNeckbandManager.isSupport(SupportCheckModel.Type.HYPER_LAPSE)) {
            int timeLapseState = mNeckbandManager.getSetManage().isTimeLapseActivate() ? R.string.on : R.string.off;
            items.add(new TitleAndSubItem(ID.ID_TIME_LAPSE_STATE, getString(R.string.setting_timelapse_state), getString(timeLapseState)));
            float timeLapseRate = mNeckbandManager.getSetManage().getTimeLapseRate();
            items.add(new TitleAndSubItem(ID.ID_TIME_LAPSE_RATE, getString(R.string.setting_timelapse_rate), TIME_LAPSE_RATE_TITLE[findCorrectTimeLapseRate(timeLapseRate, recordSetItem.mFPS)]));
        }
        return items;
    }

    @Override
    public void clickedItem(int position) {
        TitleAndSubItem item = mAdapter.getItem(position);
        int selectedIdPosition = -1;
        switch (item.mRPId) {
            case ID_CAMERA_MODE: selectedIdPosition = 0; break;
            case ID_RESOLUTION: selectedIdPosition = 1; break;
            case ID_FPS: selectedIdPosition = 2; break;
            case ID_BITRATE: selectedIdPosition = 3; break;
            case ID_CODEC:
                Toast.makeText(this, R.string.support_not_yet, Toast.LENGTH_SHORT).show();
                break;
            case ID_DELAY: selectedIdPosition = 5; break;
            case ID_CAMERA_POSITION: selectedIdPosition = 6; break;
            case ID_TIME_LAPSE_STATE: selectedIdPosition = 7; break;
            case ID_TIME_LAPSE_RATE: selectedIdPosition = 8; break;
        }
        if (selectedIdPosition != -1) {
            Intent intent = new Intent(this, SettingRecordSelectActivity.class);
            intent.putExtra("selected_id_position", selectedIdPosition);
            startActivityForResult(intent, REQUEST_RESULT);
        }
    }

    private int findCorrectBitrateStatus(int bitrate) {
        int value = mBitrateMap.get(bitrate);
        if (value == 0) {
            if (bitrate < 10) {
                return R.string.bitrate_low;
            } else if (bitrate > 20 && bitrate < 30) {
                return R.string.bitrate_middle;
            } else {
                return R.string.bitrate_high;
            }
        }
        return value;
    }

    private int findCorrectTimeLapseRate(float currentRate, int currentFPS) {
        int size = TIME_LAPSE_RATE.length;
        int position = 0;
        int targetRate = Math.round(currentFPS / currentRate);
        int beforeGab = Math.abs(TIME_LAPSE_RATE[0] - targetRate);
        for (int i = 1; i < size; i++) {
            int gap = Math.abs(TIME_LAPSE_RATE[i] - targetRate);
            if (beforeGab > gap) {
                position = i;
            }
        }
        return position;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.header_close) {
            onBackPressed();
        }
    }

    @Override
    public void onConnectState(ConnectStateManage.STATE state) {

    }

    @Override
    public void onBackPressed() {
        if (mIsChangedSettings) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
        finish();
    }
}
