package com.example.galleryapp.main.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Adapter.FolderRvAdapter;
import com.example.galleryapp.main.Model.VideoModel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FolderFragment extends Fragment {

    private static final String TAG = "FolderFragment";
    private View view;

    private RecyclerView rvFolder;
    private FolderRvAdapter folderRvAdapter;
    private ArrayList<String> folderList = new ArrayList<>();
    private ArrayList<VideoModel> videoModelList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshFolderFragment;
    private ContentObserver contentObserver;
    private ExecutorService executorService;
    private HandlerThread observerThread;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = Executors.newSingleThreadExecutor();
//        observerThread = new HandlerThread("ContentObserverThread");
//        observerThread.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadVideos();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_folder, container, false);

        initView();
        registerContentObserver();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (contentObserver != null) {
            requireContext().getContentResolver().unregisterContentObserver(contentObserver);
        }
        executorService.shutdown();
//        observerThread.quit();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initView() {
        rvFolder = view.findViewById(R.id.rv_folder);
        swipeRefreshFolderFragment = view.findViewById(R.id.swipeRefresh_folderFragment);

        swipeRefreshFolderFragment.setOnRefreshListener(() -> {
            loadVideos();
            swipeRefreshFolderFragment.setRefreshing(false);
        });


        videoModelList = fetchAllVideos(requireContext());
        if ((folderList != null) && !folderList.isEmpty() && (videoModelList != null)) {
            if (folderRvAdapter == null) {
                folderRvAdapter = new FolderRvAdapter(getContext(), folderList, videoModelList);

                // solve recycle view lag
                rvFolder.setHasFixedSize(true);
                rvFolder.setItemViewCacheSize(30);
                rvFolder.setDrawingCacheEnabled(true);
                rvFolder.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                rvFolder.setNestedScrollingEnabled(false);
                rvFolder.setLayoutManager(new GridLayoutManager(getContext(), 3));

                rvFolder.setAdapter(folderRvAdapter);
            } else {
                folderRvAdapter.updateVideoList(videoModelList);
            }

        } else {
            Toast.makeText(getContext(), "can't find Videos", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadVideos() {
        /*executorService.execute(() -> {
            ArrayList<VideoModel> loadedVideos = fetchAllVideos(requireContext());
            if (!loadedVideos.isEmpty()) {
                videoModelList.clear();
                videoModelList.addAll(loadedVideos);

                requireActivity().runOnUiThread(() -> {
                    if (folderRvAdapter == null) {
                        folderRvAdapter = new FolderRvAdapter(getContext(), folderList, videoModelList);
                        rvFolder.setAdapter(folderRvAdapter);
                    } else {
                        folderRvAdapter.updateVideoList(videoModelList);
                        folderRvAdapter.notifyDataSetChanged();
                    }
                });
            } else {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "No Video Found!", Toast.LENGTH_SHORT).show();
                });
            }
        });*/
        new Thread(() -> {
            ArrayList<VideoModel> loadedVideos = new ArrayList<>();
            if (!loadedVideos.isEmpty()) {
                videoModelList.clear();
                videoModelList.addAll(loadedVideos);
                requireActivity().runOnUiThread(() -> folderRvAdapter.notifyDataSetChanged());
                swipeRefreshFolderFragment.setRefreshing(false);
            }
        });
    }

    @SuppressLint("Range")
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
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                String width_height = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String resolution = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.RESOLUTION));
                String dateTime = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));

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


    private void registerContentObserver() {
        contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange, @Nullable Uri uri) {
                super.onChange(selfChange, uri);
                loadVideos();
            }
        };
        requireContext().getContentResolver().registerContentObserver(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                true,
                contentObserver
        );
    }

}