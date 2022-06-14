package com.example.theweathear.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.theweathear.R;

import com.example.theweathear.databinding.ActivityViewAddBinding;
import com.example.theweathear.model.City;

import com.example.theweathear.support.DataLocalManager;
import com.example.theweathear.support.IsetOnClickListener;
import com.example.theweathear.support.ItemCityAdapter;
import com.example.theweathear.support.KeyName;
import com.example.theweathear.support.MyHelperSQLite;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewADD extends AppCompatActivity {
    public static ActivityViewAddBinding binding;
    private List<City> cities;
    private ItemCityAdapter itemCityAdapter;
    private boolean clicked = false;
    private MyHelperSQLite myDB;

    private Animation fabopen, fabclose, rotateForward, rotateBackward;
    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
            , new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Place place = Autocomplete.getPlaceFromIntent(result.getData());
                        City citySearch = new City(place.getName(), String.valueOf(place.getLatLng().latitude), String.valueOf(place.getLatLng().longitude));
                        cities.add(citySearch);
                        myDB.addCity(citySearch);
                        itemCityAdapter.setData(cities);
                    } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                        Status status = Autocomplete.getStatusFromIntent(result.getData());

                        Log.e("tag", status.getStatusMessage());

                        Toast.makeText(ViewADD.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_add);
        cities = new ArrayList<>();
        myDB = new MyHelperSQLite(this);
        getListData();
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        City city = (City) bundle.get(KeyName.currentLocation);
        cities.add(city);

        fabopen = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        fabclose = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);

        itemCityAdapter = new ItemCityAdapter(this, new IsetOnClickListener() {
            @Override
            public void onClickItemListener(int position) {
                sendData(position);
            }
        });
        itemCityAdapter.setData(cities);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);


        binding.listCiti.setAdapter(itemCityAdapter);
        binding.listCiti.setLayoutManager(layoutManager);

        Places.initialize(getApplicationContext(), "AIzaSyDYFMmh1DEqsr3wB1dNrd220c7UUXkuaT4");

        binding.exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData(0);
            }
        });
        binding.addButton.setOnClickListener(view -> onAddButtonClicked());
        binding.deteleButton.setOnClickListener(view -> openDialog());
        binding.searchButton.setOnClickListener(view -> {
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                    .build(ViewADD.this);
            mActivityResultLauncher.launch(intent);
        });

    }
    private void openDialog(){
        final Dialog dialog = new Dialog(this);
        List<String> country = new ArrayList<>();
        for (City list: cities){
            country.add(list.getName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.layout_custom_autocomple,
                R.id.textviewAutocomple,country);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_dialog);
        Window window = dialog.getWindow();
        if (window==null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wLayoutParams = window.getAttributes();
        wLayoutParams.gravity = Gravity.CENTER;

        window.setAttributes(wLayoutParams);
        dialog.setCancelable(false);
        AutoCompleteTextView editText = dialog.findViewById(R.id.searchName);
        TextView textView = dialog.findViewById(R.id.notificationDelete);
        Button delete = dialog.findViewById(R.id.buttondelete);
        Button cancel = dialog.findViewById(R.id.cancelButton);
        editText.setAdapter(arrayAdapter);
        delete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equalsIgnoreCase("")){
                    textView.setText("Bạn chưa nhập tên thành phố");
                    textView.setVisibility(View.VISIBLE);
                }else {
                    int check=0;
                    for (int i = 0;i<cities.size();i++){
                        if (editText.getText().toString().equalsIgnoreCase(cities.get(i).getName())){
                            cities.remove(i);
                            myDB.deleteCity(editText.getText().toString());
                            itemCityAdapter.setData(cities);
                            editText.setText("");
                            textView.setVisibility(View.VISIBLE);
                            textView.setText("xóa thành công");
                            textView.setTextColor(R.color.green);
                            break;
                        }else {
                            check = check + 1;
                        }
                    }
                    if (check == cities.size()){
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("Không có tên thành phố");
                    }
                }
            }
        });
        cancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();

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

    private void onAddButtonClicked() {
        setVisibility(clicked);
        setAnimation(clicked);
        clicked = !clicked;
    }

    private void setAnimation(boolean click) {
        if (!click) {
            binding.deteleButton.setAnimation(fabopen);
            binding.searchButton.setAnimation(fabopen);
            binding.addButton.setAnimation(rotateForward);
        } else {
            binding.deteleButton.setAnimation(fabclose);
            binding.searchButton.setAnimation(fabclose);
            binding.addButton.setAnimation(rotateBackward);
        }

    }

    private void setVisibility(boolean click) {
        if (!click) {
            binding.deteleButton.setVisibility(View.VISIBLE);
            binding.searchButton.setVisibility(View.VISIBLE);
        } else {
            binding.deteleButton.setVisibility(View.INVISIBLE);
            binding.searchButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendData(0);
    }

    private void sendData(int position) {
        Intent intent = new Intent(ViewADD.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(KeyName.position,position);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }


}