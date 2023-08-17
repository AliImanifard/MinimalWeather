package com.ali.minimalweather.RetrofitModal;

import com.google.gson.annotations.SerializedName;

public class Cloud {
    @SerializedName("all")
    double all;

    public double getAll() {
        return all;
    }

    public void setAll(double all) {
        this.all = all;
    }
}
