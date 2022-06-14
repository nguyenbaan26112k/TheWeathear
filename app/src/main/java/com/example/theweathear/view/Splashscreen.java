package com.example.theweathear.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bumptech.glide.Glide;
import com.example.theweathear.R;
import com.example.theweathear.databinding.ActivitySplashscreenBinding;
import com.squareup.picasso.Picasso;

public class Splashscreen extends AppCompatActivity {
    ActivitySplashscreenBinding binding;
    public static int SPLASH_SCREEN = 5000;//thời gian splashscreen kết thúc
    String urlGif = "https://cdn.dribbble.com/users/1353252/screenshots/7430583/media/f456446ffc1c9a1608b94d6d136dbc0d.gif";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_splashscreen);
        loadImg(urlGif);
        getTimeActivity();
    }
    private void getTimeActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splashscreen.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
    void loadImg(String urlGif){
        Glide.with(this).load(urlGif).into(binding.gif);
    }

}