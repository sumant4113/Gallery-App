package com.example.galleryapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private static final String TAG = "ImageAdapter";
    private final Context context;
    private final ArrayList<String> images;

    public ImageAdapter(Context context) {
        this.context = context;
        images = getAllShownImagesPath(context);
    }

    public int getCount() {
        Log.d(TAG, "getCount: ++++" + images.size());
        return images.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView picturesView;
        if (convertView == null) {
            picturesView = new ImageView(context);
            picturesView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            picturesView.setLayoutParams(new GridView.LayoutParams(270, 270));
        } else {
            picturesView = (ImageView) convertView;
        }

        Glide.with(context).load(images.get(position)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_launcher_background)
                .centerCrop().into(picturesView);
        Log.d(TAG, "getAllShownImagesPath: +-+-" + images.get(position));

        return picturesView;
    }

    private ArrayList<String> getAllShownImagesPath(Context context) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        cursor = context.getContentResolver().query(uri, projection, null, null, null);

        assert cursor != null;
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }
}

