package com.example.galleryapp.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class FullScreenVideoUtil {

    public static void enterFullScreenVideo(Activity activity) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            activity.getWindow().getInsetsController().hide(WindowInsetsCompat.Type.statusBars());
            activity.getWindow().getInsetsController().hide(WindowInsetsCompat.Type.navigationBars());
        }
    }

    public static void exitFullScreenVideo(Activity activity) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            activity.getWindow().getInsetsController().show(WindowInsetsCompat.Type.statusBars());
            activity.getWindow().getInsetsController().show(WindowInsetsCompat.Type.navigationBars());
        }
    }

    public static void toggleVisibility(Activity activity, View layoutTopVideo, View layoutBottomVideo, View mainLayoutVideo) {
        int visibility = (layoutTopVideo.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
        layoutTopVideo.setVisibility(visibility);
        layoutBottomVideo.setVisibility(visibility);
        mainLayoutVideo.setBackgroundColor(visibility == View.VISIBLE ? Color.WHITE : Color.BLACK);

        if (visibility == View.VISIBLE) {
            exitFullScreenVideo(activity);
        } else {
            enterFullScreenVideo(activity);
        }
    }
}