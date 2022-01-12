package com.c323FinalProject.nicmacki.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.c323FinalProject.nicmacki.AlarmNotifier;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmNotifier alarmNotifier = new AlarmNotifier(context);
        NotificationCompat.Builder builder = alarmNotifier.buildNotification();
        Notification notification = builder.build();
        NotificationManager notificationManager = alarmNotifier.getNotificationManager();
        notificationManager.notify(1, notification);
    }
}
