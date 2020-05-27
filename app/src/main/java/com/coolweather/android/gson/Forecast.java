package com.coolweather.android.gson;


import com.google.gson.annotations.SerializedName;

public class Forecast
{
    //predict date
    @SerializedName("date")
    public  String date;
    //predict temperature
    @SerializedName("tmp")
    public Temperature temperature;
    //more infomation
    @SerializedName("cond")
    public More more;

    public class Temperature{
        //max temperture
        @SerializedName("max")
        public String max;
        //min temperature
        @SerializedName("min")
        public String min;
    }

    public class More{
        //predicted info
        @SerializedName("txt_d")
        public String info;
    }
}
