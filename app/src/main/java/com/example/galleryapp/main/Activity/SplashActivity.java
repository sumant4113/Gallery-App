package com.example.galleryapp.main.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Fragment.MainFragment;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.viewPager);
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);

        if (mainFragment != null) {
            mainFragment.loadImages();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 1000);

    }
}