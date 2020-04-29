package com.linkflow.fitt360sdk.item;

import android.bluetooth.BluetoothDevice;

public class BTItem {
    public static final int STATE_NONE = 0, STATE_NEAR = 1, STATE_FINDING = 2;
    public BluetoothDevice mDevice;
    public int mState;
    public boolean mChecked;

    public BTItem(BluetoothDevice device, int state) {
        mDevice = device;
        mState = state;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }
}
