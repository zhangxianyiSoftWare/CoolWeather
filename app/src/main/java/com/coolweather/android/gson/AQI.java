package com.coolweather.android.gson;

public class AQI
{
    //city
    public AQICity city;

    public class  AQICity{
        //air quality index
        public String api;
        //pm 2.5 concertration
        public String pm25;
    }
}
