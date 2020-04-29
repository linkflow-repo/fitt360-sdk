package com.linkflow.fitt360sdk.model;

import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.library.linkflow.manager.NeckbandRestApiClient;
import app.library.linkflow.manager.helper.DownloadHelper;

import com.linkflow.fitt360sdk.adapter.GalleryRecyclerAdapter;
import com.linkflow.fitt360sdk.item.GalleryItem;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public class MediaModel {
    private Listener mListener;
    private boolean[] mIsWorking = new boolean[3];

    public MediaModel(Listener listener) {
        mListener = listener;
    }

    public void getMediaList(final String accessToken, int skip, int take) {
        if (!mIsWorking[0]) {
            mIsWorking[0] = true;
            Service service = NeckbandRestApiClient.getInstance().create(Service.class);
            Call<JsonObject> callback = service.getMediaList(accessToken, take, skip);
            callback.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    mIsWorking[0] = false;
                    boolean success = false;
                    ArrayList<GalleryItem> items = new ArrayList<>();
                    JsonObject body = response.body();
                    int childId = 0;
                    if (body != null) {
                        success = body.get("success").getAsBoolean();
                        if (success) {
                            String beforeDate = null;
                            ArrayList<GalleryItem> allItems = new ArrayList<>();
                            JsonObject result = body.getAsJsonObject("result");
                            if (result.has("next")) {
                                boolean hasNext = result.get("next").getAsBoolean();
                            }
                            JsonParser parser = new JsonParser();
                            JsonArray files = parser.parse(result.get("files").getAsString()).getAsJsonArray();
                            for (JsonElement file : files) {
                                String name = file.getAsString();
                                items.add(new GalleryItem(name.contains(".mp4") ? GalleryRecyclerAdapter.VIEW_TYPE_VIDEO : GalleryRecyclerAdapter.VIEW_TYPE_PHOTO,
                                        name, NeckbandRestApiClient.getThumbnailPath(accessToken, name)));
                            }
                        }
                    }
                    mListener.completedGetMediaList(true, items);
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mIsWorking[0] = false;
                    mListener.completedGetMediaList(false,null);
                }
            });
        }
    }

    public void delete(String accessToken, final String[] filenames) {
        if (!mIsWorking[1]) {
            mIsWorking[1] = true;
            Gson gson = new Gson();
            JSONObject params = new JSONObject();
            try {
                params.put("access_token", accessToken);
                params.put("files", gson.toJson(filenames));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Service service = NeckbandRestApiClient.getInstance().create(Service.class);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), params.toString());
            Call<JsonObject> callback = service.delete(body);
            callback.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    mIsWorking[1] = false;
                    boolean success = false;
                    JsonObject body = response.body();
                    if (body != null) {
                        success = body.get("success").getAsBoolean();
                    }
                    mListener.completedDelete(success, filenames, null);
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mIsWorking[1] = false;
                    mListener.completedDelete(false, filenames, null);
                }
            });
        }
    }

    public void download(String accessToken, GalleryItem item, final DownloadHelper.DownloadListener listener) {
        if (item != null) {
            Service service = NeckbandRestApiClient.getInstance().create(Service.class);
            Call<ResponseBody> callback = service.downloadFile(NeckbandRestApiClient.getDownloadUrl(accessToken, item.getFileName()));
            callback.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        new DownloadHelper(Looper.getMainLooper(), response.body(), listener).download();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }

    public void downloadGPS(String accessToken, String filename, final DownloadHelper.DownloadListener listener) {
        if (filename != null) {
            Service service = NeckbandRestApiClient.getInstance().create(Service.class);
            Call<ResponseBody> callback = service.downloadFile(NeckbandRestApiClient.getGPSDownloadUrl(accessToken, filename));
            callback.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        new DownloadHelper(Looper.getMainLooper(), response.body(), listener).download();
                    } else {
                        listener.endDownload(false);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    listener.endDownload(false);
                }
            });
        }
    }

    private interface Service {
        @GET("app/media/list/videophoto/{accessToken}/{take}/{skip}")
        Call<JsonObject> getMediaList(@Path("accessToken") String accessToken, @Path("take") int take, @Path("skip") int skip);

        @POST("app/media/delete")
        Call<JsonObject> delete(@Body RequestBody body);

        @Streaming
        @GET
        Call<ResponseBody> downloadFile(@Url String url);
    }

    public interface Listener {
        void completedGetMediaList(boolean success, ArrayList<GalleryItem> allItems);
        void completedDelete(boolean success, String[] filenames, String path);
    }
}
