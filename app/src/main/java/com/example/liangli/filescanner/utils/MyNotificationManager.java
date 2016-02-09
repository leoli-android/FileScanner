package com.example.liangli.filescanner.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat;

import com.example.liangli.filescanner.activities.MainActivity;

/**
 * Created by liangli on 2/9/16.
 *
 * Notification manager
 */
public class MyNotificationManager {

    static final String TAG = "MyNotifMgr";

    public static final int NOTIFICATION_ID = 100;

    private static final String NOTIF_TITLE = "File Scanner is Working";
    private static final String NOTIF_MSG = "File Scanner is scanning your SD card. The statistics will be updated soon.";

    public static void sendNotification(Context context) {

        final NotificationManagerCompat mgr = NotificationManagerCompat.from(context);

        mgr.notify(NOTIFICATION_ID,
                createNotification(context).build());
    }

    private static NotificationCompat.Builder createNotification(Context context) {
        Intent openActivityIntent;
        openActivityIntent = new Intent(context, MainActivity.class);
        openActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent openActivityPendingIntent = PendingIntent.getActivity(context,
                0, openActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setContentTitle(NOTIF_TITLE)
                .setContentText(NOTIF_MSG)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(NOTIF_MSG))
                .setSmallIcon(android.R.drawable.btn_radio)
                .setContentIntent(openActivityPendingIntent)
                .setAutoCancel(true)
                .setLocalOnly(true)
                .setOngoing(false);
        return notification;
    }

    /**
     * Cancels notification.
     */
    public static void cancelNotification(Context context) {
        final NotificationManagerCompat mgr = NotificationManagerCompat.from(context);
        mgr.cancel(NOTIFICATION_ID);
    }
}
