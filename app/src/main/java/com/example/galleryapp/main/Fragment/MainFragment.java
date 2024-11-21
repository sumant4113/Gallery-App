package com.example.galleryapp.main.Fragment;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
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
import com.example.galleryapp.main.Adapter.GalleryRvAdapter;
import com.example.galleryapp.main.Model.ImageModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private RecyclerView rvGallery;
    private final ArrayList<ImageModel> imagesList = new ArrayList<>();
    private View view;
    private GalleryRvAdapter galleryRvAdapter;
    private Context context;
    private ExecutorService service;
    private SwipeRefreshLayout swipeRefreshMainFragment;
    private ContentObserver contentObserver;

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
            Log.d(TAG, "onResume: +-+-");
            Log.d(TAG, "onResume: +-+- list : " + imagesList.size());
            galleryRvAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(TAG, "onResume: +-+-" + e);
        }
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

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        // solve recycle view lag
        rvGallery.setHasFixedSize(true);
        rvGallery.setItemViewCacheSize(20);
        rvGallery.setDrawingCacheEnabled(true);
        rvGallery.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rvGallery.setNestedScrollingEnabled(false);

        rvGallery.setLayoutManager(layoutManager);
        rvGallery.setAdapter(galleryRvAdapter);
    }

    public void loadImages() {
        // Here loading task gone background
        new Thread(() -> {
            ArrayList<ImageModel> loadedImages = loadImagesFromStorage();

            requireActivity().runOnUiThread(() -> {
                if (!loadedImages.isEmpty()) {
                    imagesList.clear();
                    imagesList.addAll(loadedImages);
                    galleryRvAdapter.notifyDataSetChanged();
                }
                swipeRefreshMainFragment.setRefreshing(false);
            });
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

   /* public ArrayList<ImageModel> loadImages(Context context) {
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
//        String[] selectionArgs = new String[]{"%" +  + "%"}; // this is selection that show only this folder type
        String[] selectionArgs = new String[]{"%Camera%"};
//        String[] selectionArgs = new String[]{};

        Cursor cursor = getContext().getContentResolver()
                .query(uri, projection, selection, selectionArgs , orderBy);

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
                imageModelList.add(imageModel);

            }
            cursor.close();
        }
        return imageModelList;
    }*/

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
            });
        });
    }

}