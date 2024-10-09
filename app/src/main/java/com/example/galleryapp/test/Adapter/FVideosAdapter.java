package com.example.galleryapp.test.Adapter;

import android.content.Context;
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
import com.example.galleryapp.test.Model.VideoModel;

import java.util.ArrayList;

public class FVideosAdapter extends RecyclerView.Adapter<FVideosAdapter.MyViewHolder> {

    private ArrayList<VideoModel> videoFolderList;
    private Context context;

    public FVideosAdapter(ArrayList<VideoModel> videoFolderList, Context context) {
        this.videoFolderList = videoFolderList;
        this.context = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_view, parent, false);
        return new MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        VideoModel  videoModel  = videoFolderList.get(position);
        Glide.with(context)
                .load(videoModel.getPath())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.imgVideoThumbnail);

        holder.txtVideoTime.setText(videoModel.getDuration());
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
