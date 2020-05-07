package com.linkflow.fitt360sdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.item.SettingItem;

import java.util.ArrayList;

public class SettingRecyclerAdapter extends RecyclerView.Adapter {
    private static final int TYPE_BUTTON = 101, TYPE_PANEL = 102, TYPE_BUTTON_WITH_NOTIFY = 103;
    public enum ID {
        ID_CONNECT, ID_SPEC, ID_MANAGEMENT_STORAGE, ID_SETTING_PHOTO, ID_SETTING_RECORD, ID_SETTING_OTHERS, ID_LANGUAGE,
        ID_GPS_PHONE, ID_AP, ID_A2DP
    }
    private int[] mTitles = new int[] { R.string.connect, R.string.spec, R.string.storage_management, R.string.setting_photo, R.string.setting_record,
            R.string.setting_extra, R.string.setting_language, R.string.setting_gps, R.string.setting_ap, R.string.setting_a2dp};
    private int[] mIds = new int[] {};
    private ArrayList<SettingItem> mItems = new ArrayList<>();
    private Context mContext;
    private ItemClickedListener mListener;

    public SettingRecyclerAdapter(Context context, boolean isSupportedA2DP, ItemClickedListener listener) {
        mContext = context;
        mListener = listener;

        init(isSupportedA2DP);
    }

    public void init(boolean isSupportedA2DP) {
        mItems.clear();
        for (int i = 0; i < mTitles.length; i++) {
            if (ID.values()[i] == ID.ID_A2DP && !isSupportedA2DP) {
                continue;
            }
            mItems.add(new SettingItem(ID.values()[i], TYPE_BUTTON, mTitles[i]));
        }
        mItems.add(1, new SettingItem(null, TYPE_PANEL, R.string.device_info));
        mItems.add(4, new SettingItem(null, TYPE_PANEL, R.string.setting_camera));
        mItems.add(7, new SettingItem(null, TYPE_PANEL, R.string.etc));
        notifyDataSetChanged();
    }

    public SettingItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).mType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_BUTTON) {
            return new HolderButton(LayoutInflater.from(mContext).inflate(R.layout.holder_button_arrow, parent, false), mListener);
        } else if (viewType == TYPE_PANEL) {
            return new HolderPanel(LayoutInflater.from(mContext).inflate(R.layout.holder_panel, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HolderButton) {
            ((HolderButton) holder).setData(mItems.get(position).mTitle);
        } else if (holder instanceof HolderPanel) {
            ((HolderPanel) holder).setData(mItems.get(position).mTitle);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private static class HolderButton extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RelativeLayout mContainer;
        private TextView mTitleView;
        private ItemClickedListener mListener;
        public HolderButton(@NonNull View itemView, ItemClickedListener listener) {
            super(itemView);

            mListener = listener;

            mContainer = itemView.findViewById(R.id.container);
            mTitleView = itemView.findViewById(R.id.title);

            mContainer.setOnClickListener(this);
        }

        public void setData(int title) {
            mTitleView.setText(title);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.container) {
                mListener.clickedItem(getLayoutPosition());
            }
        }
    }

    private static class HolderPanel extends RecyclerView.ViewHolder {
        private TextView mTitleTv;
        public HolderPanel(@NonNull View itemView) {
            super(itemView);
            mTitleTv = itemView.findViewById(R.id.title);
        }

        public void setData(int title) {
            mTitleTv.setText(title);
        }
    }


    public interface ItemClickedListener {
        void clickedItem(int position);
    }
}
