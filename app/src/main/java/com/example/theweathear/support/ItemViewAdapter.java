package com.example.theweathear.support;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.theweathear.R;
import com.example.theweathear.model.City;
import com.example.theweathear.model.HourlyWeather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ItemViewAdapter extends RecyclerView.Adapter<ItemViewAdapter.ItemViewHolder>  {
    private final List<City> cityList;
    private final Context context;
    public static final String DATE_FORMAT = "EE,dd MMM yy";
    private List<HourlyWeather> hourlyWeatherList;
    public static final Date date = Calendar.getInstance().getTime();
    public String lat;
    public String lon;
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
    public static final String baseURL = "https://api.openweathermap.org/data/2.5/weather?";
    private static final String baseURLHourly = "https://api.openweathermap.org/data/2.5/onecall";
    private static String comlpURL;
    private static String comlpURLHourly;
    private static ItemViewHourlyAdapter adapter;
    public static DecimalFormat df = new DecimalFormat("#.#");

    public ItemViewAdapter(List<City> cityList, Context context) {
        this.cityList = cityList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viewpager, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        City city = cityList.get(position);
        if (city == null) {
            return;
        }
        holder.dateToday.setText(format.format(date));
        if (checknull(city)) {
            comlpURL = baseURL + "lat=" + city.getLat().replaceAll(" ","")
                    + "&lon=" + city.getLon().replaceAll(" ","")
                    + KeyName.apiKey + KeyName.metric + KeyName.lang;
        } else {
            comlpURL = baseURL + "q=" + city.getName()
                    + KeyName.apiKey + KeyName.metric + KeyName.lang;

        }
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, comlpURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObjectToday = new JSONObject(response);
                    //
                    JSONArray jsonArrayWeather = jsonObjectToday.getJSONArray(KeyName.weather);
                    JSONObject jsonObjectweather = jsonArrayWeather.getJSONObject(0);
                    //
                    String icon = jsonObjectweather.getString(KeyName.icon);
                    String status = jsonObjectweather.getString(KeyName.description);
                    //
                    JSONObject jsonObjectMain = jsonObjectToday.getJSONObject(KeyName.main);
                    Double temp = jsonObjectMain.getDouble(KeyName.temp);
                    Double humidity = jsonObjectMain.getDouble(KeyName.humidity);
                    Double temphigh = jsonObjectMain.getDouble(KeyName.temphigh);
                    Double templow = jsonObjectMain.getDouble(KeyName.templow);
                    Double feelslike = jsonObjectMain.getDouble(KeyName.feelslike);
                    //
                    JSONObject jsonObjectWind = jsonObjectToday.getJSONObject(KeyName.wind);
                    Double speedwind = jsonObjectWind.getDouble(KeyName.speed);
                    //
                    JSONObject jsonObjectCoord = jsonObjectToday.getJSONObject(KeyName.coord);
                    lat = jsonObjectCoord.getString(KeyName.lat);
                    lon = jsonObjectCoord.getString(KeyName.lon);
                    //
                    JSONObject jsonObjectSys = jsonObjectToday.getJSONObject(KeyName.sys);
                    int sunRise = jsonObjectSys.getInt(KeyName.sunrise);
                    int sunSet = jsonObjectSys.getInt(KeyName.sunset);
                    //

                    holder.address.setText(city.getName());
                    holder.tempLow.setText(df.format(templow) + KeyName.doc);
                    holder.tempHigh.setText(df.format(temphigh) + KeyName.doc);
                    holder.temp.setText(df.format(temp) + KeyName.doc);
                    holder.sunrise.setText(covertUnixToHour(sunRise));
                    holder.sunset.setText(covertUnixToHour(sunSet));
                    holder.speedwind.setText(speedwind + KeyName.speedunit);
                    holder.humidity.setText(humidity + "%");
                    holder.feelsLike.setText(df.format(feelslike) + KeyName.doc);
                    holder.status.setText(status);
                    holder.imgWeather.setImageResource(seticon(icon));
                    holder.relativeLayout.setBackgroundResource(setbackground(icon));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("TAG", e.toString());
                }

            }
        }, error -> Log.e("TAG", error.toString()));
        // URL Hourly
        comlpURLHourly = baseURLHourly + "?" + KeyName.lat + "=" + city.getLat().replaceAll(" ","")
                + "&" + KeyName.lon + "=" + city.getLon().replaceAll(" ","") +
                KeyName.metric + KeyName.lang + KeyName.apiKey;
        hourlyWeatherList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        adapter = new ItemViewHourlyAdapter(context);
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, comlpURLHourly, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObjectHourly = new JSONObject(response);
                    JSONArray jsonArrayHourly = jsonObjectHourly.getJSONArray(KeyName.hourly);
                    // lay gia tri trong 10 gio toi
                    for (int i = 0; i < 10; i++) {
                        JSONObject jsonObj = jsonArrayHourly.getJSONObject(i);
                        //
                        int time = jsonObj.getInt(KeyName.dt);
                        double templ = jsonObj.getDouble(KeyName.temp);
                        //
                        JSONArray jsonlist = jsonObj.getJSONArray(KeyName.weather);
                        JSONObject object = jsonlist.getJSONObject(0);
                        String icon = object.getString(KeyName.icon);
                        //
                        hourlyWeatherList.add(new HourlyWeather(icon, time, templ));
                    }
                    adapter.setData(hourlyWeatherList);
                    holder.listhourly.setLayoutManager(layoutManager);
                    holder.listhourly.setAdapter(adapter);

                } catch (JSONException e) {
                    Log.e("TAG", e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Tag2", error.toString());

            }
        });
        requestQueue.add(stringRequest1);
        requestQueue.add(stringRequest2);


    }

    @Override
    public int getItemCount() {
        if (cityList != null) {
            return cityList.size();
        }
        return 0;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView address, temp, tempHigh, tempLow, dateToday, status;
        private final TextView sunrise, sunset, speedwind, humidity, feelsLike;
        private final ImageView imgWeather;
        private final RecyclerView listhourly;
        private final RelativeLayout relativeLayout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            dateToday = itemView.findViewById(R.id.datetoday);
            temp = itemView.findViewById(R.id.temlpToday);
            tempHigh = itemView.findViewById(R.id.temlpHigh);
            tempLow = itemView.findViewById(R.id.temlpLow);
            sunrise = itemView.findViewById(R.id.sunRise);
            sunset = itemView.findViewById(R.id.sunSet);
            speedwind = itemView.findViewById(R.id.speedWind);
            humidity = itemView.findViewById(R.id.humidity);
            feelsLike = itemView.findViewById(R.id.feelsLike);
            status = itemView.findViewById(R.id.statusToday);
            imgWeather = itemView.findViewById(R.id.imgWeather);
            listhourly = itemView.findViewById(R.id.listHourly);
            relativeLayout = itemView.findViewById(R.id.backgrounditem);
        }
    }

    @NonNull
    public static String covertUnixToHour(int time) {
        Date date = new Date(time * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

    public static boolean checknull(@NonNull City city) {
        boolean check = false;
        if (city.getLat() != "" && city.getLat() != "") {
            check = true;
        } else if (city.getName() != "" && city.getLat() == "" && city.getLat() == "") {
            check = false;
        }
        return check;
    }

    public static int seticon(@NonNull String stricon) {
        int icon;
        if (stricon.equalsIgnoreCase(KeyName.d01)) {
            icon = R.drawable.d01;
        } else if (stricon.equalsIgnoreCase(KeyName.n01)) {
            icon = R.drawable.n01;

        } else if (stricon.equalsIgnoreCase(KeyName.d02)) {
            icon = R.drawable.d02;

        } else if (stricon.equalsIgnoreCase(KeyName.n02)) {
            icon = R.drawable.n02;

        } else if (stricon.equalsIgnoreCase(KeyName.d03) || stricon.equalsIgnoreCase(KeyName.n03)) {
            icon = R.drawable.d03;

        } else if (stricon.equalsIgnoreCase(KeyName.d04) || stricon.equalsIgnoreCase(KeyName.n04)) {
            icon = R.drawable.d04;

        } else if (stricon.equalsIgnoreCase(KeyName.d09) ||
                stricon.equalsIgnoreCase(KeyName.n09) ||
                stricon.equalsIgnoreCase(KeyName.d10)||
                stricon.equalsIgnoreCase(KeyName.n10)) {
            icon = R.drawable.d09;

        } else if (stricon.equalsIgnoreCase(KeyName.d11) || stricon.equalsIgnoreCase(KeyName.n11)) {
            icon = R.drawable.d11;

        } else if (stricon.equalsIgnoreCase(KeyName.d13) || stricon.equalsIgnoreCase(KeyName.n13)) {
            icon = R.drawable.d13;

        } else if (stricon.equalsIgnoreCase(KeyName.d50) || stricon.equalsIgnoreCase(KeyName.n50)) {
            icon = R.drawable.d50;

        } else {
            icon = R.drawable.errorimg;
        }
        return icon;
    }
    public static int setbackground(@NonNull String stricon) {
        int background;
        if (stricon.equalsIgnoreCase(KeyName.d01)) {
            background = R.drawable.sunday;
        } else if (stricon.equalsIgnoreCase(KeyName.n01)) {
            background = R.drawable.moonday;

        } else if (stricon.equalsIgnoreCase(KeyName.d02)) {
            background = R.drawable.clearsky;

        } else if (stricon.equalsIgnoreCase(KeyName.n02)) {
            background = R.drawable.clearsky;

        } else if (stricon.equalsIgnoreCase(KeyName.d03) || stricon.equalsIgnoreCase(KeyName.n03)) {
            background = R.drawable.cloudy;

        } else if (stricon.equalsIgnoreCase(KeyName.d04) || stricon.equalsIgnoreCase(KeyName.n04)) {
            background =  R.drawable.fog;

        } else if (stricon.equalsIgnoreCase(KeyName.d09) ||
                stricon.equalsIgnoreCase(KeyName.n09) ||
                stricon.equalsIgnoreCase(KeyName.d10)||
                stricon.equalsIgnoreCase(KeyName.n10)) {
            background = R.drawable.rainbackground;

        } else if (stricon.equalsIgnoreCase(KeyName.d11) || stricon.equalsIgnoreCase(KeyName.n11)) {
            background = R.drawable.storm;

        } else if (stricon.equalsIgnoreCase(KeyName.d13) || stricon.equalsIgnoreCase(KeyName.n13)) {
            background = R.drawable.snow;

        } else if (stricon.equalsIgnoreCase(KeyName.d50) || stricon.equalsIgnoreCase(KeyName.n50)) {
            background = R.drawable.fog;

        } else {
            background = R.drawable.clearsky;
        }
        return background;
    }
}
