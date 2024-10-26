package com.example.galleryapp.main.Activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ViewPictureActivity extends AppCompatActivity {

    private static final String TAG = "BottomSheet";

    // BottomSheetProperty
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private LinearLayout llBottomSheet;

    public LinearLayout layoutBottom, layoutTop;
    private ViewPager vpFullPhoto;
    private RelativeLayout mainLayout;
    private VPPhotoAdapter viewPagerPhotoAdapter;

    private int currentPosition; // hold current position ViewPager
    private boolean isWhiteBG = false;

    private ArrayList<ImageModel> imageModelArrayList = new ArrayList<>();

    private ImageView imgBackBtn, imgShare, imgEdit, imgFavorite, imgDelete, imgMore;
    private TextView txtImgDate, txtImgTime, txtImgDateTime, txtImgName, txtImgMp, txtImgResolution, txtImgOnDeviceSize, txtImgFilePath;
    private int position;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        if (getIntent().hasExtra("image_path") && getIntent().getParcelableArrayListExtra("image_path") != null) {
            imageModelArrayList = getIntent().getParcelableArrayListExtra("image_path");
            position = getIntent().getIntExtra("position", -1);
        } else {
            // Handle the case where there is no data passed
            Toast.makeText(this, "No image data found", Toast.LENGTH_SHORT).show();
            finish(); // Exit activity if no data is found
        }
        initView();
        showImageProperties((position));

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
            showImageProperties(position); // Show properties for the initial image
            viewPagerPhotoAdapter.notifyDataSetChanged();

            vpFullPhoto.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int p) {
                    currentPosition = p;
                    showImageProperties(p);

                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                    if (isWhiteBG) {
                        mainLayout.setBackgroundColor(Color.BLACK);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
//                    showImageProperties(currentPosition);
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                }

            });
        }


        setBottomSheetBehavior();
        imgMore.setOnClickListener(v -> {
            showImageProperties(vpFullPhoto.getCurrentItem()); // Ensure we show properties for the current image
//            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);

            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                // Expand the bottom sheet
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                // Collapse the bottom sheet
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);  // Or use STATE_COLLAPSED
            }
        });

        imgBackBtn.setOnClickListener(v -> onBackPressed());
        imgShare.setOnClickListener(v -> shareFile(vpFullPhoto.getCurrentItem()));
        imgDelete.setOnClickListener(v -> deleteFile(vpFullPhoto.getCurrentItem(), v));
        imgEdit.setOnClickListener(v -> renameFile(vpFullPhoto.getCurrentItem(), v));
        imgFavorite.setOnClickListener(v -> addFavorite(vpFullPhoto.getCurrentItem()));
    }

    private void addFavorite(int currentItem) {

    }


    private void shareFile(int position) {
        String imagePath = imageModelArrayList.get(position).getPath();
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Rename :");

        EditText editText = new EditText(this);
        String path = imageModelArrayList.get(position).getPath();
        File file = new File(path);
        String imageName = file.getName();
        imageName = imageName.substring(0, imageName.lastIndexOf("."));
        editText.setText(imageName);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        int marginDp = 16;
        float scale = getResources().getDisplayMetrics().density;
        int marginPx = (int) (marginDp * scale + 0.5f);
        params.setMargins((marginPx), (marginPx), (marginPx), (marginPx));

        editText.setLayoutParams(params);
        editText.setGravity(Gravity.TOP | Gravity.START);
        editText.setSingleLine(false);
        editText.setHorizontallyScrolling(false);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(editText);

        alertDialog.setView(linearLayout);
        editText.requestFocus();

        alertDialog.setPositiveButton("Ok", (dialog, which) -> {
            String newName = editText.getText().toString().trim();
            if (newName.isEmpty()) {
                Snackbar.make(view, "Rename Failed. New name is Empty.", Snackbar.LENGTH_LONG).show();
                return;
            }
            String onlyPath = file.getParentFile().getAbsolutePath();
            String ext = file.getAbsolutePath();
            ext = ext.substring(ext.lastIndexOf("."));
            String newPath = onlyPath + "/" + newName + ext;
            File newFile = new File(newPath);
            boolean rename = file.renameTo(newFile);

            if (rename) {
                ContentResolver resolver = getApplicationContext().getContentResolver();
                resolver.delete(
                        MediaStore.Files.getContentUri("external"),
                        MediaStore.MediaColumns.DATA + "=?",
                        new String[]{file.getAbsolutePath()}
                );

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(newFile));
                getApplicationContext().sendBroadcast(intent);
                Snackbar.make(view, "Rename Successfully", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(view, "Rename Failed", Snackbar.LENGTH_LONG).show();
            }
        });
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertDialog.create().show();

    }

    private void showImageProperties(int viewPosition) {
        String imgId = imageModelArrayList.get(viewPosition).getId();
        String imgPath = imageModelArrayList.get(viewPosition).getPath();
        String imgName = imageModelArrayList.get(viewPosition).getTitle();
        String imgResolution = imageModelArrayList.get(viewPosition).getResolution();
        String imgDateTaken = imageModelArrayList.get(viewPosition).getDateTaken();
        String imgSize = imageModelArrayList.get(viewPosition).getSize();

        String sizeWithoutUnits = imgSize.replaceAll("[^0-9.]", ""); // Remove non-numeric characters except for decimal points
        String humanCanRead = null;
        try {
            double sizeInBytes = Double.parseDouble(sizeWithoutUnits);

            if (sizeInBytes < 1024) {
                humanCanRead = String.format("%.2f B", sizeInBytes);
            } else if (sizeInBytes < 1024 * 1024) {
                humanCanRead = String.format("%.2f KB", sizeInBytes / (1024.0));
            } else if (sizeInBytes < 1024 * 1024 * 1024) {
                humanCanRead = String.format("%.2f MB", sizeInBytes / (1024.0 * 1024));
            } else {
                humanCanRead = String.format("%.2f GB", sizeInBytes / (1024.0 * 1024 * 1024));
            }

            txtImgOnDeviceSize.setText("On Device (" + humanCanRead + ")");
        } catch (NumberFormatException e) {
            // Handle the exception in case the input is invalid
            Log.e("ViewVideoActivity", "Invalid video size format: " + imgSize, e);
            txtImgOnDeviceSize.setText("Unknown size");
        }

        // Convert the date taken from String to long
        long dateTakenMillis = Long.parseLong(imgDateTaken) * 1000; // Convert seconds to milliseconds
        Date dateTaken = new Date(dateTakenMillis);

        // Create SimpleDateFormat instances for formatting
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a", Locale.getDefault());
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

        // Format the date
        String formattedDateTime = dateTimeFormatter.format(dateTaken);
        String formattedTime = timeFormatter.format(dateTaken);
        String formattedDate = dateFormatter.format(dateTaken);

        // Set the formatted date and time to the TextViews
        txtImgDateTime.setText(formattedDateTime);
        txtImgTime.setText(formattedTime);
        txtImgDate.setText(formattedDate);
        /*// Date and Time
        Instant instant = Instant.ofEpochSecond(Long.parseLong(imgDateTaken));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy hh:mm a").withZone(ZoneId.systemDefault());
        String formattedDateTime = dateTimeFormatter.format(instant); // Get the formatted date as a string
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a").withZone(ZoneId.systemDefault());
        String formattedTime = timeFormatter.format(instant);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy").withZone(ZoneId.systemDefault());
        String formattedDate = dateFormatter.format(instant);

        txtImgDateTime.setText(formattedDateTime);
        txtImgTime.setText(formattedTime);
        txtImgDate.setText(formattedDate);*/

        txtImgFilePath.setText(imgPath);
//        txtImgOnDeviceSize.setText(imgSize);
        txtImgMp.setText(imgSize);
//        txtImgResolution.setText(String.format("Size of ImageView: Height: %s Width: %s", String.valueOf(vpFullPhoto.getWidth()), String.valueOf(vpFullPhoto.getHeight())));
        txtImgResolution.setText(imgResolution + "px");
        txtImgName.setText(imgName);

        Log.d(TAG, "showImageProperties: +-+- id" + imgId);
        Log.d(TAG, "showImageProperties: +-+- path" + imgPath);
        Log.d(TAG, "showImageProperties: +-+- name" + imgName);
        Log.d(TAG, "showImageProperties: +-+- resu" + imgResolution);
        Log.d(TAG, "showImageProperties: +-+- dateTaken" + imgDateTaken);
        Log.d(TAG, "showImageProperties: +-+- size" + imgSize);
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