package com.example.galleryapp.test.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.galleryapp.R;
import com.example.galleryapp.test.Adapter.VPVideoAdapter;

import java.util.ArrayList;

public class ViewVideoActivity extends AppCompatActivity {

    private RelativeLayout mainLayoutVideo;
    private LinearLayout layoutTopVideo, layoutBottomVideo;

    private ImageView imgBackBtn, imgShare, imgEdit, imgFavorite, imgDelete, imgMore;
    private TextView txtImgDate;
    private TextView txtImgTime;
    private ViewPager vpFullVideo;
    private VPVideoAdapter vpVideoAdapter;
    private ArrayList<String> videoArrayList = new ArrayList<>();
    //    public static ImageView imgPlayVideoBtn;
    private boolean isWhiteBG = true;
    int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_video);

        initView();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        vpFullVideo.setOnClickListener(view -> toggleVisibility());

        if (videoArrayList != null && !videoArrayList.isEmpty()) {
            vpVideoAdapter = new VPVideoAdapter(ViewVideoActivity.this, videoArrayList);
            vpFullVideo.setAdapter(vpVideoAdapter);
            vpFullVideo.setCurrentItem(position);

            vpVideoAdapter.notifyDataSetChanged();

            // Add a listener to detect page changes and stop the video when swiped
            vpFullVideo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    // Stop the current video when a new page is selected
                    vpVideoAdapter.stopCurrentVideo();
                    VideoView videoView = findViewById(R.id.video_view);
                    if (videoView.isPlaying()) {
                        videoView.stopPlayback();
                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    VideoView videoView = findViewById(R.id.video_view);
                    if (videoView.isPlaying()) {
                        videoView.stopPlayback();
                        vpVideoAdapter.stopCurrentVideo();
                    }
                }
            });
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
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


        imgBackBtn.setOnClickListener(v -> onBackPressed());


        if (getIntent() != null) {
            videoArrayList = getIntent().getStringArrayListExtra("video_path");
            position = getIntent().getIntExtra("position", -1);

            String path = videoArrayList.get(position);

        }
        /*// Add page change listener to ViewPager
        vpFullVideo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // When page changes, ensure video in the previous page is stopped
                View currentView = vpFullVideo.findViewWithTag("videoView" + position);
                if (currentView != null) {
                    VideoView videoView = currentView.findViewById(R.id.video_view);
                    if (videoView.isPlaying()) {
                        videoView.stopPlayback();
//                        ViewVideoActivity.imgPlayVideoBtn.setVisibility(View.VISIBLE); // Show play button again
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        if (videoList != null && !videoList.isEmpty()) {
            vpVideoAdapter = new VPVideoAdapter(ViewVideoActivity.this, videoList);
            vpFullVideo.setAdapter(vpVideoAdapter);
            vpFullVideo.setCurrentItem(position);
            vpVideoAdapter.notifyDataSetChanged();
        }*/

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        VideoView videoView = findViewById(R.id.video_view);
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
            finish();
        } else {
            finish();
        }
        finish();
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