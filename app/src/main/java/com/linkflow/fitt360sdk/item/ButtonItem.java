package com.linkflow.fitt360sdk.item;

public class ButtonItem extends Item {
    public String mTitle;

    public ButtonItem(String title) {
        super(VIEW_TYPE_BUTTON);
        mTitle = title;
    }
}
