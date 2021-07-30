package com.elishevada.ex3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;



public class AlarmClockReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "appChanel2";
    private static final CharSequence CHANNEL_NAME = "App Chanel #2";
    private int notificationID=1;
    private NotificationManager notificationManager;



    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("mylog", "alarmclockresiver1111");

        String alarmtitleusername = intent.getStringExtra("username");
        String tasktitle = intent.getStringExtra("tasktitle");


        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 2. Create Notification-Channel. ONLY for Android 8.0 (OREO API level 26) and higher.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID, // Constant for Channel ID
                    CHANNEL_NAME, // Constant for Channel NAME
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Log.d("mylog", "alarmclockresiver222");
        Intent tapIntent = new Intent(context,ToDoListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, tapIntent, 0);

        // 3. Create & show the Notification. on Build.VERSION < OREO notification avoid CHANEL_ID
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle("Hi "+alarmtitleusername+ "it's time to do your task")
                .setContentText("task title:"+tasktitle+"")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(notificationID, notification);
        notificationID++;

    }
}
