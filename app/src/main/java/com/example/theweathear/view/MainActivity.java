package com.example.theweathear.view;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.example.theweathear.R;
import com.example.theweathear.databinding.ActivityMainBinding;
import com.example.theweathear.model.City;
import com.example.theweathear.support.DataLocalManager;
import com.example.theweathear.support.ItemViewAdapter;
import com.example.theweathear.support.KeyName;
import com.example.theweathear.support.MyHelperSQLite;
import com.example.theweathear.support.NetworkChangeListenner;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static ActivityMainBinding binding;
    private ItemViewAdapter itemViewAdapter;
    private List<City> cities;
    private long backPressedTime;
    public Toast makeText;
    public FusedLocationProviderClient fusedLocationProviderClient;
    public NetworkChangeListenner listenner = new NetworkChangeListenner();
    private MyHelperSQLite myDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        cities = new ArrayList<>();
        myDB = new MyHelperSQLite(this);
        getListData();
        checkLocation();
        binding.menu.setOnClickListener(view -> {
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).getName().equalsIgnoreCase(KeyName.currentLocation)) {
                    nextActivityAdd(cities.get(i));
                    break;
                }
            }
        });
        itemViewAdapter = new ItemViewAdapter(cities, MainActivity.this);
        binding.viewPager.setAdapter(itemViewAdapter);
        binding.circleIncicator.setViewPager(binding.viewPager);
        setCurrentItem();

    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(listenner,filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(listenner);
        super.onStop();
    }
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis())
        {
            super.onBackPressed();
            makeText.cancel();
            return;
        }else {
            makeText = Toast.makeText(this,"Press back again to exit application",Toast.LENGTH_LONG);
            makeText.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
    private void getListData(){
        Cursor cursor = myDB.getAllCity();
        if (cursor.getCount()==0){
            Log.e("Load Data From SQLite","No Data");
        }else {
            while (cursor.moveToNext()){
                // 1 vi tri cot thu nhat
                // 2 vi tri cot thu hai
                // 3 vi tri cot thu ba
                City city = new City(cursor.getString(1),cursor.getString(2),cursor.getString(3) );
                cities.add(city);
            }
        }
    }

    private void checkLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        City city = new City(KeyName.currentLocation, String.valueOf(list.get(0).getLatitude()), String.valueOf(list.get(0).getLongitude()));
                        cities.add(city);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("TAG", "nulll");
                }
            });
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            checkLocation();
        }

    }

    public void nextActivityAdd(City city) {
        Intent intent = new Intent(MainActivity.this, ViewADD.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KeyName.currentLocation, city);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void setCurrentItem() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null){
            binding.viewPager.setCurrentItem(0);
        }else {
            binding.viewPager.setCurrentItem(bundle.getInt(KeyName.position));

        }
    }
    public static void checkconect(boolean check){
        if (check){
            binding.disconnected.setVisibility(View.VISIBLE);
        }else {
            binding.disconnected.setVisibility(View.GONE);
        }
    }

}