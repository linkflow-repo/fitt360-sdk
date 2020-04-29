package com.linkflow.fitt360sdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.item.RadioItem;

import java.util.ArrayList;

public class SettingSelectRecyclerAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private ItemSelectedListener mListener;

    private ArrayList<RadioItem> mItems = new ArrayList<>();
    private int mDefaultSelectedPosition, mBeforeSelectedPosition;

    public SettingSelectRecyclerAdapter(Context context,  ItemSelectedListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setSelectedPosition(int position) {
        mDefaultSelectedPosition = position;
        mBeforeSelectedPosition = position;
    }

    public void setItems(ArrayList<RadioItem> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public RadioItem getItem(int position) {
        return mItems.get(position);
    }

    public boolean isUpdated() {
        return mDefaultSelectedPosition != mBeforeSelectedPosition;
    }

    public boolean updateAdapter(int position) {
        if (mBeforeSelectedPosition != position) {
            int size = mItems.size();
            for (int i = 0; i < size; i++) {
                if (i == position) {
                    mItems.get(i).setChecked(true);
                } else {
                    mItems.get(i).setChecked(false);
                }
            }
            notifyItemChanged(mBeforeSelectedPosition);
            notifyItemChanged(position);
            mBeforeSelectedPosition = position;
            return true;
        }
        return false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RadioHolder(LayoutInflater.from(mContext).inflate(R.layout.holder_radio, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RadioHolder) {
            RadioItem item = mItems.get(position);
            ((RadioHolder) holder).setData(item.mTitle, item.getChecked());
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private static class RadioHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTv;
        private RadioButton mRadioBtn;
        private ItemSelectedListener mListener;
        public RadioHolder(@NonNull View itemView, ItemSelectedListener listener) {
            super(itemView);
            mListener = listener;
            RelativeLayout container = itemView.findViewById(R.id.container);
            container.setOnClickListener(this);
            mTitleTv = itemView.findViewById(R.id.title);
            mRadioBtn = itemView.findViewById(R.id.radio);
            mRadioBtn.setClickable(false);
        }

        public void setData(String title, boolean isChecked) {
            mTitleTv.setText(title);
            mRadioBtn.setChecked(isChecked);
        }

        @Override
        public void onClick(View v) {
            mListener.selectedItem(getLayoutPosition());
        }
    }

    public interface ItemSelectedListener {
        void selectedItem(int position);
    }
}
