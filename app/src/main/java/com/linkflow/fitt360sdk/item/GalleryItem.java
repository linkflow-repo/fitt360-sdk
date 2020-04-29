package com.linkflow.fitt360sdk.item;

import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class GalleryItem implements Parcelable {
    public static final int VIEW_TYPE_VIDEO = 1, VIEW_TYPE_PHOTO = 2;

    public int mViewType;
    private String mFilePath, mFileName;
    private String mThumbnailPath;
    private long mDuration;
    private boolean mSelected = false;

    public GalleryItem(int viewType, String fileName, String thumbNailPath) {
        mViewType = viewType;
        mFileName = fileName;
        mThumbnailPath = thumbNailPath;
    }

    public GalleryItem(int viewType, File file, String thumbNailPath) {
        mViewType = viewType;
        mFilePath = file.getPath();
        mFileName = file.getName();
        mThumbnailPath = thumbNailPath;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(file.getPath());
        } catch (Exception e) {
            mDuration = 0;
            return;
        }

        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        if (duration != null) {
            this.mDuration = Long.parseLong(duration);
        } else {
            this.mDuration = 0;
        }
    }

    protected GalleryItem(Parcel in) {
        mViewType = in.readInt();
        mFilePath = in.readString();
        mFileName = in.readString();
        mThumbnailPath = in.readString();
        mDuration = in.readLong();
        mSelected = in.readByte() != 0;
    }

    public static final Creator<GalleryItem> CREATOR = new Creator<GalleryItem>() {
        @Override
        public GalleryItem createFromParcel(Parcel in) {
            return new GalleryItem(in);
        }

        @Override
        public GalleryItem[] newArray(int size) {
            return new GalleryItem[size];
        }
    };

    public String getFilePath() {
        return mFilePath;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getThumbnailPath() {
        return mThumbnailPath;
    }

    public long getDuration() {
        return mDuration;
    }

    public String getDurationText() {
        long totalSeconds = mDuration / 1000;
        long hours = totalSeconds / (60 * 60);

        totalSeconds = totalSeconds % (60 * 60);
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        String durationText;
        if (hours > 0) {
            durationText = String.format("%s:%s:%s", get2xStringFromInt(hours), get2xStringFromInt(minutes), get2xStringFromInt(seconds));
        } else {
            durationText = String.format("%s:%s", get2xStringFromInt(minutes), get2xStringFromInt(seconds));
        }
        return durationText;
    }

    private String get2xStringFromInt(long number) {
        if (number == 0) {
            return "00";
        } else if (number < 10) {
            return String.format("0%d", number);
        } else {
            return String.format("%d", number);
        }
    }

    public boolean ismSelected() {
        return mSelected;
    }

    public void selectToggle() {
        mSelected = !mSelected;
    }

    public void select() {
        mSelected = true;
    }

    public void unselect() {
        mSelected = false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mViewType);
        parcel.writeString(mFilePath);
        parcel.writeString(mFileName);
        parcel.writeString(mThumbnailPath);
        parcel.writeLong(mDuration);
        parcel.writeByte((byte) (mSelected ? 1 : 0));
    }
}
