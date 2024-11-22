package com.example.galleryapp.main.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.galleryapp.R;
import com.example.galleryapp.main.Activity.ViewPictureActivity;
import com.example.galleryapp.main.Model.ImageDataHolder;
import com.example.galleryapp.main.Model.ImageModel;

import java.io.File;
import java.util.ArrayList;

public class GalleryRvAdapter extends RecyclerView.Adapter<GalleryRvAdapter.ViewHolder> {

    private static final String TAG = "GalleryAdapter";

    private final Context context;
    private ArrayList<ImageModel> image_list;
    public static Animation translate_anim;

    public GalleryRvAdapter(Context context, ArrayList<ImageModel> image_list) {
        this.context = context;
        this.image_list = image_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gellery, parent, false);
        return new ViewHolder(view);
    }

    // This Gallery Adapter works for images only and work for MainFragment
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ImageModel imageModel = image_list.get(position);
        File image_file = new File(imageModel.getPath());

        if (image_file.exists()) {
            Glide.with(context)
                    .load(image_file)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .thumbnail(0.1f)
                    .override(300, 300)  // Resize image
                    .into(holder.imgGalleryItem);
        }

        holder.imgGalleryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImageDataHolder.getInstance().setImageList(image_list);

                Intent intent = new Intent(context, ViewPictureActivity.class);
//                intent.putParcelableArrayListExtra("image_path", image_list);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return image_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgGalleryItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgGalleryItem = itemView.findViewById(R.id.img_gallery_item);

            translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            imgGalleryItem.setAnimation(translate_anim);
        }
    }
}
