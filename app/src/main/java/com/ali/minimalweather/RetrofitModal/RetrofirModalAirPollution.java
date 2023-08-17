package com.ali.minimalweather.RetrofitModal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetrofirModalAirPollution {

    @SerializedName("list")
    private List<ListAirPollution> list;

    public List<ListAirPollution> getAirPollutionList() {
        return list;
    }

    public void setList(List<ListAirPollution> list) {
        this.list = list;
    }
}
