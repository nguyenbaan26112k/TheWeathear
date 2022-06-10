package com.example.theweathear.support;

import android.app.Application;

public class TheWeatherSupport extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DataLocalManager.init(getApplicationContext());
    }
}
