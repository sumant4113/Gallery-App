package com.example.galleryapp.main.Fragment;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Adapter.DateWiseAdapter;
import com.example.galleryapp.main.Adapter.GalleryRvAdapter;
import com.example.galleryapp.main.Model.ImageModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private View view;
    private Context context;

    private RecyclerView rvGallery;
    private GalleryRvAdapter galleryRvAdapter;
    private final ArrayList<ImageModel> imagesList = new ArrayList<>();
    private DateWiseAdapter dateWiseAdapter;

    private SwipeRefreshLayout swipeRefreshMainFragment;
    private ContentObserver contentObserver;
    private ExecutorService service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshImageList();
        try {
//            Log.d(TAG, "onResume: +-+-");
//            Log.d(TAG, "onResume: +-+- list : " + imagesList.size());
            galleryRvAdapter.notifyDataSetChanged();
//            dateWiseAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(TAG, "onResume: +-+- E : " + e);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        initView();
        loadImages();
        registerContentObserver();
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

//        Map<String, List<ImageModel>> groupedDate = groupImagesByDate(imagesList);
//        dateWiseAdapter = new DateWiseAdapter(groupedDate);
//        Log.d(TAG, "initView: +-+- groupedDate : " + groupedDate);
//        Log.d(TAG, "initView: +-+- dateWiseAdapter : " + dateWiseAdapter);
//        Log.d(TAG, "initView: +-+- imagesList.size() : " + imagesList.size());
//        Log.d(TAG, "initView: +-+- dateWiseAdapter.getItemCount() : " + dateWiseAdapter.getItemCount());

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        // solve recycle view lag
        rvGallery.setHasFixedSize(true);
        rvGallery.setItemViewCacheSize(20);
        rvGallery.setDrawingCacheEnabled(true);
        rvGallery.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        rvGallery.setNestedScrollingEnabled(false);

        rvGallery.setLayoutManager(layoutManager);
//        rvGallery.setAdapter(dateWiseAdapter);
        rvGallery.setAdapter(galleryRvAdapter);
    }
   /* public void loadImages() {
        new Thread(() -> {
            ArrayList<ImageModel> loadedImages = loadImagesFromStorage();
            Log.d(TAG, "Loaded images count: " + loadedImages.size());

            requireActivity().runOnUiThread(() -> {
                if (!loadedImages.isEmpty()) {
                    imagesList.clear();
                    imagesList.addAll(loadedImages);

                    // Group images by date after loading
                    Map<String, List<ImageModel>> groupedData = groupImagesByDate(imagesList);
                    dateWiseAdapter = new DateWiseAdapter(groupedData);
                    rvGallery.setAdapter(dateWiseAdapter); // Set the new adapter
                }
                swipeRefreshMainFragment.setRefreshing(false);
            });
        }).start();
    }*/
    public void loadImages() {
        // Here loading task gone background
        new Thread(() -> {
            ArrayList<ImageModel> loadedImages = loadImagesFromStorage();

            requireActivity().runOnUiThread(() -> {
                if (!loadedImages.isEmpty()) {
                    imagesList.clear();
                    imagesList.addAll(loadedImages);
                    galleryRvAdapter.notifyDataSetChanged();
//                    dateWiseAdapter.notifyDataSetChanged();
                }
                swipeRefreshMainFragment.setRefreshing(false);
            });
        }).start();

    }

    private Map<String, List<ImageModel>> groupImagesByDate(List<ImageModel> imageList) {
        Map<String, List<ImageModel>> groupedData = new TreeMap<>(Collections.reverseOrder()); // Dates in reverse order (newest first)

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (ImageModel image : imageList) {
            try {
                long dateTakenMillis = Long.parseLong(image.getDateTaken());
                String dateKey = dateFormat.format(new Date(dateTakenMillis));

                if (!groupedData.containsKey(dateKey)) {
                    groupedData.put(dateKey, new ArrayList<>());
                }
                groupedData.get(dateKey).add(image);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid date format for image: " + image.getTitle(), e);
                // Optionally, handle the error (e.g., skip this image)
            }
        }
        return groupedData;
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
//                MediaStore.Images.Media.RESOLUTION,
                MediaStore.Images.Media.DATE_ADDED
        };
        String selection = MediaStore.Images.Media.DATA + " like?";

        String[] selectionArgs = new String[]{"%Camera%"};

        Cursor cursor = getContext().getContentResolver().query(
                uri, projection,
                null, null, orderBy);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                int size = cursor.getInt(3);
//                String resolution = cursor.getString(4);
                String dateTaken = cursor.getString(4);

                // Use BitmapFactory to retrieve image resolution
                String resolution;
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, options);
                    resolution = options.outWidth + "x" + options.outHeight;
                } catch (Exception e) {
                    resolution = "Unknown";
                }
                String humanReadableSize = convertSizeToReadable(size);

                ImageModel imageModel = new ImageModel(id, path, title, humanReadableSize, resolution, dateTaken);
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

    private void refreshImageList() {
        new Thread(() -> {
            Iterator<ImageModel> iterator = imagesList.iterator();
            while (iterator.hasNext()) {
                ImageModel model = iterator.next();
                File file = new File(model.getPath());
                if (!file.exists()) {
                    iterator.remove();
                }
            }
            // Update adapter on main thread
            requireActivity().runOnUiThread(() -> {
                galleryRvAdapter.notifyDataSetChanged();
//                dateWiseAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterContentObserver();
    }

    private void unregisterContentObserver() {
        if (contentObserver != null) {
            context.getContentResolver().unregisterContentObserver(contentObserver);
        }
    }

    private void registerContentObserver() {
        contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange, @Nullable Uri uri) {
                super.onChange(selfChange, uri);
                Log.d(TAG, "onChange: MediaStore Chnaged Uri : " + uri);
                loadImages();
            }
        };
        context.getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,// Uri to observe
                true,
                contentObserver);
    }
}