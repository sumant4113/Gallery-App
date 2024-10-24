package com.example.galleryapp.main.Fragment;

import android.content.Context;
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
import com.example.galleryapp.main.Activity.ImageManager;
import com.example.galleryapp.main.Adapter.GalleryRvAdapter;
import com.example.galleryapp.main.Model.ImageModel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainFragment extends Fragment {

    private RecyclerView rvGallery;
    private final ArrayList<ImageModel> imagesList = new ArrayList<>();
    private View view;
    private GalleryRvAdapter galleryRvAdapter;
    private Context context;
    private ImageManager imageManager;
    private ExecutorService service;
    private SwipeRefreshLayout swipeRefreshMainFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        initView();
        loadImages();
        return view;
    }

    private void initView() {
        rvGallery = view.findViewById(R.id.rv_gallery);
        swipeRefreshMainFragment = view.findViewById(R.id.swipeRefresh_mainFragment);

        swipeRefreshMainFragment.setOnRefreshListener(() -> {
            loadImages();
        });

        galleryRvAdapter = new GalleryRvAdapter(getContext(), imagesList);
        service = Executors.newSingleThreadExecutor();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        // solve recycle view lag
        rvGallery.setHasFixedSize(true);
        rvGallery.setItemViewCacheSize(50);
        rvGallery.setDrawingCacheEnabled(true);
        rvGallery.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rvGallery.setNestedScrollingEnabled(false);

        rvGallery.setLayoutManager(layoutManager);
        rvGallery.setAdapter(galleryRvAdapter);

        service.execute(new Runnable() {
            @Override
            public void run() {
                loadImages();
                swipeRefreshMainFragment.setRefreshing(false);
            }
        });

        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });

    }

    public void loadImages() {
        new Thread(() -> {
            ArrayList<ImageModel> loadedImages = loadImagesFromStorage();
            if (!loadedImages.isEmpty()) {
                imagesList.clear();
                imagesList.addAll(loadedImages);
                requireActivity().runOnUiThread(() -> galleryRvAdapter.notifyDataSetChanged());
                swipeRefreshMainFragment.setRefreshing(false);
            }
        }).start();

    }

    private ArrayList<ImageModel> loadImagesFromStorage() {
        ArrayList<ImageModel> imageModelList = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_ADDED
        };

        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, orderBy);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                int size = cursor.getInt(3);
                String dateTaken = cursor.getString(4);

                String humanReadableSize = convertSizeToReadable(size);

                ImageModel imageModel = new ImageModel(id, path, title, humanReadableSize, "", dateTaken);
                imageModelList.add(imageModel);
            }
            cursor.close();
        }
        return imageModelList;
    }

    private String convertSizeToReadable(int size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return size / 1024 + " KB";
        } else if (size < 1024 * 1024 * 1024) {
            return size / (1024 * 1024) + " MB";
        } else {
            return size / (1024 * 1024 * 1024) + " GB";
        }
    }

    public ArrayList<ImageModel> loadImages(Context context) {
        ArrayList<ImageModel> imageModelList = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.SIZE,
//                MediaStore.Images.Media.RESOLUTION,
                MediaStore.Images.Media.DATE_ADDED
        };

        String selection = MediaStore.Images.Media.DATA + " like?";

        Cursor cursor = getContext().getContentResolver()
                .query(uri, projection, null, null, orderBy);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                int size = cursor.getInt(3);
//                String resolution = cursor.getString(4);
                String dateTaken = cursor.getString(4);

                String humanReadableSize;
                if (size < 1024) {
                    humanReadableSize = size + " Bytes";
                } else if (size < 1024 * 1024) {
                    humanReadableSize = size / 1024 + " KB";
                } else {
                    humanReadableSize = size / (1024 * 1024) + " MB";
                }

                ImageModel imageModel = new ImageModel(id, path, title, humanReadableSize, "", dateTaken);
                /*String humanCanRead = null;
                if (size < 1024) {
                    humanCanRead = String.format(context.getString(R.string.size_bytes), size);
                } else if (size < Math.pow(1024, 2)) {
                    humanCanRead = String.format(context.getString(R.string.size_kb), size / 1024);
                } else if (size < Math.pow(1024, 3)) {
                    humanCanRead = String.format(context.getString(R.string.size_mb), size / Math.pow(1024, 2));
                } else {
                    humanCanRead = String.format(context.getString(R.string.size_gb), size / Math.pow(1024, 3));
                }
                String resolution = "";

                ImageModel imageModel = new ImageModel(id, path, title, humanCanRead, resolution, dateTaken);*/
                imageModelList.add(imageModel);

            }
            cursor.close();
        }
        return imageModelList;
    }
    /*if (getContext() != null) {
            if (Environment.getExternalStorageState().equals(MEDIA_MOUNTED)) {
                final String[] columns = {
                        MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID
                };
                final String order = MediaStore.Images.Media.DATE_TAKEN + " DESC";

                Cursor cursor = getContext().getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                columns, null, null, order);
                if (cursor != null) {
                    try {
                        int count = cursor.getCount();
//                        txtTotalItem.setText("Total Items: " + count);
                        for (int i = 0; i < count; i++) {
                            cursor.moveToPosition(i);
                            int columIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//                            imagesList.add(new ImageModel());
                        }
                        rvGallery.getAdapter().notifyDataSetChanged();
                    } finally {
                        cursor.close();
                    }
                }
            }
        }*/
}