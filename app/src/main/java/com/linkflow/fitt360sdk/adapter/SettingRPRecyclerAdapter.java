package com.linkflow.fitt360sdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.item.TitleAndSubItem;

import java.util.ArrayList;

public class SettingRPRecyclerAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private ItemClickListener mListener;

    private ArrayList<TitleAndSubItem> mItems = new ArrayList<>();

    public SettingRPRecyclerAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setItems(ArrayList<TitleAndSubItem> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public TitleAndSubItem getItem(int position) {
        return mItems.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TitleAndSubHolder(LayoutInflater.from(mContext).inflate(R.layout.holder_title_sub, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TitleAndSubHolder) {
            TitleAndSubItem item = mItems.get(position);
            ((TitleAndSubHolder) holder).setData(item.mTitle, item.mSubtitle);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private static class TitleAndSubHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTv, mSubtitleTv;
        private ItemClickListener mListener;
        public TitleAndSubHolder(@NonNull View itemView, ItemClickListener listener) {
            super(itemView);
            mListener = listener;
            LinearLayout container = itemView.findViewById(R.id.container);
            container.setOnClickListener(this);
            mTitleTv = itemView.findViewById(R.id.title);
            mSubtitleTv = itemView.findViewById(R.id.subtitle);
        }

        public void setData(String title, String subtitle) {
            mTitleTv.setText(title);
            mSubtitleTv.setText(subtitle);
        }

        @Override
        public void onClick(View v) {
            mListener.clickedItem(getLayoutPosition());
        }
    }

    public interface ItemClickListener {
        void clickedItem(int position);
    }
}
