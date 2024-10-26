package com.example.galleryapp.main.Fragment;

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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Adapter.FolderRvAdapter;
import com.example.galleryapp.main.Model.VideoModel;

import java.util.ArrayList;

public class FolderFragment extends Fragment {

    private static final String TAG = "FolderFragment";
    private View view;

    private RecyclerView rvFolder;
    private FolderRvAdapter folderRvAdapter;
    private ArrayList<String> folderList = new ArrayList<>();
    private ArrayList<VideoModel> videoModelList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshFolderFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        videoModelList = fetchAllVideos(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_folder, container, false);

        initView();
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initView() {
        rvFolder = view.findViewById(R.id.rv_folder);
        swipeRefreshFolderFragment = view.findViewById(R.id.swipeRefresh_folderFragment);

        videoModelList = fetchAllVideos(requireContext());

        swipeRefreshFolderFragment.setOnRefreshListener(() -> {
            loadVideos();
            swipeRefreshFolderFragment.setRefreshing(false);
        });

        if ((folderList != null) && !folderList.isEmpty() && (videoModelList != null)) {
            if (folderRvAdapter == null) {
                folderRvAdapter = new FolderRvAdapter(getContext(), folderList, videoModelList);

                // solve recycle view lag
                rvFolder.setHasFixedSize(true);
                rvFolder.setItemViewCacheSize(30);
                rvFolder.setDrawingCacheEnabled(true);
                rvFolder.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                rvFolder.setNestedScrollingEnabled(false);
//            folderAdapter.notifyDataSetChanged();

                rvFolder.setLayoutManager(new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false));
                rvFolder.setAdapter(folderRvAdapter);
            } else {
                folderRvAdapter.updateVideoList(videoModelList);
            }

        } else {
            Toast.makeText(getContext(), "can't find Videos", Toast.LENGTH_SHORT).show();
        }

    }

    public void loadVideos(){
        new Thread(() -> {
            ArrayList<VideoModel> loadedVideos = new ArrayList<>();
            if (!loadedVideos.isEmpty()) {
                videoModelList.clear();
                videoModelList.addAll(loadedVideos);
                requireActivity().runOnUiThread(() -> folderRvAdapter.notifyDataSetChanged());
                swipeRefreshFolderFragment.setRefreshing(false);
            }
        } );
    }

    private ArrayList<VideoModel> fetchAllVideos(Context context) {
        ArrayList<String> latestFileInFolder = new ArrayList<>();
        ArrayList<VideoModel> videoModels = new ArrayList<>();

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
                MediaStore.Video.Media.DATE_ADDED
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
                String dateTime = cursor.getString(8);

                VideoModel videoFiles = new VideoModel(id, path, title, size, resolution, duration, displayName, width_height, dateTime);

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