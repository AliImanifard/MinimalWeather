package com.ali.minimalweather;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.ali.minimalweather.DataBase.City;
import com.ali.minimalweather.DataBase.CityDBHelper;
import com.ali.minimalweather.RetrofitModal.RetrofirModalAirPollution;
import com.ali.minimalweather.RetrofitModal.RetrofitModalCurrentWeather;
import com.ali.minimalweather.RetrofitModal.RetrofitModalForecast;
import com.ali.minimalweather.databinding.ActivityMainBinding;
import com.codeboy.pager2_transformers.Pager2_VerticalFlipTransformer;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import me.ibrahimsn.lib.OnItemReselectedListener;
import me.ibrahimsn.lib.OnItemSelectedListener;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity implements AddCityFragment.AddCityInterface {

    public CitiesFragment currentCitiesFragment;
    City city;
    int cityIntForRetrofit = 0;
    double cityLatForRetrofit = 0, cityLonForRetrofit = 0;
    MyPagerAdapter myPagerAdapter;
    Bundle args;
    Disposable disposable, soundEffectDisposable;
    double latUser, lonUser;
    GpsTracker gpsTracker;
    boolean isFirstRunLanguage = false;
    private ActivityMainBinding mainBinding;
    private String currentMeasurement;
    private boolean isSoundEffectEnabled = true;
    private boolean isNotificationAllowed = true;
    private boolean isTimeFormat24H;
    private int nightModeStatus;
    private Toolbar customToolbar;
    private boolean isMyPermissionsGranted = false;
    private MyNotifications myNotifications;
    private List<Fragment> fragments = new ArrayList<>();
    private Toast loadingToast;
    private String API_KEY;

    private WeatherCardUtils weatherCardUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        API_KEY = this.getResources().getString(R.string.API_KEY);


        if (customToolbar == null) {
            customToolbar = findViewById(R.id.custom_toolbar);
            if (customToolbar != null) {
                setSupportActionBar(customToolbar);
            }
        }


        checkPermissions();

        // getting the user's geographic location in real time
        gpsTracker = new GpsTracker(this);
        latUser = gpsTracker.getLatitude();
        lonUser = gpsTracker.getLongitude();

        // handling Settings Fragments (Preferences)
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentMeasurement = sharedPreferences.getString("measurement", "");
        String currentNightMode = sharedPreferences.getString("night", "system_default");
        isSoundEffectEnabled = sharedPreferences.getBoolean("sound_effect", true);
        isNotificationAllowed = sharedPreferences.getBoolean("notifications", true);
        isTimeFormat24H = sharedPreferences.getBoolean("time_format",true);

        switch (Objects.requireNonNull(currentNightMode)) {
            case "on":
                nightModeStatus = AppCompatDelegate.MODE_NIGHT_YES;
                break;

            case "off":
                nightModeStatus = AppCompatDelegate.MODE_NIGHT_NO;
                break;

            case "follow_system":
                nightModeStatus = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;
        }

        if (nightModeStatus != AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.setDefaultNightMode(nightModeStatus);
        }

        args = new Bundle();

        // initialize database
        init();

        // initialize loading weather animation to showing before retrofit response
        mainBinding.loadingWeather.setAnimation(R.raw.weather_on_phone);
        mainBinding.loadingWeather.setVisibility(View.VISIBLE);
        mainBinding.loadingWeather.playAnimation();

        // The user should not change the page during the api request.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        mainBinding.viewPager.setPageTransformer(new Pager2_VerticalFlipTransformer());     //VerticalFlip
        mainBinding.viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        // Preference for first run
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
        if (isFirstRun) {
            // The code that must be executed the first time

            isFirstRunLanguage = true;

            Log.i("myLog","city : " + city);

            if (checkPermissions())
                retrofitGetJson(0, latUser, lonUser);


            // bool => true
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstRun", false);
            editor.apply();

        } else {

            isFirstRunLanguage = false;

            if (checkPermissions())
                retrofitGetJson(cityIntForRetrofit, cityLatForRetrofit, cityLonForRetrofit);
        }


        // set ViewPager Current item when click on smooth bottom bar
        mainBinding.smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                mainBinding.viewPager.setCurrentItem(i, true);
                return false;
            }
        });
        mainBinding.smoothBottomBar.setOnItemReselectedListener(new OnItemReselectedListener() {
            @Override
            public void onItemReselect(int i) {
                mainBinding.viewPager.setCurrentItem(i, true);
            }
        });



        SharedPreferences pageIndexPref = getSharedPreferences("myPageIndexPref",MODE_PRIVATE);
        int currentPageIndex = pageIndexPref.getInt("currentPageIndex",0);
        if (currentPageIndex == 2)
            mainBinding.smoothBottomBar.setItemActiveIndex(currentPageIndex);
        else if (currentPageIndex == 0)
            mainBinding.smoothBottomBar.setItemActiveIndex(0);

        SharedPreferences.Editor editor = pageIndexPref.edit();
        editor.putInt("currentPageIndex",0);
        editor.apply();


        
        //  Change Smooth Bottom Bar when ViewPager Swiped!
        mainBinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            boolean isDragging = false;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (isDragging)
                    mainBinding.smoothBottomBar.setItemActiveIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    isDragging = true;

                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    mainBinding.loadingWeather.setVisibility(View.INVISIBLE);

                }
            }

        });

        weatherCardUtils = new WeatherCardUtils(this);
    }

    private void init() {
        // Getting city information that is shown in the database.
        CityDBHelper dbHelper = new CityDBHelper(this);
        city = dbHelper.getShownCity();
        cityIntForRetrofit = city.getCity_id();
        cityLatForRetrofit = city.getLatitude();
        cityLonForRetrofit = city.getLongitude();

        if (cityIntForRetrofit == 0) {
            if (gpsTracker.canGetLocation()) {
                cityLatForRetrofit = gpsTracker.getLatitude();
                cityLonForRetrofit = gpsTracker.getLongitude();

            } else {
                gpsTracker.showGpsAlertDialog();
            }
        }

    }

    private void initSmoothBottomBar(Bundle arguments) {

        myPagerAdapter = new MyPagerAdapter(this);
        myPagerAdapter.add(WeatherCardFragment.newInstance(arguments), "Weather");
        myPagerAdapter.add(new CitiesFragment(), "Cities");
        myPagerAdapter.add(new SettingsFragment(), "Settings");

        mainBinding.viewPager.setAdapter(myPagerAdapter);

    }


    // update display after shown button clicked in Cities Fragment
    public void updateDisplay(String functionInputType) {

        // functionInputType has two types.
        // functionInputType : shownButtonBased
        // To run when the shown button is clicked.

        // functionInputType : locationButtonBased
        // To run when the location button is clicked.

        init();
        myPagerAdapter.updateDataUsingDiffUtil(fragments);
        mainBinding.smoothBottomBar.setItemActiveIndex(0);

        if (Objects.equals(functionInputType, "shownButtonBased"))
            retrofitGetJson(cityIntForRetrofit, cityLatForRetrofit, cityLonForRetrofit);
        else if (Objects.equals(functionInputType, "locationButtonBased"))
            retrofitGetJson(0, gpsTracker.getLatitude(), gpsTracker.getLongitude());
    }

    public void retrofitGetJson(int cityId, double latitude, double longitude) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyCustomApplication.strUrlOpenWeatherMapApi)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        MyGetJsonApi myGetJsonApi = retrofit.create(MyGetJsonApi.class);


        //At the beginning of the application,
        // it should request an API based on the geographical location.

        Observable<RetrofitModalCurrentWeather> currentWeatherObservable;
        Observable<RetrofitModalForecast> forecastObservable;
        Observable<RetrofirModalAirPollution> airPollutionObservable;

        if (cityId != 0) {  // The request should be submitted based on the city ID that has the indicated shown in the database.

            currentWeatherObservable = myGetJsonApi.getJsonDataCurrentWeatherUsingCityId(API_KEY,cityId, Locale.getDefault().getLanguage(), currentMeasurement);
            forecastObservable = myGetJsonApi.getJsonDataForecastUsingCityId(API_KEY,cityId, Locale.getDefault().getLanguage(), currentMeasurement);
            airPollutionObservable = myGetJsonApi.getJsonDataAirPollutionUsingLatLon(API_KEY,cityLatForRetrofit, cityLonForRetrofit, Locale.getDefault().getLanguage(), currentMeasurement);

        } else {    // The request should be sent based on the user's current location.

            currentWeatherObservable = myGetJsonApi.getJsonDataCurrentWeatherUsingLatLon(API_KEY,latitude, longitude, Locale.getDefault().getLanguage(), currentMeasurement);
            forecastObservable = myGetJsonApi.getJsonDataForecastUsingLatLon(API_KEY,latitude, longitude, Locale.getDefault().getLanguage(), currentMeasurement);
            airPollutionObservable = myGetJsonApi.getJsonDataAirPollutionUsingLatLon(API_KEY,latitude, longitude, Locale.getDefault().getLanguage(), currentMeasurement);

        }
        Observable.zip(currentWeatherObservable, forecastObservable, airPollutionObservable,
                        (respond1, respond2, respond3) -> {

                            args.putInt("cityId", respond1.getCityId());
                            args.putString("cityName", respond1.getCityName());
                            args.putDouble("temperature", respond1.getMain().getTemp());
                            args.putString("weatherTitle", respond1.getWeatherList().get(0).getMain());
                            args.putString("weatherDescription", respond1.getWeatherList().get(0).getDescription());
                            args.putInt("weatherIconDrawableID",
                                    weatherCardUtils.setWeatherIconSVGFromWeatherId(
                                            respond1.getWeatherList().get(0).getId(),
                                            weatherCardUtils.isNight(respond1.getSys().getSunrise(), respond1.getSys().getSunset())
                                    ));

                            double[] tempArrayForecast = new double[respond2.getForecastList().size()];
                            int[] weatherIconDrawableIDArrayForecast = new int[respond2.getForecastList().size()];
                            double[] pops = new double[respond2.getForecastList().size()];
                            int[] humidities = new int[respond2.getForecastList().size()];
                            String[] dt_txtArray = new String[respond2.getForecastList().size()];
                            double[] tempMin = new double[respond2.getForecastList().size()];
                            double[] tempMax = new double[respond2.getForecastList().size()];

                            for (int i = 0; i < respond2.getForecastList().size(); i++) {
                                tempArrayForecast[i] = respond2.getForecastList().get(i).getMain().getTemp();
                                weatherIconDrawableIDArrayForecast[i] = respond2.getForecastList().get(i).getWeatherList().get(0).getId();
                                pops[i] = respond2.getForecastList().get(i).getPop();
                                humidities[i] = respond2.getForecastList().get(i).getMain().getHumidity();
                                dt_txtArray[i] = respond2.getForecastList().get(i).getDt_txt();
                                tempMin[i] = respond2.getForecastList().get(i).getMain().getTemp_min();
                                tempMax[i] = respond2.getForecastList().get(i).getMain().getTemp_max();
                            }


                            // put args for forecast daily
                            args.putDoubleArray("temperatureForecast", tempArrayForecast);
                            //handle weather icon drawables
                            int[] handledWeatherIcons = weatherCardUtils.handleWeatherIconDrawableForecast(weatherIconDrawableIDArrayForecast);
                            int weatherIconDrawableIDIntForecast1DayAfterToday = handledWeatherIcons[0];
                            int weatherIconDrawableIDIntForecast2DaysAfterToday = handledWeatherIcons[1];
                            int weatherIconDrawableIDIntForecast3DaysAfterToday = handledWeatherIcons[2];
                            int weatherIconDrawableIDIntForecast4DaysAfterToday = handledWeatherIcons[3];
                            int weatherIconDrawableIDIntForecast5DaysAfterToday = handledWeatherIcons[4];


                            args.putInt("weatherIconDrawableIDForecast1DayAfterToday",
                                    weatherCardUtils.setWeatherIconSVGFromWeatherId(
                                            weatherIconDrawableIDIntForecast1DayAfterToday,
                                            weatherCardUtils.isNight(respond1.getSys().getSunrise(), respond1.getSys().getSunset())
                                    ));
                            args.putInt("weatherIconDrawableIDForecast2DaysAfterToday",
                                    weatherCardUtils.setWeatherIconSVGFromWeatherId(
                                            weatherIconDrawableIDIntForecast2DaysAfterToday,
                                            weatherCardUtils.isNight(respond1.getSys().getSunrise(), respond1.getSys().getSunset())
                                    ));
                            args.putInt("weatherIconDrawableIDForecast3DaysAfterToday",
                                    weatherCardUtils.setWeatherIconSVGFromWeatherId(
                                            weatherIconDrawableIDIntForecast3DaysAfterToday,
                                            weatherCardUtils.isNight(respond1.getSys().getSunrise(), respond1.getSys().getSunset())
                                    ));
                            args.putInt("weatherIconDrawableIDForecast4DaysAfterToday",
                                    weatherCardUtils.setWeatherIconSVGFromWeatherId(
                                            weatherIconDrawableIDIntForecast4DaysAfterToday,
                                            weatherCardUtils.isNight(respond1.getSys().getSunrise(), respond1.getSys().getSunset())
                                    ));
                            args.putInt("weatherIconDrawableIDForecast5DaysAfterToday",
                                    weatherCardUtils.setWeatherIconSVGFromWeatherId(
                                            weatherIconDrawableIDIntForecast5DaysAfterToday,
                                            weatherCardUtils.isNight(respond1.getSys().getSunrise(), respond1.getSys().getSunset())
                                    ));


                            // put remaining args for next few hour forecast
                            args.putDoubleArray("pops", pops);
                            args.putIntArray("humidities", humidities);

                            int[] handledWeatherIconsForNextHours = weatherCardUtils.handleWeatherIconDrawableNextHoursForecast(weatherIconDrawableIDArrayForecast);
                            int weatherIconDrawable3h = handledWeatherIconsForNextHours[1];
                            int weatherIconDrawable6h = handledWeatherIconsForNextHours[2];
                            int weatherIconDrawable9h = handledWeatherIconsForNextHours[3];
                            int weatherIconDrawable12h = handledWeatherIconsForNextHours[4];
                            int weatherIconDrawable15h = handledWeatherIconsForNextHours[5];
                            int weatherIconDrawable18h = handledWeatherIconsForNextHours[6];
                            int weatherIconDrawable21h = handledWeatherIconsForNextHours[7];


                            String[] handledStringArrayTimeFormatFromDt_txt = weatherCardUtils.handleTimeFormatFromDt_txt(dt_txtArray);
                            args.putStringArray("dt_txt", handledStringArrayTimeFormatFromDt_txt);


                            args.putInt("weatherIconDrawable3h",
                                    weatherCardUtils.setWeatherIconSVGFromWeatherId(
                                            weatherIconDrawable3h,
                                            weatherCardUtils.isNight(handledStringArrayTimeFormatFromDt_txt[1])
                                    ));

                            args.putInt("weatherIconDrawable6h",
                                    weatherCardUtils.setWeatherIconSVGFromWeatherId(
                                            weatherIconDrawable6h,
                                            weatherCardUtils.isNight(handledStringArrayTimeFormatFromDt_txt[2])
                                    ));

                            args.putInt("weatherIconDrawable9h",
                                    weatherCardUtils.setWeatherIconSVGFromWeatherId(
                                            weatherIconDrawable9h,
                                            weatherCardUtils.isNight(handledStringArrayTimeFormatFromDt_txt[3])
                                    ));

                            args.putInt("weatherIconDrawable12h",
                                    weatherCardUtils.setWeatherIconSVGFromWeatherId(
                                            weatherIconDrawable12h,
                                            weatherCardUtils.isNight(handledStringArrayTimeFormatFromDt_txt[4])
                                    ));

                            args.putInt("weatherIconDrawable15h",
                                    weatherCardUtils.setWeatherIconSVGFromWeatherId(
                                            weatherIconDrawable15h,
                                            weatherCardUtils.isNight(handledStringArrayTimeFormatFromDt_txt[5])
                                    ));

                            args.putInt("weatherIconDrawable18h",
                                    weatherCardUtils.setWeatherIconSVGFromWeatherId(
                                            weatherIconDrawable18h,
                                            weatherCardUtils.isNight(handledStringArrayTimeFormatFromDt_txt[6])
                                    ));

                            args.putInt("weatherIconDrawable21h",
                                    weatherCardUtils.setWeatherIconSVGFromWeatherId(
                                            weatherIconDrawable21h,
                                            weatherCardUtils.isNight(handledStringArrayTimeFormatFromDt_txt[7])
                                    ));


                            args.putDoubleArray("temp_min", tempMin);
                            args.putDoubleArray("temp_max", tempMax);


                            // Handle Air Pollution
                            args.putInt("aqi", respond3.getAirPollutionList().get(0).getAirPollutionMain().getAqi());


                            // When the weather becomes critical in the future, it will be notified.
                            if (isNotificationAllowed)
                                for (int i = 0; i < respond2.getForecastList().size(); i++) {

                                    switch (respond2.getForecastList().get(i).getWeatherList().get(0).getId()) {
                                        //thunderstorm
                                        case 200:
                                        case 201:
                                        case 202:
                                        case 210:
                                        case 211:
                                        case 212:
                                        case 221:
                                        case 230:
                                        case 231:
                                        case 232:
                                            // drizzle (shower rain)
                                        case 300:
                                        case 301:
                                        case 302:
                                        case 310:
                                        case 311:
                                        case 312:
                                        case 313:
                                        case 314:
                                        case 321:
                                        case 520:
                                        case 521:
                                        case 522:
                                        case 531:
                                            //rain
                                        case 500:
                                        case 501:
                                        case 502:
                                        case 503:
                                        case 504:
                                            //snow
                                        case 511:
                                        case 600:
                                        case 601:
                                        case 602:
                                        case 611:
                                        case 612:
                                        case 613:
                                        case 615:
                                        case 616:
                                        case 620:
                                        case 621:
                                        case 622:
                                            myNotifications = new MyNotifications(this);
                                            myNotifications
                                                    .weatherNotification(
                                                            MyNotifications.criticalChangeWeatherNotificationID,
                                                            weatherCardUtils.setWeatherIconSVGFromWeatherId(
                                                                    respond2.getForecastList().get(i).getWeatherList().get(0).getId(),
                                                                    weatherCardUtils.isNight(respond1.getSys().getSunrise(), respond1.getSys().getSunset())
                                                            ),

                                                            getResources().getString(R.string.weather_emergency) +
                                                                    respond2.getForecastList().get(i).getWeatherList().get(0).getMain(),

                                                            getResources().getString(R.string.weather_emergency_description_part_one) +
                                                                    respond2.getForecastList().get(i).getWeatherList().get(0).getDescription() +
                                                                    getResources().getString(R.string.weather_emergency_description_part_two)
                                                    );

                                            break;
                                        default:
                                            break;
                                    }

                                }


                            // Handle sound effects
                            switch (respond1.getWeatherList().get(0).getId()) {
                                //thunderstorm
                                case 200:
                                case 201:
                                case 202:
                                case 210:
                                case 211:
                                case 212:
                                case 221:
                                case 230:
                                case 231:
                                case 232:
                                    playAppropriateSoundEffect(R.raw.thunderstorm);
                                    break;
                                // drizzle (shower rain)
                                case 300:
                                case 301:
                                case 302:
                                case 310:
                                case 311:
                                case 312:
                                case 313:
                                case 314:
                                case 321:
                                case 520:
                                case 521:
                                case 522:
                                case 531:
                                    playAppropriateSoundEffect(R.raw.drizzle);
                                    break;
                                //rain
                                case 500:
                                case 501:
                                case 502:
                                case 503:
                                case 504:
                                    playAppropriateSoundEffect(R.raw.rain);
                                    break;
                                //snow
                                case 511:
                                case 600:
                                case 601:
                                case 602:
                                case 611:
                                case 612:
                                case 613:
                                case 615:
                                case 616:
                                case 620:
                                case 621:
                                case 622:
                                    playAppropriateSoundEffect(R.raw.snow);
                                    break;
                                //cloudy (just cloud)
                                case 802:
                                case 803:
                                case 804:
                                    playAppropriateSoundEffect(R.raw.wind);
                                    break;
                                default:
                                    break;
                            }


                            return "";
                        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(d -> disposable = d)
                .subscribe(
                        s -> {},    //onSubscribe is empty because the required command has been executed in the doOnSubscribe() method.
                        e -> {
                            // onError
                            mainBinding.loadingWeather.setVisibility(View.INVISIBLE);
                            new NetworkErrorDialog(MainActivity.this).show();
                            Toast.makeText(MainActivity.this, R.string.internet_connection_could_not_be_established, Toast.LENGTH_SHORT).show();
                        },
                        () -> {
                            // onCompleted
                            if (loadingToast != null && loadingToast.getView().getWindowVisibility() == View.VISIBLE)
                                loadingToast.cancel();
                            initSmoothBottomBar(args);
                            mainBinding.loadingWeather.setVisibility(View.INVISIBLE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                );


    }

    public interface MyGetJsonApi {
        @GET(MyCustomApplication.APP_ID_CURRENT_WEATHER)
        Observable<RetrofitModalCurrentWeather> getJsonDataCurrentWeatherUsingCityId(@Query("appid") String apiKey, @Query("id") int cityId, @Query("lang") String language, @Query("units") String measurement);

        @GET(MyCustomApplication.APP_ID_CURRENT_WEATHER)
        Observable<RetrofitModalCurrentWeather> getJsonDataCurrentWeatherUsingLatLon(@Query("appid") String apiKey, @Query("lat") double citylat, @Query("lon") double citylon, @Query("lang") String language, @Query("units") String measurement);


        @GET(MyCustomApplication.APP_ID_FORECAST)
        Observable<RetrofitModalForecast> getJsonDataForecastUsingCityId(@Query("appid") String apiKey, @Query("id") int cityId, @Query("lang") String language, @Query("units") String measurement);

        @GET(MyCustomApplication.APP_ID_FORECAST)
        Observable<RetrofitModalForecast> getJsonDataForecastUsingLatLon(@Query("appid") String apiKey, @Query("lat") double citylat, @Query("lon") double citylon, @Query("lang") String language, @Query("units") String measurement);


        @GET(MyCustomApplication.APP_ID_AIR_POLLUTION)
        Observable<RetrofirModalAirPollution> getJsonDataAirPollutionUsingLatLon(@Query("appid") String apiKey, @Query("lat") double citylat, @Query("lon") double citylon, @Query("lang") String language, @Query("units") String measurement);

    }


    @Override
    public void addCity(int id) {
        try {
            currentCitiesFragment.displayReceivedData(id);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private boolean checkPermissions() {


        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        // this method is called when all permissions are granted
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {

                            isMyPermissionsGranted = true;
                        }
                        // check for permanent denial of any permission
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {


                            new AlertDialog.Builder(MainActivity.this)
                                    .setCancelable(false)
                                    .setTitle(R.string.permissions_are_not_granted)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setMessage(R.string.please_grant_the_necessary_permissions_in_the_application_settings_otherwise_you_can_close_the_app)
                                    .setPositiveButton(R.string.grant_permissions, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(
                                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                    Uri.fromParts("package", getPackageName(), null)
                                            );
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .setPositiveButtonIcon(ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_menu_send, null))
                                    .setNegativeButton(R.string.close_app, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .setNegativeButtonIcon(ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_delete, null))
                                    .show();


                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        // this method is called when user grants some
                        // permission and denies some of them.
                        permissionToken.continuePermissionRequest();
                    }
                })
                // this method is use to handle error
                // in runtime permissions
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError dexterError) {
                        Toast.makeText(MainActivity.this,
                                R.string.error_occurred,
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread().check();

        return isMyPermissionsGranted;


    }

    public void playAppropriateSoundEffect(int soundEffectRawResourceFile) {

        if (isSoundEffectEnabled) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, soundEffectRawResourceFile);

            float volume = 0.15f;
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();


            soundEffectDisposable = Observable.interval(5, TimeUnit.SECONDS)
                    .take(1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        // Stop the media player after five seconds
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    });

        }

    }

    public void setSoundEffectEnabled(boolean soundEffectEnabled) {
        isSoundEffectEnabled = soundEffectEnabled;
    }

    public void restartApp() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        MainActivity.this.finish();
    }

    public void setNotificationAllowed(boolean notificationAllowed) {
        isNotificationAllowed = notificationAllowed;
    }

    public void setTimeFormat24H(boolean timeFormat24H) {
        isTimeFormat24H = timeFormat24H;
    }

    public void showLoadingToast() {
        loadingToast = new Toast(MainActivity.this);
        loadingToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        loadingToast.setView(getLayoutInflater()
                .inflate(R.layout.toast_loading,
                        MainActivity.this.findViewById(R.id.toast_loading)));
        loadingToast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null)
            disposable.dispose();

        if (soundEffectDisposable != null)
            soundEffectDisposable.dispose();


    }

    private class MyPagerAdapter extends FragmentStateAdapter {


        public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }

        public void add(Fragment fragment, String title) {
            fragments.add(fragment);
        }

        public void updateDataUsingDiffUtil(List<Fragment> newData) {
            DiffUtil.Callback callback = new MyDiffUtil<>(fragments, newData);
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
            fragments = newData;
            result.dispatchUpdatesTo(this);
        }
    }


}