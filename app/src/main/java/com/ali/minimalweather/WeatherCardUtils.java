package com.ali.minimalweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class WeatherCardUtils {

    private final String currentMeasurement;
    private final boolean isTimeFormat24H;
    private final Context context;



    public WeatherCardUtils(Context context) {
        this.context = context;

        // handling Settings Fragments (Preferences)
        PreferenceManager.setDefaultValues(context, R.xml.root_preferences, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        currentMeasurement = sharedPreferences.getString("measurement", "");
        isTimeFormat24H = sharedPreferences.getBoolean("time_format", true);
    }

    public int setWeatherIconSVGFromWeatherId(int weatherId, boolean isNight) {

        switch (weatherId) {

            //clear
            case 800:
                return isNight ? R.drawable.weather_icon_clear_night : R.drawable.weather_icon_clear_day;


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
                return isNight ? R.drawable.weather_icon_thunderstorm_night : R.drawable.weather_icon_thunderstorm_day;

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
                return isNight ? R.drawable.weather_icon_shower_rain_night : R.drawable.weather_icon_shower_rain_day;

            //rain
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
                return isNight ? R.drawable.weather_icon_rain_night : R.drawable.weather_icon_rain_day;


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
                return isNight ? R.drawable.weather_icon_snow_night : R.drawable.weather_icon_snow_day;

            //atmosphere
            case 701:
            case 711:
            case 721:
            case 731:
            case 741:
            case 751:
            case 761:
            case 762:
            case 771:
            case 781:
                return R.drawable.weather_icon_windy;


            //cloudy (sun)
            case 801:
                return isNight ? R.drawable.weather_icon_clouds_moon : R.drawable.weather_icon_clouds_sun;

            //cloudy (just cloud)
            case 802:
            case 803:
            case 804:
                return R.drawable.weather_icon_clouds_scattered;
            default:
                break;
        }

        return 0;

    }

    public boolean isNight(long sunrise, long sunset) {

        //getting current time
        long currentTime = new Date().getTime() / 1000;
        return currentTime < sunrise || currentTime >= sunset;
    }

    public boolean isNight(String time) throws ParseException {

        SimpleDateFormat sdf24H = new SimpleDateFormat("HH:mm", Locale.getDefault()),
                sdf12H = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date timeDate, nightTimePart1, nightTimePart2;

        timeDate = isTimeFormat24H ? sdf24H.parse(time) : sdf24H.parse(sdf24H.format(Objects.requireNonNull(sdf12H.parse(time))));
        nightTimePart1 = sdf24H.parse("06:00");
        nightTimePart2 = sdf24H.parse("18:00");


        return timeDate != null && (timeDate.before(nightTimePart1) || timeDate.after(nightTimePart2));
    }

    public String aqiToConditionText(int aqiInt) {

        switch (aqiInt) {
            case 1:
                return context.getResources().getString(R.string.pollution_good);

            case 2:
                return context.getResources().getString(R.string.pollution_fair);

            case 3:
                return context.getResources().getString(R.string.pollution_moderate);

            case 4:
                return context.getResources().getString(R.string.pollution_poor);

            case 5:
                return context.getResources().getString(R.string.pollution_very_poor);

            default:
                return "";
        }

    }

    public int[] handleWeatherIconDrawableNextHoursForecast(int[] weatherIconDrawableIDArrayForecast) {
        int[] weatherIconDrawableIDArrayForecastNextHours = new int[8];
        System.arraycopy(weatherIconDrawableIDArrayForecast, 0, weatherIconDrawableIDArrayForecastNextHours, 0, 8);
        return weatherIconDrawableIDArrayForecastNextHours;
    }

    //handle Weather Icon Drawable Forecast
    public int[] handleWeatherIconDrawableForecast(int[] weatherIconDrawableIDArrayForecast) {

        int[] weatherIconDrawableIDArrayForecast1DayAfterToday = new int[8],
                weatherIconDrawableIDArrayForecast2DaysAfterToday = new int[8],
                weatherIconDrawableIDArrayForecast3DaysAfterToday = new int[8],
                weatherIconDrawableIDArrayForecast4DaysAfterToday = new int[8],
                weatherIconDrawableIDArrayForecast5DaysAfterToday = new int[8];

        int weatherIconDrawableIDIntForecast1DayAfterToday,
                weatherIconDrawableIDIntForecast2DaysAfterToday,
                weatherIconDrawableIDIntForecast3DaysAfterToday,
                weatherIconDrawableIDIntForecast4DaysAfterToday,
                weatherIconDrawableIDIntForecast5DaysAfterToday;


        // divide weatherIconDrawableIDArrayForecast int array[40] to 5 parts (5 days) (every day has 8 icons for weather)
        System.arraycopy(weatherIconDrawableIDArrayForecast, 0, weatherIconDrawableIDArrayForecast1DayAfterToday, 0, 8);
        System.arraycopy(weatherIconDrawableIDArrayForecast, 8, weatherIconDrawableIDArrayForecast2DaysAfterToday, 0, 8);
        System.arraycopy(weatherIconDrawableIDArrayForecast, 16, weatherIconDrawableIDArrayForecast3DaysAfterToday, 0, 8);
        System.arraycopy(weatherIconDrawableIDArrayForecast, 24, weatherIconDrawableIDArrayForecast4DaysAfterToday, 0, 8);
        System.arraycopy(weatherIconDrawableIDArrayForecast, 32, weatherIconDrawableIDArrayForecast5DaysAfterToday, 0, 8);

        // choose between weather icon drawables forecast for a day from an int[8]
        weatherIconDrawableIDIntForecast1DayAfterToday =
                findElementWithMaximumFrequencyInAnIntArray(weatherIconDrawableIDArrayForecast1DayAfterToday);
        weatherIconDrawableIDIntForecast2DaysAfterToday =
                findElementWithMaximumFrequencyInAnIntArray(weatherIconDrawableIDArrayForecast2DaysAfterToday);
        weatherIconDrawableIDIntForecast3DaysAfterToday =
                findElementWithMaximumFrequencyInAnIntArray(weatherIconDrawableIDArrayForecast3DaysAfterToday);
        weatherIconDrawableIDIntForecast4DaysAfterToday =
                findElementWithMaximumFrequencyInAnIntArray(weatherIconDrawableIDArrayForecast4DaysAfterToday);
        weatherIconDrawableIDIntForecast5DaysAfterToday =
                findElementWithMaximumFrequencyInAnIntArray(weatherIconDrawableIDArrayForecast5DaysAfterToday);


        return new int[]{weatherIconDrawableIDIntForecast1DayAfterToday,
                weatherIconDrawableIDIntForecast2DaysAfterToday,
                weatherIconDrawableIDIntForecast3DaysAfterToday,
                weatherIconDrawableIDIntForecast4DaysAfterToday,
                weatherIconDrawableIDIntForecast5DaysAfterToday};

    }

    //find the element that is repeated the most in weather icon drawable id array
    public int findElementWithMaximumFrequencyInAnIntArray(int[] ints) {
        int result;

        // Create a HashMap to store the frequency of each element
        Map<Integer, Integer> freq = new HashMap<>();

        // Update the frequency count for each element in the array
        for (int key : ints) {
            if (freq.containsKey(key)) {
                int value = freq.get(key);
                freq.put(key, value + 1);
            } else {
                freq.put(key, 1);
            }
        }

        // Find the element with the maximum frequency in the HashMap
        int maxFreq = 0;
        result = ints[ints.length / 2];  // Middlemost element
        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();

            if (value > maxFreq) {
                maxFreq = value;
                result = key;
            }
        }

        return result;
    }

    public String[] handleTimeFormatFromDt_txt(String[] dt_txtArray) throws ParseException {

        String[] dt_txtNextHours = new String[8];
        System.arraycopy(dt_txtArray, 0, dt_txtNextHours, 0, 8);

        String[] timeString = new String[dt_txtNextHours.length];

        for (int i = 0; i < dt_txtNextHours.length; i++) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(dt_txtNextHours[i]);

            SimpleDateFormat outputFormat;

            if (isTimeFormat24H)
                outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            else
                outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

            timeString[i] = outputFormat.format(Objects.requireNonNull(date));
        }

        return timeString;

    }

    public String measurementFormat() {

        switch (currentMeasurement) {
            case "metric":
                // Celsius degree
                return "\u2103";

            case "imperial":
                // Fahrenheit degree
                return "\u2109";

            case "standard":
                // Kelvin degree
                return "\u212A";

            default:
                return "";
        }
    }

    @ColorInt
    public int chooseRightColorForTemperatureTextColor(double temperature) {

        TypedValue typedValue = new TypedValue();
        context.getTheme()
                .resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);

        @ColorInt int rightColor = typedValue.data;

        int temperatureThreshold1 = 0, temperatureThreshold2 = 0, temperatureThreshold3 = 0;

        switch (currentMeasurement) {
            case "metric":

                temperatureThreshold1 = 10;
                temperatureThreshold2 = 20;
                temperatureThreshold3 = 30;

                break;


            case "imperial":

                temperatureThreshold1 = 50;
                temperatureThreshold2 = 68;
                temperatureThreshold3 = 86;

                break;


            case "standard":

                temperatureThreshold1 = 283;
                temperatureThreshold2 = 293;
                temperatureThreshold3 = 303;

                break;

            default:
                break;
        }

        if (temperature < temperatureThreshold1)
            rightColor = Color.rgb(27, 109, 242);
        else if (temperature >= temperatureThreshold1 && temperature < temperatureThreshold2)
            rightColor = Color.rgb(28, 215, 232);
        else if (temperature >= temperatureThreshold2 && temperature < temperatureThreshold3)
            rightColor = Color.rgb(232, 195, 28);
        else if (temperature >= temperatureThreshold3)
            rightColor = Color.rgb(247, 88, 67);

        return rightColor;
    }

    public double averageDoubleArray(double[] doubles) {
        double averagedDouble = 0.0;

        for (double aDouble : doubles) {
            averagedDouble += aDouble;
        }
        averagedDouble /= doubles.length;
        return averagedDouble;
    }



}
