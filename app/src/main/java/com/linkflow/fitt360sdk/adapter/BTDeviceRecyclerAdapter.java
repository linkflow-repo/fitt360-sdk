package com.linkflow.fitt360sdk.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.item.BTItem;

import java.util.ArrayList;

public class BTDeviceRecyclerAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private ItemClickListener mListener;

    private ArrayList<BTItem> mItems = new ArrayList<>();

    public BTDeviceRecyclerAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    public BTItem getItem(int position) {
        return mItems.get(position);
    }

    public int getCorrectDevicePosition(String address) {
        int size = mItems.size();
        for (int i = 0; i < size; i++) {
            if (mItems.get(i).mDevice.getAddress().equals(address)) {
                return i;
            }
        }
        return -1;
    }

    public void addItems(ArrayList<BluetoothDevice> devices) {
        mItems.clear();
        for (BluetoothDevice device : devices) {
            mItems.add(new BTItem(device, BTItem.STATE_FINDING));
        }
        notifyDataSetChanged();
    }

    public void addItem(BluetoothDevice device) {
        if (device != null) {
            boolean noDevice = true;
            int size = mItems.size();
            for (int i = 0; i < size; i++) {
                if (mItems.get(i).mDevice.getAddress().equals(device.getAddress())) {
                    noDevice = false;
                    mItems.get(i).mState = BTItem.STATE_NEAR;
                    notifyItemChanged(i);
                    break;
                }
            }
            if (noDevice) {
                mItems.add(new BTItem(device, BTItem.STATE_NEAR));
                notifyDataSetChanged();
            }
        }
    }

    public void changeItemsStateNone() {
        for(BTItem item : mItems) {
            if (item.mState == BTItem.STATE_FINDING) {
                item.mState = BTItem.STATE_NONE;
            }
        }
        notifyDataSetChanged();
    }

    public void changeItemStateFinding() {
        for(BTItem item : mItems) {
            item.mState = BTItem.STATE_FINDING;
        }
        notifyDataSetChanged();
    }

    public void changedBonded(String address) {
        int size = mItems.size();
        for (int i = 0; i < size; i++) {
            if (mItems.get(i).mDevice.getAddress().equals(address)) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void checked(int beforePosition, int currentPosition) {
        if (beforePosition != -1) {
            mItems.get(beforePosition).setChecked(false);
            notifyItemChanged(beforePosition);
        }
        mItems.get(currentPosition).setChecked(true);
        notifyItemChanged(currentPosition);
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).mDevice.getBondState();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == 12) {
            return new BTBondedHolder(LayoutInflater.from(mContext).inflate(R.layout.holder_bt_bonded, viewGroup, false), mListener);
        }
        return new BTDeviceHolder(LayoutInflater.from(mContext).inflate(R.layout.holder_bt_device, viewGroup, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof BTDeviceHolder) {
            BTItem item = mItems.get(i);
            ((BTDeviceHolder) viewHolder).setData(item.mDevice.getName(), item.mChecked);
        } else if (viewHolder instanceof BTBondedHolder) {
            BTItem item = mItems.get(i);
            ((BTBondedHolder) viewHolder).setData(item.mDevice.getName(), item.mState, item.mChecked);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private static class BTDeviceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RelativeLayout mContainer;
        private TextView mNameTv;
        private ImageView mStateIv;
        private ItemClickListener mListener;

        BTDeviceHolder(@NonNull View itemView, ItemClickListener listener) {
            super(itemView);
            mListener = listener;

            mContainer = itemView.findViewById(R.id.container);
            mContainer.setOnClickListener(this);

            mNameTv = itemView.findViewById(R.id.name);
            mStateIv = itemView.findViewById(R.id.state);
        }

        public void setData(String name, boolean checked) {
            mNameTv.setText(name);
            mStateIv.setImageResource(checked ? R.drawable.found_actived : R.drawable.found);
            mContainer.setBackgroundColor(checked ? Color.parseColor("#88EEEDED") : 0);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.container) {
                mListener.clickedItem(getLayoutPosition());
            }
        }
    }

    private static class BTBondedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RelativeLayout mContainer;
        private TextView mNameTv;
        private ImageView mStateIv;
        private ItemClickListener mListener;
        private int[] mState = new int[] { R.drawable.notfound, R.drawable.found, R.drawable.bluetoothserching};
        public BTBondedHolder(@NonNull View itemView, ItemClickListener listener) {
            super(itemView);
            mListener = listener;

            mContainer = itemView.findViewById(R.id.container);
            mContainer.setOnClickListener(this);

            mStateIv = itemView.findViewById(R.id.state);
            mNameTv = itemView.findViewById(R.id.name);
        }

        public void setData(String name, int state, boolean checked) {
            mNameTv.setText(name);
            if (checked) {
                mStateIv.setImageResource(R.drawable.found_actived);
                mContainer.setBackgroundColor(Color.parseColor("#88EEEDED"));
            } else {
                mStateIv.setImageResource(mState[state]);
                mContainer.setBackgroundColor(0);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.container) {
                mListener.clickedItem(getLayoutPosition());
            }
        }
    }

    public interface ItemClickListener {
        void clickedItem(int position);
    }
}
