package com.ali.minimalweather.RetrofitModal;

import com.google.gson.annotations.SerializedName;

public class Snow {

    @SerializedName("3h")
    private double snow_3h;

    public double getSnow_3h() {
        return snow_3h;
    }

    public void setSnow_3h(double snow_3h) {
        this.snow_3h = snow_3h;
    }
}
