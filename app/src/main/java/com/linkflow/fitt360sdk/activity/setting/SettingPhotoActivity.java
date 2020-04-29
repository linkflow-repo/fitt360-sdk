package com.linkflow.fitt360sdk.activity.setting;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.item.TitleAndSubItem;

import java.util.ArrayList;

import app.library.linkflow.manager.item.PhotoExtendSetItem;
import app.library.linkflow.manager.item.PhotoSetItem;

public class SettingPhotoActivity extends SettingBaseRPActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(R.string.setting_photo);
    }

    @Override
    protected ArrayList<TitleAndSubItem> initItems() {
        ArrayList<TitleAndSubItem> items = new ArrayList<>();
        PhotoSetItem photoSetItem = mNeckbandManager.getSetManage().getPhotoSetItem();
        PhotoExtendSetItem photoExtendSetItem = mNeckbandManager.getSetManage().getPhotoExtendSetItem();
        items.add(new TitleAndSubItem(ID.ID_CAMERA_MODE, getString(R.string.camera_mode), photoSetItem.mViewMode));
        if (photoSetItem.mViewMode.equals("single")) {
            items.add(new TitleAndSubItem(ID.ID_CAMERA_POSITION, getString(R.string.camera_position),getString(CAMERA_POSITION[photoSetItem.mSingle - 1])));
        }
        items.add(new TitleAndSubItem(ID.ID_RESOLUTION, getString(R.string.resolution), photoSetItem.getWidth() + "x" + photoSetItem.getHeight()));
        items.add(new TitleAndSubItem(ID.ID_FORMAT, getString(R.string.format), photoExtendSetItem.mFormat));
        items.add(new TitleAndSubItem(ID.ID_DELAY, getString(R.string.delay), (float)photoExtendSetItem.mDelay / 1000.0f + " " + getString(R.string.second)));
        return items;
    }

    @Override
    public void clickedItem(int position) {
        TitleAndSubItem item = mAdapter.getItem(position);
        int selectedIdPosition = -1;
        switch (item.mRPId) {
            case ID_CAMERA_MODE: selectedIdPosition = 0; break;
            case ID_RESOLUTION: selectedIdPosition = 1; break;
            case ID_FORMAT: selectedIdPosition = 2; break;
            case ID_DELAY: selectedIdPosition = 3; break;
            case ID_CAMERA_POSITION: selectedIdPosition = 4; break;
        }
        if (selectedIdPosition != -1) {
            Intent intent = new Intent(this, SettingPhotoSelectActivity.class);
            intent.putExtra("selected_id_position", selectedIdPosition);
            startActivityForResult(intent, REQUEST_RESULT);
        }
    }
}
