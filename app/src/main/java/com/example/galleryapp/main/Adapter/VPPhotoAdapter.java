package com.example.galleryapp.main.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.galleryapp.R;
import com.example.galleryapp.main.Activity.ViewPictureActivity;
import com.example.galleryapp.main.Model.ImageModel;
import com.ortiz.touchview.TouchImageView;

import java.io.File;
import java.util.ArrayList;

public class VPPhotoAdapter extends PagerAdapter {

    private final Context context;
    private static final String TAG = "VPPhotoAdapter";
    private ArrayList<ImageModel> imageList;

    public VPPhotoAdapter(Context context, ArrayList<ImageModel> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageModel imageModel = imageList.get(position);

        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slider, container, false);
        TouchImageView imgFullPhoto = view.findViewById(R.id.img_fullPhoto);

        if (imageModel != null && imageModel.getPath() != null) {
            File file = new File(imageModel.getPath());  // Get the correct image path
            if (file.exists()) {
                Glide.with(context)
                        .load(file)
                        .override(1080, 1920) // Resize the image
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgFullPhoto);
            }
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
