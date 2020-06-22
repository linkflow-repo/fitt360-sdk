package com.linkflow.fitt360sdk.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.item.Item;
import com.linkflow.fitt360sdk.item.RadioItem;

import java.util.ArrayList;

import app.library.linkflow.manager.StoredDataManager;
import app.library.linkflow.manager.item.BaseExtendSetItem;
import app.library.linkflow.manager.item.BaseSetItem;
import app.library.linkflow.manager.item.RecordExtendSetItem;
import app.library.linkflow.manager.item.RecordSetItem;
import app.library.linkflow.manager.neckband.SettingListener;

public class SettingRecordSelectActivity extends SettingBaseSelectActivity implements SettingListener {
    private static final String[] STITCHING_RESOLUTION = new String[] { "1440x720", "1280x720", "1920x1080", "2160x1080", "2880x1440", "3840x1920" };
    private static final String[] SINGLE_RESOLUTION = new String[] { "720x720", "1080x1080", "1440x1440", "2160x2160", "2400x2400" };
    private static final String[] SIDE_BY_SIDE_RESOLUTION = new String[] { "1440x480", "1920x640", "2160x720", "3240x1080", "3840x1280" };
    public static final int[] BITRATE = new int[] { 10, 20, 30 };
    public static final int[] BITRATE_TITLE = new int[] { R.string.bitrate_low, R.string.bitrate_middle, R.string.bitrate_high};
    private static final String[] FPS = new String[] { "30fps", "15fps" };
    private static final String[] CODEC = new String[] { "H.264", "H.265" };
    public static final int[] TIME_LAPSE_RATE = new int[] { 2, 4, 8, 16, 32 };
    public static final String[] TIME_LAPSE_RATE_TITLE = new String[] { "2x", "4x", "8x", "16x", "32x" };

    private static final int[] TITLES = new int[] { R.string.camera_mode, R.string.resolution, R.string.fps, R.string.bitrate, R.string.codec, R.string.delay,
            R.string.camera_position, R.string.setting_timelapse_state, R.string.setting_timelapse_rate };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(TITLES[mSelectedIdPosition]);
        mAdapter.setItems(initItems());

