package com.ali.minimalweather;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class MyNotifications {

    public static final int criticalChangeWeatherNotificationID = 1001;
    public static final int middayWeatherNotificationID = 1002;
    private final Context context;
    private PendingIntent pendingIntent;

    public MyNotifications(Context context) {
        this.context = context;
        initNotification(context);
    }

    private void initNotification(Context context) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    public void weatherNotification(int notificationID, int smallIconDrawable, CharSequence contentTitle, CharSequence contentText){
        Notification notification = new NotificationCompat.Builder(context,"myApp")
                .setSmallIcon(smallIconDrawable)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID, notification);

    }

}
