package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    //city name
    @SerializedName("city")
    public String cityName;
    //weather id
    @SerializedName("id")
    public String weatherId;
    //update time
    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;  //update time
    }
}
