package com.ali.minimalweather;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.ali.minimalweather.databinding.FragmentWeatherCardBinding;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class WeatherCardFragment extends Fragment {

    public static final String TAG = "WeatherCardFragment";
    private final double[] temperatureArrayForecast1DayAfterToday = new double[8];
    private final double[] temperatureArrayForecast2DaysAfterToday = new double[8];
    private final double[] temperatureArrayForecast3DaysAfterToday = new double[8];
    private final double[] temperatureArrayForecast4DaysAfterToday = new double[8];
    private final double[] temperatureArrayForecast5DaysAfterToday = new double[8];
    private final String[] weekdayArray = new String[5];
    private final double[] popTomorrow = new double[8];
    private final int[] humiditiesTomorrow = new int[8];
    private final double[] tempMin = new double[8];
    private final double[] tempMax = new double[8];
    protected MainActivity mainActivity;
    protected Context context;
    private int cityId = 0;
    private String cityName = "";
    // variables for current weather
    private double temperature = 0.0;
    private String weatherTitle = "";
    private String weatherDescription = "";
    private int weatherIconDrawableID = 0;
    // variables for forecast daily
    private double[] temperatureArrayForecast = new double[40];
    private int weatherIconDrawableIDArrayForecast1DayAfterToday = 0;
    private int weatherIconDrawableIDArrayForecast2DaysAfterToday = 0;
    private int weatherIconDrawableIDArrayForecast3DaysAfterToday = 0;
    private int weatherIconDrawableIDArrayForecast4DaysAfterToday = 0;
    private int weatherIconDrawableIDArrayForecast5DaysAfterToday = 0;
    private double temperatureDoubleForecast1DayAfterToday = 0.0,
            temperatureDoubleForecast2DaysAfterToday = 0.0,
            temperatureDoubleForecast3DaysAfterToday = 0.0,
            temperatureDoubleForecast4DaysAfterToday = 0.0,
            temperatureDoubleForecast5DaysAfterToday = 0.0;
    // variables for next few hours forecast
    private double[] pops = new double[40];
    private int[] humidities = new int[40];
    private int weatherIconDrawable3h = 0,
            weatherIconDrawable6h = 0,
            weatherIconDrawable9h = 0,
            weatherIconDrawable12h = 0,
            weatherIconDrawable15h = 0,
            weatherIconDrawable18h = 0,
            weatherIconDrawable21h = 0;
    private String[] dt_txt = new String[8];
    private double[] tempMins = new double[40];
    private double[] tempMaxs = new double[40];
    // variable for Air Pollution
    private int aqi;
    private FragmentWeatherCardBinding binding;

    private WeatherCardUtils weatherCardUtils;


    public WeatherCardFragment() {
        // Required empty public constructor
    }

    public static WeatherCardFragment newInstance(Bundle args) {
        WeatherCardFragment fragment = new WeatherCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weatherCardUtils = new WeatherCardUtils(mainActivity);

        if (getArguments() != null) {
            cityId = getArguments().getInt("cityId");
            cityName = getArguments().getString("cityName");
            temperature = getArguments().getDouble("temperature");
            weatherTitle = getArguments().getString("weatherTitle");
            weatherDescription = getArguments().getString("weatherDescription");
            weatherIconDrawableID = getArguments().getInt("weatherIconDrawableID");

            temperatureArrayForecast = getArguments().getDoubleArray("temperatureForecast");
            weatherIconDrawableIDArrayForecast1DayAfterToday = getArguments().getInt("weatherIconDrawableIDForecast1DayAfterToday");
            weatherIconDrawableIDArrayForecast2DaysAfterToday = getArguments().getInt("weatherIconDrawableIDForecast2DaysAfterToday");
            weatherIconDrawableIDArrayForecast3DaysAfterToday = getArguments().getInt("weatherIconDrawableIDForecast3DaysAfterToday");
            weatherIconDrawableIDArrayForecast4DaysAfterToday = getArguments().getInt("weatherIconDrawableIDForecast4DaysAfterToday");
            weatherIconDrawableIDArrayForecast5DaysAfterToday = getArguments().getInt("weatherIconDrawableIDForecast5DaysAfterToday");

            // divide temperatureArrayForecast double array[40] to 5 parts (5 days) (every day has 8 temperature)
            System.arraycopy(temperatureArrayForecast, 0, temperatureArrayForecast1DayAfterToday, 0, 8);
            System.arraycopy(temperatureArrayForecast, 8, temperatureArrayForecast2DaysAfterToday, 0, 8);
            System.arraycopy(temperatureArrayForecast, 16, temperatureArrayForecast3DaysAfterToday, 0, 8);
            System.arraycopy(temperatureArrayForecast, 24, temperatureArrayForecast4DaysAfterToday, 0, 8);
            System.arraycopy(temperatureArrayForecast, 32, temperatureArrayForecast5DaysAfterToday, 0, 8);

            // averaging 8 tempratures to get an averaged temp for everyday in these 5 days
            temperatureDoubleForecast1DayAfterToday = weatherCardUtils.averageDoubleArray(temperatureArrayForecast1DayAfterToday);
            temperatureDoubleForecast2DaysAfterToday = weatherCardUtils.averageDoubleArray(temperatureArrayForecast2DaysAfterToday);
            temperatureDoubleForecast3DaysAfterToday = weatherCardUtils.averageDoubleArray(temperatureArrayForecast3DaysAfterToday);
            temperatureDoubleForecast4DaysAfterToday = weatherCardUtils.averageDoubleArray(temperatureArrayForecast4DaysAfterToday);
            temperatureDoubleForecast5DaysAfterToday = weatherCardUtils.averageDoubleArray(temperatureArrayForecast5DaysAfterToday);

            // get the names of the next 5 days
            // i.e. weekdayArray = {wed,thu,fri,sat,sun}
            SimpleDateFormat sdf = new SimpleDateFormat("EE", Locale.getDefault());
            for (int i = 0; i < 5; i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, i + 1);
                String day = sdf.format(calendar.getTime());
                weekdayArray[i] = day;
            }


            // Handle next few hours forecast
            // temperature handled.
            // handle pop and humidity for next few hours
            pops = getArguments().getDoubleArray("pops");
            humidities = getArguments().getIntArray("humidities");
            System.arraycopy(pops, 0, popTomorrow, 0, 8);
            System.arraycopy(humidities, 0, humiditiesTomorrow, 0, 8);

            // handle weather icon for next few hours
            weatherIconDrawable3h = getArguments().getInt("weatherIconDrawable3h");
            weatherIconDrawable6h = getArguments().getInt("weatherIconDrawable6h");
            weatherIconDrawable9h = getArguments().getInt("weatherIconDrawable9h");
            weatherIconDrawable12h = getArguments().getInt("weatherIconDrawable12h");
            weatherIconDrawable15h = getArguments().getInt("weatherIconDrawable15h");
            weatherIconDrawable18h = getArguments().getInt("weatherIconDrawable18h");
            weatherIconDrawable21h = getArguments().getInt("weatherIconDrawable21h");


            dt_txt = getArguments().getStringArray("dt_txt");


            // handle temperature min and Max for next few hours
            tempMins = getArguments().getDoubleArray("temp_min");
            tempMaxs = getArguments().getDoubleArray("temp_max");

            System.arraycopy(tempMins, 0, tempMin, 0, 8);
            System.arraycopy(tempMaxs, 0, tempMax, 0, 8);

            // handle Air Quality Index
            aqi = getArguments().getInt("aqi");

        }

        mainActivity = (MainActivity) getActivity();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentWeatherCardBinding.inflate(inflater, container, false);


        // Calculate the total height of the child views in the FrameLayout
        int totalHeight = 0;
        for (int i = 0; i < binding.FrameLayoutWeatherCardFragment.getChildCount(); i++)
            totalHeight += binding.FrameLayoutWeatherCardFragment.getChildAt(i).getHeight();

        // Enable or disable the scrollbar based on the total height
        binding.scrollViewWeatherCardFragment
                .setVerticalScrollBarEnabled(
                        totalHeight > binding.scrollViewWeatherCardFragment.getHeight()
                );


        binding.tvCity.setText(cityName);
        binding.tvTemp.setText(Math.round(temperature) + weatherCardUtils.measurementFormat());

        //binding.tvTemp.setTextColor(chooseRightColorForTemperatureTextColor());
        binding.tvWeatherTitle.setText(weatherTitle);
        binding.tvWeatherDescription.setText(weatherDescription);
        if (weatherIconDrawableID != 0)
            binding.ivWeatherIconCurrentWeather.setImageDrawable
                    (ResourcesCompat.getDrawable(getResources(), weatherIconDrawableID, null));

        // set Forecast data to its views
        binding.cvForecast1DayAfterToday.tvTempForecast
                .setText(new DecimalFormat("#.#")
                        .format(temperatureDoubleForecast1DayAfterToday) + " " + weatherCardUtils.measurementFormat());
        binding.cvForecast2DaysAfterToday.tvTempForecast
                .setText(new DecimalFormat("#.#")
                        .format(temperatureDoubleForecast2DaysAfterToday) + " " + weatherCardUtils.measurementFormat());
        binding.cvForecast3DaysAfterToday.tvTempForecast
                .setText(new DecimalFormat("#.#")
                        .format(temperatureDoubleForecast3DaysAfterToday) + " " + weatherCardUtils.measurementFormat());
        binding.cvForecast4DaysAfterToday.tvTempForecast
                .setText(new DecimalFormat("#.#")
                        .format(temperatureDoubleForecast4DaysAfterToday) + " " + weatherCardUtils.measurementFormat());
        binding.cvForecast5DaysAfterToday.tvTempForecast
                .setText(new DecimalFormat("#.#")
                        .format(temperatureDoubleForecast5DaysAfterToday) + " " + weatherCardUtils.measurementFormat());


        if (weatherIconDrawableIDArrayForecast1DayAfterToday != 0)
            binding.cvForecast1DayAfterToday.ivWeatherIconForecast.setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(),
                            weatherIconDrawableIDArrayForecast1DayAfterToday,
                            null));

        if (weatherIconDrawableIDArrayForecast2DaysAfterToday != 0)
            binding.cvForecast2DaysAfterToday.ivWeatherIconForecast.setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(),
                            weatherIconDrawableIDArrayForecast2DaysAfterToday,
                            null));

        if (weatherIconDrawableIDArrayForecast3DaysAfterToday != 0)
            binding.cvForecast3DaysAfterToday.ivWeatherIconForecast.setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(),
                            weatherIconDrawableIDArrayForecast3DaysAfterToday,
                            null));

        if (weatherIconDrawableIDArrayForecast4DaysAfterToday != 0)
            binding.cvForecast4DaysAfterToday.ivWeatherIconForecast.setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(),
                            weatherIconDrawableIDArrayForecast4DaysAfterToday,
                            null));

        if (weatherIconDrawableIDArrayForecast5DaysAfterToday != 0)
            binding.cvForecast5DaysAfterToday.ivWeatherIconForecast.setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(),
                            weatherIconDrawableIDArrayForecast5DaysAfterToday,
                            null));

        binding.cvForecast1DayAfterToday.tvWeekday.setText(weekdayArray[0]);
        binding.cvForecast2DaysAfterToday.tvWeekday.setText(weekdayArray[1]);
        binding.cvForecast3DaysAfterToday.tvWeekday.setText(weekdayArray[2]);
        binding.cvForecast4DaysAfterToday.tvWeekday.setText(weekdayArray[3]);
        binding.cvForecast5DaysAfterToday.tvWeekday.setText(weekdayArray[4]);


        // set view for next few hours
        //temperature
        binding.lForecast3h.tvTempForecastNextHours
                .setText(new DecimalFormat("#.#")
                        .format(temperatureArrayForecast1DayAfterToday[1]) + " " + weatherCardUtils.measurementFormat());
        binding.lForecast6h.tvTempForecastNextHours
                .setText(new DecimalFormat("#.#")
                        .format(temperatureArrayForecast1DayAfterToday[2]) + " " + weatherCardUtils.measurementFormat());
        binding.lForecast9h.tvTempForecastNextHours
                .setText(new DecimalFormat("#.#")
                        .format(temperatureArrayForecast1DayAfterToday[3]) + " " + weatherCardUtils.measurementFormat());
        binding.lForecast12h.tvTempForecastNextHours
                .setText(new DecimalFormat("#.#")
                        .format(temperatureArrayForecast1DayAfterToday[4]) + " " + weatherCardUtils.measurementFormat());
        binding.lForecast15h.tvTempForecastNextHours
                .setText(new DecimalFormat("#.#")
                        .format(temperatureArrayForecast1DayAfterToday[5]) + " " + weatherCardUtils.measurementFormat());
        binding.lForecast18h.tvTempForecastNextHours
                .setText(new DecimalFormat("#.#")
                        .format(temperatureArrayForecast1DayAfterToday[6]) + " " + weatherCardUtils.measurementFormat());
        binding.lForecast21h.tvTempForecastNextHours
                .setText(new DecimalFormat("#.#")
                        .format(temperatureArrayForecast1DayAfterToday[7]) + " " + weatherCardUtils.measurementFormat());

        binding.lForecast3h.tvTempForecastNextHours.setTextColor(
                weatherCardUtils.chooseRightColorForTemperatureTextColor(temperatureArrayForecast1DayAfterToday[1]));
        binding.lForecast6h.tvTempForecastNextHours.setTextColor(
                weatherCardUtils.chooseRightColorForTemperatureTextColor(temperatureArrayForecast1DayAfterToday[2]));
        binding.lForecast9h.tvTempForecastNextHours.setTextColor(
                weatherCardUtils.chooseRightColorForTemperatureTextColor(temperatureArrayForecast1DayAfterToday[3]));
        binding.lForecast12h.tvTempForecastNextHours.setTextColor(
                weatherCardUtils.chooseRightColorForTemperatureTextColor(temperatureArrayForecast1DayAfterToday[4]));
        binding.lForecast15h.tvTempForecastNextHours.setTextColor(
                weatherCardUtils.chooseRightColorForTemperatureTextColor(temperatureArrayForecast1DayAfterToday[5]));
        binding.lForecast18h.tvTempForecastNextHours.setTextColor(
                weatherCardUtils.chooseRightColorForTemperatureTextColor(temperatureArrayForecast1DayAfterToday[6]));
        binding.lForecast21h.tvTempForecastNextHours.setTextColor(
                weatherCardUtils.chooseRightColorForTemperatureTextColor(temperatureArrayForecast1DayAfterToday[7]));


        binding.lForecast3h.tvPrecipitationForecastNextHours.setText(popTomorrow[1] + " %");
        binding.lForecast6h.tvPrecipitationForecastNextHours.setText(popTomorrow[2] + " %");
        binding.lForecast9h.tvPrecipitationForecastNextHours.setText(popTomorrow[3] + " %");
        binding.lForecast12h.tvPrecipitationForecastNextHours.setText(popTomorrow[4] + " %");
        binding.lForecast15h.tvPrecipitationForecastNextHours.setText(popTomorrow[5] + " %");
        binding.lForecast18h.tvPrecipitationForecastNextHours.setText(popTomorrow[6] + " %");
        binding.lForecast21h.tvPrecipitationForecastNextHours.setText(popTomorrow[7] + " %");

        binding.lForecast3h.tvHumidityForecastNextHours.setText(humiditiesTomorrow[1] + " %");
        binding.lForecast6h.tvHumidityForecastNextHours.setText(humiditiesTomorrow[2] + " %");
        binding.lForecast9h.tvHumidityForecastNextHours.setText(humiditiesTomorrow[3] + " %");
        binding.lForecast12h.tvHumidityForecastNextHours.setText(humiditiesTomorrow[4] + " %");
        binding.lForecast15h.tvHumidityForecastNextHours.setText(humiditiesTomorrow[5] + " %");
        binding.lForecast18h.tvHumidityForecastNextHours.setText(humiditiesTomorrow[6] + " %");
        binding.lForecast21h.tvHumidityForecastNextHours.setText(humiditiesTomorrow[7] + " %");

        if (weatherIconDrawable3h != 0)
            binding.lForecast3h.ivWeatherIconForecastNextHours.setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(), weatherIconDrawable3h, null));

        if (weatherIconDrawable6h != 0)
            binding.lForecast6h.ivWeatherIconForecastNextHours.setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(), weatherIconDrawable6h, null));

        if (weatherIconDrawable9h != 0)
            binding.lForecast9h.ivWeatherIconForecastNextHours.setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(), weatherIconDrawable9h, null));

        if (weatherIconDrawable12h != 0)
            binding.lForecast12h.ivWeatherIconForecastNextHours.setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(), weatherIconDrawable12h, null));

        if (weatherIconDrawable15h != 0)
            binding.lForecast15h.ivWeatherIconForecastNextHours.setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(), weatherIconDrawable15h, null));

        if (weatherIconDrawable18h != 0)
            binding.lForecast18h.ivWeatherIconForecastNextHours.setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(), weatherIconDrawable18h, null));

        if (weatherIconDrawable21h != 0)
            binding.lForecast21h.ivWeatherIconForecastNextHours.setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(), weatherIconDrawable21h, null));


        binding.lForecast3h.tvNextHoursForecastNextHours.setText(dt_txt[1]);
        binding.lForecast6h.tvNextHoursForecastNextHours.setText(dt_txt[2]);
        binding.lForecast9h.tvNextHoursForecastNextHours.setText(dt_txt[3]);
        binding.lForecast12h.tvNextHoursForecastNextHours.setText(dt_txt[4]);
        binding.lForecast15h.tvNextHoursForecastNextHours.setText(dt_txt[5]);
        binding.lForecast18h.tvNextHoursForecastNextHours.setText(dt_txt[6]);
        binding.lForecast21h.tvNextHoursForecastNextHours.setText(dt_txt[7]);

        binding.lForecast3h.tvTempMin.setText(new DecimalFormat("#").format(tempMin[1]));
        binding.lForecast6h.tvTempMin.setText(new DecimalFormat("#").format(tempMin[2]));
        binding.lForecast9h.tvTempMin.setText(new DecimalFormat("#").format(tempMin[3]));
        binding.lForecast12h.tvTempMin.setText(new DecimalFormat("#").format(tempMin[4]));
        binding.lForecast15h.tvTempMin.setText(new DecimalFormat("#").format(tempMin[5]));
        binding.lForecast18h.tvTempMin.setText(new DecimalFormat("#").format(tempMin[6]));
        binding.lForecast21h.tvTempMin.setText(new DecimalFormat("#").format(tempMin[7]));

        binding.lForecast3h.tvTempMax.setText(new DecimalFormat("#").format(tempMax[1]));
        binding.lForecast6h.tvTempMax.setText(new DecimalFormat("#").format(tempMax[2]));
        binding.lForecast9h.tvTempMax.setText(new DecimalFormat("#").format(tempMax[3]));
        binding.lForecast12h.tvTempMax.setText(new DecimalFormat("#").format(tempMax[4]));
        binding.lForecast15h.tvTempMax.setText(new DecimalFormat("#").format(tempMax[5]));
        binding.lForecast18h.tvTempMax.setText(new DecimalFormat("#").format(tempMax[6]));
        binding.lForecast21h.tvTempMax.setText(new DecimalFormat("#").format(tempMax[7]));


        binding.tvAqi.setText(weatherCardUtils.aqiToConditionText(aqi));


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof MainActivity)
            this.mainActivity = (MainActivity) context;
    }


}