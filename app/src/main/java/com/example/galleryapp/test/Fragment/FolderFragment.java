package com.example.galleryapp.test.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryapp.R;
import com.example.galleryapp.test.Adapter.FolderAdapter;
import com.example.galleryapp.test.Model.VideoModel;

import java.util.ArrayList;


public class FolderFragment extends Fragment {

    private ArrayList<VideoModel> videoModelList = new ArrayList<>();
    private ArrayList<String> folderList = new ArrayList<>();
    private FolderAdapter folderAdapter;
    private RecyclerView rvFolder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);
        rvFolder = view.findViewById(R.id.rv_folder);

        initView();
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initView() {
        videoModelList = fetchAllVideos(requireContext());

        if (folderList != null && !folderList.isEmpty() && videoModelList != null) {
            folderAdapter = new FolderAdapter(getContext(), folderList, videoModelList);
            rvFolder.setAdapter(folderAdapter);

            rvFolder.setLayoutManager(new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false));
            folderAdapter.notifyDataSetChanged();

        } else {
            Toast.makeText(getContext(), "can't find Videos", Toast.LENGTH_SHORT).show();
        }

    }

    private ArrayList<VideoModel> fetchAllVideos(Context context) {
        ArrayList<VideoModel> videoModels = new ArrayList<>();
        ArrayList<String> latestFileInFolder = new ArrayList<>();

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
                MediaStore.Video.Media.RESOLUTION
        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, orderBy);
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

                VideoModel videoFiles = new VideoModel(id, path, title, size, resolution, duration, displayName, width_height);

                int slashFirstIndex = path.lastIndexOf("/");
                String subString = path.substring(0, slashFirstIndex);

                if (!folderList.contains(subString)) { // here only unique folder added
                    folderList.add(subString);      // if that unique folder is not add then here add.
                    latestFileInFolder.add(path); // it store latest file in the folder
                }
                videoModels.add(videoFiles);
            }
            cursor.close();
        }
        return videoModels;
    }

}