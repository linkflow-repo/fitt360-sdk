package com.linkflow.fitt360sdk.item;


import com.linkflow.fitt360sdk.adapter.SettingRecyclerAdapter;

public class SettingItem {
    private SettingRecyclerAdapter.ID mId;
    public int mType;
    public int mTitle;

    public SettingItem(SettingRecyclerAdapter.ID id, int type, int title) {
        mId = id;
        mType = type;
        mTitle = title;
    }

    public SettingRecyclerAdapter.ID getId() {
        return mId;
    }
}
