package com.example.galleryapp.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Activity.HomeActivity;
import com.example.galleryapp.main.Fragment.MainFragment;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.viewPager);

        if (mainFragment != null) {
            mainFragment.loadImages();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);

    }
}