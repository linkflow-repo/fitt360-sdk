package com.linkflow.fitt360sdk.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.item.TitleAndSubItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import app.library.linkflow.manager.model.SupportCheckModel;

public class SettingOthersActivity extends SettingBaseRPActivity {
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final static String CENTER_MODE_ENABLED_DATE = "2019-09-11";

    private SparseIntArray mBlendMap = new SparseIntArray();
    {
        mBlendMap.put(0, R.string.blend_high);
        mBlendMap.put(7, R.string.blend_low);
        mBlendMap.put(13, R.string.blend_middle);
        mBlendMap.put(21, R.string.blend_high);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(R.string.setting_extra);
    }

    @Override
    protected ArrayList<TitleAndSubItem> initItems() {
        ArrayList<TitleAndSubItem> items = new ArrayList<>();
        int currentDistanceType = mNeckbandManager.getSetManage().getStitchingDistance() == 0 ? R.string.distance_near : R.string.distance_far;
        items.add(new TitleAndSubItem(ID.ID_DISTANCE, getString(R.string.setting_distance), getString(currentDistanceType)));
        int currentReverseMode = mNeckbandManager.getSetManage().isActivatedReverseMode() ? R.string.on : R.string.off;
        items.add(new TitleAndSubItem(ID.ID_FIRST_PERSON, getString(R.string.setting_stitching_center_mode), getString(currentReverseMode)));
        int currentBlendDegree = mNeckbandManager.getSetManage().getBlend();
        items.add(new TitleAndSubItem(ID.ID_BLEND, getString(R.string.setting_stitching_blending), getString(findCorrectBlend(currentBlendDegree))));
        return items;
    }

    @Override
    public void clickedItem(int position) {
        TitleAndSubItem item = mAdapter.getItem(position);
        int selectedIdPosition = -1;
        switch (item.mRPId) {
            case ID_DISTANCE: selectedIdPosition = 0; break;
            case ID_FIRST_PERSON: selectedIdPosition = 1; break;
            case ID_BLEND: selectedIdPosition = 2; break;
        }
        if (selectedIdPosition != -1) {
            if (selectedIdPosition == 1) {
                try {
                    if (mNeckbandManager.getConnectStateManage().isConnected()) {
                        if (mDateFormat.parse(CENTER_MODE_ENABLED_DATE).getTime() > mDateFormat.parse(mNeckbandManager.getInfoManage().getSoftwareItem().mReleaseDate).getTime() && !mNeckbandManager.isSupport(SupportCheckModel.Type.REVERSE)) {
                            Toast.makeText(this, R.string.firmware_need_update, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        Toast.makeText(this, R.string.try_after_connected, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent(this, SettingOthersSelectActivity.class);
            intent.putExtra("selected_id_position", selectedIdPosition);
            startActivityForResult(intent, REQUEST_RESULT);
        }
    }

    private int findCorrectBlend(int blend) {
        int value = mBlendMap.get(blend);
        if (value == 0) {
            if (blend < 8) {
                return R.string.blend_low;
            } else if (blend > 12 && blend < 21) {
                return R.string.blend_middle;
            } else {
                return R.string.blend_high;
            }
        }
        return value;
    }
}
