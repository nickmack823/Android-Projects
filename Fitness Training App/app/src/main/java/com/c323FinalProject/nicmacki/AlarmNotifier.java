package com.c323FinalProject.nicmacki;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.c323FinalProject.nicmacki.R;
import com.c323FinalProject.nicmacki.activities.MainActivity;

public class AlarmNotifier extends ContextWrapper {

    private NotificationManager notificationManager;
    NotificationChannel channel;


    public AlarmNotifier(Context base) {
        super(base);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    /**
     * Creates a NotificationChannel.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        channel = new NotificationChannel("defaultId", "Generic Fitness App",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Notification channel");
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * Creates a NotificationCompat.Builder.
     * @return The NotificationCompat.Builder object created
     */
    public NotificationCompat.Builder buildNotification() {
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "defaultId")
                .setSmallIcon(R.drawable.ic_baseline_sports_martial_arts_24)
                .setContentTitle("5 minutes until scheduled workout.")
                .setContentInfo("Generic Fitness App")
                .setSound(sound)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        return builder;
    }
}
