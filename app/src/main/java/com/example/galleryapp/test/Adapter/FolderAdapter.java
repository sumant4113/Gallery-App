package com.example.galleryapp.test.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.galleryapp.R;
import com.example.galleryapp.test.Activity.FolderActivity;
import com.example.galleryapp.test.Fragment.FolderFragment;
import com.example.galleryapp.test.Model.VideoModel;

import java.io.File;
import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyClassHolder> {

    private final Context context;
    private ArrayList<String> folderNameList;
    private ArrayList<VideoModel> videoModel;

    public FolderAdapter(Context context, ArrayList<String> folderNameList, ArrayList<VideoModel> videoModel) {
        this.context = context;
        this.folderNameList = folderNameList;
        this.videoModel = videoModel;
    }

    // Add a method to update the adapter's data using DiffUtil
    public void updateVideoList(ArrayList<VideoModel> newVideoModel) {
        FolderFragment.FolderDiffCallback diffCallback = new FolderFragment.FolderDiffCallback(this.videoModel, newVideoModel);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.videoModel.clear();
        this.videoModel.addAll(newVideoModel);

        // Apply the updates to the adapter
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public MyClassHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.folder_view, parent, false);
        return new MyClassHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyClassHolder holder, int position) {
        String folderPath = folderNameList.get(position);

        // Extract folder name from path
        int index = folderNameList.get(position).lastIndexOf("/");
        String folderName = (index != -1) ? folderPath.substring(index + 1) : folderPath;
//        String folderName = folderNameList.get(position).substring(index);
//        String folderNAME = folderPath.substring(index + 1);

        holder.txtFolderName.setText(folderName);
        holder.txtFolderCount.setText(String.valueOf(countVideos(folderNameList.get(position))));

        String latestFilePath = getLatestFilePath(folderPath);

        if (latestFilePath != null) {
            Glide.with(context).load(latestFilePath).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(holder.imgFolderThumbnail);
        } else {
            holder.imgFolderThumbnail.setImageResource(R.drawable.brand_google_photos_white);
        }

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, folderName, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, FolderActivity.class);
            intent.putExtra("folderName", folderName);
            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return folderNameList.size();
    }

    public static class MyClassHolder extends RecyclerView.ViewHolder {
        TextView txtFolderName, txtFolderCount;
        ImageView imgFolderThumbnail;

        public MyClassHolder(@NonNull View itemView) {
            super(itemView);
            imgFolderThumbnail = itemView.findViewById(R.id.img_folder_thumbnail);
            txtFolderCount = itemView.findViewById(R.id.txt_folder_count);
            txtFolderName = itemView.findViewById(R.id.txt_folder_name);
        }
    }

    private String getLatestFilePath(String folderPath) {
        for (VideoModel model : videoModel) {
            String path = model.getPath();
            if (path.startsWith(folderPath)) {
                return path;  // Return immediately as videos are ordered by newest first
            }
        }
        return null;
    }

//    private String getLatestFilePath(String folderPath) {
//        String latestFile = null;
//        long latestModified = Long.MIN_VALUE;
//
//        for (VideoModel model : videoModel) {
//            String path = model.getPath();
//            if (path.startsWith(folderPath)) {
//                File file = new File(path);
//                if (file.lastModified() > latestModified) {
//                    latestModified = file.lastModified();
//                    latestFile = path;
//                }
//            }
//        }
//        return latestFile;
//    }


    private int countVideos(String folder) {
        int count = 0;
        for (VideoModel model : videoModel) {
            String videoFolder = new File(model.getPath()).getParent();
            if (videoFolder != null && videoFolder.equals(folder)) {
                count++;
            }

           /*if (model.getPath()
                   .substring(0, model.getPath().lastIndexOf("/"))
                   .endsWith(folder)) {
               count++;
           }*/
        }
        return count;
    }

}
