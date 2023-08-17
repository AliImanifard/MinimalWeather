package com.ali.minimalweather.RetrofitModal;

import com.google.gson.annotations.SerializedName;

public class AirPollutionMain {
    //aqi =  Air Quality Index.
    // Possible values: 1, 2, 3, 4, 5.
    // Where 1 = Good, 2 = Fair, 3 = Moderate, 4 = Poor, 5 = Very Poor.
    @SerializedName("aqi")
    private int aqi;

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }
}
