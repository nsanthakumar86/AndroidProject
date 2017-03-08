package com.mb.android.lec.ui;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mb.android.lec.R;

/**
 * Created by LetX on 20-02-2017.
 */

public class CustomReceiver extends BroadcastReceiver {


    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);

        Log.d("Notification", "Notified..");
    }

    private Notification getNotification(Context context, String content) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.events);
        builder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, LECLoginActivity.class), 0));
        return builder.build();
    }
}
