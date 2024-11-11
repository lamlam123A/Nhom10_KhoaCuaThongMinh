package com.example.smartdoorlock.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.smartdoorlock.R;
import com.example.smartdoorlock.activities.MainActivity;

public class NotificationHelper {
    private static final String CHANNEL_ID = "smart_door_lock_channel";
    private static final int NOTIFICATION_ID = 1;

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Smart Door Lock Notifications", NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(null, null);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void showNotification(Context context, String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.door_lock_32);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.baseline_notifications_active_24).setContentTitle(title).setContentText(message).setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setLargeIcon(largeIcon).setContentIntent(pendingIntent).setAutoCancel(true).setColor(Color.BLUE).setPriority(NotificationCompat.PRIORITY_HIGH);

        MediaPlayer notificationSound = MediaPlayer.create(context, R.raw.livechat_notification);
        notificationSound.start();

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

}