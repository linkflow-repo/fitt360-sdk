package com.linkflow.fitt360sdk.activity.setting;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.item.RadioItem;

import java.util.ArrayList;

public class SettingOthersSelectActivity extends SettingBaseSelectActivity {
    private final int[] DISTANCE = new int[] { 0, 1 };
    public static final int[] BLEND = new int[] { 7, 13, 21 };
    public static final int[] BLEND_TITLE = new int[] { R.string.blend_low, R.string.blend_middle, R.string.blend_high};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(getTitle(mSelectedIdPosition));

        mAdapter.setItems(initItems());
    }

    private int getTitle(int position) {
        switch (position) {
            case 0: return R.string.setting_distance;
            case 1: return R.string.setting_stitching_center_mode;
            case 2: return R.string.setting_stitching_blending;
        }
        return 0;
    }

    @Override
    protected ArrayList<RadioItem> initItems() {
        int stitchingDistance = mNeckbandManager.getSetManage().getStitchingDistance();
        boolean reverseModeState = mNeckbandManager.getSetManage().isActivatedReverseMode();
        int blend = mNeckbandManager.getSetManage().getBlend();
        switch (mSelectedIdPosition) {
            case 0: return makeItems(findPosition(stitchingDistance, DISTANCE), new String[] { getString(R.string.distance_near), getString(R.string.distance_far) });
            case 1: return makeItems(reverseModeState ? 1 : 0, new String[] { getString(R.string.off), getString(R.string.on) });
            case 2:
                int selectedPosition = blend == 0 ? 2 : findPosition(blend, BLEND);
                return makeItems(selectedPosition, BLEND_TITLE);
        }
        return super.initItems();
    }

    @Override
    public void selectedItem(int position) {
        if (!mNeckbandManager.getConnectStateManage().isConnected()) {
            Toast.makeText(this, R.string.try_after_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAdapter.updateAdapter(position)) {
            switch (mSelectedIdPosition) {
                case 0: mNeckbandManager.getSetManage().getStitchingModel().setDistance(mNeckbandManager.getAccessToken(), position); break;
                case 1: mNeckbandManager.getSetManage().getStitchingModel().setReverseModeState(mNeckbandManager.getAccessToken(), position == 1); break;
                case 2: mNeckbandManager.getSetManage().getStitchingModel().setBlend(mNeckbandManager.getAccessToken(), BLEND[position]); break;
            }
        }
    }
}
