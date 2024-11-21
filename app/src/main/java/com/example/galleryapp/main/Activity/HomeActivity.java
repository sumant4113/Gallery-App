package com.example.galleryapp.main.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Adapter.ViewP_Frag_PagerAdapter;
import com.example.galleryapp.main.Fragment.MainFragment;
import com.example.galleryapp.main.PermissionManager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class HomeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static final String TAG = "HomeActivity";
    private MainFragment mainFragment;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ImageView imgDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        requestPermissions();
        initView();
    }

    private void initView() {
        navigationView = findViewById(R.id.navigation_view);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        drawerLayout = findViewById(R.id.drawer_layout);
        imgDrawer = findViewById(R.id.img_drawer);

        imgDrawer.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
//            drawerLayout.closeDrawer(GravityCompat.START);

            int itemId = item.getItemId();
            if (itemId == R.id.menu_createAlbum) {
                Toast.makeText(this, "Create Album", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.menu_favoriteItem) {
                startActivity(new Intent(this, ViewFavoriteActivity.class));
//                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (itemId == R.id.menu_rateUs) {
                Toast.makeText(this, "Rate Us", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.menu_shareApp) {
                Toast.makeText(this, "Share App", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.menu_privacyPolicy) {
                Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "About Us", Toast.LENGTH_SHORT).show();
            }
            return false;
        });

        mainFragment = new MainFragment();
        // App start and show MainFragment and images
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.viewPager, mainFragment);
        fragmentTransaction.commit();

        ViewP_Frag_PagerAdapter vpGAdapter = new ViewP_Frag_PagerAdapter(fragmentManager);
        viewPager.setAdapter(vpGAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void requestPermissions() {
        if (PermissionManager.hasAllPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        })) {
            Log.d("HomeActivity", "All permissions granted.");
        } else {
            PermissionManager.requestPermissions(this, new PermissionManager.PermissionCallback() {
                @Override
                public void onPermissionGranted() {
                    Log.d("HomeActivity", "Permission granted.");
                    if (mainFragment != null) {
                        mainFragment.loadImages();
                    }
                }

                @Override
                public void onPermissionDenied() {
                    Log.d("HomeActivity", "Permission denied.");
//                    Toast.makeText(HomeActivity.this, "Permission denied. Please enable in settings.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

  /*  @SuppressLint("NewApi")
    private void requestPermissions() {
        PermissionManager.requestPermissions(HomeActivity.this, new PermissionManager.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "Permission granted");
                Toast.makeText(HomeActivity.this, "Permission granted. Loading images...", Toast.LENGTH_SHORT).show();
                // Call loadImages
                if (mainFragment != null) {
                    mainFragment.loadImages(HomeActivity.this);
                }
            }

            @Override
            public void onPermissionDenied() {
                Log.d(TAG, "Permission denied");
                Toast.makeText(HomeActivity.this, "Permissions denied. You can enable them in settings.", Toast.LENGTH_LONG).show();
*//*                Intent openSetting = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION,
                        Uri.fromParts("package", getPackageName(), null));
                openSetting.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(openSetting);*//*

     *//*  Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*//*
            }
        });
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults, new PermissionManager.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "Permission granted");
                Toast.makeText(HomeActivity.this, "Permission granted. Loading images...", Toast.LENGTH_SHORT).show();
                if (mainFragment != null) {
                    mainFragment.loadImages();
                }
            }

            @Override
            public void onPermissionDenied() {
                Log.d(TAG, "Permission denied");
//                Toast.makeText(HomeActivity.this, "Permissions denied. You can enable them in settings.", Toast.LENGTH_LONG).show();
               /* Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check permissions again in case user enabled them in the settings
        if (PermissionManager.hasAllPermissions(this, new String[]{
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO})) {
            if (mainFragment != null) {
                mainFragment.loadImages();  // Load images if permissions are granted
            }
        } else {
            if (mainFragment != null) {
                mainFragment.loadImages();  // Load images if permissions are granted
            }
//            Objects.requireNonNull(mainFragment).loadImages(HomeActivity.this);

//            Toast.makeText(this, "Permissions are still missing. Please grant them.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}