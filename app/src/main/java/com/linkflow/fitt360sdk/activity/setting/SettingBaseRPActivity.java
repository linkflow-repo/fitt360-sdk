package com.linkflow.fitt360sdk.activity.setting;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.activity.BaseActivity;
import com.linkflow.fitt360sdk.adapter.SettingRPRecyclerAdapter;
import com.linkflow.fitt360sdk.item.TitleAndSubItem;

import java.util.ArrayList;

public abstract class SettingBaseRPActivity extends BaseActivity implements SettingRPRecyclerAdapter.ItemClickListener {
    public static final int REQUEST_RESULT = 1001;
    public enum ID {
        ID_CAMERA_MODE, ID_RESOLUTION, ID_FPS, ID_BITRATE, ID_CODEC, ID_FORMAT, ID_DELAY, ID_DISTANCE, ID_FIRST_PERSON,
        ID_CAMERA_POSITION, ID_A2DP, ID_BLEND, ID_TIME_LAPSE_STATE, ID_TIME_LAPSE_RATE,
        ID_STITCHING_TRIM_UPPER, ID_STITCHING_TRIM_LOWER,
        ID_STITCHING_FILTER_TYPE, ID_STITCHING_FILTER_ENABLE,
        ID_STITCHING_COLOR_BRIGHTNESS, ID_STITCHING_COLOR_CONTRAST, ID_STITCHING_COLOR_SATURATION,
        ID_STITCHING_FILTER_DUAL_SCALE_H, ID_STITCHING_FILTER_DUAL_SCALE_V, ID_STITCHING_FILTER_DUAL_OFFSET_H, ID_STITCHING_FILTER_DUAL_OFFSET_V,
        ID_STITCHING_REVOLUTION_ENABLED, ID_STITCHING_REVOLUTION_SPEED,
        ID_STABILIZATION_CAL_RESET
    }
    protected final int[] CAMERA_POSITION = new int[] { R.string.camera_position_1, R.string.camera_position_3, R.string.camera_position_2 };
    protected RecyclerView mRecyclerView;
    protected SettingRPRecyclerAdapter mAdapter;
    protected boolean mIsChangedSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBodyView(R.layout.activity_setting_rp);

        mRecyclerView = findViewById(R.id.recycler);
        mAdapter = new SettingRPRecyclerAdapter(this, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setItems(initItems());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_RESULT) {
                mIsChangedSettings = true;
                mAdapter.setItems(initItems());
            }
        }
    }

    protected ArrayList<TitleAndSubItem> initItems() {
        return null;
    }
}
