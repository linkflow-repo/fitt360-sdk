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

import app.library.linkflow.manager.item.SoftwareItem;
import app.library.linkflow.manager.item.SpecItem;
import app.library.linkflow.manager.item.StatusItem;

public class SpecRecyclerAdapter extends RecyclerView.Adapter {
    private final int VIEW_TYPE_PANEL = 1, VIEW_TYPE_SPEC = 2;
    private Context mContext;

    private SpecItem mSpecItem;
    private SoftwareItem mSoftwareItem;
    private StatusItem mStatusItem;
    private ArrayList<Item> mItems = new ArrayList<>();

    public SpecRecyclerAdapter(Context context) {
        mContext = context;
    }

    private void init() {
        if (mSpecItem != null) {
            mItems.clear();
            mItems.add(new Item(VIEW_TYPE_PANEL, mContext.getString(R.string.spec_software), null));
            //mItems.add(new Item(VIEW_TYPE_SPEC, "OS", mSpecItem.mAndroidVersion));
            mItems.add(new Item(VIEW_TYPE_SPEC, "FIRMWARE", mSoftwareItem.mVersion));
            mItems.add(new Item(VIEW_TYPE_SPEC, "RELEASE DATE", mSoftwareItem.mReleaseDate));
            //mItems.add(new Item(VIEW_TYPE_PANEL, mContext.getString(R.string.spec_processor), null));
            //mItems.add(new Item(VIEW_TYPE_SPEC, "CPU SPEED", "2.35 GHz, 1.9 GHz"));
            //mItems.add(new Item(VIEW_TYPE_SPEC, "CPU TYPE", "Octa-Core"));
            mItems.add(new Item(VIEW_TYPE_PANEL, mContext.getString(R.string.spec_memory), null));
            mItems.add(new Item(VIEW_TYPE_SPEC, "RAM", mSpecItem.mRam));
            mItems.add(new Item(VIEW_TYPE_PANEL, mContext.getString(R.string.spec_storage), null));
            mItems.add(new Item(VIEW_TYPE_SPEC, "TOTAL INTERNAL STORAGE SIZE", "64GB"));
            mItems.add(new Item(VIEW_TYPE_SPEC, "AVAILABLE INTERNAL STORAGE SIZE", mSpecItem.mStorage[SpecItem.STORAGE_TYPE_INTERNAL] / 1000 + "GB"));
            if (mSpecItem.mStorage[SpecItem.STORAGE_TYPE_EXTERNAL] != -1) {
                mItems.add(new Item(VIEW_TYPE_SPEC, "AVAILABLE EXTERNAL STORAGE SIZE", mSpecItem.mStorage[SpecItem.STORAGE_TYPE_EXTERNAL] / 1000 + "GB"));
            }
            mItems.add(new Item(VIEW_TYPE_PANEL, mContext.getString(R.string.spec_battery), null));
            mItems.add(new Item(VIEW_TYPE_SPEC, "BATTERY CAPACITY", mSpecItem.mBatteryAmt));
            mItems.add(new Item(VIEW_TYPE_PANEL, mContext.getString(R.string.spec_video), null));
            mItems.add(new Item(VIEW_TYPE_SPEC, "FORMAT", mSpecItem.mVideoFormat));
            mItems.add(new Item(VIEW_TYPE_PANEL, mContext.getString(R.string.spec_photo), null));
            mItems.add(new Item(VIEW_TYPE_SPEC, "FORMAT", mSpecItem.mPhotoFormat));
        }
    }

    public void setSpecAndSoftware(SpecItem specItem, SoftwareItem softwareItem, StatusItem statusItem) {
        mSpecItem = specItem;
        mSoftwareItem = softwareItem;
        mStatusItem = statusItem;
        init();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).mViewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_PANEL) {
            return new PanelHolder(LayoutInflater.from(mContext).inflate(R.layout.holder_spec_panel, parent, false));
        } else if (viewType == VIEW_TYPE_SPEC) {
            return new SpecHolder(LayoutInflater.from(mContext).inflate(R.layout.holder_spec, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PanelHolder) {
            ((PanelHolder) holder).setData(mItems.get(position).mTitle);
        } else if (holder instanceof SpecHolder) {
            Item item = mItems.get(position);
            ((SpecHolder) holder).setData(item.mTitle, item.mValue);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private static class PanelHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTv;
        public PanelHolder(@NonNull View itemView) {
            super(itemView);
            mTitleTv = itemView.findViewById(R.id.title);
        }

        public void setData(String title) {
            mTitleTv.setText(title);
        }
    }

    private static class SpecHolder extends RecyclerView.ViewHolder {
        private TextView mNameTv, mValueTv;
        public SpecHolder(@NonNull View itemView) {
            super(itemView);
            mNameTv = itemView.findViewById(R.id.name);
            mValueTv = itemView.findViewById(R.id.value);
        }

        public void setData(String name, String value) {
            mNameTv.setText(name);
            mValueTv.setText(value);
        }
    }

    private class Item {
        public int mViewType;
        public String mTitle;
        public String mValue;
        public Item(int viewType, String title, String value) {
            mViewType = viewType;
            mTitle = title;
            mValue = value;
        }
    }
}
