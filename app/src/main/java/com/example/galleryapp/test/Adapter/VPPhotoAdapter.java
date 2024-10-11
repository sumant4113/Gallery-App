package com.example.galleryapp.test.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.galleryapp.R;
import com.example.galleryapp.test.Activity.ViewPictureActivity;
import com.ortiz.touchview.TouchImageView;

import java.io.File;
import java.util.ArrayList;

public class VPPhotoAdapter extends PagerAdapter {

    private final Context context;
    private static final String TAG = "VPPhotoAdapter";
    private ArrayList<String> imageList;

    public VPPhotoAdapter(Context context, ArrayList<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@   NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        String imgPath = imageList.get(position);
        File image_file = new File(imageList.get(position));

        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slider, container, false);
        TouchImageView imgFullPhoto = view.findViewById(R.id.img_fullPhoto);

//        image_File = getIntent().getStringExtra("image_file");
//        File file = new File(String.valueOf(image_file));
//        if (file.exists()) {
//            Glide.with(context).load(image_file).into(imgFullPhoto);
//        }

        try {
            Glide.with(context).load(imgPath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgFullPhoto);
        } catch (Exception e) {
            Toast.makeText(context, "Glide Fail" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "instantiateItem: Glide Fail" + e.getMessage());
        }


        imgFullPhoto.setOnClickListener(view1 -> {
            // Call toggleVisibility in ViewPictureActivity
            if (context instanceof ViewPictureActivity) {
                ((ViewPictureActivity) context).toggleVisibility();

/*            Intent intent = new Intent(context, ViewPictureActivity.class);
                intent.putExtra("image_file", image_file);
                context.startActivity(intent);*/
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
