package com.example.theweathear.support;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
    private static final String My_Shared_Preferences = "My_Shared_Preferences";
    private Context mContext;

    public MySharedPreferences(Context mContext) {
        this.mContext = mContext;
    }
    public void putStringJson(String key,String value){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(My_Shared_Preferences,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public String getStringJson(String key){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(My_Shared_Preferences,Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }
}
