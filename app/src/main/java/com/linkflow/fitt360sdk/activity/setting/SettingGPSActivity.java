package com.linkflow.fitt360sdk.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.activity.BaseActivity;
import com.linkflow.fitt360sdk.adapter.SettingRPRecyclerAdapter;
import com.linkflow.fitt360sdk.item.TitleAndSubItem;

import java.util.ArrayList;


public class SettingGPSActivity extends BaseActivity implements SettingRPRecyclerAdapter.ItemClickListener {
    public static final int REQUEST_RESULT = 1001;

    public enum ID {
        ID_GPS_BELONG, ID_GPS_PERIOD
    }

    private RecyclerView mRecyclerView;
    private SettingRPRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(R.string.setting_gps);
        setBodyView(R.layout.activity_setting_gps);

        mRecyclerView = findViewById(R.id.recycler);
        mAdapter = new SettingRPRecyclerAdapter(this, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setItems(initItems());
    }

    private ArrayList<TitleAndSubItem> initItems() {
        ArrayList<TitleAndSubItem> items = new ArrayList<>();
        items.add(new TitleAndSubItem(ID.ID_GPS_BELONG, getString(R.string.setting_gps_belong), getString(R.string.phone)));
        items.add(new TitleAndSubItem(ID.ID_GPS_PERIOD, getString(R.string.setting_gps_period), mNeckbandManager.getSetManage().getGPSPeriod() / 1000 + " " + getString(R.string.second)));
        return items;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_RESULT) {
                mAdapter.setItems(initItems());
            }
        }
    }

    @Override
    public void clickedItem(int position) {
        TitleAndSubItem item = mAdapter.getItem(position);
        int selectedIdPosition = -1;
        switch (item.mGPSId) {
            case ID_GPS_PERIOD: selectedIdPosition = 1; break;
        }
        if (selectedIdPosition != -1) {
            Intent intent = new Intent(this, SettingGPSSelectActivity.class);
            intent.putExtra("selected_id_position", selectedIdPosition);
            startActivityForResult(intent, REQUEST_RESULT);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.header_close) {
            onBackPressed();
        }
    }
}
