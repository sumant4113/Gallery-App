package com.example.galleryapp.main.Activity;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Adapter.FVideosRvAdapter;
import com.example.galleryapp.main.Model.VideoModel;

import java.util.ArrayList;
import java.util.Locale;

public class ViewFolderActivity extends AppCompatActivity {

    private static final String TAG = "ViewFolderActivity";
    private RecyclerView rvFolder;
    private FVideosRvAdapter fVideosRvAdapter;
    private ArrayList<VideoModel> videoInFolder = new ArrayList<>();
    private TextView txtFolderName;
    private String folderPath, folderName;
    private ImageView imgBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_activty);

        initView();
    }

    private void initView() {
        rvFolder = findViewById(R.id.rv_folder);
        txtFolderName = findViewById(R.id.txt_folderName);
        imgBackBtn = findViewById(R.id.img_backBtn);

        imgBackBtn.setOnClickListener(v -> onBackPressed());

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

        if (folderName != null && !videoInFolder.isEmpty()) {
            fVideosRvAdapter = new FVideosRvAdapter(this, videoInFolder);

            // solve recycle view lag
            rvFolder.setHasFixedSize(true);
            rvFolder.setItemViewCacheSize(20);
            rvFolder.setDrawingCacheEnabled(true);
            rvFolder.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            rvFolder.setNestedScrollingEnabled(false);

            rvFolder.setLayoutManager(new GridLayoutManager(this, 3));
            rvFolder.setAdapter(fVideosRvAdapter);
        } else {
            Toast.makeText(this, "can't find any video", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<VideoModel> getAllVideoFromFolder(Context context, String folderPath) {
        ArrayList<VideoModel> videoModelList = new ArrayList<>();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Video.Media.DATE_TAKEN + " DESC"; // DESC = Descending order and ASC = Ascending order
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.RESOLUTION,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DATE_ADDED
        };

        String selection = MediaStore.Video.Media.DATA + " like?"; // Here like for flexible matching of file and ? makes SQL injection safely insert value
        String[] selectionArgs = new String[]{"%" + folderName + "%"}; // this is selection that show only this folder type

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
                String dateTime = cursor.getString(9);

                // Size
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

                // Duration
                String durationFormatted;
                int sec = (duration / 1000) % 60;
                int min = (duration / (1000 * 60)) % 60;
                int hrs = (duration / (1000 * 60 * 60));

                if (hrs == 0) {
                    durationFormatted = String.valueOf(min).concat(":".concat(String.format(Locale.getDefault(), "%02d", sec))); // here concat add colon between min & sec, %02d convert 2 digit like (5 -> 05), Locale.getDefault() give mobile default language
                } else {
                    durationFormatted = String.valueOf(hrs).concat(":".concat(String.format(Locale.getDefault(), "%02d", min)
                            .concat(String.format(Locale.UK, "%02d", sec))));
                }

                // Give to Model Class
                VideoModel videoModel = new VideoModel(id, path, title, humanCanRead, resolution, durationFormatted, displayName, widthHeight, dateTime);
                if (folderName.endsWith(bucketDisplayName))
                    videoModelList.add(videoModel);
            }
            cursor.close();
        }
        return videoModelList;
    }

}