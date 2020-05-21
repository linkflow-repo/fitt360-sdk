package com.linkflow.fitt360sdk.dialog;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linkflow.fitt360sdk.R;

public class USBTetheringDialog extends BaseDialogFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInnerView(R.layout.dialog_usb_tethering, "dialog_usb_tethering");
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setAgreeText(R.string.go);
        setDisagreeText(R.string.close);
    }
}
