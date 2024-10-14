package com.example.galleryapp.main.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Adapter.ViewP_Frag_PagerAdapter;
import com.example.galleryapp.main.Fragment.MainFragment;
import com.example.galleryapp.main.PermissionManager;
import com.google.android.material.tabs.TabLayout;

public class HomeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static final String TAG = "HomeActivity";
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        requestPermissions();
        initView();
    }

    private void initView() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        mainFragment = new MainFragment();
        // App start and show this MainFragment and images
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.viewPager, mainFragment);
        fragmentTransaction.commit();

//        ViewPagerGalleryAdapter galleryAdapter = new ViewPagerGalleryAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(galleryAdapter);
//        tabLayout.setupWithViewPager(viewPager);

        ViewP_Frag_PagerAdapter vpGAdapter = new ViewP_Frag_PagerAdapter(fragmentManager);
        viewPager.setAdapter(vpGAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    @SuppressLint("NewApi")
    private void requestPermissions() {
        PermissionManager.requestPermissions(HomeActivity.this, new PermissionManager.PermissionCallback() {
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