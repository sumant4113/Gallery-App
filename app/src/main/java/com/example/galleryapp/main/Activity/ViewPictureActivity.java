package com.example.galleryapp.main.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ComponentCaller;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.IntentSender;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Adapter.VPPhotoAdapter;
import com.example.galleryapp.main.InfoUtil;
import com.example.galleryapp.main.Model.ImageDataHolder;
import com.example.galleryapp.main.Model.ImageModel;
import com.example.galleryapp.main.room.FavoriteItem;
import com.example.galleryapp.main.sqlite.FavDbHelper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

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

    private boolean isWhiteBG = false;

    private ArrayList<ImageModel> imageModelArrayList = new ArrayList<>();

    private FavoriteItem item;
    private String currentItemUri; // URI of current item, this for fav.item
    private ImageView imgBackBtn, imgShare, imgEdit, imgFavorite, imgDelete, imgMore;
    private TextView txtImgDate, txtImgTime, txtImgDateTime, txtImgName,
            txtImgMp, txtImgResolution, txtImgOnDeviceSize, txtImgFilePath;
    private int position;
    private GestureDetector gestureDetector;

    private FavDbHelper dbHelper;
    private ArrayList<ImageModel> favModelList = new ArrayList<>();
    private ImageModel currentImage;

    private static final int DELETE_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        position = getIntent().getIntExtra("position", -1);
        imageModelArrayList = ImageDataHolder.getInstance().getImageList();
        if (imageModelArrayList == null || imageModelArrayList.isEmpty() || position < 0) {
            Toast.makeText(this, "Image Data not available.", Toast.LENGTH_SHORT).show();
            finish();
        }

