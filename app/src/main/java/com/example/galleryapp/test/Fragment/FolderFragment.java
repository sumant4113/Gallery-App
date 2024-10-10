package com.example.galleryapp.test.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryapp.R;
import com.example.galleryapp.test.Adapter.FolderAdapter;
import com.example.galleryapp.test.Model.VideoModel;

import java.util.ArrayList;
import java.util.List;

public class FolderFragment extends Fragment {

    private static final String TAG = "FolderFragment";
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
            if (folderAdapter == null) {
                folderAdapter = new FolderAdapter(getContext(), folderList, videoModelList);
                rvFolder.setAdapter(folderAdapter);

                rvFolder.setLayoutManager(new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false));
//            folderAdapter.notifyDataSetChanged();

                rvFolder.setItemViewCacheSize(20);  // Increase the cache size for smoother scrolling
                rvFolder.setHasFixedSize(true);  // If the size of RecyclerView won't change
            } else {
                folderAdapter.updateVideoList(videoModelList);
            }

        } else {
            Toast.makeText(getContext(), "can't find Videos", Toast.LENGTH_SHORT).show();
        }

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

    public static class FolderDiffCallback extends DiffUtil.Callback {

        private final List<VideoModel> oldList;
        private final List<VideoModel> newList;

        public FolderDiffCallback(List<VideoModel> oldList, List<VideoModel> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getPath().equals(newList.get(newItemPosition).getPath());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            VideoModel oldItem = oldList.get(oldItemPosition);
            VideoModel newItem = newList.get(newItemPosition);

            Log.d("DiffUtil", "Comparing " + oldItem.getPath() + " with " + newItem.getPath());

            return (oldItem.getPath() == null ? newItem.getPath() == null : oldItem.getPath().equals(newItem.getPath())) &&
                    (oldItem.getTitle() == null ? newItem.getTitle() == null : oldItem.getTitle().equals(newItem.getTitle())) &&
                    (oldItem.getDuration() == null ? newItem.getDuration() == null : oldItem.getDuration().equals(newItem.getDuration()));
        }


    }
}