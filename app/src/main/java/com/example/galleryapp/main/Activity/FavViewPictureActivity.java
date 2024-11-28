package com.example.galleryapp.main.Activity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Adapter.VPFavItemAdapter;
import com.example.galleryapp.main.InfoUtil;
import com.example.galleryapp.main.Model.FavoriteDataHolder;
import com.example.galleryapp.main.Model.FavoriteModel;
import com.example.galleryapp.main.sqlite.FavDbHelper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class FavViewPictureActivity extends AppCompatActivity {

    private static final String TAG = "FavViewPictureActivity";


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
    private String itemPath;
    private int position;
    private ArrayList<FavoriteModel> favoriteList;
    private VPFavItemAdapter viewFavItemAdapter;
    private static final int DELETE_REQUEST_CODE = 1301;


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
//        getImageDetails(itemPath);
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


        if (favoriteList != null && !favoriteList.isEmpty()) {
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
                    itemPath = favoriteList.get(position).getPath();
                    if (itemPath != null) {
                        getImageDetails(itemPath);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }
        imgMore.setOnClickListener(v -> {
            itemPath = favoriteList.get(vpFullPhoto.getCurrentItem()).getPath();

            if (itemPath != null) {   // If the itemPath is not null, load the image details
                getImageDetails(itemPath);
            } else {
                Toast.makeText(this, "Image path is null", Toast.LENGTH_SHORT).show();
            }
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
        imgDelete.setOnClickListener(v -> showDeleteDialog(vpFullPhoto.getCurrentItem()));
        imgEdit.setOnClickListener(v -> renameFile(vpFullPhoto.getCurrentItem()));
        imgFavorite.setOnClickListener(v -> addFavorite(vpFullPhoto.getCurrentItem()));
    }

    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete this Item?")
                .setMessage("Are you sure you want to delete this image?")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> deletePhoto(position))
                .create()
                .show();
    }

    private void deletePhoto(int position) {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(favoriteList.get(position).getPath()));

        removeFavorite(position); // Ensure the favorite is removed first

        try {
            getContentResolver().delete(contentUri, null, null);
            handleSuccessfulDeletion(position);
        } catch (RecoverableSecurityException e) {
            PendingIntent pendingIntent = e.getUserAction().getActionIntent();
            try {
                startIntentSenderForResult(pendingIntent.getIntentSender(), DELETE_REQUEST_CODE, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException sendEx) {
                Log.d(TAG, "deleteFile: Failed to request user permission for file deletion : " + sendEx.getMessage());
                Snackbar.make(vpFullPhoto, "Unable to delete file.", Snackbar.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Snackbar.make(vpFullPhoto, "Error: Unable to delete file.", Snackbar.LENGTH_SHORT).show();
        }
    }


    private void handleSuccessfulDeletion(int position) {
        favoriteList.remove(position);
        removeFavorite(position);

        if (favoriteList.isEmpty()) {
            Toast.makeText(this, "No images left!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toast.makeText(this, "Image Deleted", Toast.LENGTH_SHORT).show();
        viewFavItemAdapter.notifyDataSetChanged();

        // Set the next position for ViewPager
        int newPos = Math.min(position, favoriteList.size() - 1);
        vpFullPhoto.setCurrentItem(newPos);
        Snackbar.make(vpFullPhoto, "File deleted successfully.", Snackbar.LENGTH_SHORT).show();
    }

    private void renameFile(int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Rename:");

        EditText editText = new EditText(this);
        String path = favoriteList.get(position).getPath();
        File file = new File(path);
        String imageName = file.getName();
        imageName = imageName.substring(0, imageName.lastIndexOf("."));
        editText.setText(imageName);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        alertDialog.setView(editText);
        editText.requestFocus();

        alertDialog.setPositiveButton("Ok", (dialog, which) -> {
            String newName = editText.getText().toString().trim();
            if (newName.isEmpty()) {
                Snackbar.make(vpFullPhoto, "Rename Failed. New name is Empty.", Snackbar.LENGTH_LONG).show();
                return;
            }
            String newPath = file.getParentFile().getAbsolutePath() + "/" + newName + ".jpg";  // Assume it's a jpg for simplicity
            File newFile = new File(newPath);
            boolean rename = file.renameTo(newFile);

            if (rename) {
                getContentResolver().delete(
                        MediaStore.Files.getContentUri("external"),
                        MediaStore.MediaColumns.DATA + "=?",
                        new String[]{file.getAbsolutePath()}
                );

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(newFile));
                sendBroadcast(intent);
                Snackbar.make(vpFullPhoto, "Rename Successful", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(vpFullPhoto, "Rename Failed", Snackbar.LENGTH_LONG).show();
            }
        });

        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertDialog.create().show();
    }

    private void addFavorite(int currentItem) {
        if (dbHelper == null) {
            Toast.makeText(this, "Database is not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        currentFavItem = favoriteList.get(currentItem);  // Get the current favorite item
        if (currentFavItem == null) {
            Toast.makeText(this, "No image loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = String.valueOf(favoriteList.get(vpFullPhoto.getId()));
        String path = currentFavItem.getPath();

        String title = "";
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
        currentFavItem = favoriteList.get(currentItem);  // Get the current favorite item
        if (currentFavItem == null) {
            Toast.makeText(this, "No Image", Toast.LENGTH_SHORT).show();
            return;
        }
//        String id = currentFavItem.getId();
        String id = String.valueOf(favoriteList.get(vpFullPhoto.getId()));

        if (dbHelper.isFavorite(id)) {
            dbHelper.removeFav(id);
            updateLikeState(currentItem);
            Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_LONG).show();
        }
    }

    private void updateLikeState(int position) {
        if (dbHelper == null) {
            Toast.makeText(this, "Database is not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        currentFavItem = favoriteList.get(position);  // Get the current favorite item
        if (currentFavItem == null) {
            imgFavorite.setImageResource(R.drawable.img_heart);  // Set default heart if no item found
            return;
        }

        String id = String.valueOf(favoriteList.get(vpFullPhoto.getId()));
        if (dbHelper.isFavorite(id)) {
            imgFavorite.setImageResource(R.drawable.img_heart_filled);  // Filled heart for favorite
        } else {
            imgFavorite.setImageResource(R.drawable.img_heart);  // Empty heart for not favorite
        }
    }

    private void shareFile(int position) {
        String imagePath = favoriteList.get(position).getPath();
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

    public void getImageDetails(String imagePath) {
        if (imagePath == null) {
            Toast.makeText(this, "Image path is null", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentResolver contentResolver = getContentResolver();

        // Define the columns you want to retrieve
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA, // Path
                MediaStore.Images.Media.DISPLAY_NAME, // Title
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.DATE_TAKEN, // Timestamp of capture
                MediaStore.Images.Media.DATE_ADDED
        };

        // Query MediaStore for a specific image
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = {imagePath};

        // Query MediaStore
        try (Cursor cursor = contentResolver.query(imageUri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                // Retrieve the values
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                String width = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));
                String height = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));
                String dateTaken = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
                String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));

                // Combine width and height
                String resolution = width + "x" + height;

                // Display properties
                showFavItemProperties(title, size, resolution, dateTaken, dateAdded, imagePath);
            }
        }
    }

    private void showFavItemProperties(String title, String size, String resolution, String dateTaken, String dateAdded, String path) {
        // Set UI elements with the retrieved data

        Uri uri = Uri.fromFile(new File(itemPath));
        // Retrieve and display image details
        InfoUtil.InfoItem fileSizeItem = InfoUtil.retrieveFileSize(this, uri);
        String fileSize = fileSizeItem != null ? fileSizeItem.getValue() : "Unknown Size";

        InfoUtil.DateItem dateItem = InfoUtil.retrieveFormattedDateAndTime(this, dateTaken);
        String fileDate = dateItem.date();
        String fileTime = dateItem.time();
        String fileDateTime = dateItem.dateTime();

        txtImgDateTime.setText(fileDateTime);
        txtImgTime.setText(fileTime);
        txtImgDate.setText(fileDate);
        txtImgName.setText(title);
        txtImgOnDeviceSize.setText("On Device (" + size + ")");
        txtImgResolution.setText(resolution + " px");
        txtImgFilePath.setText(path);

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