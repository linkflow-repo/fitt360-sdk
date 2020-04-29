package com.linkflow.fitt360sdk.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.linkflow.fitt360sdk.R;

/**
 * Created by choiseokwon on 2018. 3. 27..
 */

public abstract class BaseDialogFragment extends DialogFragment {
    private static final int MAX_ADDED_DELAY = 1000;
    public static final int CLICKED_TYPE_DISAGREE = 0, CLICKED_TYPE_AGREE = 1;
    protected static final int SINGLE_MODE_DISAGREE = 10, SINGLE_MODE_AGREE = 11, MULTI_MODE = 12;
    protected int mInnerViewId;
    protected RelativeLayout mBtnContainer;
    protected TextView mDisagreeBtn, mAgreeBtn;
    protected View.OnClickListener mClickListener;
    protected String mMessage;
    protected String mTag;
    private boolean mDisableButton;
    private int mSingleMode = -1;
    private long mBeforeAddTime;

    protected DialogBtnClickListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_dialog, container, false);
        RelativeLayout innerView = view.findViewById(R.id.base_dialog_inner_view);
        innerView.addView(inflater.inflate(mInnerViewId, container, false));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mBtnContainer = view.findViewById(R.id.btn_container);
        mDisagreeBtn = view.findViewById(R.id.base_dialog_disagree);
        mAgreeBtn = view.findViewById(R.id.base_dialog_agree);
        if (mClickListener != null) {
            mDisagreeBtn.setOnClickListener(mClickListener);
            mAgreeBtn.setOnClickListener(mClickListener);
        }
    }

    public void setClickListener(View.OnClickListener listener){
        mClickListener = listener;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDisableButton) {
            mBtnContainer.setVisibility(View.GONE);
        } else if(mSingleMode != -1){
            mBtnContainer.setVisibility(View.VISIBLE);
            if(mSingleMode == SINGLE_MODE_DISAGREE){
                mAgreeBtn.setVisibility(View.GONE);
            }else if(mSingleMode == SINGLE_MODE_AGREE){
                mDisagreeBtn.setVisibility(View.GONE);
            }else if(mSingleMode == MULTI_MODE){
                mAgreeBtn.setVisibility(View.VISIBLE);
                mDisagreeBtn.setVisibility(View.VISIBLE);
            }
            mSingleMode = -1;
        }

    }

    protected void setSingleButton(final int singleMode, int text){
        mSingleMode = singleMode;
        if (text != -1) {
            mAgreeBtn.setText(text);
        }
    }

    protected void setMultiButton(){
        mSingleMode = MULTI_MODE;
    }

    public void disableButton() {
        mDisableButton = true;
    }

    public void setDisagreeText(int text) {
        mDisagreeBtn.setText(text);
    }

    public void setAgreeText(int text) {
        mAgreeBtn.setText(text);
    }

    public void setInnerView(int viewId, String tag){
        mInnerViewId = viewId;
        mTag = tag;
    }

    public void setListener(DialogBtnClickListener listener) {
        mListener = listener;
    }

    public void showWithMessage(FragmentManager fm, String message){
        if(!isAdded() && System.currentTimeMillis() - mBeforeAddTime > MAX_ADDED_DELAY){
            mBeforeAddTime = System.currentTimeMillis();
            mMessage = message;
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment =  fm.findFragmentByTag(mTag);
            if(fragment != null){
                ft.remove(fragment);
                ft.addToBackStack(null);
            }
            ft.add(this, mTag);
            ft.commitAllowingStateLoss();
        }
    }

    public void show(FragmentManager fm){
        if(!isAdded() && System.currentTimeMillis() - mBeforeAddTime > MAX_ADDED_DELAY){
            mBeforeAddTime = System.currentTimeMillis();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment =  fm.findFragmentByTag(mTag);
            if(fragment != null){
                ft.remove(fragment);
                ft.addToBackStack(null);
            }
            ft.add(this, mTag);
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    public void dismiss(){
        mMessage = null;
        if(isAdded()){
            try{
                super.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void dismissAllowingStateLoss() {
        mMessage = null;
        if(isAdded()){
            try{
                super.dismissAllowingStateLoss();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public interface DialogBtnClickListener {
        void clicked(int action);
    }
}
