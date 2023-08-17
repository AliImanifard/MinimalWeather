package com.ali.minimalweather.RetrofitModal;

import com.google.gson.annotations.SerializedName;

public class Wind {
    @SerializedName("speed")
    float speed;

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
