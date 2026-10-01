package com.xmall75.steamdealsalert.notification;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.xmall75.steamdealsalert.R;
import com.xmall75.steamdealsalert.data.local.DealEntity;
import com.xmall75.steamdealsalert.data.remote.ApiClient;
import com.xmall75.steamdealsalert.ui.OpenStoreActivity;

import java.util.Locale;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class NotificationHelper {

    public static final String CHANNEL_ID = "steam_deals";

    private NotificationHelper() {}

    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(context.getString(R.string.channel_desc));
        NotificationManager nm = context.getSystemService(NotificationManager.class);
        if (nm != null) nm.createNotificationChannel(channel);
    }

    public static boolean canNotify(Context context) {
        if (Build.VERSION.SDK_INT >= 33
                && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    @SuppressLint("MissingPermission")
    public static void notifyDeal(Context context, DealEntity deal) {
        createChannel(context);

        if (!canNotify(context)) return;

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        int requestCode = deal.steamAppId != null ? deal.steamAppId.hashCode() : (int) System.currentTimeMillis();

        Intent open = new Intent(context, OpenStoreActivity.class)
                .putExtra(OpenStoreActivity.EXTRA_APP_ID, deal.steamAppId);
        PendingIntent contentIntent = PendingIntent.getActivity(context, requestCode, open, flags);

        Intent web = new Intent(Intent.ACTION_VIEW,
                Uri.parse(OpenStoreActivity.webUrl(deal.steamAppId)));
        PendingIntent webIntent = PendingIntent.getActivity(context, ~requestCode, web, flags);

        String title = context.getString(R.string.notif_title, deal.title != null ? deal.title : "Game");
        String text = deal.ratingPercent > 0
                ? context.getString(R.string.notif_text_rated, deal.salePrice, deal.normalPrice,
                deal.ratingPercent, deal.ratingText == null ? "-" : deal.ratingText)
                : context.getString(R.string.notif_text_unrated, deal.salePrice, deal.normalPrice);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_PROMO)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .addAction(0, context.getString(R.string.notif_action_browser), webIntent);

        Bitmap bitmap = fetchBitmap(deal.thumb);
        if (bitmap != null) {
            builder.setLargeIcon(bitmap)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap)
                            .bigLargeIcon((Bitmap) null));
        } else {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
        }

        NotificationManagerCompat.from(context).notify(requestCode, builder.build());
    }

    private static Bitmap fetchBitmap(String url) {
        if (url == null || url.trim().isEmpty()) return null;
        try {
            Request request = new Request.Builder().url(url).build();
            try (Response response = ApiClient.http().newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) return null;
                ResponseBody body = response.body();
                return BitmapFactory.decodeStream(body.byteStream());
            }
        } catch (Exception e) {
            return null;
        }
    }
}