package com.linkflow.fitt360sdk.dialog;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linkflow.fitt360sdk.R;

public class TemperLimitAlertDialog extends BaseDialogFragment {
    private TextView mMessageTv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInnerView(R.layout.dialog_temper_limit_alert, "temper_limit_alert");
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        getDialog().setCanceledOnTouchOutside(false);
        setCancelable(false);

        mMessageTv = view.findViewById(R.id.message);

        mMessageTv.setText(mMessage);
    }

    public void setMessage(String message) {
        mMessage = message;
        if (mMessageTv != null) {
            mMessageTv.setText(message);
        }
    }
}
