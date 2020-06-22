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

import app.library.linkflow.manager.StoredDataManager;
import app.library.linkflow.manager.neckband.SetManage;

public class SettingLanguageSelectActivity extends BaseActivity implements SettingSelectRecyclerAdapter.ItemSelectedListener, SetManage.Listener  {
    private static final String[] LANGUAGES = new String[] { "en", "ko" };
    private static final int[] LANGUAGES_R = new int[] { R.string.language_en, R.string.language_kr };

    private SetManage mSetManage;

    private RecyclerView mRecyclerView;
    private SettingSelectRecyclerAdapter mAdapter;

    private int mSelectedIdPosition;
    private String mSelectedLanguage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(R.string.setting_language);
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
        mSelectedLanguage = intent.getStringExtra("selected_language");
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

    private ArrayList<Item> initItems() {
        switch (mSelectedIdPosition) {
            case 0: return makeItems(findPosition(mSelectedLanguage, LANGUAGES), LANGUAGES_R);
        }
        return null;
    }


    @Override
    public void selectedItem(int position) {
        if (!mNeckbandManager.getConnectStateManage().isConnected()) {
            Toast.makeText(this, R.string.try_after_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAdapter.updateAdapter(position)) {
            switch (mSelectedIdPosition) {
                case 0:
                    StoredDataManager.getInstance().setData(this, StoredDataManager.KEY_SELECTED_LANGUAGE, LANGUAGES[position]);
                    mSetManage.getLanguageModel().setLanguage(mNeckbandManager.getAccessToken(), true, LANGUAGES[position]);
                    break;
            }
        }
    }

    private int findPosition(String value, String[] list) {
        for(int i = 0; i < list.length; i++) {
            if (list[i].equals(value)) {
                return i;
            }
        }
        return 0;
    }

    protected ArrayList<Item> makeItems(int selectedPosition, int[] list) {
        mAdapter.setSelectedPosition(selectedPosition);
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            items.add(new RadioItem(getString(list[i]), i == selectedPosition));
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
