package com.linkflow.fitt360sdk.item;

public class RadioItem {
    public String mTitle;
    public int mValue = -1;
    private boolean mIsChecked;

    public RadioItem(String title, boolean isChecked) {
        mTitle = title;
        mIsChecked = isChecked;
    }

    public RadioItem(String title, int value, boolean isChecked) {
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
