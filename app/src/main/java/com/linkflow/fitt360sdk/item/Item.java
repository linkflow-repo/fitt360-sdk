package com.linkflow.fitt360sdk.item;

public abstract class Item {
    public static final int VIEW_TYPE_RADIO = 1, VIEW_TYPE_INPUT = 2, VIEW_TYPE_BUTTON = 3;
    public int mViewType;

    public Item(int viewType) {
        mViewType = viewType;
    }
}
