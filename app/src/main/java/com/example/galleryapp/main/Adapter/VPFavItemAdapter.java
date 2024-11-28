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
import com.example.galleryapp.main.Activity.FavViewPictureActivity;
import com.example.galleryapp.main.Model.FavoriteModel;
import com.ortiz.touchview.TouchImageView;

import java.io.File;
import java.util.ArrayList;

public class VPFavItemAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<FavoriteModel> favList;

    public VPFavItemAdapter(Context context, ArrayList<FavoriteModel> favList) {
        this.context = context;
        this.favList = favList;
    }

    @Override
    public int getCount() {
        return favList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        FavoriteModel model = favList.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slider, container, false);

        TouchImageView imgFullPhoto = view.findViewById(R.id.img_fullPhoto);

        if (model != null && model.getPath() != null) {
            File file = new File(model.getPath());
            if (file.exists()) {
                Glide.with(context).load(file).override(1080, 1920).diskCacheStrategy(DiskCacheStrategy.ALL).into(imgFullPhoto);
            }
        }
        imgFullPhoto.setOnClickListener(v -> {
            if (context instanceof FavViewPictureActivity) {
                ((FavViewPictureActivity)context).toggleFavVisibility();
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
