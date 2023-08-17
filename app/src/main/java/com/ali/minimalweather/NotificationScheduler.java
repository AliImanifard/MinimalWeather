package com.ali.minimalweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

public class NotificationScheduler {

    public static void scheduleNotification(Context context, Class<?> cls, int hourOfDay, int minute){
        // Create a calendar object and set the desired time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Log.i("myLog","calendar.getTimeInMillis() : " + calendar.getTimeInMillis());
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        Log.i("myLog","calendar.getTimeInMillis() : " + calendar.getTimeInMillis());

        // Create an intent for the BroadcastReceiver that will handle the notification
        Intent intent = new Intent(context, cls);
        PendingIntent pendingIntent = null;
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        // Get the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Set the alarm to trigger the specified time every day
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
            }catch (SecurityException e){
                e.printStackTrace();
            }
        }
        else
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
