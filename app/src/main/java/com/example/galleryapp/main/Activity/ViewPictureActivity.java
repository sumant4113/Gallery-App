package com.example.galleryapp.main.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Adapter.VPPhotoAdapter;
import com.example.galleryapp.main.Model.ImageModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.util.ArrayList;

public class ViewPictureActivity extends AppCompatActivity {

    private static final String TAG = "BottomSheet";

    // BottomSheetProperty
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private LinearLayout llBottomSheet;

    public LinearLayout layoutBottom, layoutTop;
    private ViewPager vpFullPhoto;
    private RelativeLayout mainLayout;
    private VPPhotoAdapter viewPagerPhotoAdapter;

    private int currentPosition = 0; // hold current position ViewPager
    private boolean isWhiteBG = false;

    private ArrayList<ImageModel> imageModelArrayList = new ArrayList<>();

    private ImageView imgBackBtn, imgShare, imgEdit, imgFavorite, imgDelete, imgMore;
    private TextView txtImgDate, txtImgTime, txtDateTime, txtImgName, txtImgMp, txtImgResolution, txtOnDeviceSize, txtFilePath;
    private int position;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        initView();

        // In This Activity shows full Image
        /*View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);*/

        // Set up Gesture Detector
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                toggleVisibility();
                return true;
            }
        });
        // activity open time only show photo
        enterFullScreen();

        vpFullPhoto.setOnClickListener(view -> toggleVisibility());
    }

    private void enterFullScreen() {
//        Window window = getWindow();
//        window.getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hide navigation bar
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // Hide status bar
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Keep the mode sticky
//        );+
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            getWindow().getInsetsController().hide(WindowInsetsCompat.Type.statusBars());
            getWindow().getInsetsController().hide(WindowInsetsCompat.Type.navigationBars());
        }
    }
    public void toggleVisibility() {
        int visibility = (layoutTop.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
        layoutTop.setVisibility(visibility);
        layoutBottom.setVisibility(visibility);
        mainLayout.setBackgroundColor(visibility == View.VISIBLE ? Color.WHITE : Color.BLACK);

        if (visibility == View.VISIBLE) {
            exitFullScreen();
        } else {
            enterFullScreen();
        }
    }

    /*public void toggleVisibility() {
        if (layoutTop.getVisibility() == View.VISIBLE) {
            layoutTop.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
            mainLayout.setBackgroundColor(Color.BLACK);
            enterFullScreen();
        } else {
            layoutTop.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);
            mainLayout.setBackgroundColor(Color.WHITE);
            exitFullScreen();
        }
    }*/

    /* public void toggleVisibility() {
     *//*if (isWhiteBG) {
            layoutTop.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setStatusBarColor(Color.BLACK);
            mainLayout.setBackgroundColor(Color.BLACK);
            isWhiteBG = false;
        } else {
            layoutTop.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setStatusBarColor(Color.WHITE);
            mainLayout.setBackgroundColor(Color.WHITE);
            isWhiteBG = true;
        }*//*

        if (!isWhiteBG) {
            // Hide all and show only photo
            layoutTop.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
            mainLayout.setBackgroundColor(Color.BLACK);
            enterFullScreen();
            isWhiteBG = true;
        } else {
            // Show all with photo
            layoutTop.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);
            mainLayout.setBackgroundColor(Color.WHITE);
            exitFullScreen();
            isWhiteBG = false;
        }

        *//*if (isWhiteBG) {
            layoutTop.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
            mainLayout.setBackgroundColor(Color.BLACK);
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
            // API 30 thi less
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                getWindow().getInsetsController().hide(WindowInsetsCompat.Type.statusBars());
                getWindow().getInsetsController().hide(WindowInsetsCompat.Type.navigationBars());
                getWindow().getInsetsController().hide(WindowInsetsCompat.Type.systemBars());
//                getWindow().getInsetsController().setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
            isWhiteBG = false;
        } else {
            layoutTop.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);
            mainLayout.setBackgroundColor(Color.WHITE);

            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                getWindow().getInsetsController().show(WindowInsetsCompat.Type.statusBars());
                getWindow().getInsetsController().show(WindowInsetsCompat.Type.navigationBars());
//                getWindow().getInsetsController().setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_TOUCH);
            }
            isWhiteBG = true;
        }*//*
    }*/

    private void exitFullScreen() {
//        Window window = getWindow();
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            getWindow().getInsetsController().show(WindowInsetsCompat.Type.statusBars());
            getWindow().getInsetsController().show(WindowInsetsCompat.Type.navigationBars());
        }
    }

    private void initView() {
        imgFavorite = findViewById(R.id.img_favorite);
        imgBackBtn = findViewById(R.id.img_backBtn);
        imgShare = findViewById(R.id.img_share);
        imgEdit = findViewById(R.id.img_edit);
        imgMore = findViewById(R.id.img_more);
        imgDelete = findViewById(R.id.img_delete);
        txtImgTime = findViewById(R.id.txt_img_time);
        txtImgDate = findViewById(R.id.txt_img_date);

        vpFullPhoto = findViewById(R.id.vpFullPhoto);
        layoutTop = findViewById(R.id.layout_top);
        layoutBottom = findViewById(R.id.layout_bottom);
        mainLayout = findViewById(R.id.main_layout);

        // Initialize BottomSheet
        llBottomSheet = findViewById(R.id.ll_bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        txtDateTime = findViewById(R.id.txt_dateTime);
        txtImgName = findViewById(R.id.txt_vidName);
        txtImgMp = findViewById(R.id.txt_vidMP);
        txtImgResolution = findViewById(R.id.txt_vidResolution);
        txtOnDeviceSize = findViewById(R.id.txt_onDeviceSize);
        txtFilePath = findViewById(R.id.txt_filePath);

        txtImgName.setText("");

        if (getIntent().hasExtra("image_path") && getIntent().getParcelableArrayListExtra("image_path") != null) {
            imageModelArrayList = getIntent().getParcelableArrayListExtra("image_path");
            position = getIntent().getIntExtra("position", 0);
        } else {
            // Handle the case where there is no data passed
            Toast.makeText(this, "No image data found", Toast.LENGTH_SHORT).show();
            finish(); // Exit activity if no data is found
        }

     /*   if (getIntent().getExtras() != null) {
            // Get Data from Intent
            imageModelArrayList = getIntent().getParcelableArrayListExtra("image_path");
            position = getIntent().getIntExtra("position", 0);
        }*/

        if (imageModelArrayList != null && !imageModelArrayList.isEmpty()) {

            if (viewPagerPhotoAdapter == null) {
                viewPagerPhotoAdapter = new VPPhotoAdapter(this, imageModelArrayList);
                vpFullPhoto.setAdapter(viewPagerPhotoAdapter);
            }
            vpFullPhoto.setCurrentItem(position);

            viewPagerPhotoAdapter.notifyDataSetChanged();

            vpFullPhoto.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPosition = position;
                    showImageProperties(position);

                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                    if (isWhiteBG) {
                        mainLayout.setBackgroundColor(Color.BLACK);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                }
            });
        }

        setBottomSheetBehavior();
        imgMore.setOnClickListener(v -> {

//            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);

            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                // Expand the bottom sheet
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                // Collapse the bottom sheet
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);  // Or use STATE_COLLAPSED
            }
        });

        imgShare.setOnClickListener(v -> shareFile());
        imgBackBtn.setOnClickListener(v -> onBackPressed());
        txtImgDate.setText("set Karo");


    }


    private void shareFile() {
        String imagePath = imageModelArrayList.get(currentPosition).getPath();
        File file = new File(imagePath);
        if (file.exists()) {
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Share Image via"));
        } else {
            Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show();
        }
    }

    // Delete Images
    /*private void deleteFile(int position, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete File")
                .setMessage(videoModelArrayList.get(position).getTitle())
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            Long.parseLong(videoModelArrayList.get(position).getId()));
                    File file = new File(videoModelArrayList.get(position).getPath());
                    boolean deleted = file.delete();
                    if (deleted) {
                        getApplicationContext().getContentResolver().delete(contentUri, null, null);
                        videoModelArrayList.remove(position);
                        vpVideoAdapter.notifyDataSetChanged();
                        Snackbar.make(view, "File deleted.", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(view, "File delete Fail.", Snackbar.LENGTH_SHORT).show();
                    }
                }).show();
    }*/

    private void showImageProperties(int position) {
        String imgId = imageModelArrayList.get(position).getId();
        String imgPath = imageModelArrayList.get(position).getPath();
        String imgName = imageModelArrayList.get(position).getTitle();
        String imgSize = imageModelArrayList.get(position).getSize();
        String imgResolution = imageModelArrayList.get(position).getResolution();
        String imgDateTaken = imageModelArrayList.get(position).getDateTaken();

        txtDateTime = findViewById(R.id.txt_dateTime);
        txtImgMp = findViewById(R.id.txt_vidMP);
        txtImgResolution = findViewById(R.id.txt_vidResolution);
        txtOnDeviceSize = findViewById(R.id.txt_onDeviceSize);
        txtFilePath = findViewById(R.id.txt_filePath);

        txtDateTime.setText(imgDateTaken);
        txtImgDate.setText("");
        txtImgTime.setText("");

        txtFilePath.setText(imgPath);
        txtOnDeviceSize.setText(imgSize);
        txtImgMp.setText(imgSize);
        txtImgResolution.setText(imgResolution);
        txtImgName.setText(imgName);


        Log.d(TAG, "showImageProperties: +-+-" + imgDateTaken);
    }

    private void setBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.ll_bottomSheet));

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.d("BottomSheet", "STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.d("BottomSheet", "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.d("BottomSheet", "STATE_HIDDEN");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Handle slide behavior if needed
            }
        });

        // Initially hide the bottom sheet
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_pop_up_layout);

        TextView txtImgName, txtDateTime, txtImgMP, txtImgResolution, txtOnDeviceSize, txtFilePath;

        txtImgName = dialog.findViewById(R.id.txt_vidName);
        txtDateTime = dialog.findViewById(R.id.txt_dateTime);
        txtImgMP = dialog.findViewById(R.id.txt_vidMP);
        txtImgResolution = dialog.findViewById(R.id.txt_vidResolution);
        txtOnDeviceSize = dialog.findViewById(R.id.txt_onDeviceSize);
        txtFilePath = dialog.findViewById(R.id.txt_filePath);


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomSheetDialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
//        finish();
    }

}