package com.example.galleryapp.main.Activity;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Adapter.VPVideoAdapter;
import com.example.galleryapp.main.Model.VideoModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ViewVideoActivity extends AppCompatActivity {

    private static final String TAG = "ViewVideoActivity";

    // BottomSheetProperty
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private LinearLayout llBottomSheet;

    private RelativeLayout mainLayoutVideo;
    private LinearLayout layoutTopVideo, layoutBottomVideo;

    private ImageView imgBackBtn, imgShare, imgEdit, imgFavorite, imgDelete, imgMore;
    private TextView txtImgDate, txtImgTime;
    private ViewPager vpFullVideo;
    private VPVideoAdapter vpVideoAdapter;

    private ArrayList<String> videoArrayList = new ArrayList<>();
    // For FolderVideo To video
    private ArrayList<VideoModel> videoModelArrayList = new ArrayList<>();

    private TextView txtDateTime, txtVidName, txtVidMp, txtVidResolution, txtOnDeviceSize, txtFilePath;
    //    public static ImageView imgPlayVideoBtn;
    private boolean isWhiteBG = true;
    int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);

        initView();
        showFileProperties(position);
        vpFullVideo.setOnClickListener(view -> toggleVisibility());
    }

    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }*/

    public void toggleVisibility() {
        if (!isWhiteBG) {
            layoutTopVideo.setVisibility(View.GONE);
            layoutBottomVideo.setVisibility(View.GONE);
            mainLayoutVideo.setBackgroundColor(Color.BLACK);

            isWhiteBG = false;
        } else {
            layoutTopVideo.setVisibility(View.VISIBLE);
            layoutBottomVideo.setVisibility(View.VISIBLE);
            mainLayoutVideo.setBackgroundColor(Color.WHITE);

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
        vpFullVideo = findViewById(R.id.vpFullVideo);
//        imgPlayVideoBtn = findViewById(R.id.img_play_videoBtn);

        mainLayoutVideo = findViewById(R.id.main_layout_video);
        layoutTopVideo = findViewById(R.id.layout_top_video);
        layoutBottomVideo = findViewById(R.id.layout_bottom_video);

        // Initialize BottomSheet
        llBottomSheet = findViewById(R.id.ll_bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        txtVidName = findViewById(R.id.txt_vidName);
        txtVidMp = findViewById(R.id.txt_vidMP);
        txtVidResolution = findViewById(R.id.txt_vidResolution);
        txtDateTime = findViewById(R.id.txt_dateTime);
        txtFilePath = findViewById(R.id.txt_filePath);
        txtOnDeviceSize = findViewById(R.id.txt_onDeviceSize);

        if (getIntent() != null) {
            Intent intent = getIntent();
            videoModelArrayList = intent.getParcelableArrayListExtra("video_path");
            position = intent.getIntExtra("position", -1);
        }

        if (videoModelArrayList != null && !videoModelArrayList.isEmpty()) {
            vpVideoAdapter = new VPVideoAdapter(ViewVideoActivity.this, videoModelArrayList);
            vpFullVideo.setAdapter(vpVideoAdapter);
            vpFullVideo.setCurrentItem(position);

            vpVideoAdapter.notifyDataSetChanged();

            vpFullVideo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                    if (isWhiteBG) {
                        mainLayoutVideo.setBackgroundColor(Color.BLACK);
                    }
                    // Stop the current video when a new page is selected
                    vpVideoAdapter.stopCurrentVideo();
                    VideoView videoView = findViewById(R.id.video_view);
                    if (videoView.isPlaying()) {
                        videoView.stopPlayback();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                    VideoView videoView = findViewById(R.id.video_view);
                    if (videoView.isPlaying()) {
                        videoView.stopPlayback();
                        vpVideoAdapter.stopCurrentVideo();
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
        imgBackBtn.setOnClickListener(v -> onBackPressed());
        imgShare.setOnClickListener(v -> {
            shareFile(position);
        });
        imgDelete.setOnClickListener(v -> {
            deleteFile(position, v);
        });
        imgEdit.setOnClickListener(v -> {
            renameFIle(position, v);
        });

    }

    private void shareFile(int position) {
        String videoPath = videoModelArrayList.get(position).getPath();
        File file = new File(videoPath);
        if (file.exists()) {
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("video/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Share video via"));
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
        } else {
            // If any Error then...
            Toast.makeText(this, "Video file not found", Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteFile(int position, View view) {
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
    }

    private void renameFIle(int position, View view) {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.rename_layout);
        final EditText etRenameFile = dialog.findViewById(R.id.et_renameFile);
        Button btnRenameFile = dialog.findViewById(R.id.btn_renameFile);
        Button btnCancelRename = dialog.findViewById(R.id.btn_cancelRename);

        final File renameFile = new File(videoModelArrayList.get(position).getPath());
        String nameText = renameFile.getName();
        nameText = nameText.substring(nameText.lastIndexOf("."));
        etRenameFile.setText(nameText);
        etRenameFile.clearFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        btnCancelRename.setOnClickListener(v -> {
            dialog.cancel();
        });

        btnRenameFile.setOnClickListener(v -> {
            String onlyPath = renameFile.getParentFile().getAbsolutePath();
            String ext = renameFile.getAbsolutePath();
            ext = ext.substring(0, ext.lastIndexOf("."));
            String newPath = onlyPath + "/" + etRenameFile.getText().toString() + ext;
            File newFile = new File(newPath);
            boolean rename = renameFile.renameTo(newFile);

            if (rename) {
                getApplicationContext().getContentResolver().delete(
                        MediaStore.Files.getContentUri("external"),
                        MediaStore.MediaColumns.DATA + "=?",
                        new String[]{renameFile.getAbsolutePath()}
                );
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(newFile));
                getApplicationContext().sendBroadcast(intent);
                Snackbar.make(view, "Rename Successfully.", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(view, "Rename Failed.", Snackbar.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showFileProperties(int position) {
        String vidName = videoModelArrayList.get(position).getTitle();
        String vidId = videoModelArrayList.get(position).getId();
        String vidDisplayName = videoModelArrayList.get(position).getDisplayName();
        String vidPath = videoModelArrayList.get(position).getPath();
        String vidResolution = videoModelArrayList.get(position).getResolution();
//        String vidsize = videoModelArrayList.get(position).getSize();
        String duration = videoModelArrayList.get(position).getDuration();
        String widthHeight = videoModelArrayList.get(position).getWidthHeight();
        String vidMp = videoModelArrayList.get(position).getDataAdded();

     /*   // Size
        String humanCanRead = null;
        long vidSize = Long.parseLong(vidsize);
        try {
            if (vidSize < 1024) {
                humanCanRead = String.format(getString(R.string.size_bytes), (double) vidSize);
            } else if (vidSize < Math.pow(1024, 2)) {
                humanCanRead = String.format(getString(R.string.size_kb), (double) vidSize / 1024);
            } else if (vidSize < Math.pow(1024, 3)) {
                humanCanRead = String.format(getString(R.string.size_mb), (double) vidSize / Math.pow(1024, 2));
            } else {
                humanCanRead = String.format(getString(R.string.size_gb), (double) vidSize / Math.pow(1024, 3));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
        String vidSize = videoModelArrayList.get(position).getSize();
        String sizeWithoutUnits = vidSize.replaceAll("[^0-9.]", ""); // Remove non-numeric characters except for decimal points

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

            txtOnDeviceSize.setText("On Device (" + humanCanRead + ")");
        } catch (NumberFormatException e) {
            // Handle the exception in case the input is invalid
            Log.e("ViewVideoActivity", "Invalid video size format: " + vidSize, e);
            txtOnDeviceSize.setText("Unknown size");
        }

        // Date and Time
        Instant instant = Instant.ofEpochSecond(Long.parseLong(vidMp));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy hh:mm a").withZone(ZoneId.systemDefault());
        String formattedDateTime = dateTimeFormatter.format(instant); // Get the formatted date as a string

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
        String formattedTime = timeFormatter.format(instant);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy").withZone(ZoneId.systemDefault());
        String formattedDate = dateFormatter.format(instant);

        txtImgDate.setText(formattedDate);
        txtImgTime.setText(formattedTime);
        txtDateTime.setText(formattedDateTime);

        txtVidName.setText(vidDisplayName);
        txtVidMp.setText(vidMp);
        txtVidResolution.setText(vidResolution);
        txtFilePath.setText(vidPath);

//        Toast.makeText(this, "File Name : " + vidName + "Id : " + vidId + "Display Name : " + vidDisplayName + "Path : " + vidPath, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "showFileProperties: +-+-name" + vidName);
        Log.d(TAG, "showFileProperties: +-+-id" + vidId);
        Log.d(TAG, "showFileProperties: +-+-DisName" + vidDisplayName);
        Log.d(TAG, "showFileProperties: +-+-path" + vidPath);
        Log.d(TAG, "showFileProperties: +-+-vidMp" + vidMp);
        Log.d(TAG, "showFileProperties: +-+-Reso" + vidResolution);
        Log.d(TAG, "showFileProperties: +-+-size" + vidSize);
        Log.d(TAG, "showFileProperties: +-+-size" + humanCanRead);
        Log.d(TAG, "showFileProperties: +-+-duration" + duration);
        Log.d(TAG, "showFileProperties: +-+-wh" + widthHeight);

    }

    // SetUp Bottom Sheet Behavior
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        VideoView videoView = findViewById(R.id.video_view);
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            if (videoView.isPlaying()) {
                videoView.stopPlayback();
//                finish();
            }
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Ensure the video stops when the activity is stopped
        if (vpVideoAdapter != null) {
            vpVideoAdapter.stopCurrentVideo();
            finish();
        }
        finish();
    }


}