package com.example.galleryapp.main.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Activity.ViewVideoActivity;
import com.example.galleryapp.main.Model.VideoModel;

import java.util.ArrayList;

public class VPVideoAdapter extends PagerAdapter {

    private static final String TAG = "VPVideoAdapter";
    private final Context context;
//    public static ArrayList<String> videoListPA;
    public ArrayList<VideoModel> videoModelArrayList;
    private VideoView currentVideoView; // To keep track of the currently playing video

    public VPVideoAdapter(Context context, ArrayList<VideoModel> videoModelArrayList) {
        this.context = context;
        this.videoModelArrayList = videoModelArrayList;
    }

/*    public VPVideoAdapter(Context context, ArrayList<String> videoList) {
        this.context = context;
        this.videoListPA = videoList;
    }*/

    @Override
    public int getCount() {
        return videoModelArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        String vidPath = videoListPA.get(position);
        VideoModel videoModel = videoModelArrayList.get(position);

        View view = LayoutInflater.from(context).inflate(R.layout.item_video_slider, container, false);

        VideoView videoView = view.findViewById(R.id.video_view);
        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

//        videoView.setVideoURI(Uri.parse(vidPath));
        videoView.setVideoURI(Uri.parse(videoModel.getPath()));


        videoView.setOnClickListener(view1 -> {
            if (context instanceof ViewVideoActivity) {
                if (videoView.isPlaying()) {
                    ((ViewVideoActivity) context).toggleVisibility();
                }
            }
        });

        if (currentVideoView != null) {
            currentVideoView.pause(); // Pause any previously playing video when a new one is loaded
        }
        currentVideoView = videoView;

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        // Stop the video when the user swipes away from this video
        View view = (View) object;
        VideoView videoView = view.findViewById(R.id.video_view);
        if (videoView.isPlaying()) {
            videoView.stopPlayback(); // Stop the video from playing
        }
        container.removeView(view);
    }

    // Optional: Add a method to stop the current video externally
    public void stopCurrentVideo() {
        if (currentVideoView != null && currentVideoView.isPlaying()) {
            currentVideoView.pause();
        }
    }

}