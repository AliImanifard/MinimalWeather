package com.ali.minimalweather.RetrofitModal;

import com.google.gson.annotations.SerializedName;

public class ListAirPollution {
    @SerializedName("main")
    private AirPollutionMain airPollutionMain;

    public AirPollutionMain getAirPollutionMain() {
        return airPollutionMain;
    }

    public void setAirPollutionMain(AirPollutionMain airPollutionMain) {
        this.airPollutionMain = airPollutionMain;
    }
}
