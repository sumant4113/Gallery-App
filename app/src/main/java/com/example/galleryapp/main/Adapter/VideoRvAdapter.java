package com.example.galleryapp.main.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.galleryapp.R;
import com.example.galleryapp.main.Activity.ViewVideoActivity;
import com.example.galleryapp.main.Model.VideoModel;

import java.util.ArrayList;

public class VideoRvAdapter extends RecyclerView.Adapter<VideoRvAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<VideoModel> videoList;

    public VideoRvAdapter(Context context, ArrayList<VideoModel> videoList) {
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
        VideoModel videoModel = videoList.get(position);
        String videoPath = videoModel.getPath();

        Glide.with(context)
                .load(videoPath) // Load video thumbnail
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .error(R.drawable.img_error)// Error image
                .fitCenter()
                .thumbnail(0.1f)
                .placeholder(R.drawable.ic_launcher_foreground)  // Placeholder image
                .into(holder.videoThumbnail);

        holder.videoThumbnail.setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewVideoActivity.class);
            intent.putParcelableArrayListExtra("video_path", videoList);
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

    private Uri getVideoThumbnail(String videoPath) {
        // Get the thumbnail URI from the video path
        return Uri.parse("content://" + MediaStore.Video.Media.EXTERNAL_CONTENT_URI + "/video/" + videoPath);
    }
}
