package com.example.galleryapp.test.Activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
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
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.galleryapp.R;
import com.example.galleryapp.test.Adapter.VPPhotoAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

public class ViewPictureActivity extends AppCompatActivity {

    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private LinearLayout llBottomSheet;
    //    public static RelativeLayout ;
    public static LinearLayout layoutBottom, layoutTop;
    private ViewPager vpFullPhoto;
    private RelativeLayout mainLayout;
    private VPPhotoAdapter viewPagerPhotoAdapter;
    private ArrayList<String> imageList = new ArrayList<>();
    private boolean isWhiteBG = true;

    private ImageView imgBackBtn, imgShare, imgEdit, imgFavorite, imgDelete, imgMore;
    private TextView txtImgDate;
    private TextView txtImgTime;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        initView();
        vpFullPhoto.setOnClickListener(view -> toggleVisibility());
    }

    public void toggleVisibility() {
        /*if (isWhiteBG) {
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
        }*/
        if (isWhiteBG) {
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



        if (getIntent().getExtras() != null) {
            // Get Data from Intent
            imageList = getIntent().getStringArrayListExtra("image_path");
            position = getIntent().getIntExtra("position", 0);
        }

        if (imageList != null && !imageList.isEmpty()) {
            viewPagerPhotoAdapter = new VPPhotoAdapter(this, imageList);
            vpFullPhoto.setAdapter(viewPagerPhotoAdapter);
            vpFullPhoto.setCurrentItem(position);
        } else {// if null then ...
            Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
        }

        vpFullPhoto.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (!isWhiteBG) {
                    mainLayout.setBackgroundColor(Color.BLACK);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setBottomSheetBehavior();

        imgMore.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() != bottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        imgShare.setOnClickListener(view -> {
        });
        imgBackBtn.setOnClickListener(v -> onBackPressed());
        txtImgDate.setText("set Karo");

    }

    private void setBottomSheetBehavior() {
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    }


    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_pop_up_layout);

        TextView txtImgName, txtDateTime, txtImgMP, txtImgResolution, txtOnDeviceSize, txtFilePath;

        txtImgName = dialog.findViewById(R.id.txt_imgName);
        txtDateTime = dialog.findViewById(R.id.txt_dateTime);
        txtImgMP = dialog.findViewById(R.id.txt_imgMP);
        txtImgResolution = dialog.findViewById(R.id.txt_imgResolution);
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
        super.onBackPressed();
        finish();
    }

}