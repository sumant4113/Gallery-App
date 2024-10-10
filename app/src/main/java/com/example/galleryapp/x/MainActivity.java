package com.example.galleryapp.x;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.galleryapp.R;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String[] PERMISSION_15 = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED};
    private static final String[] PERMISSION_14 = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO};
    private GridView gallery;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private Button btnGrantPermission;
    private ArrayList<String> images = new ArrayList<String>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gallery = findViewById(R.id.galleryGridView);
        btnGrantPermission = findViewById(R.id.btn_grant_permission);


        if (permission()) {
            loadAllImages();
            btnGrantPermission.setVisibility(View.INVISIBLE);
        } else {
            btnGrantPermission.setVisibility(View.VISIBLE);
//            askForPermission();
        }

        btnGrantPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForPermission();
            }
        });

    }

    private void askForPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSION_15, 15);

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION_READ_EXTERNAL_STORAGE)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This app required permission to access your photos.")
                    .setTitle("Permission Required")
                    .setCancelable(false)
                    .setPositiveButton("Ok", (dialogInterface, i) -> {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{PERMISSION_READ_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_CODE);
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        btnGrantPermission.setVisibility(View.VISIBLE);
                    });
            builder.show();
            btnGrantPermission.setVisibility(View.INVISIBLE);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{PERMISSION_READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

    }

    private void loadAllImages() {
        gallery.setAdapter(new ImageAdapter(this));

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (null != images && !images.isEmpty())
                    Toast.makeText(
                            getApplicationContext(), "position " + position + " " + images.get(position),
                            Toast.LENGTH_SHORT).show();

            }
        });

    }

    private boolean permission() {
        if (ActivityCompat.checkSelfPermission(this, PERMISSION_15[0]) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, PERMISSION_15[1]) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted.", Toast.LENGTH_SHORT).show();
            return true;
        } else if (ActivityCompat.checkSelfPermission(this, PERMISSION_READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted.", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 15) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load the gallery
                Toast.makeText(this, "15 Granted", Toast.LENGTH_SHORT).show();
                btnGrantPermission.setVisibility(View.INVISIBLE);
                loadAllImages();
            }
        } else if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, PERMISSION_READ_EXTERNAL_STORAGE)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This feature is unavailable because this feature require permission that you denied." +
                            "Please allow that permission from settings and try again.")
                    .setTitle("Permission Required")
                    .setCancelable(false)
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Settings", (dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);

                        dialogInterface.dismiss();
                    });
            builder.show();

            Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
        } else if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load the gallery
                Toast.makeText(this, "15 Granted", Toast.LENGTH_SHORT).show();
                btnGrantPermission.setVisibility(View.INVISIBLE);
                loadAllImages();
            }

        }
//        askForPermission();

    }

}