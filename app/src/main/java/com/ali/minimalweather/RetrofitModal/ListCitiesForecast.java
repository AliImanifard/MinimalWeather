package com.ali.minimalweather.RetrofitModal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListCitiesForecast {


    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private List<Weather> weatherList;

    @SerializedName("clouds")
    private Cloud cloud;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("pop")
    private double pop;

    @SerializedName("rain")
    private Rain rain;

    @SerializedName("snow")
    private Snow snow;

    @SerializedName("dt_txt")
    private String dt_txt;


    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public List<Weather> getWeatherList() {
        return weatherList;
    }

    public void setWeatherList(List<Weather> weatherList) {
        this.weatherList = weatherList;
    }

    public Cloud getCloud() {
        return cloud;
    }

    public void setCloud(Cloud cloud) {
        this.cloud = cloud;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public double getPop() {
        return pop;
    }

    public void setPop(double pop) {
        this.pop = pop;
    }

    public Rain getRain() {
        return rain;
    }

    public void setRain(Rain rain) {
        this.rain = rain;
    }

    public Snow getSnow() {
        return snow;
    }

    public void setSnow(Snow snow) {
        this.snow = snow;
    }

    public String getDt_txt() {
        return dt_txt;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;
    }
}