//        if (getIntent().hasExtra("image_path") && getIntent().getParcelableArrayListExtra("image_path") != null) {
//            imageModelArrayList = getIntent().getParcelableArrayListExtra("image_path");
//            position = getIntent().getIntExtra("position", -1);
//        } else {
//             Handle the case where there is no data passed
//            Toast.makeText(this, "No image data found", Toast.LENGTH_SHORT).show();
//            finish(); // Exit activity if no data is found
//        }

        initView();

        updateLikeState(position);
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
//        toggleSystemUI(false);
        vpFullPhoto.setOnClickListener(view -> toggleVisibility());
    }

    private void enterFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            getWindow().getInsetsController().hide(WindowInsetsCompat.Type.statusBars());
            getWindow().getInsetsController().hide(WindowInsetsCompat.Type.navigationBars());
        }
    }

    private void enableEdgeToEdge() {
        Window window = getWindow();

        // Set the window to full screen and transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);
        }
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        // Set navigation bar and status bar to be transparent
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }

    public void toggleVisibility() {
        int visibility = (layoutTop.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
        layoutTop.setVisibility(visibility);
        layoutBottom.setVisibility(visibility);
        mainLayout.setBackgroundColor(visibility == View.VISIBLE ? Color.WHITE : Color.BLACK);

        if (visibility == View.VISIBLE) {
            exitFullScreen();
//            toggleSystemUI(true);
        } else {
            enterFullScreen();
//            toggleSystemUI(false);
        }
    }

    private void exitFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            getWindow().getInsetsController().show(WindowInsetsCompat.Type.statusBars());
            getWindow().getInsetsController().show(WindowInsetsCompat.Type.navigationBars());
        }
    }

    private void enterFullScreenForViewPager() {
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Set the ViewPager to edge-to-edge
        vpFullPhoto.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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

//        currentItemUri = getIntent().getStringExtra("ITEM_URI");

        dbHelper = new FavDbHelper(this);

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
                    currentImage = getImageAtListPosition(position);

                    showImageProperties(p);
                    updateLikeState(p);
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
        imgDelete.setOnClickListener(v -> showDeleteDailog(vpFullPhoto.getCurrentItem()));
        imgEdit.setOnClickListener(v -> renameFile(vpFullPhoto.getCurrentItem(), v));
        imgFavorite.setOnClickListener(v -> addFavorite(vpFullPhoto.getCurrentItem()));

        Log.d(TAG, "onCreate: Total list : " + imageModelArrayList.size());
    }

    private ImageModel getImageAtListPosition(int position) {
        return ImageDataHolder.getInstance().getImageAt(position);
    }

    private void addFavorite(int currentItem) {
        if (dbHelper == null) {
            Toast.makeText(this, "Database is not initilised", Toast.LENGTH_SHORT).show();
            return;
        }
        currentImage = getImageAtListPosition(currentItem); // Get the current image based on the position
        if (currentImage == null) {
            Toast.makeText(this, "No image loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = currentImage.getId();
        String path = currentImage.getPath();
        String title = currentImage.getTitle();
        String type = "image"; // or "video" based on your logic

        if (dbHelper.isFavorite(id)) {
            dbHelper.removeFav(id);
            Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
            imgFavorite.setImageResource(R.drawable.img_heart); // Change to white heart
        } else {
            dbHelper.addFav(id, path, type, title);
            Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
            imgFavorite.setImageResource(R.drawable.img_heart_filled); // Change to red heart
        }
    }

    private void removeFavorite(int currentItem) {
        currentImage = getImageAtListPosition(currentItem); // Get the current image based on the position
        if (currentImage == null) {
            Toast.makeText(this, "No Image", Toast.LENGTH_SHORT).show();
            return;
        }
        String id = currentImage.getId();

        if (dbHelper.isFavorite(id)) {
            dbHelper.removeFav(id);
            updateLikeState(currentItem);
            Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_LONG).show();
        }
    }

    private void updateLikeState(int position) {
        if (dbHelper == null) {
            Toast.makeText(this, "Database is not initialised", Toast.LENGTH_SHORT).show();
            return;
        }
        currentImage = getImageAtListPosition(position);
        if (currentImage == null) {
            imgFavorite.setImageResource(R.drawable.img_heart);
            return;
        }

        String id = currentImage.getId();
        if (dbHelper.isFavorite(id)) {
            imgFavorite.setImageResource(R.drawable.img_heart_filled);
        } else {
            imgFavorite.setImageResource(R.drawable.img_heart);
        }
    }

    private void shareFile(int position) {
        String imagePath = ImageDataHolder.getInstance().getImageList().get(position).getPath();
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

    private void showDeleteDailog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete this Item?")
                .setMessage("Are you sure you want to delete this image?")
                .setNegativeButton("Cansel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> deletePhoto(position))
                .create()
                .show();
    }

    // Delete Images
    private void deletePhoto(int viewPosition) {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(imageModelArrayList.get(viewPosition).getId())
        );
        removeFavorite(viewPosition); // Ensure the favorite is removed first

        try {
            getContentResolver().delete(contentUri, null, null);
            handleSuccessfulDeletion(viewPosition);
        } catch (RecoverableSecurityException e) {
            // Request user consent for deletion
            PendingIntent pendingIntent = e.getUserAction().getActionIntent();
            try {
                startIntentSenderForResult(
                        pendingIntent.getIntentSender(),
                        DELETE_REQUEST_CODE,
                        null, 0, 0, 0
                );
            } catch (IntentSender.SendIntentException sendEx) {
                Log.d(TAG, "deleteFile: +-+- Failed to request user permission for file deletion : " + sendEx.getMessage());
                Snackbar.make(vpFullPhoto, "Unable to delete file.", Snackbar.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
//                        Log.d(TAG, "deleteFile: +-+-" + e.getMessage());
            Snackbar.make(vpFullPhoto, "Error: Unable to delete file.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void handleSuccessfulDeletion(int viewPosition) {
        imageModelArrayList.remove(viewPosition);
        removeFavorite(vpFullPhoto.getCurrentItem());
        // Check if the list is now empty
        if (imageModelArrayList.isEmpty()) {
            Toast.makeText(this, "No images left!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Notify user and update UI
        Toast.makeText(this, "Image Deleted", Toast.LENGTH_SHORT).show();
        viewPagerPhotoAdapter.notifyDataSetChanged();
        finish();
        int newPos = Math.min(viewPosition, imageModelArrayList.size()); // Set the next image or previous image in the ViewPager
        vpFullPhoto.setCurrentItem(newPos);
//        vpFullPhoto.setCurrentItem(newPos, false);
        Snackbar.make(vpFullPhoto, "File deleted successfully.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data, @NonNull ComponentCaller caller) {
        super.onActivityResult(requestCode, resultCode, data, caller);
        if (resultCode == DELETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int currentPosition = vpFullPhoto.getCurrentItem();
                handleSuccessfulDeletion(currentPosition);
                removeFavorite(currentPosition);
                finish();
            } else {
                Snackbar.make(vpFullPhoto, "File deletion canceled by user.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

 /*   @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DELETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Successfully granted permission
                Toast.makeText(this, "File deleted after user consent", Toast.LENGTH_SHORT).show();
                // Remove item from the list and update UI
                int currentPosition = vpFullPhoto.getCurrentItem();
                imageModelArrayList.remove(currentPosition);
                if (imageModelArrayList.isEmpty()) {
                    finish();
                    return;
                }
                viewPagerPhotoAdapter.notifyDataSetChanged();
                // Set new position for ViewPager
                int newPos = Math.min(currentPosition, imageModelArrayList.size() - 1);
                vpFullPhoto.setCurrentItem(newPos, false);

                Log.d(TAG, "onActivityResult:Dontknow  delete list count : " + imageModelArrayList.size());
            } else {
                Snackbar.make(vpFullPhoto, "File deletion canceled by user.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }*/

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

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void showImageProperties(int viewPosition) {
        String imgId = imageModelArrayList.get(viewPosition).getId();
        String imgPath = imageModelArrayList.get(viewPosition).getPath();
        String imgName = imageModelArrayList.get(viewPosition).getTitle();
        String imgResolution = imageModelArrayList.get(viewPosition).getResolution();
        String imgDateTaken = imageModelArrayList.get(viewPosition).getDateTaken();
        String imgSize = imageModelArrayList.get(viewPosition).getSize();

        Uri uri = Uri.fromFile(new File(imgPath));
        // Retrieve and display image details
        InfoUtil.InfoItem fileSizeItem = InfoUtil.retrieveFileSize(this, uri);
        String fileSize = fileSizeItem != null ? fileSizeItem.getValue() : "Unknown Size";

        InfoUtil.DateItem dateItem = InfoUtil.retrieveFormattedDateAndTime(this, imgDateTaken);
        String fileDate = dateItem.date();
        String fileTime = dateItem.time();
        String fileDateTime = dateItem.dateTime();

        txtImgDateTime.setText(fileDateTime);
        txtImgTime.setText(fileTime);
        txtImgDate.setText(fileDate);
        txtImgFilePath.setText(imgPath);
        txtImgResolution.setText(imgResolution + "px");
        txtImgName.setText(imgName);
        txtImgOnDeviceSize.setText("On Device (" + fileSize + ")");
        txtImgMp.setText("IOS ");

     /*   Log.d(TAG, "showImageProperties: +-+- id" + imgId);
        Log.d(TAG, "showImageProperties: +-+- path" + imgPath);
        Log.d(TAG, "showImageProperties: +-+- name" + imgName);
        Log.d(TAG, "showImageProperties: +-+- resu" + imgResolution);
        Log.d(TAG, "showImageProperties: +-+- dateTaken" + imgDateTaken);
        Log.d(TAG, "showImageProperties: +-+- size" + imgSize);*/

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}