        mSetManage.getRecordSetModel().setListener(this);
    }

    @Override
    protected ArrayList<Item> initItems() {
        RecordSetItem recordSetItem = mNeckbandManager.getSetManage().getRecordSetItem();
        RecordExtendSetItem recordExtendSetItem = mNeckbandManager.getSetManage().getRecordExtendSetItem();
        switch (mSelectedIdPosition) {
            case 0: return makeItems(findPosition(recordSetItem.mViewMode, CAMERA_MODE), CAMERA_MODE);
            case 1: return makeResolutions(recordSetItem.getWidth() + "x" + recordSetItem.getHeight());
            case 2: return makeItems(findPosition(recordSetItem.mFPS + "fps", FPS), FPS);
            case 3: return makeItems(findPosition(recordSetItem.mBitrate, BITRATE), BITRATE_TITLE, BITRATE);
            case 4: return makeItems(findPosition(recordExtendSetItem.mCodec, CODEC), CODEC);
            case 5: return makeItems(findPosition(recordExtendSetItem.mDelay, DELAY), DELAY_VALUE, DELAY, R.string.delay_title);
            case 6:
                int position = recordSetItem.mSingle - 1;
                position = position == 1 ? 2 : position == 2 ? 1 : position;
                return makeItems(position, CAMERA_POSITION);
            case 7: return makeItems(mNeckbandManager.getSetManage().isTimeLapseActivate() ? 1 : 0, new String[] { getString(R.string.off), getString(R.string.on) });
            case 8:
                float currentRate = mNeckbandManager.getSetManage().getTimeLapseRate();
                return makeItems(findCorrectTimeLapseRate(currentRate, recordSetItem.mFPS), TIME_LAPSE_RATE_TITLE);
        }
        return super.initItems();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    public void selectedItem(int position) {
        if (!mNeckbandManager.getConnectStateManage().isConnected()) {
            Toast.makeText(this, R.string.try_after_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAdapter.updateAdapter(position)) {
            RadioItem item = mAdapter.getItemAsRadio(position);
            switch (mSelectedIdPosition) {
                case 0:
                    if (!mSetManage.getRecordSetItem().mViewMode.equals(item.mTitle)) {
                        String[] resolution = null;
                        switch (item.mTitle) {
                            case "stitching": resolution = STITCHING_RESOLUTION[5].split("x"); break;
                            case "sidebyside": resolution = SIDE_BY_SIDE_RESOLUTION[4].split("x"); break;
                            case "single": resolution = SINGLE_RESOLUTION[4].split("x"); break;
                        }
                        if (resolution != null) {
                            mSetManage.getRecordSetItem().setResolution(Integer.parseInt(resolution[0]), Integer.parseInt(resolution[1]));
                        }
                    }
                    mSetManage.getRecordSetItem().setViewMode(item.mTitle);
                    mSetManage.getRecordSetModel().setBaseSettings(mNeckbandManager.getAccessToken(), true, mSetManage.getRecordSetItem());
                    break;
                case 1:
                    String[] resolution = item.mTitle.split("x");
                    mSetManage.getRecordSetItem().setResolution(Integer.parseInt(resolution[0]), Integer.parseInt(resolution[1]));
                    mSetManage.getRecordSetModel().setBaseSettings(mNeckbandManager.getAccessToken(), true, mSetManage.getRecordSetItem());
                    break;
                case 2:
                    int ratePosition = findCorrectTimeLapseRate(mSetManage.getTimeLapseRate(), mSetManage.getRecordSetItem().mFPS);
                    mSetManage.getRecordSetItem().mFPS = Integer.parseInt(item.mTitle.replaceAll("fps", ""));
                    mSetManage.getRecordSetModel().setBaseSettings(mNeckbandManager.getAccessToken(), true, mSetManage.getRecordSetItem());
                    mSetManage.getTimeLapseModel().setRate(mNeckbandManager.getAccessToken(), (float)mSetManage.getRecordSetItem().mFPS / TIME_LAPSE_RATE[ratePosition]);
                    break;
                case 3:
                    mSetManage.getRecordSetItem().mBitrate = item.mValue;
                    mSetManage.getRecordSetModel().setBaseSettings(mNeckbandManager.getAccessToken(), true, mSetManage.getRecordSetItem());
                    break;
                case 4:
                    mSetManage.getRecordExtendSetItem().mCodec = item.mTitle;
                    mSetManage.getRecordSetModel().setExtendSettings(mNeckbandManager.getAccessToken(), mSetManage.getRecordExtendSetItem());
                    break;
                case 5:
                    mSetManage.getRecordExtendSetItem().mDelay = item.mValue;
                    mSetManage.getRecordSetModel().setExtendSettings(mNeckbandManager.getAccessToken(), mSetManage.getRecordExtendSetItem());
                    break;
                case 6:
                    int cameraPosition = position + 1;
                    cameraPosition = cameraPosition == 3 ? 2 : cameraPosition == 2 ? 3 : cameraPosition;
                    mSetManage.getRecordSetItem().mSingle = cameraPosition;
                    mSetManage.getRecordSetModel().setBaseSettings(mNeckbandManager.getAccessToken(), true, mSetManage.getRecordSetItem());
                    break;
                case 7: mNeckbandManager.getSetManage().getTimeLapseModel().setState(mNeckbandManager.getAccessToken(), position == 1); break;
                case 8:
                    float rate = mSetManage.getRecordSetItem().mFPS / (float)TIME_LAPSE_RATE[position];
                    mNeckbandManager.getSetManage().getTimeLapseModel().setRate(mNeckbandManager.getAccessToken(), rate);
                    break;
            }
        }
    }

    @Override
    public void completedSetBaseSetting(boolean success, BaseSetItem item) {
        if (success && item instanceof RecordSetItem) {
            StoredDataManager.getInstance().setData(this, StoredDataManager.KEY_RECORD_WIDTH, item.getWidth());
            StoredDataManager.getInstance().setData(this, StoredDataManager.KEY_RECORD_HEIGHT, item.getHeight());
            StoredDataManager.getInstance().setData(this, StoredDataManager.KEY_BITRATE, ((RecordSetItem) item).mBitrate);
            StoredDataManager.getInstance().setData(this, StoredDataManager.KEY_FPS, ((RecordSetItem) item).mFPS);
        }
    }

    @Override
    public void completedSetExtendSetting(boolean success, BaseExtendSetItem item) {

    }

    private ArrayList<Item> makeResolutions(String currentValue) {
        switch (mSetManage.getRecordSetItem().mViewMode) {
            case "stitching": return makeItems(findPosition(currentValue, STITCHING_RESOLUTION), STITCHING_RESOLUTION);
            case "sidebyside": return makeItems(findPosition(currentValue, SIDE_BY_SIDE_RESOLUTION), SIDE_BY_SIDE_RESOLUTION);
            case "single": return makeItems(findPosition(currentValue, SINGLE_RESOLUTION), SINGLE_RESOLUTION);
        }
        return null;
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
}
