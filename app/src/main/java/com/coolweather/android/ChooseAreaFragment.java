package com.coolweather.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.jetbrains.annotations.NotNull;
import org.litepal.crud.DataSupport;
import org.litepal.util.BaseUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment
{
    /*
    * define the level
    * */
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    /*
    * create some control
    * */
    private ProgressDialog progress_dialog;
    private TextView title_text;
    private Button back_button;
    private ListView list_view;
    private ArrayAdapter<String> adapters;
    private List<String> data_list = new ArrayList<>();

    /*
     * province city county list
     */
    private List<Province> province_list;
    private List<City> city_list;
    private List<County> county_list;

    //selected province
    private Province selected_province;
    //selected city
    private City selected_city;
    //selected level
    private int current_level;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        //get the control bean
        title_text = view.findViewById(R.id.title_text);
        back_button = view.findViewById(R.id.back_button);
        list_view = view.findViewById(R.id.list_view);
        //init arraydadpter
        adapters = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,data_list);
        list_view.setAdapter(adapters);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //set list_view and button click event
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (current_level == LEVEL_PROVINCE)
                {
                    selected_province = province_list.get(position);
                    queryCities();
                }
                else if(current_level == LEVEL_CITY)
                {
                    selected_city = city_list.get(position);
                    queryCounties();
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_level == LEVEL_COUNTY)
                {
                    queryCities();
                }
                else if(current_level == LEVEL_CITY)
                {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /*
    *query all province first from databases if not in database and query from service
    *
    */
    private void queryProvinces()
    {
        title_text.setText("中国");
        //disvisibilit vack button
        back_button.setVisibility(View.GONE);
        //user LitePal query interface from databases read province data
        province_list = DataSupport.findAll(Province.class);
        if(province_list.size() > 0)
        {
            //read and show in fragment
            data_list.clear();
            for(Province pro:province_list)
            {
                data_list.add(pro.getProvinceName());
            }
            adapters.notifyDataSetChanged();
            list_view.setSelection(0);
            current_level = LEVEL_PROVINCE;

        }
        else
        {
            //not read must read data from service
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    /*
    * query selected city first read from databases if not in database read from server
    * */
    private void queryCities()
    {
        title_text.setText(selected_province.getProvinceName());
        //disvisibilit vack button
        back_button.setVisibility(View.VISIBLE);
        //user LitePal query interface from databases read province data
        city_list = DataSupport.where("provinceId=?",String.valueOf(selected_province.getId())).find(City.class);
        if (city_list.size() > 0)
        {
            data_list.clear();
            for (City city:city_list)
            {
                data_list.add(city.getCityName());
            }
            adapters.notifyDataSetChanged();
            list_view.setSelection(0);
            current_level = LEVEL_CITY;

        }
        else
        {
            int provinceCode = selected_province.getProvinceCode();
            String address = "http://guolin.tech/api/china" + provinceCode;
            queryFromServer(address,"city");
        }
    }

    /*
    * query select city all county first quert from database if not in database then read from server
    * */
    private void queryCounties()
    {
        title_text.setText(selected_city.getCityName());
        //disvisibilit vack button
        back_button.setVisibility(View.VISIBLE);
        county_list = DataSupport.where("cityId=?",String.valueOf(selected_city.getId())).find(County.class);
        if(county_list.size() > 0)
        {
            data_list.clear();
            for(County county:county_list)
            {
                data_list.add(county.getCountyName());
            }
            adapters.notifyDataSetChanged();
            list_view.setSelection(0);
            current_level = LEVEL_COUNTY;
        }
        else
        {
            int provinceCode = selected_province.getProvinceCode();
            int cityCode = selected_city.getCityCode();
            String address = "http://guolin.tech/api/china" +provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }
    /*
    * query from server by address
    * */
    private void queryFromServer(String address,final String type)
    {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //deal load failed event
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //recv response data and deal
                String responseText= response.body().string();
                boolean result = false;
                if ("province".equals(type))
                {
                    result = Utility.handleProvinceResponse(responseText);
                }
                else if("city".equals(type))
                {
                    result = Utility.handleCityResponse(responseText,selected_province.getId());
                }
                else if("county".equals(type))
                {
                    result = Utility.handleCountyResponse(responseText,selected_city.getId());
                }

                if(result)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type))
                            {
                                queryProvinces();
                            }
                            else if("city".equals(type))
                            {
                                queryCities();
                            }
                            else if("county".equals(type))
                            {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /*
    * show progress dialog
    * */
    private void showProgressDialog()
    {
        if (progress_dialog == null)
        {
            progress_dialog = new ProgressDialog(getActivity());
            progress_dialog.setMessage("正在加载中");
            progress_dialog.setCanceledOnTouchOutside(false);
        }
    }

    /*
    * close progress dialog
    * */
    private void closeProgressDialog()
    {
        if(progress_dialog != null)
        {
            progress_dialog.dismiss();
        }
    }

}
