package com.coolweather.android.util;

import android.text.TextUtils;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility
{
    /*
    * parse the province response from service
    * */
    public static boolean handleProvinceResponse(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
                try
                {
                    //data is not empty
                    JSONArray allProvince = new JSONArray(response);
                    //create a array to parse data
                    for (int i = 0;i<allProvince.length();++i)
                    {
                        JSONObject provinceObject = allProvince.getJSONObject(i);
                        Province pro = new Province();
                        //get the response name string and set
                        pro.setProvinceName(provinceObject.getString("name"));
                        pro.setProvinceCode(provinceObject.getInt("id"));
                        pro.save();
                    }
                    return true;
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
        }
        return false;
    }

    /*
     * parse the city response from service
     * */
    public static boolean handleCityResponse(String response,int provinceId)
    {

        if (!TextUtils.isEmpty(response))
        {
            try
            {
                //data is not empty
                JSONArray all_citys = new JSONArray(response);
                //create a array to parse data
                for (int i = 0;i<all_citys.length();++i)
                {
                    JSONObject city_object = all_citys.getJSONObject(i);
                    City city = new City();
                    //get the response name string and set
                    city.setCityName(city_object.getString("name"));
                    city.setCityCode(city_object.getInt("id"));
                    city.setProvinceCode(provinceId);
                    city.save();
                }
                return true;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
      }

        return false;
    }


    /*
     * parse the city response from service
     * */
    public static boolean handleCountyResponse(String response,int city_id)
    {

        if (!TextUtils.isEmpty(response))
        {
            try
            {
                //data is not empty
                JSONArray all_countrys = new JSONArray(response);
                //create a array to parse data
                for (int i = 0;i<all_countrys.length();++i)
                {
                    JSONObject country_object = all_countrys.getJSONObject(i);
                    County country = new County();
                    //get the response name string and set
                    country.setCountyName(country_object.getString("name"));
                    country.setCityId(city_id);
                    country.setWeatherId(country_object.getString("weather_id"));
                    country.save();
                }
                return true;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return false;
    }

    /*
    * parse the JSON data into weather entity class
    * */
    public static Weather handleWeatherResponse(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            Weather temp = new Gson().fromJson(weatherContent,Weather.class);
            return temp;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
