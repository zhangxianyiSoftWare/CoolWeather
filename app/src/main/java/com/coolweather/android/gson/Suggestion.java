package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    //comfort
    @SerializedName("comf")
    public Comfort comfort;
    //wash car suggest
    @SerializedName("cw")
    public CarWash carWash;
    //sport suggest
    @SerializedName("sport")
    public Sport sport;

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }
    public class CarWash{
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
