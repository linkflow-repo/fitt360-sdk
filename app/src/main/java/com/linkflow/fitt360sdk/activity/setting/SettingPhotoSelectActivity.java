package com.linkflow.fitt360sdk.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.item.Item;
import com.linkflow.fitt360sdk.item.RadioItem;

import java.util.ArrayList;

import app.library.linkflow.manager.item.PhotoExtendSetItem;
import app.library.linkflow.manager.item.PhotoSetItem;

public class SettingPhotoSelectActivity extends SettingBaseSelectActivity {
    private static final String[] STITCHING_RESOLUTION = new String[] { "1440x720", "1280x720", "1920x1080", "2160x1080", "2880x1440", "3840x1920", "4800x2400" };
    private static final String[] SIDE_BY_SIDE_RESOLUTION = new String[] { "2160x720", "3240x1080", "4320x1440", "6480x2160", "7200x2400" };
    private static final String[] SINGLE_RESOLUTION = new String[] { "720x720", "1080x1080", "1440x1440", "2160x2160", "2400x2400" };
    private static final String[] FORMAT = new String[] { "JPEG" };

    private static final int[] TITLES = new int[] { R.string.camera_mode, R.string.resolution, R.string.format, R.string.delay, R.string.camera_position };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(TITLES[mSelectedIdPosition]);

        mAdapter.setItems(initItems());
    }

    @Override
    protected ArrayList<Item> initItems() {
        PhotoSetItem photoSetItem = mNeckbandManager.getSetManage().getPhotoSetItem();
        PhotoExtendSetItem photoExtendSetItem = mNeckbandManager.getSetManage().getPhotoExtendSetItem();
        switch (mSelectedIdPosition) {
            case 0: return makeItems(findPosition(photoSetItem.mViewMode, CAMERA_MODE), CAMERA_MODE);
            case 1: return makeResolutions(photoSetItem.getWidth() + "x" + photoSetItem.getHeight());
            case 2: return makeItems(findPosition(photoExtendSetItem.mFormat, FORMAT), FORMAT);
            case 3: return makeItems(findPosition(photoExtendSetItem.mDelay, DELAY), DELAY_VALUE, DELAY, R.string.delay_title);
            case 4:
                int position = photoSetItem.mSingle - 1;
                position = position == 1 ? 2 : position == 2 ? 1 : position;
                return makeItems(position, CAMERA_POSITION);
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
                    if (!mSetManage.getPhotoSetItem().mViewMode.equals(item.mTitle)) {
                        String[] resolution = null;
                        switch (item.mTitle) {
                            case "stitching": resolution = STITCHING_RESOLUTION[5].split("x"); break;
                            case "sidebyside": resolution = SIDE_BY_SIDE_RESOLUTION[4].split("x"); break;
                            case "single": resolution = SINGLE_RESOLUTION[4].split("x"); break;
                        }
                        if (resolution != null) {
                            mSetManage.getPhotoSetItem().setResolution(Integer.parseInt(resolution[0]), Integer.parseInt(resolution[1]));
                        }
                    }
                    mSetManage.getPhotoSetItem().setViewMode(item.mTitle);
                    mSetManage.getPhotoSetModel().setBaseSettings(mNeckbandManager.getAccessToken(), mSetManage.getPhotoSetItem());
                    break;
                case 1:
                    String[] resolution = item.mTitle.split("x");
                    mSetManage.getPhotoSetItem().setResolution(Integer.parseInt(resolution[0]), Integer.parseInt(resolution[1]));
                    mSetManage.getPhotoSetModel().setBaseSettings(mNeckbandManager.getAccessToken(), mSetManage.getPhotoSetItem());
                    break;
                case 2:
                    mSetManage.getPhotoExtendSetItem().mFormat = item.mTitle;
                    mSetManage.getPhotoSetModel().setExtendSettings(mNeckbandManager.getAccessToken(), mSetManage.getPhotoExtendSetItem());
                    break;
                case 3:
                    mSetManage.getPhotoExtendSetItem().mDelay = item.mValue;
                    mSetManage.getPhotoSetModel().setExtendSettings(mNeckbandManager.getAccessToken(), mSetManage.getPhotoExtendSetItem());
                    break;
                case 4:
                    int cameraPosition = position + 1;
                    cameraPosition = cameraPosition == 3 ? 2 : cameraPosition == 2 ? 3 : cameraPosition;
                    mSetManage.getPhotoSetItem().mSingle = cameraPosition;
                    mSetManage.getPhotoSetModel().setBaseSettings(mNeckbandManager.getAccessToken(), mSetManage.getPhotoSetItem());
                    break;
            }
        }
    }

    private ArrayList<Item> makeResolutions(String currentValue) {
        switch (mSetManage.getPhotoSetItem().mViewMode) {
            case "stitching": return makeItems(findPosition(currentValue, STITCHING_RESOLUTION), STITCHING_RESOLUTION);
            case "sidebyside": return makeItems(findPosition(currentValue, SIDE_BY_SIDE_RESOLUTION), SIDE_BY_SIDE_RESOLUTION);
            case "single": return makeItems(findPosition(currentValue, SINGLE_RESOLUTION), SINGLE_RESOLUTION);
        }
        return null;
    }
}
