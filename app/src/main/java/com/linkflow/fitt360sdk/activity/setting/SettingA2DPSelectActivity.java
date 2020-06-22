package com.linkflow.fitt360sdk.activity.setting;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.item.Item;

import java.util.ArrayList;

public class SettingA2DPSelectActivity extends SettingBaseSelectActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(R.string.setting_a2dp);

        mAdapter.setItems(initItems());
    }

    @Override
    protected ArrayList<Item> initItems() {
        boolean a2dpModeEnable = mNeckbandManager.getSetManage().isActivateA2DPMode();
        switch (mSelectedIdPosition) {
            case 0: return makeItems(a2dpModeEnable ? 1 : 0, new String[] { getString(R.string.off), getString(R.string.on) });
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
                case 0: mNeckbandManager.getSetManage().getA2DPModel().setA2DP(mNeckbandManager.getAccessToken(), position == 1); break;
            }
        }
    }
}
