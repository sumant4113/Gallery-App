package com.example.galleryapp.main;

import android.app.Application;
import android.util.Log;

public class App extends Application {
    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App created");

    }
}