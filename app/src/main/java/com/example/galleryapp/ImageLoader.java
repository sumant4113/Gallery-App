package com.example.galleryapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ImageLoader {

    private Context mContext;
    private List<Image> mImageList;

    public ImageLoader(Context context) {
        mContext = context;
        mImageList = new ArrayList<>();
    }

    public void loadImage(Context context, String imagePath, ImageView imageView) {
        new LoadImageTask(imageView).execute(imagePath);
    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imagePath = params[0];
            Bitmap bitmap = null;
            try {
                File file = new File(imagePath);
                FileInputStream fis = new FileInputStream(file);
                bitmap = BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
    public void loadImages() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME
        };

        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));

                com.example.galleryapp.Image image = new com.example.galleryapp.Image(id, data, name);
                mImageList.add(image);
            }
            cursor.close();
        }
    }

    public List<Image> getImage() {
        return mImageList;
    }

}