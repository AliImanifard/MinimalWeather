package com.ali.minimalweather.RetrofitModal;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitModalCurrentWeather {


    @SerializedName("coord")
    private Coord coord;

    @SerializedName("sys")
    private Sys sys;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private List<Weather> weatherList;

    @SerializedName("clouds")
    private Cloud cloud;

    @SerializedName("id")
    private int cityId;

    @SerializedName("name")
    private String cityName;


    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public Sys getSys() { return sys; }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public List<Weather> getWeatherList() { return weatherList; }

    public void setWeatherList(List<Weather> weather) { this.weatherList = weatherList; }

    public Cloud getCloud() {
        return cloud;
    }

    public void setCloud(Cloud cloud) {
        this.cloud = cloud;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}

