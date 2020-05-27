package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Now
{
    //temp feather
    @SerializedName("tmp")
    public String temperature;
    // more info
    @SerializedName("cond")
    public More more;

    public class More{
        // weather info
        @SerializedName("txt")
        public String info;
    }
}
