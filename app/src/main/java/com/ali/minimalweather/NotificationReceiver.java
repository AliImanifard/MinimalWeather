package com.ali.minimalweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.ali.minimalweather.RetrofitModal.RetrofirModalAirPollution;
import com.ali.minimalweather.RetrofitModal.RetrofitModalCurrentWeather;

import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationReceiver extends BroadcastReceiver {

    private double latitude, longitude;
    private Disposable disposable;
    private String API_KEY;

    @Override
    public void onReceive(Context context, Intent intent) {

        // Perform the API request using Retrofit and display the result in the notification
        // You can use the NotificationCompat.Builder class to create and display the notification

        GpsTracker gpsTracker = new GpsTracker(context);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        WeatherCardUtils weatherCardUtils = new WeatherCardUtils(context);

        // getting current measurement
        PreferenceManager.setDefaultValues(context,R.xml.root_preferences,false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String currentMeasurement = sharedPreferences.getString("measurement", "");
        boolean isNotificationAllowed = sharedPreferences.getBoolean("notifications", true);

        API_KEY = context.getResources().getString(R.string.API_KEY);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyCustomApplication.strUrlOpenWeatherMapApi)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        MainActivity.MyGetJsonApi myGetJsonApi = retrofit.create(MainActivity.MyGetJsonApi.class);


        Observable<RetrofitModalCurrentWeather> currentWeatherObservable =
                myGetJsonApi.getJsonDataCurrentWeatherUsingLatLon(
                        API_KEY,
                        latitude,
                        longitude,
                        Locale.getDefault().getLanguage(),
                        currentMeasurement);

        Observable<RetrofirModalAirPollution> airPollutionObservable =
                myGetJsonApi.getJsonDataAirPollutionUsingLatLon(
                        API_KEY,
                        latitude,
                        longitude,
                        Locale.getDefault().getLanguage(),
                        currentMeasurement);

        Observable.zip(currentWeatherObservable, airPollutionObservable,
                        (cwoRespond, afoRespond) -> {

                            MyNotifications myNotifications = new MyNotifications(context);
                            if (isNotificationAllowed)
                                myNotifications.weatherNotification(
                                        MyNotifications.middayWeatherNotificationID,
                                        weatherCardUtils.setWeatherIconSVGFromWeatherId(cwoRespond.getWeatherList().get(0).getId(),
                                                weatherCardUtils.isNight(cwoRespond.getSys().getSunrise(), cwoRespond.getSys().getSunset())),

                                        cwoRespond.getCityName() + " : " + cwoRespond.getWeatherList().get(0).getMain(),

                                        context.getResources().getString(R.string.weather) + " : "
                                                + cwoRespond.getWeatherList().get(0).getDescription()
                                                + " , "
                                                + context.getResources().getString(R.string.pollution) + " : "
                                                + weatherCardUtils.aqiToConditionText(afoRespond.getAirPollutionList().get(0).getAirPollutionMain().getAqi()));


                            return "";
                        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(d -> disposable = d)
                .subscribe(
                        s -> {},    //onSubscribe is empty because the required command has been executed in the doOnSubscribe() method.
                        e -> {
                            // onError
                            
                        },
                        // onComplete
                        () -> {}

                );

    }

}

