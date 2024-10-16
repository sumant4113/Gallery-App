package com.example.galleryapp.main;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class PermissionManager {

    private static final int REQUEST_PERMISSIONS_CODE = 100;

    public interface PermissionCallback {
        void onPermissionGranted();
        void onPermissionDenied();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static void requestPermissions(Activity activity, PermissionCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11+ requires MANAGE_EXTERNAL_STORAGE permission
                permissions = new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_MEDIA_IMAGES,
                        android.Manifest.permission.READ_MEDIA_VIDEO};
            } else {
                permissions = new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
            }

            // Only request permissions if they aren't already granted
            if (!hasAllPermissions(activity, permissions)) {
                ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSIONS_CODE);
            } else {
                callback.onPermissionGranted();  // Permissions already granted
            }
        } else {
            callback.onPermissionGranted();  // No runtime permissions needed
        }
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                  @NonNull int[] grantResults, PermissionCallback callback) {
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                callback.onPermissionGranted();
            } else {
                callback.onPermissionDenied();
            }

            /*if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callback.onPermissionGranted();
            } else {
                callback.onPermissionDenied();
            }*/
        }
    }
    // Helper method to check if all required permissions are granted
    public static boolean hasAllPermissions(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
