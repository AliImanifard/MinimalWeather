package com.ali.minimalweather.RetrofitModal;

import com.google.gson.annotations.SerializedName;

public class Rain {
    @SerializedName("3h")
    private double rain_3h;

    public double getRain_3h() {
        return rain_3h;
    }

    public void setRain_3h(double rain_3h) {
        this.rain_3h = rain_3h;
    }
}
