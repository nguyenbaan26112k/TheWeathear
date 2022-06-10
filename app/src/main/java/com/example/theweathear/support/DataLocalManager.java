package com.example.theweathear.support;

import android.content.Context;

import com.example.theweathear.model.City;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataLocalManager {
    private static DataLocalManager instance;
    private MySharedPreferences mySharedPreferences;

    public static void init(Context context) {
        instance = new DataLocalManager();
        instance.mySharedPreferences = new MySharedPreferences(context);
    }

    public static DataLocalManager getInstance() {
        if (instance == null) {
            instance = new DataLocalManager();
        }
        return instance;
    }

    public static void setStringJsonArray(List<City> cityList) {
        Gson gson = new Gson();
        JsonArray jsonArray = gson.toJsonTree(cityList).getAsJsonArray();
        String jsonArrayString = jsonArray.toString();
        DataLocalManager.getInstance().mySharedPreferences.putStringJson(KeyName.PREF_FIRST_INSTALL, jsonArrayString);
    }

    public static List<City> getListData() {
        Gson gson = new Gson();
        String strJsonArray = DataLocalManager.getInstance().mySharedPreferences.getStringJson(KeyName.PREF_FIRST_INSTALL);
        List<City> list = new ArrayList<>();
        City city;
        try {
            JSONArray jsonArray = new JSONArray(strJsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                city = gson.fromJson(object.toString(), City.class);
                if (city.getName().equalsIgnoreCase(KeyName.currentLocation)) {
                    continue;
                } else {
                    list.add(city);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;

    }
}
