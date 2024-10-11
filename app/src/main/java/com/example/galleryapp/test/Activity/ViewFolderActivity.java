package com.example.galleryapp.test.Activity;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryapp.R;
import com.example.galleryapp.test.Adapter.FVideosAdapter;
import com.example.galleryapp.test.Model.VideoModel;

import java.util.ArrayList;
import java.util.Locale;

public class ViewFolderActivity extends AppCompatActivity {

    private static final String TAG = "ViewFolderActivity";
    private RecyclerView rvFolder;

    private FVideosAdapter fVideosAdapter;
    private ArrayList<VideoModel> videoInFolder = new ArrayList<>();
    private String folderPath, folderName;
    private TextView txtFolderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_activty);

        initView();
    }

    private void initView() {
        rvFolder = findViewById(R.id.rv_folder);
        txtFolderName = findViewById(R.id.txt_folderName);

        // FolderAdapter throw data come
        if (getIntent() != null) {
            folderPath = getIntent().getStringExtra("folderPath");
            folderName = getIntent().getStringExtra("folderName");

        }
        txtFolderName.setText(folderName);

        loadVideos();

    }

    private void loadVideos() {
        videoInFolder = getAllVideoFromFolder(this, folderName);
        if (folderName != null && videoInFolder.size() > 0) {
            fVideosAdapter = new FVideosAdapter(this, videoInFolder);

            // solve recycle view lag
            rvFolder.setHasFixedSize(true);
            rvFolder.setItemViewCacheSize(50);
            rvFolder.setDrawingCacheEnabled(true);
            rvFolder.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            rvFolder.setNestedScrollingEnabled(false);

            rvFolder.setLayoutManager(new GridLayoutManager(this, 3));
            rvFolder.setAdapter(fVideosAdapter);
        } else {
            Toast.makeText(this, "can't find any video", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<VideoModel> getAllVideoFromFolder(Context context, String folderPath) {
        ArrayList<VideoModel> videoModelList = new ArrayList<>();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Video.Media.DATE_ADDED + " DESC";
        String projection[] = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.RESOLUTION,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.HEIGHT
        };

        String selection = MediaStore.Video.Media.DATA + " like?";
        String selectionArgs[] = new String[]{"%" + folderName + "%"};

        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, orderBy);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                int size = cursor.getInt(3);
                String resolution = cursor.getString(4);
                int duration = cursor.getInt(5);
                String displayName = cursor.getString(6);
                String bucketDisplayName = cursor.getString(7);
                String widthHeight = cursor.getString(8);

                String humanCanRead = null;
                if (size < 1024) {
                    humanCanRead = String.format(context.getString(R.string.size_bytes), (double) size);
                } else if (size < Math.pow(1024, 2)) {
                    humanCanRead = String.format(context.getString(R.string.size_kb), (double) size / 1024);
                } else if (size < Math.pow(1024, 3)) {
                    humanCanRead = String.format(context.getString(R.string.size_mb), (double) size / Math.pow(1024, 2));
                } else {
                    humanCanRead = String.format(context.getString(R.string.size_gb), (double) size / Math.pow(1024, 3));
                }

                String durationFormatted;
                int sec = (duration / 1000) % 60;
                int min = (duration / (1000 * 60)) % 60;
                int hrs = (duration / (1000 * 60 * 60));

                if (hrs == 0) {
                    durationFormatted = String.valueOf(min).concat(":".concat(String.format(Locale.UK, "%02d", sec)));
                } else {
                    durationFormatted = String.valueOf(hrs).concat(":".concat(String.format(Locale.UK, "%02d", min)
                            .concat(String.format(Locale.UK, "%02d", sec))));
                }

                VideoModel videoModel = new VideoModel(id, path, title, humanCanRead, resolution, durationFormatted, displayName, widthHeight);
                if (folderName.endsWith(bucketDisplayName))
                    videoModelList.add(videoModel);
            }
            cursor.close();
        }
        return videoModelList;
    }


    private ArrayList<VideoModel> fetchVideosInFolder(String folderPath) {
        ArrayList<VideoModel> videos = new ArrayList<>();
        for (VideoModel video : videoInFolder) {
            if (video.getPath().contains(folderPath)) {
                videos.add(video);
            }
        }
        return videos;
    }

}