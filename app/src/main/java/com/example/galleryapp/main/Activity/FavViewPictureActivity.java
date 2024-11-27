package com.example.galleryapp.main.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Adapter.VPFavItemAdapter;
import com.example.galleryapp.main.Model.FavoriteDataHolder;
import com.example.galleryapp.main.Model.FavoriteModel;
import com.example.galleryapp.main.sqlite.FavDbHelper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

public class FavViewPictureActivity extends AppCompatActivity {

    // BottomSheetProperty
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private LinearLayout llBottomSheet;
    public LinearLayout layoutBottom, layoutTop;
    private ViewPager vpFullPhoto;
    private RelativeLayout mainLayout;

    private ImageView imgBackBtn, imgShare, imgEdit, imgFavorite, imgDelete, imgMore;
    private TextView txtImgDate, txtImgTime, txtImgDateTime, txtImgName,
            txtImgMp, txtImgResolution, txtImgOnDeviceSize, txtImgFilePath;

    private FavDbHelper dbHelper;
    private FavoriteModel currentFavItem;

    private int position;
    private ArrayList<FavoriteModel> favoriteList;
    private VPFavItemAdapter viewFavItemAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_view_picture);


        position = getIntent().getIntExtra("position", -1);
        favoriteList = FavoriteDataHolder.getInstance().getFavoriteList();

        if (favoriteList == null || favoriteList.isEmpty() || position < 0) {
            Toast.makeText(this, "No Fav. Data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        initView();
        vpFullPhoto.setOnClickListener(v -> toggleFavVisibility());
    }

    public void toggleFavVisibility() {
        int visibility = (layoutTop.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
        layoutTop.setVisibility(visibility);
        layoutBottom.setVisibility(visibility);
        mainLayout.setBackgroundColor(visibility == View.VISIBLE ? Color.WHITE : Color.BLACK);


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
        llBottomSheet = findViewById(R.id.ll_img_bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        txtImgDateTime = findViewById(R.id.txt_img_dateTime);
        txtImgName = findViewById(R.id.txt_imgName);
        txtImgMp = findViewById(R.id.txt_imgMP);
        txtImgResolution = findViewById(R.id.txt_imgResolution);
        txtImgOnDeviceSize = findViewById(R.id.txt_img_onDeviceSize);
        txtImgFilePath = findViewById(R.id.txt_img_filePath);

        dbHelper = new FavDbHelper(this);


        if ( favoriteList != null && !favoriteList.isEmpty()) {
            if (viewFavItemAdapter == null) {
                viewFavItemAdapter = new VPFavItemAdapter(this, favoriteList);
                vpFullPhoto.setAdapter(viewFavItemAdapter);
            }

            vpFullPhoto.setCurrentItem(position);

            viewFavItemAdapter.notifyDataSetChanged();

            vpFullPhoto.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }



    }

    private void showFavItemProperties(int viewPosition) {
        String imgPath  = favoriteList.getFirst().getPath();
    }


    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}