package com.example.galleryapp.main.Fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Adapter.VideoRvAdapter;
import com.example.galleryapp.main.Model.VideoModel;

import java.util.ArrayList;

public class VideoFragment extends Fragment {

    private static final String TAG = "VideoFragment";
    private View view;

    private RecyclerView rvVideos;
    private VideoRvAdapter videoRvAdapter;
    private final ArrayList<VideoModel> videosList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshVideoFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_video, container, false);

        initView();
        loadVideos();  // Load videos from storage
        setupSwipeRefresh(); // Setup SwipeRefreshLayout
        return view;
    }

    private void initView() {
        rvVideos = view.findViewById(R.id.rv_video);
        swipeRefreshVideoFragment = view.findViewById(R.id.swipeRefresh_videoFragment);

        videoRvAdapter = new VideoRvAdapter(getContext(), videosList);
        // solve recycle view lag
        rvVideos.setHasFixedSize(true);
        rvVideos.setItemViewCacheSize(30);
        rvVideos.setDrawingCacheEnabled(true);
        rvVideos.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rvVideos.setNestedScrollingEnabled(false);

        rvVideos.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvVideos.setAdapter(videoRvAdapter);

        loadVideos();
    }

    private void setupSwipeRefresh() {
        swipeRefreshVideoFragment.setOnRefreshListener(() -> {
            loadVideos(); // Reload videos when swiped down
            swipeRefreshVideoFragment.setRefreshing(false); // Stop the refreshing animation
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private ArrayList<VideoModel> loadVideos() {
        ArrayList<VideoModel> videosList = new ArrayList<>();
        videosList.clear(); // Clear the previous list of videos

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Video.Media.DATE_ADDED + " DESC";

        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.RESOLUTION,
                MediaStore.Video.Media.DATE_TAKEN
        };

        try {
            Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, orderBy);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(0);
                    String path = cursor.getString(1);
                    String title = cursor.getString(2);
                    String size = cursor.getString(3);
                    String width_height = cursor.getString(4);
                    String duration = cursor.getString(5);
                    String displayName = cursor.getString(6);
                    String resolution = cursor.getString(7);
                    String dateTime = cursor.getString(8);

                    VideoModel videos = new VideoModel(id, path, title, size, width_height,
                            duration, displayName, resolution, dateTime);
                    videosList.add(videos);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        videoRvAdapter.notifyDataSetChanged(); // Notify adapter that data has changed

        return videosList;
    }
}