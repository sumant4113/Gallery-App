package com.example.galleryapp.test.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.galleryapp.R;
import com.example.galleryapp.test.Activity.ViewPictureActivity;

import java.io.File;
import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<String> image_list = new ArrayList<>();

    public GalleryAdapter(Context context, ArrayList<String> image_list) {
        this.context = context;
        this.image_list = image_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gellery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        File image_file = new File(image_list.get(position));

        if (image_file.exists()) {
//            Glide.with(context).load(image_file).into(holder.imgGalleryItem);
            Glide.with(context)
                    .load(image_file) // Load video thumbnail
                    .error(R.drawable.img_error)// Error image
//                    .thumbnail(0.1f)
//                    .override(300, 300)  // Resize image
//                    .placeholder(R.drawable.ic_launcher_foreground)  // Placeholder image
                    .into(holder.imgGalleryItem);

        }

        holder.imgGalleryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewPictureActivity.class);
                intent.putStringArrayListExtra("image_path", image_list);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return image_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgGalleryItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgGalleryItem = itemView.findViewById(R.id.img_gallery_item);
        }
    }
}
