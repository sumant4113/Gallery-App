package com.example.galleryapp.test.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.galleryapp.R;
import com.example.galleryapp.test.Adapter.ViewPagerGalleryAdapter;
import com.example.galleryapp.test.MainFragment;
import com.example.galleryapp.test.PermissionManager;
import com.google.android.material.tabs.TabLayout;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private TabLayout tabLayout;
    private MainFragment mainFragment;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();
    }

    private void initView() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        mainFragment = new MainFragment();
        requestPermissions();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.viewPager, mainFragment);
        fragmentTransaction.commit();

//        ViewPagerGalleryAdapter galleryAdapter = new ViewPagerGalleryAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(galleryAdapter);
//        tabLayout.setupWithViewPager(viewPager);

        ViewPagerGalleryAdapter vpGAdapter = new ViewPagerGalleryAdapter(fragmentManager);
        viewPager.setAdapter(vpGAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void requestPermissions() {
        PermissionManager.requestPermissions(this, new PermissionManager.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "Permission granted");
                Toast.makeText(HomeActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                // Call loadImages
                if (mainFragment != null) {
                    mainFragment.loadImages();
                }
            }

            @Override
            public void onPermissionDenied() {
                Log.d(TAG, "Permission denied");
                Toast.makeText(HomeActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults, new PermissionManager.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "Permission granted");
                Toast.makeText(HomeActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                if (mainFragment != null) {
                    mainFragment.loadImages();
                }
            }

            @Override
            public void onPermissionDenied() {
                Log.d(TAG, "Permission denied");
                Toast.makeText(HomeActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mainFragment != null) {
            mainFragment.loadImages();
        }
    }
}