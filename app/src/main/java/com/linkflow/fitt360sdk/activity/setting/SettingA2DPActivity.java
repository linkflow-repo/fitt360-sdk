package com.linkflow.fitt360sdk.activity.setting;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.item.TitleAndSubItem;

import java.util.ArrayList;

public class SettingA2DPActivity extends SettingBaseRPActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(R.string.setting_a2dp);
    }

    @Override
    protected ArrayList<TitleAndSubItem> initItems() {
        ArrayList<TitleAndSubItem> items = new ArrayList<>();
        int currentA2DPEnable = mNeckbandManager.getSetManage().isActivateA2DPMode() ? R.string.on : R.string.off;
        items.add(new TitleAndSubItem(ID.ID_A2DP, getString(R.string.setting_a2dp_mode), getString(currentA2DPEnable)));
        return items;
    }

    @Override
    public void clickedItem(int position) {
        TitleAndSubItem item = mAdapter.getItem(position);
        int selectedIdPosition = -1;
        switch (item.mRPId) {
            case ID_A2DP: selectedIdPosition = 0; break;
        }
        if (selectedIdPosition != -1) {
            Intent intent = new Intent(this, SettingA2DPSelectActivity.class);
            intent.putExtra("selected_id_position", selectedIdPosition);
            startActivityForResult(intent, REQUEST_RESULT);
        }
    }
}
