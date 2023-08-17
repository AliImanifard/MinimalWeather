package com.ali.minimalweather;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.ali.minimalweather.DataBase.AssetDatabaseHelper;

public class MyCustomApplication extends Application {

    public static final String APP_ID_CURRENT_WEATHER = "weather?";
    public static final String APP_ID_FORECAST = "forecast?";
    public static final String APP_ID_AIR_POLLUTION = "air_pollution?";


    public static final String strUrlOpenWeatherMapApi = "https://api.openweathermap.org/data/2.5/";


    @Override
    public void onCreate() {
        super.onCreate();

        new AssetDatabaseHelper(getApplicationContext()).checkDB();

        createNotificationChannel();

        // schedule notification for 15:03 every day.
        NotificationScheduler.scheduleNotification(this,NotificationReceiver.class,15,3);
    }

    private void createNotificationChannel() {

        // creating notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel("myApp",
                    getResources().getString(R.string.climate_changes),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(getResources().getString(R.string.notification_channel_description));

            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);
        }
    }


}
