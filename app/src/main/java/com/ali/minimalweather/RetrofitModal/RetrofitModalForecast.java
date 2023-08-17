package com.ali.minimalweather.RetrofitModal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofitModalForecast {

    @SerializedName("list")
    private List<ListCitiesForecast> list;

    public List<ListCitiesForecast> getForecastList() {
        return list;
    }

    public void setList(List<ListCitiesForecast> list) {
        this.list = list;
    }
}
