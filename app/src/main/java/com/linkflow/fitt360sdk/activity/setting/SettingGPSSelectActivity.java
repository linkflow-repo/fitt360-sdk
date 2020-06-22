package com.linkflow.fitt360sdk.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.activity.BaseActivity;
import com.linkflow.fitt360sdk.adapter.SettingSelectRecyclerAdapter;
import com.linkflow.fitt360sdk.item.Item;
import com.linkflow.fitt360sdk.item.RadioItem;

import java.util.ArrayList;

import app.library.linkflow.manager.neckband.SetManage;

public class SettingGPSSelectActivity extends BaseActivity implements SettingSelectRecyclerAdapter.ItemSelectedListener, SetManage.Listener {
    private static final int[] PERIOD  = new int[] { 5, 10, 30, 60 };

    private SetManage mSetManage;

    private RecyclerView mRecyclerView;
    private SettingSelectRecyclerAdapter mAdapter;

    private int mSelectedIdPosition;
    private int mSelectedPeriodValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(R.string.setting_gps_period);
        setBodyView(R.layout.activity_setting_gps_select);

        onNewIntent(getIntent());

        mSetManage = mNeckbandManager.getSetManage();
        mSetManage.setListener(this);

        mRecyclerView = findViewById(R.id.recycler);
        mAdapter = new SettingSelectRecyclerAdapter(this, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setItems(initItems());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mSelectedIdPosition = intent.getIntExtra("selected_id_position", 0);
    }

    private ArrayList<Item> initItems() {
        switch (mSelectedIdPosition) {
            case 1: return makeItems(findPosition(mSetManage.getGPSPeriod() / 1000, PERIOD), PERIOD);
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.header_close) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (mAdapter.isUpdated()) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
        finish();
    }

    @Override
    public void selectedItem(int position) {
        if (mAdapter.updateAdapter(position)) {
            RadioItem item = mAdapter.getItemAsRadio(position);
            switch (mSelectedIdPosition) {
                case 1:
                    int value = Integer.parseInt(item.mTitle.replaceAll(getString(R.string.second), ""));
                    mSetManage.getGPSModel().setGPSPeriod(mNeckbandManager.getAccessToken(), value * 1000);
                    break;
            }
        }
    }

    private int findPosition(int value, int[] list) {
        for(int i = 0; i < list.length; i++) {
            if (list[i] == value) {
                return i;
            }
        }
        return 0;
    }

    protected ArrayList<Item> makeItems(int selectedPosition, int[] list) {
        mAdapter.setSelectedPosition(selectedPosition);
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            items.add(new RadioItem(list[i] + getString(R.string.second), i == selectedPosition));
        }
        return items;
    }

    @Override
    public void completedCallSetApi(boolean success, boolean isSet) {
        if (isSet) {
            Toast.makeText(this, success ? R.string.applied : R.string.applied_fail, Toast.LENGTH_SHORT).show();
        }
    }
}
