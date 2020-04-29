package com.linkflow.fitt360sdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.linkflow.fitt360sdk.R;

import java.util.ArrayList;

import com.linkflow.fitt360sdk.item.GalleryItem;

public class GalleryRecyclerAdapter extends RecyclerView.Adapter {
    public static final int VIEW_TYPE_VIDEO = 1, VIEW_TYPE_PHOTO = 2;
    private Context mContext;
    private ItemClickListener mListener;

    private ArrayList<GalleryItem> mItems = new ArrayList<>();

    public GalleryRecyclerAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setAllList(ArrayList<GalleryItem> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public GalleryItem getItem(int position) {
        return mItems.get(position);
    }

    public ArrayList<GalleryItem> getItems() {
        return mItems;
    }

    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).mViewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_VIDEO) {
            return new VideoHolder(LayoutInflater.from(mContext).inflate(R.layout.holder_video, viewGroup, false), mListener);
        } else if (i == VIEW_TYPE_PHOTO) {
            return new PhotoHolder(LayoutInflater.from(mContext).inflate(R.layout.holder_photo, viewGroup, false), mListener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof VideoHolder) {
            GalleryItem item = mItems.get(i);
            Glide.with(mContext).load(item.getThumbnailPath()).into(((VideoHolder) viewHolder).mThumbIv);
            String title = item.getFileName();
            ((VideoHolder)viewHolder).mTitle.setText(title);

        } else if (viewHolder instanceof PhotoHolder) {
            GalleryItem item = mItems.get(i);
            Glide.with(mContext).load(item.getThumbnailPath()).into(((PhotoHolder) viewHolder).mThumbIv);
            ((PhotoHolder)viewHolder).mTitle.setText(item.getFileName());
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private static class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemClickListener mListener;
        public ImageView mThumbIv;
        public TextView mDownloadTv, mDeleteTv, mTitle ;

        public VideoHolder(@NonNull View itemView, ItemClickListener listener) {
            super(itemView);
            mListener = listener;
            mThumbIv = itemView.findViewById(R.id.image);
            mDownloadTv = itemView.findViewById(R.id.download);
            mDeleteTv = itemView.findViewById(R.id.delete);
            mTitle = (TextView) itemView.findViewById(R.id.mTitle);
            mDownloadTv.setOnClickListener(this);
            mDeleteTv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.clickedItem(getLayoutPosition(), v.getId() == R.id.delete);
        }
    }

    private static class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemClickListener mListener;
        public ImageView mThumbIv;
        public TextView mDownloadTv, mDeleteTv, mTitle;

        public PhotoHolder(@NonNull View itemView, ItemClickListener listener) {
            super(itemView);
            mListener = listener;
            mThumbIv = itemView.findViewById(R.id.image);
            mDownloadTv = itemView.findViewById(R.id.download);
            mDeleteTv = itemView.findViewById(R.id.delete);
            mDownloadTv.setOnClickListener(this);
            mDeleteTv.setOnClickListener(this);
            mTitle = itemView.findViewById(R.id.mTitle);
        }

        @Override
        public void onClick(View v) {
            mListener.clickedItem(getLayoutPosition(), v.getId() == R.id.delete);
        }
    }

    public interface ItemClickListener {
        void clickedItem(int position, boolean isDelete);
    }
}
