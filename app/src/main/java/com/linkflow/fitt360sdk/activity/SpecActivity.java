package com.linkflow.fitt360sdk.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.adapter.SpecRecyclerAdapter;

import app.library.linkflow.manager.neckband.ConnectStateManage;

public class SpecActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private SpecRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(R.string.spec);
        setBodyView(R.layout.activity_spec);

        mAdapter = new SpecRecyclerAdapter(this);

        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onConnectState(ConnectStateManage.STATE state) {
        super.onConnectState(state);
        if (state == ConnectStateManage.STATE.STATE_DONE) {
            if (mAdapter != null) {
                mAdapter.setSpecAndSoftware(mNeckbandManager.getInfoManage().getSpecItem(), mNeckbandManager.getInfoManage().getSoftwareItem(),
                        mNeckbandManager.getInfoManage().getStatusItem());
            }
        }
    }
}
