package com.linkflow.fitt360sdk.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.adapter.GalleryRecyclerAdapter;

import java.util.ArrayList;

import app.library.linkflow.Constant;
import app.library.linkflow.manager.helper.DownloadHelper;
import app.library.linkflow.manager.helper.GPSXmlToGPSTxtHelper;

import com.linkflow.fitt360sdk.item.GalleryItem;
import com.linkflow.fitt360sdk.model.MediaModel;

public class GalleryActivity extends BaseActivity implements GalleryRecyclerAdapter.ItemClickListener, MediaModel.Listener {
    private GalleryRecyclerAdapter mAdapter;
    private MediaModel mMediaModel;
    private GPSXmlToGPSTxtHelper mParser;

    private TextView mDownloadAllBtn, mDeleteAllBtn;
    Button bt_sort;
    public boolean sort = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBodyView(R.layout.activity_gallery);
        setHeaderTitle(R.string.gallery);

        mParser = new GPSXmlToGPSTxtHelper();
        mMediaModel = new MediaModel(this);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mAdapter = new GalleryRecyclerAdapter(this, this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMediaList();
    }

    public void getMediaList() {
        if (mNeckbandManager != null) {
            if (mNeckbandManager.getConnectStateManage().isConnected()) {
                if (mMediaModel != null) {
                    mMediaModel.getMediaList(mNeckbandManager.getAccessToken(), 0, 5000);
                }
            }
        }
    }

    @Override
    public void clickedItem(final int position, boolean isDelete) {
        GalleryItem item = mAdapter.getItem(position);
        if (isDelete) {
            mMediaModel.delete(mNeckbandManager.getAccessToken(), new String[] { item.getFileName() });
        } else {
            download(item);
        }
    }

    private void download(final GalleryItem item) {
        if (item.mViewType == GalleryItem.VIEW_TYPE_VIDEO) {
            mMediaModel.downloadGPS(mNeckbandManager.getAccessToken(), item.getFileName() + ".xml", new DownloadHelper.DownloadListener() {
                @Override
                public void beginDownload() {

                }

                @Override
                public void progress(long current, long max) {

                }

                @Override
                public void endDownload(boolean success) {
                    double[] firstLocation = null;
                    if (success) {
                        mParser.convertToTxt(Constant.GPS_SAVE_PATH + item.getFileName() + ".xml", Constant.GPS_SAVE_PATH + item.getFileName() + ".txt");
                        firstLocation = mParser.getFirstLocation();
                    }
                    downloadMedia(item, success, firstLocation);
                }

                @Override
                public String downloadPath() {
                    return Constant.GPS_SAVE_PATH + item.getFileName() + ".xml";
                }
            });
        } else {
            downloadMedia(item,false, null);
        }
    }

    private void downloadMedia(final GalleryItem item, final boolean hasMap, final double[] firstLocation) {
        mMediaModel.download(mNeckbandManager.getAccessToken(), item, new DownloadHelper.DownloadListener() {
            @Override
            public void beginDownload() {
                Toast.makeText(GalleryActivity.this, R.string.download_start, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void progress(long current, long max) {

            }

            @Override
            public void endDownload(boolean success) {
                Toast.makeText(GalleryActivity.this, success ? R.string.download_done : R.string.download_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public String downloadPath() {
                String path = item.mViewType == GalleryItem.VIEW_TYPE_PHOTO ? Constant.PICTURE_SAVE_PATH : Constant.RECORD_SAVE_PATH;
                return path + item.getFileName();
            }
        });
    }

    @Override
    public void completedGetMediaList(boolean success, ArrayList<GalleryItem> items) {
        if (success) {
            mAdapter.setAllList(items);
        }
    }

    @Override
    public void completedDelete(boolean success, String[] filenames, String path) {
        if (success) {
            getMediaList();
            Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
        }
    }
}
