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
import java.util.HashMap;

public class SettingLanguageActivity extends BaseActivity implements SettingRPRecyclerAdapter.ItemClickListener {
    public static final int REQUEST_RESULT = 1001;
    private HashMap<String, Integer> mLanguageMap = new HashMap<>();
    {
        mLanguageMap.put("ko", R.string.language_kr);
        mLanguageMap.put("en", R.string.language_en);
    }

    public enum ID {
        ID_LANGUAGE
    }

    private RecyclerView mRecyclerView;
    private SettingRPRecyclerAdapter mAdapter;

    private String mSelectedLanguage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(R.string.setting_language);
        setBodyView(R.layout.activity_setting_gps);

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
                mAdapter.setItems(initItems());
            }
        }
    }

    private ArrayList<TitleAndSubItem> initItems() {
        mSelectedLanguage = mNeckbandManager.getSetManage().getSelectedLanguage();
        ArrayList<TitleAndSubItem> items = new ArrayList<>();
        if (mSelectedLanguage != null && mLanguageMap.containsKey(mSelectedLanguage)) {
            items.add(new TitleAndSubItem(ID.ID_LANGUAGE, getString(R.string.setting_language_type), getString(mLanguageMap.get(mSelectedLanguage))));
        }
        return items;
    }

    @Override
    public void clickedItem(int position) {
        TitleAndSubItem item = mAdapter.getItem(position);
        int selectedIdPosition = -1;
        if (item.mLanguageId == ID.ID_LANGUAGE) {
            selectedIdPosition = 0;
        }
        if (selectedIdPosition != -1) {
            Intent intent = new Intent(this, SettingLanguageSelectActivity.class);
            intent.putExtra("selected_id_position", selectedIdPosition);
            intent.putExtra("selected_language", mSelectedLanguage);
            startActivityForResult(intent, REQUEST_RESULT);
        }
    }
}
