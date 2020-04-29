package com.linkflow.fitt360sdk.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.linkflow.fitt360sdk.R;
import com.linkflow.fitt360sdk.service.RTMPStreamService;

public class NotificationHelper {
    public static final int RTMP_STREAM_NOTIFY_ID = 103;
    private static NotificationHelper mInstance;

    private NotificationManager mNotificationManager;

    public static NotificationHelper getInstance() {
        if (mInstance == null) {
            mInstance = new NotificationHelper();
        }
        return mInstance;
    }

    public NotificationHelper init(Context context) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mInstance;
    }

    public Notification makeRTMPStreamStatus(Context context, String connectionStatus, int connectionStatusColor, String time, String transfer) {
        Intent intent = new Intent(RTMPStreamService.ACTION_CANCEL_RTMP_STREAM);
        intent.putExtra("close", 10);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getForegroundService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notify_rtmp_stream_layout);
        remoteViews.setTextViewText(R.id.connectState, connectionStatus);
        remoteViews.setTextColor(R.id.connectState, connectionStatusColor);
        remoteViews.setTextViewText(R.id.timer, time);
        remoteViews.setTextViewText(R.id.transfer, transfer);
        remoteViews.setOnClickPendingIntent(R.id.status, pendingIntent);

        NotificationCompat.Builder builder = createBuilder(context, mNotificationManager, false);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContent(remoteViews);
        return builder.build();
    }

    public void cancelNotification(int id) {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(id);
        }
    }

    public boolean hasNotification(int id) {
        for (StatusBarNotification notification : mNotificationManager.getActiveNotifications()) {
            if (notification.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private NotificationCompat.Builder createBuilder(Context context, NotificationManager notificationManager, boolean isHeadUp) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = isHeadUp ? new NotificationChannel("fitt-channel-5", "fitt360", NotificationManager.IMPORTANCE_HIGH)
                    : new NotificationChannel("fitt-channel-4", "fitt360", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(context, channel.getId());
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        return builder;
    }

    private boolean isDarkTheme(Context context) {
        return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    public interface Listener {
        void makeDisconnect();
    }
}
