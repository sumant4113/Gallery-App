package com.example.galleryapp.main.Activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ViewVideoActivity extends AppCompatActivity {

    private static final String TAG = "ViewVideoActivity";

    // BottomSheetProperty
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private LinearLayout llBottomSheet;

    private RelativeLayout mainLayoutVideo;
    private LinearLayout layoutTopVideo, layoutBottomVideo;

    private ImageView imgBackBtn, imgShare, imgEdit, imgFavorite, imgDelete, imgMore;
    private TextView txtVidDate, txtVidTime;
    private ViewPager vpFullVideo;
    private VPVideoAdapter vpVideoAdapter;

    // For FolderVideo To video
    private ArrayList<VideoModel> videoModelArrayList = new ArrayList<>();
    private TextView txtVidDateTime, txtVidName, txtVidMp, txtVidResolution, txtOnDeviceSize, txtFilePath;

    private int currentVidPosition;
    private boolean isWhiteBG = true;
    private int position;
    private GestureDetector gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);

        if (getIntent() != null) {
            Intent intent = getIntent();
            videoModelArrayList = intent.getParcelableArrayListExtra("video_path");
            position = intent.getIntExtra("position", -1);
        }

        initView();
        showFileProperties(position);
        // Set up Gesture Detector
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                toggleVisibility();
                return true;
            }
        });
        vpFullVideo.setOnClickListener(view -> toggleVisibility());
    }

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
        imgFavorite = findViewById(R.id.img_vid_favorite);
        imgBackBtn = findViewById(R.id.img_vid_backBtn);
        imgShare = findViewById(R.id.img_vid_share);
        imgEdit = findViewById(R.id.img_vid_edit);
        imgMore = findViewById(R.id.img_vid_more);
        imgDelete = findViewById(R.id.img_vid_delete);
        txtVidTime = findViewById(R.id.txt_vid_time);
        txtVidDate = findViewById(R.id.txt_vid_date);
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
        txtVidDateTime = findViewById(R.id.txt_vid_dateTime);
        txtFilePath = findViewById(R.id.txt_vid_filePath);
        txtOnDeviceSize = findViewById(R.id.txt_vid_onDeviceSize);

        if (videoModelArrayList != null && !videoModelArrayList.isEmpty()) {
            if (vpVideoAdapter == null) {
                vpVideoAdapter = new VPVideoAdapter(ViewVideoActivity.this, videoModelArrayList);
                vpFullVideo.setAdapter(vpVideoAdapter);
            }
            vpFullVideo.setCurrentItem(position);
            showFileProperties(position);
            vpVideoAdapter.notifyDataSetChanged();

            vpFullVideo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int p) {
                    currentVidPosition = p;
                    showFileProperties(p);

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
            showFileProperties(vpFullVideo.getCurrentItem());
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
        imgShare.setOnClickListener(v -> shareFile(vpFullVideo.getCurrentItem()));
        imgDelete.setOnClickListener(v -> deleteFile(vpFullVideo.getCurrentItem(), v));
        imgEdit.setOnClickListener(v -> renameFIle(vpFullVideo.getCurrentItem(), v));

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
//            Toast.makeText(this, "Video file not found", Toast.LENGTH_SHORT).show();
        }

    }

    private void deleteFile(int ViewPosition, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete File")
                .setMessage(videoModelArrayList.get(ViewPosition).getTitle())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("OK", (dialog, which) -> {
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            Long.parseLong(videoModelArrayList.get(ViewPosition).getId()));
                    File file = new File(videoModelArrayList.get(ViewPosition).getPath());
                    try {
                        boolean deleted = file.delete();
                        if (deleted) {
                            getApplicationContext().getContentResolver().delete(contentUri, null, null);
                            videoModelArrayList.remove(ViewPosition);
                            vpVideoAdapter.notifyDataSetChanged();
                            Snackbar.make(view, "File deleted.", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make( view, "File delete Fail.", Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).show();
    }

    private void renameFIle(int position, View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Rename to");

        // EditText
        EditText editText = new EditText(this);
        String path = videoModelArrayList.get(position).getPath();
        File file = new File(path);
        String videoName = file.getName();
//        videoName = videoName.substring(0, videoName.lastIndexOf("."));
        editText.setText(videoName);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);

        // LayoutParams for width and height control
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Width: fill parent
                LinearLayout.LayoutParams.WRAP_CONTENT // Height: wrap content
        );
// Optional: Add margins (adjust values as needed)
        int marginDp = 16; // 16dp margin
        float scale = getResources().getDisplayMetrics().density;
        int marginPx = (int) (marginDp * scale + 0.5f);
        params.setMargins(marginPx, marginPx, marginPx, marginPx);

        editText.setLayoutParams(params);
        editText.setGravity(Gravity.TOP | Gravity.START); // Text alignment
        editText.setSingleLine(false); // For older devices (API < 23)
        editText.setHorizontallyScrolling(false); // Prevent horizontal scrolling

        // Create a LinearLayout to hold the EditText (for better control)
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(editText);

        alertDialog.setView(linearLayout);
        editText.requestFocus();

        alertDialog.setPositiveButton("OK", (dialog, which) -> {
            String newName = editText.getText().toString().trim();
            if (newName.isEmpty()) {
                Snackbar.make(view, "Rename failed. New name is empty.", Snackbar.LENGTH_LONG).show();
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
                /*SystemClock.sleep(200);
                    ((Activity) getApplicationContext()).recreate();*/
            } else {
                Snackbar.make(view, "Rename Failed", Snackbar.LENGTH_LONG).show();
            }
        });

        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        alertDialog.create().show();
    }

    private void showFileProperties(int ViewPosition) {
        String vidName = videoModelArrayList.get(ViewPosition).getTitle();
        String vidId = videoModelArrayList.get(ViewPosition).getId();
        String vidDisplayName = videoModelArrayList.get(ViewPosition).getDisplayName();
        String vidPath = videoModelArrayList.get(ViewPosition).getPath();
        String vidResolution = videoModelArrayList.get(ViewPosition).getResolution();
        String duration = videoModelArrayList.get(ViewPosition).getDuration();
        String widthHeight = videoModelArrayList.get(ViewPosition).getWidthHeight();
        String vidMp = videoModelArrayList.get(ViewPosition).getDataAdded();
        String vidSize = videoModelArrayList.get(ViewPosition).getSize();

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

            txtOnDeviceSize.setText("On Device (" + vidSize + ")");
        } catch (NumberFormatException e) {
            // Handle the exception in case the input is invalid
            Log.e("ViewVideoActivity", "Invalid video size format: " + vidSize, e);
            txtOnDeviceSize.setText("Unknown size");
        }

        long dateTakenMillisecond = Long.parseLong(vidMp) * 1000;
        Date dateTakenVid = new Date(dateTakenMillisecond);

        SimpleDateFormat vidDateTimeFormatter = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a", Locale.getDefault());
        SimpleDateFormat vidTimeFormatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        SimpleDateFormat vidDateFormatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

        String vidFormattedDateTime = vidDateTimeFormatter.format(dateTakenVid);
        String vidFormattedTime = vidTimeFormatter.format(dateTakenVid);
        String vidFormattedDate = vidDateFormatter.format(dateTakenVid);

        txtVidDate.setText(vidFormattedDate);
        txtVidTime.setText(vidFormattedTime);
        txtVidDateTime.setText(vidFormattedDateTime);

        txtVidName.setText(vidDisplayName);
        txtVidMp.setText(duration);
        txtVidResolution.setText(vidResolution);
        txtFilePath.setText(vidPath);

//        Toast.makeText(this, "File Name : " + vidName + "Id : " + vidId + "Display Name : " + vidDisplayName + "Path : " + vidPath, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "showFileProperties: +-+-name" + vidName);            // Only Name
        Log.d(TAG, "showFileProperties: +-+-id" + vidId);               // Id
        Log.d(TAG, "showFileProperties: +-+-DisName" + vidDisplayName); // Name With extension MP3 Mp4
        Log.d(TAG, "showFileProperties: +-+-path" + vidPath);       // Full path
        Log.d(TAG, "showFileProperties: +-+-vidMp" + vidMp);        // Image Size number
        Log.d(TAG, "showFileProperties: +-+-Reso" + vidResolution);  // Give Resolution (Width x Height)
        Log.d(TAG, "showFileProperties: +-+-size" + vidSize);       // Size in MP
        Log.d(TAG, "showFileProperties: +-+-size" + humanCanRead); // Size in B
        Log.d(TAG, "showFileProperties: +-+-duration" + duration); // Duration Time
        Log.d(TAG, "showFileProperties: +-+-wh" + widthHeight);     // not need

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
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
        // Activity Stop then Video Stop
        if (vpVideoAdapter != null) {
            vpVideoAdapter.stopCurrentVideo();
            finish();
        }
        finish();
    }

    private void releaseMediaPlayer() {
        VideoView videoView = findViewById(R.id.video_view);
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }


}