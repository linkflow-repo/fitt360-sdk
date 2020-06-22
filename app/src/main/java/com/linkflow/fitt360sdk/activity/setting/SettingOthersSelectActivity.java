package com.linkflow.fitt360sdk.activity.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.item.InputItem;
import com.linkflow.fitt360sdk.item.Item;

import java.util.ArrayList;

public class SettingOthersSelectActivity extends SettingBaseSelectActivity {
    private final int[] DISTANCE = new int[] { 0, 1 };
    public static final int[] BLEND = new int[] { 7, 13, 21 };
    public static final int[] BLEND_TITLE = new int[] { R.string.blend_low, R.string.blend_middle, R.string.blend_high};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mSelectedIdPosition > 2 && mSelectedIdPosition < 16 && mSelectedIdPosition != 5 && mSelectedIdPosition != 14) {
            mSubmitTv.setText(R.string.apply);
        }
        setHeaderTitle(getTitle(mSelectedIdPosition));

        mAdapter.setItems(initItems());
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.header_submit) {
            InputItem item = mAdapter.getItemAsInput(0);
            switch (mSelectedIdPosition) {
                case 3:
                    int value = Integer.parseInt(item.mValue);
                    if (value > -1 && value < 51) {
                        int lower = mNeckbandManager.getSetManage().getStitchingTrim()[1];
                        mNeckbandManager.getSetManage().getStitchingModel().setTrim(mNeckbandManager.getAccessToken(), value, lower);
                    } else {
                        Toast.makeText(this, "range 0 ~ 50", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 4:
                    value = Integer.parseInt(item.mValue);
                    if (value > -1 && value < 51) {
                        int upper = mNeckbandManager.getSetManage().getStitchingTrim()[0];
                        mNeckbandManager.getSetManage().getStitchingModel().setTrim(mNeckbandManager.getAccessToken(), upper, value);
                    } else {
                        Toast.makeText(this, "range 0 ~ 50", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 6:
                    value = Integer.parseInt(item.mValue);
                    mNeckbandManager.getSetManage().getStitchingModel().setFilterType(mNeckbandManager.getAccessToken(), value);
                    break;
                case 7:
                    float value2 = Float.parseFloat(item.mValue);
                    float[] colors = mNeckbandManager.getSetManage().getStitchingColor();
                    mNeckbandManager.getSetManage().getStitchingModel().setColor(mNeckbandManager.getAccessToken(), value2, colors[1], colors[2]);
                    break;
                case 8:
                    value2 = Float.parseFloat(item.mValue);
                    colors = mNeckbandManager.getSetManage().getStitchingColor();
                    mNeckbandManager.getSetManage().getStitchingModel().setColor(mNeckbandManager.getAccessToken(), colors[0], value2, colors[2]);
                    break;
                case 9:
                    value2 = Float.parseFloat(item.mValue);
                    colors = mNeckbandManager.getSetManage().getStitchingColor();
                    mNeckbandManager.getSetManage().getStitchingModel().setColor(mNeckbandManager.getAccessToken(), colors[0], colors[1], value2);
                    break;
                case 10:
                    value2 = Float.parseFloat(item.mValue);
                    float[] dualParam = mNeckbandManager.getSetManage().getStitchingDualParam();
                    mNeckbandManager.getSetManage().getStitchingModel().setFilterDualParam(mNeckbandManager.getAccessToken(), value2, dualParam[1], dualParam[2], dualParam[3]);
                    break;
                case 11:
                    value2 = Float.parseFloat(item.mValue);
                    dualParam = mNeckbandManager.getSetManage().getStitchingDualParam();
                    mNeckbandManager.getSetManage().getStitchingModel().setFilterDualParam(mNeckbandManager.getAccessToken(), dualParam[0], value2, dualParam[2], dualParam[3]);
                    break;
                case 12:
                    value2 = Float.parseFloat(item.mValue);
                    dualParam = mNeckbandManager.getSetManage().getStitchingDualParam();
                    mNeckbandManager.getSetManage().getStitchingModel().setFilterDualParam(mNeckbandManager.getAccessToken(), dualParam[0], dualParam[1], value2, dualParam[3]);
                    break;
                case 13:
                    value2 = Float.parseFloat(item.mValue);
                    dualParam = mNeckbandManager.getSetManage().getStitchingDualParam();
                    mNeckbandManager.getSetManage().getStitchingModel().setFilterDualParam(mNeckbandManager.getAccessToken(), dualParam[0], dualParam[1], dualParam[2], value2);
                    break;
                case 15:
                    value = Integer.parseInt(item.mValue);
                    boolean enabledRevolution = mNeckbandManager.getSetManage().enabledStitchingRevolution();
                    mNeckbandManager.getSetManage().getStitchingModel().setRevolution(mNeckbandManager.getAccessToken(), enabledRevolution, value);
                    break;
            }
        }
    }

    private int getTitle(int position) {
        switch (position) {
            case 0: return R.string.setting_distance;
            case 1: return R.string.setting_stitching_center_mode;
            case 2: return R.string.setting_stitching_blending;
            case 3: return R.string.setting_stitching_trim_upper;
            case 4: return R.string.setting_stitching_trim_lower;
            case 5: return R.string.setting_stitching_filter_enable;
            case 6: return R.string.setting_stitching_filter_type;
            case 7: return R.string.setting_stitching_color_brightness;
            case 8: return R.string.setting_stitching_color_contrast;
            case 9: return R.string.setting_stitching_color_saturation;
            case 10: return R.string.setting_stitching_filter_dual_scale_h;
            case 11: return R.string.setting_stitching_filter_dual_scale_v;
            case 12: return R.string.setting_stitching_filter_dual_offset_h;
            case 13: return R.string.setting_stitching_filter_dual_offset_v;
            case 14: return R.string.setting_stitching_revolution_enable;
            case 15: return R.string.setting_stitching_revolution_speed;
            case 16: return R.string.setting_stabilization_cal_reset;
        }
        return 0;
    }

    @Override
    protected ArrayList<Item> initItems() {
        switch (mSelectedIdPosition) {
            case 0:
                int stitchingDistance = mNeckbandManager.getSetManage().getStitchingDistance();
                return makeItems(findPosition(stitchingDistance, DISTANCE), new String[] { getString(R.string.distance_near), getString(R.string.distance_far) });
            case 1:
                boolean reverseModeState = mNeckbandManager.getSetManage().isActivatedReverseMode();
                return makeItems(reverseModeState ? 1 : 0, new String[] { getString(R.string.off), getString(R.string.on) });
            case 2:
                int blend = mNeckbandManager.getSetManage().getBlend();
                int selectedPosition = blend == 0 ? 2 : findPosition(blend, BLEND);
                return makeItems(selectedPosition, BLEND_TITLE);
            case 3:
                int trimUpper = mNeckbandManager.getSetManage().getStitchingTrim()[0];
                return makeInputItems(new String[] { String.valueOf(trimUpper) });
            case 4:
                int trimLower = mNeckbandManager.getSetManage().getStitchingTrim()[1];
                return makeInputItems(new String[] { String.valueOf(trimLower) });
            case 5:
                boolean stitchingFilterEnable = mNeckbandManager.getSetManage().enabledStitchingFilter();
                return makeItems(stitchingFilterEnable ? 1 : 0, new String[] { getString(R.string.off), getString(R.string.on) });
            case 6:
                int stitchingFilterType = mNeckbandManager.getSetManage().getStitchingFilterType();
                return makeInputItems(new String[] { String.valueOf(stitchingFilterType) });
            case 7:
                float stitchingColorBrightness = mNeckbandManager.getSetManage().getStitchingColor()[0];
                return makeInputItems(new String[] { String.valueOf(stitchingColorBrightness) });
            case 8:
                float stitchingColorContrast = mNeckbandManager.getSetManage().getStitchingColor()[1];
                return makeInputItems(new String[] { String.valueOf(stitchingColorContrast) });
            case 9:
                float stitchingColorSaturation = mNeckbandManager.getSetManage().getStitchingColor()[2];
                return makeInputItems(new String[] { String.valueOf(stitchingColorSaturation) });
            case 10:
                float stitchingFilterDualScaleH = mNeckbandManager.getSetManage().getStitchingDualParam()[0];
                return makeInputItems(new String[] { String.valueOf(stitchingFilterDualScaleH) });
            case 11:
                float stitchingFilterDualScaleV = mNeckbandManager.getSetManage().getStitchingDualParam()[1];
                return makeInputItems(new String[] { String.valueOf(stitchingFilterDualScaleV) });
            case 12:
                float stitchingFilterDualOffsetH = mNeckbandManager.getSetManage().getStitchingDualParam()[2];
                return makeInputItems(new String[] { String.valueOf(stitchingFilterDualOffsetH) });
            case 13:
                float stitchingFilterDualOffsetV = mNeckbandManager.getSetManage().getStitchingDualParam()[3];
                return makeInputItems(new String[] { String.valueOf(stitchingFilterDualOffsetV) });
            case 14:
                boolean stitchingRevolutionEnable = mNeckbandManager.getSetManage().enabledStitchingRevolution();
                return makeItems(stitchingRevolutionEnable ? 1 : 0, new String[] { getString(R.string.off), getString(R.string.on) });
            case 15:
                int stitchingRevolutionSpeed = mNeckbandManager.getSetManage().getStitchingRevolutionSpeed();
                return makeInputItems(new String[] { String.valueOf(stitchingRevolutionSpeed) });
            case 16:
                return makeButtonItems(new String[] { "Reset" });
        }
        return super.initItems();
    }

    @Override
    public void selectedItem(int position) {
        if (!mNeckbandManager.getConnectStateManage().isConnected()) {
            Toast.makeText(this, R.string.try_after_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAdapter.updateAdapter(position) || mAdapter.isButtonItem(position)) {
            switch (mSelectedIdPosition) {
                case 0: mNeckbandManager.getSetManage().getStitchingModel().setDistance(mNeckbandManager.getAccessToken(), position); break;
                case 1: mNeckbandManager.getSetManage().getStitchingModel().setReverseModeState(mNeckbandManager.getAccessToken(), position == 1); break;
                case 2: mNeckbandManager.getSetManage().getStitchingModel().setBlend(mNeckbandManager.getAccessToken(), BLEND[position]); break;
                case 5: mNeckbandManager.getSetManage().getStitchingModel().setFilterEnabled(mNeckbandManager.getAccessToken(), position == 1); break;
                case 14:
                    int stitchingRevolutionSpeed = mNeckbandManager.getSetManage().getStitchingRevolutionSpeed();
                    mNeckbandManager.getSetManage().getStitchingModel().setRevolution(mNeckbandManager.getAccessToken(), position == 1, stitchingRevolutionSpeed); break;
                case 16: mNeckbandManager.getSetManage().getStitchingModel().resetStabilizationCal(mNeckbandManager.getAccessToken()); break;
            }
        }
    }
}