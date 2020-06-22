package com.linkflow.fitt360sdk.item;

public class InputItem extends Item {
    public String mValue;

    public InputItem(String value) {
        super(VIEW_TYPE_INPUT);
        mValue = value;
    }
}
