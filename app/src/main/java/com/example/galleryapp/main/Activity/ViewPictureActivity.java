package com.example.galleryapp.main.Activity;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
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
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private TextView txtImgDate,  txtImgTime, txtImgDateTime, txtImgName, txtImgMp, txtImgResolution, txtImgOnDeviceSize, txtImgFilePath;
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
        showImageProperties((position));
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
        llBottomSheet = findViewById(R.id.ll_img_bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        txtImgDateTime = findViewById(R.id.txt_img_dateTime);
        txtImgName = findViewById(R.id.txt_imgName);
        txtImgMp = findViewById(R.id.txt_imgMP);
        txtImgResolution = findViewById(R.id.txt_imgResolution);
        txtImgOnDeviceSize = findViewById(R.id.txt_img_onDeviceSize);
        txtImgFilePath = findViewById(R.id.txt_img_filePath);

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
//                    currentPosition = position;
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
            showImageProperties(position);
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
        imgDelete.setOnClickListener(v -> deleteFile(position, v));


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
    private void deleteFile(int viewPosition, View view) {
        // Make AlertDialog for delete that file
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this Item?")
                .setMessage(imageModelArrayList.get(viewPosition).getTitle())
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            Long.parseLong(imageModelArrayList.get(viewPosition).getId()));
                    File file = new File(imageModelArrayList.get(viewPosition).getPath());

                    boolean deleted = file.delete();

                    if (deleted && file.exists()) {
                        getApplicationContext().getContentResolver().delete(contentUri, null, null);
                        imageModelArrayList.remove(viewPosition);

                        if (imageModelArrayList.isEmpty()) {
                            Toast.makeText(this, "No images more!", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
//                        imageModelArrayList.notify();

                        viewPagerPhotoAdapter.notifyDataSetChanged();

                        if (imageModelArrayList.size() > 0) {
                            int newPos;
                            if (viewPosition == imageModelArrayList.size()) { // if  last image then show previous one
                                newPos = imageModelArrayList.size() - 1;
                            } else {
                                newPos = viewPosition; // Show the next image
                            }
                            vpFullPhoto.setCurrentItem(viewPosition, false);
                        } else {

                        }
                        Snackbar.make(view, "File deleted.", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(view, "File delete Fail.", Snackbar.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void renameFile(int position, View view) {

    }

    private void showImageProperties(int viewPosition) {
        String imgId = imageModelArrayList.get(viewPosition).getId();
        String imgPath = imageModelArrayList.get(viewPosition).getPath();
        String imgName = imageModelArrayList.get(viewPosition).getTitle();
        String imgSize = imageModelArrayList.get(viewPosition).getSize();
        String imgResolution = imageModelArrayList.get(viewPosition).getResolution();
        String imgDateTaken = imageModelArrayList.get(viewPosition).getDateTaken();

        String sizeWithoutUnits = imgSize.replaceAll("[^0-9.]", ""); // Remove non-numeric characters except for decimal points
        String humanCanRead = null;
        try {
            long sizeInBytes = (long) Double.parseDouble(sizeWithoutUnits);
            humanCanRead = null;

            if (sizeInBytes < 1024) {
                humanCanRead = String.format(getString(R.string.size_bytes), (double) sizeInBytes);
            } else if (sizeInBytes < Math.pow(1024, 2)) {
                humanCanRead = String.format(getString(R.string.size_kb), (double) sizeInBytes / 1024);
            } else if (sizeInBytes < Math.pow(1024, 3)) {
                humanCanRead = String.format(getString(R.string.size_mb), (double) sizeInBytes / Math.pow(1024, 2));
            } else {
                humanCanRead = String.format(getString(R.string.size_gb), (double) sizeInBytes / Math.pow(1024, 3));
            }

            txtImgOnDeviceSize.setText("On Device (" + humanCanRead + ")");
        } catch (NumberFormatException e) {
            // Handle the exception in case the input is invalid
            Log.e("ViewVideoActivity", "Invalid video size format: " + imgSize, e);
            txtImgOnDeviceSize.setText("Unknown size");
        }
        // Date and Time
        Instant instant = Instant.ofEpochSecond(Long.parseLong(imgDateTaken));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy hh:mm a").withZone(ZoneId.systemDefault());
        String formattedDateTime = dateTimeFormatter.format(instant); // Get the formatted date as a string

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
        String formattedTime = timeFormatter.format(instant);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy").withZone(ZoneId.systemDefault());
        String formattedDate = dateFormatter.format(instant);

        txtImgDateTime.setText(formattedDateTime);
        txtImgTime.setText(formattedTime);
        txtImgDate.setText(formattedDate);

        txtImgFilePath.setText(imgPath);
//        txtImgOnDeviceSize.setText(imgSize);
        txtImgMp.setText(imgSize);
        txtImgResolution.setText(imgResolution);
        txtImgName.setText(imgName);


        Log.d(TAG, "showImageProperties: +-+-" + imgDateTaken);
    }

    private void setBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.ll_img_bottomSheet));

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