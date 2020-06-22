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
        int[] currentTrim = mNeckbandManager.getSetManage().getStitchingTrim();
        items.add(new TitleAndSubItem(ID.ID_STITCHING_TRIM_UPPER, getString(R.string.setting_stitching_trim_upper), String.valueOf(currentTrim[0])));
        items.add(new TitleAndSubItem(ID.ID_STITCHING_TRIM_LOWER, getString(R.string.setting_stitching_trim_lower), String.valueOf(currentTrim[1])));
        int currentFilterEnable = mNeckbandManager.getSetManage().enabledStitchingFilter() ? R.string.on : R.string.off;
        items.add(new TitleAndSubItem(ID.ID_STITCHING_FILTER_ENABLE, getString(R.string.setting_stitching_filter_enable), getString(currentFilterEnable)));
        int currentFilterType = mNeckbandManager.getSetManage().getStitchingFilterType();
        items.add(new TitleAndSubItem(ID.ID_STITCHING_FILTER_TYPE, getString(R.string.setting_stitching_filter_type), String.valueOf(currentFilterType)));
        float[] currentStitchingColors = mNeckbandManager.getSetManage().getStitchingColor();
        items.add(new TitleAndSubItem(ID.ID_STITCHING_COLOR_BRIGHTNESS, getString(R.string.setting_stitching_color_brightness), String.valueOf(currentStitchingColors[0])));
        items.add(new TitleAndSubItem(ID.ID_STITCHING_COLOR_CONTRAST, getString(R.string.setting_stitching_color_contrast), String.valueOf(currentStitchingColors[1])));
        items.add(new TitleAndSubItem(ID.ID_STITCHING_COLOR_SATURATION, getString(R.string.setting_stitching_color_saturation), String.valueOf(currentStitchingColors[2])));
        float[] currentStitchingDualParam = mNeckbandManager.getSetManage().getStitchingDualParam();
        items.add(new TitleAndSubItem(ID.ID_STITCHING_FILTER_DUAL_SCALE_H, getString(R.string.setting_stitching_filter_dual_scale_h), String.valueOf(currentStitchingDualParam[0])));
        items.add(new TitleAndSubItem(ID.ID_STITCHING_FILTER_DUAL_SCALE_V, getString(R.string.setting_stitching_filter_dual_scale_v), String.valueOf(currentStitchingDualParam[1])));
        items.add(new TitleAndSubItem(ID.ID_STITCHING_FILTER_DUAL_OFFSET_H, getString(R.string.setting_stitching_filter_dual_offset_h), String.valueOf(currentStitchingDualParam[2])));
        items.add(new TitleAndSubItem(ID.ID_STITCHING_FILTER_DUAL_OFFSET_V, getString(R.string.setting_stitching_filter_dual_offset_v), String.valueOf(currentStitchingDualParam[3])));
        int currentStitchingRevolution = mNeckbandManager.getSetManage().enabledStitchingRevolution() ? R.string.on : R.string.off;
        items.add(new TitleAndSubItem(ID.ID_STITCHING_REVOLUTION_ENABLED, getString(R.string.setting_stitching_revolution_enable), getString(currentStitchingRevolution)));
        int currentStitchingRevolutionSpeed = mNeckbandManager.getSetManage().getStitchingRevolutionSpeed();
        items.add(new TitleAndSubItem(ID.ID_STITCHING_REVOLUTION_SPEED, getString(R.string.setting_stitching_revolution_speed), String.valueOf(currentStitchingRevolutionSpeed)));
        items.add(new TitleAndSubItem(ID.ID_STABILIZATION_CAL_RESET, getString(R.string.setting_stabilization_cal_reset), ""));
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
            case ID_STITCHING_TRIM_UPPER: selectedIdPosition = 3; break; // 0 ~ 50
            case ID_STITCHING_TRIM_LOWER: selectedIdPosition = 4; break; // 0 ~ 50
            case ID_STITCHING_FILTER_ENABLE: selectedIdPosition = 5; break;
            case ID_STITCHING_FILTER_TYPE: selectedIdPosition = 6; break; // 0, 1, 2, 4, 8 , 16, 32 ~
            case ID_STITCHING_COLOR_BRIGHTNESS: selectedIdPosition = 7; break;
            case ID_STITCHING_COLOR_CONTRAST: selectedIdPosition = 8; break;
            case ID_STITCHING_COLOR_SATURATION: selectedIdPosition = 9; break;
            case ID_STITCHING_FILTER_DUAL_SCALE_H: selectedIdPosition = 10; break;
            case ID_STITCHING_FILTER_DUAL_SCALE_V: selectedIdPosition = 11; break;
            case ID_STITCHING_FILTER_DUAL_OFFSET_H: selectedIdPosition = 12; break;
            case ID_STITCHING_FILTER_DUAL_OFFSET_V: selectedIdPosition = 13; break;
            case ID_STITCHING_REVOLUTION_ENABLED: selectedIdPosition = 14; break;
            case ID_STITCHING_REVOLUTION_SPEED: selectedIdPosition = 15; break;
            case ID_STABILIZATION_CAL_RESET: selectedIdPosition = 16; break;
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