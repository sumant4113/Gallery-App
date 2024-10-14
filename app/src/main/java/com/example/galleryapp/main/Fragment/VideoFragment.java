package com.example.galleryapp.main.Fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Adapter.VideoRvAdapter;
import com.example.galleryapp.main.Model.VideoModel;

import java.util.ArrayList;

public class VideoFragment extends Fragment {

    private RecyclerView rvVideos;
    private static final String TAG = "VideoFragment";
    private View view;
    private VideoRvAdapter videoRvAdapter;
    private final ArrayList<VideoModel> videosList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_video, container, false);
        initView();
        loadVideos();  // Load videos from storage
        return view;
    }

    private void initView() {
        rvVideos = view.findViewById(R.id.rv_video);

        videoRvAdapter = new VideoRvAdapter(getContext(), videosList);
        // solve recycle view lag
        rvVideos.setHasFixedSize(true);
        rvVideos.setItemViewCacheSize(20);
        rvVideos.setDrawingCacheEnabled(true);
        rvVideos.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rvVideos.setNestedScrollingEnabled(false);

        rvVideos.setLayoutManager(new GridLayoutManager(getContext(),3));
        rvVideos.setAdapter(videoRvAdapter);
    }

    private void loadVideos() {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME
        };

        try (Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null) {
                int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

                while (cursor.moveToNext()) {
                    String absolutePathOfVideo = cursor.getString(columnIndexData);
//                    videosList.add(absolutePathOfVideo);  // Add video paths to the list
                }
                videoRvAdapter.notifyDataSetChanged();  // Notify adapter that data has changed
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading videos: " + e.getMessage());
        }
    }
}
