package com.xmall75.steamdealsalert.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.xmall75.steamdealsalert.R;
import com.xmall75.steamdealsalert.ui.MainActivity;

public class DealsForegroundService extends Service {

    public static final String CHANNEL_ID = "running_service_channel";
    public static final int NOTIF_ID = 1001;
    public static final String EXTRA_STATUS_TEXT = "extra_status_text";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String statusText = "Monitoring steam deals in background";
        if (intent != null && intent.hasExtra(EXTRA_STATUS_TEXT)) {
            statusText = intent.getStringExtra(EXTRA_STATUS_TEXT);
        }

        Notification notification = createNotification(statusText);
        startForeground(NOTIF_ID, notification);

        return START_STICKY;
    }

    private Notification createNotification(String message) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        int pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingFlags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainIntent, pendingFlags);

        Bitmap appLogoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_splash_logo);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Active")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_splash_logo)
                .setLargeIcon(appLogoBitmap)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    public static void updateStatus(Context context, String message) {
        Intent intent = new Intent(context, DealsForegroundService.class);
        intent.putExtra(EXTRA_STATUS_TEXT, message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Background Monitoring Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Shows active monitoring status");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}