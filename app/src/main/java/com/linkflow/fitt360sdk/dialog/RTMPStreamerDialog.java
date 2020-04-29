package com.linkflow.fitt360sdk.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linkflow.fitt360sdk.R;

public class RTMPStreamerDialog extends BaseDialogFragment {
    private EditText mRTMPUrlEt;
    private CheckBox mAutoBitrateCb;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInnerView(R.layout.dialog_rtmp_streamer, "dialog_rtmp_streamer");
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRTMPUrlEt = view.findViewById(R.id.rtmp_url_et);
        mAutoBitrateCb = view.findViewById(R.id.auto_bitrate);
    }

    public String getRTMPUrl() {
        return mRTMPUrlEt.getText().toString().trim();
    }

    public boolean enableAutoBitrate() {
        return mAutoBitrateCb.isChecked();
    }
}
