package com.coolweather.android.util;


import  okhttp3.Callback;
import  okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import  okhttp3.Response;


public class HttpUtil {
    //create a utils means visited netword service
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback)
    {
        OkHttpClient client = new OkHttpClient();
        //parse the url request
        Request request = new Request.Builder().url(address).build();
        //register the callback function
        client.newCall(request).enqueue(callback);

    }

}
