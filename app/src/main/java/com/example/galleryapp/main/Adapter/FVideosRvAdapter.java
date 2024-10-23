package com.example.galleryapp.main.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.galleryapp.R;
import com.example.galleryapp.main.Activity.ViewVideoActivity;
import com.example.galleryapp.main.Model.VideoModel;

import java.util.ArrayList;

public class FVideosRvAdapter extends RecyclerView.Adapter<FVideosRvAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<VideoModel> videoFolderList;

    public FVideosRvAdapter(Context context, ArrayList<VideoModel> videoFolderList) {
        this.context = context;
        this.videoFolderList = videoFolderList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout. item_file_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        VideoModel videoModel = videoFolderList.get(position);

        Glide.with(context)
                .load(videoModel.getPath())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.imgVideoThumbnail);

        holder.txtVideoTime.setText(videoModel.getDuration());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewVideoActivity.class);
            intent.putParcelableArrayListExtra("video_path", videoFolderList);
            intent.putExtra("position", position);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return videoFolderList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgVideoThumbnail;
        TextView txtVideoTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgVideoThumbnail = itemView.findViewById(R.id.img_videoThumbnail);
            txtVideoTime = itemView.findViewById(R.id.txt_videoTime);
        }
    }

}