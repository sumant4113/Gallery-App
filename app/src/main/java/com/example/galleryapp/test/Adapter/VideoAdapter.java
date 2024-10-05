package com.example.galleryapp.test.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.galleryapp.R;
import com.example.galleryapp.test.Activity.ViewVideoActivity;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<String> videoList;

    public VideoAdapter(Context context, ArrayList<String> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Load video thumbnail using Glide
        String videoPath = videoList.get(position);

/*        Picasso.get().load(videoPath).resize(50, 50)
            .error(R.drawable.img_error).placeholder(R.drawable.baseline_photo_library_24).centerCrop().into(holder.videoThumbnail);*/

        Glide.with(context)
                .load(videoPath) // Load video thumbnail
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.img_error)// Error image
                .fitCenter()
                .thumbnail(0.1f)
                .placeholder(R.drawable.ic_launcher_foreground)  // Placeholder image
                .into(holder.videoThumbnail);

        holder.videoThumbnail.setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewVideoActivity.class);
            intent.putStringArrayListExtra("video_path", videoList);
            intent.putExtra("position", position);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView videoThumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.img_video_item); // Assume your item layout has this ImageView
        }
    }
}
