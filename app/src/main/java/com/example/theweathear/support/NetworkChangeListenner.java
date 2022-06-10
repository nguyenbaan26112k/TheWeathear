package com.example.theweathear.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.theweathear.view.MainActivity;
import com.example.theweathear.view.ViewADD;

public class NetworkChangeListenner extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Common.isConnectedInternet(context)){
            MainActivity.checkconect(true);
        }else {
            MainActivity.checkconect(false);
        }
    }
}
