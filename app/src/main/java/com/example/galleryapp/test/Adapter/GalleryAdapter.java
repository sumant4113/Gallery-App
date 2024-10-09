package com.example.galleryapp.test.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.galleryapp.R;
import com.example.galleryapp.test.Activity.ViewPictureActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private static final String TAG = "GalleryAdapter";
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


        if (image_file.exists()) {// File is exists
            if (isHeifOrHeic(image_file.getName())) { // check heif or heic
                try { // in try block convert heif to bitmap
                    Bitmap bitmap = convertHeifToBitMap(String.valueOf(image_file));
                    if (bitmap != null) {
                        holder.imgGalleryItem.setImageBitmap(bitmap);
                    } else {
                        holder.imgGalleryItem.setImageResource(R.drawable.img_error);
                    }
                } catch (IOException e) {
                    holder.imgGalleryItem.setImageResource(R.drawable.img_error);
                    Toast.makeText(context, "Glide fail Heif" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onBindViewHolder: HeifOrHeic"+e.getMessage());
                    throw new RuntimeException(e);
                }
            } else {
                Glide.with(context)
                        .load(image_file) // Load video thumbnail
                        .error(R.drawable.img_error)// Error image
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .thumbnail(0.1f)
//                    .override(300, 300)  // Resize image
//                    .placeholder(R.drawable.ic_launcher_foreground)  // Placeholder image
                        .into(holder.imgGalleryItem);
            }
        } else {
            holder.imgGalleryItem.setImageResource(R.drawable.img_error);

        }

        holder.imgGalleryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewPictureActivity.class);
                intent.putStringArrayListExtra("image_path", image_list);
//                intent.putExtra("image_name", image_list);
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

    public Bitmap convertHeifToBitMap(String heifFilePath) throws IOException {
        File file = new File(heifFilePath);
        InputStream is = new FileInputStream(file);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        is.close();
        return bitmap;
    }

    private boolean isHeifOrHeic(String fileName) {
        String fileNameLower = fileName.toLowerCase();
        return fileNameLower.endsWith("heif") || fileNameLower.endsWith("heic");
    }
}
