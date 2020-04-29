package com.linkflow.fitt360sdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.linkflow.fitt360sdk.R;

import java.util.ArrayList;

public class MainRecyclerAdapter extends RecyclerView.Adapter {
    public enum ID {
        ID_GALLERY, ID_RECORDING, ID_TAKE_PHOTO, ID_PREVIEW, ID_STREAMING, ID_SETTING
    }

    public String[] mTitles = new String[] { "GALLERY", "START RECORDING", "TAKE A PHOTO", "PREVIEW", "START RTMP STREAMING", "SETTING" };

    private Context mContext;
    private ItemClickListener mListener;
    private ArrayList<Item> mItems = new ArrayList<>();

    public MainRecyclerAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mListener = listener;
        initItems();
    }

    private void initItems() {
        mItems.clear();
        int size = ID.values().length;
        for(int i = 0; i < size; i++) {
            mItems.add(new Item(ID.values()[i], mTitles[i]));
        }
    }

    public void changeRecordState(boolean activated) {
        int size = mItems.size();
        for (int i = 0; i < size; i++) {
            if (mItems.get(i).mId == ID.ID_RECORDING) {
                mItems.get(i).mTitle = (activated ? "STOP RECORDING" : "START RECORDING");
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void changeStreamingState(boolean activated) {
        int size = mItems.size();
        for (int i = 0; i < size; i++) {
            if (mItems.get(i).mId == ID.ID_STREAMING) {
                mItems.get(i).mTitle = (activated ? "STOP RTMP STREAMING" : "START RTMP STREAMING");
                notifyDataSetChanged();
                break;
            }
        }
    }


    public Item getItem(int position) {
        return mItems.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ItemHolder(LayoutInflater.from(mContext).inflate(R.layout.holder_item, viewGroup, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ItemHolder) {
            ((ItemHolder) viewHolder).setData(mItems.get(i).mTitle);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTv;
        private ItemClickListener mListener;
        public ItemHolder(@NonNull View itemView, ItemClickListener listener) {
            super(itemView);
            mTitleTv = itemView.findViewById(R.id.title);
            mTitleTv.setOnClickListener(this);
            mListener = listener;
        }

        public void setData(String title) {
            mTitleTv.setText(title);
        }

        @Override
        public void onClick(View view) {
            mListener.clickedItem(getLayoutPosition());
        }
    }

    public interface ItemClickListener {
        void clickedItem(int position);
    }

    public class Item {
        public ID mId;
        public String mTitle;

        public Item(ID id, String title) {
            mId = id;
            mTitle = title;
        }
    }
}
