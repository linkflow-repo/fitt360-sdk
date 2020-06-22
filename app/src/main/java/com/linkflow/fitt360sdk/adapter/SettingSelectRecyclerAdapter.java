package com.linkflow.fitt360sdk.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.item.ButtonItem;
import com.linkflow.fitt360sdk.item.InputItem;
import com.linkflow.fitt360sdk.item.Item;
import com.linkflow.fitt360sdk.item.RadioItem;
import com.linkflow.fitt360sdk.listener.ItemChangeListener;

import java.util.ArrayList;

public class SettingSelectRecyclerAdapter extends RecyclerView.Adapter implements ItemChangeListener {
    private Context mContext;
    private ItemSelectedListener mListener;

    private ArrayList<Item> mItems = new ArrayList<>();
    private int mDefaultSelectedPosition, mBeforeSelectedPosition;

    public SettingSelectRecyclerAdapter(Context context,  ItemSelectedListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setSelectedPosition(int position) {
        mDefaultSelectedPosition = position;
        mBeforeSelectedPosition = position;
    }

    public void setItems(ArrayList<Item> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public RadioItem getItemAsRadio(int position) {
        return (RadioItem) mItems.get(position);
    }

    public InputItem getItemAsInput(int position) {
        return (InputItem) mItems.get(position);
    }

    public boolean isButtonItem(int position) {
        return mItems.get(position) instanceof ButtonItem;
    }

    public boolean isUpdated() {
        return mDefaultSelectedPosition != mBeforeSelectedPosition;
    }

    public boolean updateAdapter(int position) {
        if (mBeforeSelectedPosition != position) {
            int size = mItems.size();
            for (int i = 0; i < size; i++) {
                if (i == position) {
                    if (mItems.get(i) instanceof RadioItem) {
                        ((RadioItem)mItems.get(i)).setChecked(true);
                    }
                } else {
                    if (mItems.get(i) instanceof RadioItem) {
                        ((RadioItem)mItems.get(i)).setChecked(false);
                    }
                }
            }
            notifyItemChanged(mBeforeSelectedPosition);
            notifyItemChanged(position);
            mBeforeSelectedPosition = position;
            return true;
        }
        return false;
    }

    @Override
    public void textChanged(int position, String value) {
        if (mItems.get(position) instanceof InputItem) {
            mBeforeSelectedPosition = -1;
            ((InputItem) mItems.get(position)).mValue = value;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).mViewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Item.VIEW_TYPE_RADIO) {
            return new RadioHolder(LayoutInflater.from(mContext).inflate(R.layout.holder_radio, parent, false), mListener);
        } else if (viewType == Item.VIEW_TYPE_BUTTON) {
            return new ButtonHolder(LayoutInflater.from(mContext).inflate(R.layout.holder_button, parent, false), mListener);
        } else {
            return new InputHolder(LayoutInflater.from(mContext).inflate(R.layout.holder_input, parent, false), this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RadioHolder) {
            RadioItem item = (RadioItem) mItems.get(position);
            ((RadioHolder) holder).setData(item.mTitle, item.getChecked());
        } else if (holder instanceof ButtonHolder) {
            ButtonItem item = (ButtonItem) mItems.get(position);
            ((ButtonHolder) holder).setTitle(item.mTitle);
        } else if (holder instanceof InputHolder) {
            InputItem item = (InputItem) mItems.get(position);
            ((InputHolder) holder).setValue(item.mValue);
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

    private static class InputHolder extends RecyclerView.ViewHolder implements TextWatcher {
        private EditText mInputEt;
        private ItemChangeListener mListener;

        public InputHolder(@NonNull View itemView, ItemChangeListener listener) {
            super(itemView);
            mListener = listener;
            mInputEt = itemView.findViewById(R.id.input);
            mInputEt.addTextChangedListener(this);
        }

        public void setValue(String value) {
            mInputEt.setText(value);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mListener.textChanged(getLayoutPosition(), mInputEt.getText().toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private static class ButtonHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Button mBtn;
        private ItemSelectedListener mListener;
        public ButtonHolder(@NonNull View itemView,  ItemSelectedListener listener) {
            super(itemView);
            mListener = listener;
            mBtn = itemView.findViewById(R.id.button);
            mBtn.setOnClickListener(this);
        }

        public void setTitle(String title) {
            mBtn.setText(title);
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
