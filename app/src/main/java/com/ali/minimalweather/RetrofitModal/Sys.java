package com.ali.minimalweather.RetrofitModal;

import com.google.gson.annotations.SerializedName;

public class Sys {

    @SerializedName("type")
    int type;

    @SerializedName("id")
    int id;

    @SerializedName("message")
    double message;

    @SerializedName("country")
    String country;

    @SerializedName("sunrise")
    long sunrise;

    @SerializedName("sunset")
    long sunset;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMessage() {
        return message;
    }

    public void setMessage(double message) {
        this.message = message;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getSunrise() {
        return sunrise;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }
}
