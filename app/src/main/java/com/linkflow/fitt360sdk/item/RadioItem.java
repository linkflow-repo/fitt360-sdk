package com.linkflow.fitt360sdk.item;

public class RadioItem extends Item {
    public String mTitle;
    public int mValue = -1;
    private boolean mIsChecked;

    public RadioItem(String title, boolean isChecked) {
        super(VIEW_TYPE_RADIO);
        mTitle = title;
        mIsChecked = isChecked;
    }

    public RadioItem(String title, int value, boolean isChecked) {
        super(VIEW_TYPE_RADIO);
        mTitle = title;
        mValue = value;
        mIsChecked = isChecked;
    }

    public void setChecked(boolean isChecked) {
        mIsChecked = isChecked;
    }

    public boolean getChecked() {
        return mIsChecked;
    }
}
