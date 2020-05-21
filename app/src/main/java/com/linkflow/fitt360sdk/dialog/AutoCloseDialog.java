package com.linkflow.fitt360sdk.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public abstract class AutoCloseDialog extends DialogFragment {
    private Listener mListener;

    private HandlerThread mHandlerThread;

    @Override
    public void onDestroy() {
        super.onDestroy();
        close();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        getDialog().setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void showWithDelay(FragmentManager fm, int delayMs, String tag){
        if(!isAdded()){
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment =  fm.findFragmentByTag(tag);
            if(fragment != null){
                ft.remove(fragment);
                ft.addToBackStack(null);
            }
            ft.add(this, tag);
            ft.commitAllowingStateLoss();

            mHandlerThread = new HandlerThread("autoCloseDialog");
            mHandlerThread.start();
            Handler handler = new Handler(mHandlerThread.getLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isAdded()) {
                        dismissAllowingStateLoss();
                    }
                    if (mListener != null) {
                        mListener.autoDone(true);
                    }
                }
            }, delayMs);
        }
    }

    private void close() {
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        if (isAdded()) {
            dismissAllowingStateLoss();
        }
    }

    public interface Listener {
        void autoDone(boolean success);
    }
}
