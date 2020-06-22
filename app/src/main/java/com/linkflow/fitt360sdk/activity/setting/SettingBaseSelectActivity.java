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
import com.linkflow.fitt360sdk.item.ButtonItem;
import com.linkflow.fitt360sdk.item.InputItem;
import com.linkflow.fitt360sdk.item.Item;
import com.linkflow.fitt360sdk.item.RadioItem;

import java.util.ArrayList;

import app.library.linkflow.manager.neckband.SetManage;

public abstract class SettingBaseSelectActivity extends BaseActivity implements SettingSelectRecyclerAdapter.ItemSelectedListener, SetManage.Listener {
    protected static final String[] CAMERA_MODE = new String[] { "stitching", "sidebyside", "single" };
    protected static final String[] ISO = new String[] { "auto", "100", "200", "400", "800", "1600", "3200" };
    protected static final int[] DELAY = new int[] { 1000, 1500, 2000, 5000 };
    protected static final float[] DELAY_VALUE = new float[] { 1f, 1.5f, 2f, 5f };
    protected static final int[] CAMERA_POSITION = new int[] { R.string.camera_position_1, R.string.camera_position_2, R.string.camera_position_3 };

    protected RecyclerView mRecyclerView;
    protected SettingSelectRecyclerAdapter mAdapter;

    protected int mSelectedIdPosition;
    protected SetManage mSetManage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBodyView(R.layout.activity_setting_select);
        onNewIntent(getIntent());

        mSetManage = mNeckbandManager.getSetManage();
        mSetManage.setListener(this);

        mRecyclerView = findViewById(R.id.recycler);
        mAdapter = new SettingSelectRecyclerAdapter(this, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mSelectedIdPosition = intent.getIntExtra("selected_id_position", 0);
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

    protected int findPosition(String value, String[] list) {
        for(int i = 0; i < list.length; i++) {
            if (list[i].replaceAll("[\\s]+", "").equals(value)) {
                return i;
            }
        }
        return 0;
    }

    protected int findPosition(int value, int[] list) {
        for(int i = 0; i < list.length; i++) {
            if (list[i] == value) {
                return i;
            }
        }
        return 0;
    }

    protected ArrayList<Item> makeItems(int selectedPosition, String[] list) {
        mAdapter.setSelectedPosition(selectedPosition);
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            items.add(new RadioItem(list[i], i == selectedPosition));
        }
        return items;
    }

    protected ArrayList<Item> makeItems(int selectedPosition, int[] list) {
        mAdapter.setSelectedPosition(selectedPosition);
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            items.add(new RadioItem(getString(list[i]), i == selectedPosition));
        }
        return items;
    }

    protected ArrayList<Item> makeItems(int selectedPosition, float[] list, int[] values, int resource) {
        mAdapter.setSelectedPosition(selectedPosition);
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            items.add(new RadioItem(getString(resource, list[i]), values[i], i == selectedPosition));
        }
        return items;
    }

    protected ArrayList<Item> makeItems(int selectedPosition, int[] list, int[] values) {
        mAdapter.setSelectedPosition(selectedPosition);
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            items.add(new RadioItem(getString(list[i]), values[i], i == selectedPosition));
        }
        return items;
    }

    protected ArrayList<Item> makeInputItems(String[] values) {
        ArrayList<Item> items = new ArrayList<>();
        for (String value : values) {
            items.add(new InputItem(value));
        }
        return items;
    }

    protected ArrayList<Item> makeButtonItems(String[] values) {
        ArrayList<Item> items = new ArrayList<>();
        for (String value : values) {
            items.add(new ButtonItem(value));
        }
        return items;
    }

    protected ArrayList<Item> initItems() {
        return null;
    }

    @Override
    public void completedCallSetApi(boolean success, boolean isSet) {
        if (isSet) {
            Toast.makeText(this, success ? R.string.applied : R.string.applied_fail, Toast.LENGTH_SHORT).show();
        }
    }
}