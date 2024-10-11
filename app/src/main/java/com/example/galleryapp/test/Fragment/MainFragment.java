package com.example.galleryapp.test.Fragment;

import static android.os.Environment.MEDIA_MOUNTED;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryapp.R;
import com.example.galleryapp.test.Adapter.GalleryAdapter;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    private RecyclerView rvGallery;
    private final ArrayList<String> imagesList = new ArrayList<>();
    private View view;
    private GalleryAdapter galleryAdapter;
    //    private TextView txtTotalItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
//        txtTotalItem = view.findViewById(R.id.txt_totalItem);
        galleryAdapter = new GalleryAdapter(getContext(), imagesList);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        // solve recycle view lag
        rvGallery.setHasFixedSize(true);
        rvGallery.setItemViewCacheSize(50);
        rvGallery.setDrawingCacheEnabled(true);
        rvGallery.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rvGallery.setNestedScrollingEnabled(false);

        rvGallery.setLayoutManager(layoutManager);
        rvGallery.setAdapter(galleryAdapter);
        rvGallery.setNestedScrollingEnabled(false);
    }


    public void loadImages() {
        if (getContext() != null) {
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
                            imagesList.add(cursor.getString(columIndex));
                        }

                        rvGallery.getAdapter().notifyDataSetChanged();
                    } finally {
                        cursor.close();
                    }
                }

            }
        }
    }
}