package com.tomi.ohl.szakdoga.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.tomi.ohl.szakdoga.MainActivity;
import com.tomi.ohl.szakdoga.R;
import com.tomi.ohl.szakdoga.fragments.MessagesFragment;

/**
 * Értesítéseket kezelő segédosztály.
 */
public class NotificationUtils {
    public static final String MSG_CHANNEL = "messages";

    /**
     * Értesítési csatorna létrehozása.
     * @param ctx kontextus.
     */
    public static void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ctx.getString(R.string.messages);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(MSG_CHANNEL, name, importance);
            // Regisztráljuk a rendszerbe
            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Új üzenet értesítés megjelenítése.
     * @param ctx kontextus.
     */
    public static void showNewMessageNotification(Context ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.putExtra("shortcut_destination", MessagesFragment.class.getSimpleName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, MSG_CHANNEL)
                .setContentTitle(ctx.getString(R.string.notification_new_message_title))
                .setContentText(ctx.getString(R.string.notification_new_message_desc))
                .setSmallIcon(R.drawable.ic_nav_storages)
                .setColor(ctx.getColor(R.color.colorAccent))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        clearNotifications(ctx);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    /**
     * Az alkalmazás által küldött értesítések törlése.
     * @param ctx kontextus.
     */
    public static void clearNotifications(Context ctx) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        notificationManager.cancelAll();
    }
}
