package com.example.galleryapp.main.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.galleryapp.R;
import com.example.galleryapp.main.Activity.ViewPictureActivity;
import com.example.galleryapp.main.Model.ImageModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GalleryRvAdapter extends RecyclerView.Adapter<GalleryRvAdapter.ViewHolder> {

    private static final String TAG = "GalleryAdapter";
    private final Context context;
    private ArrayList<ImageModel> image_list = new ArrayList<>();

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
            // Load image into ImageView (e.g., using Glide or Picasso)
            Glide.with(context)
                    .load(image_file)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.imgGalleryItem);

        }

//        File image_file = new File(String.valueOf(image_list.get(position)));

       /* if (image_file.exists()) {// File is exists
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
                    Log.d(TAG, "onBindViewHolder: HeifOrHeic" + e.getMessage());
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
        }*/

        holder.imgGalleryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewPictureActivity.class);
                intent.putParcelableArrayListExtra("image_path", image_list);
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
       private final ImageView imgGalleryItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgGalleryItem = itemView.findViewById(R.id.img_gallery_item);
        }
    }

    // Convert heif to bitmap
    public Bitmap convertHeifToBitMap(String heifFilePath) throws IOException {
        File file = new File(heifFilePath);
        InputStream is = new FileInputStream(file);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        is.close();
        return bitmap;
    }

    // Check heif or heic
    private boolean isHeifOrHeic(String fileName) {
        String fileNameLower = fileName.toLowerCase();
        return fileNameLower.endsWith("heif") || fileNameLower.endsWith("heic");
    }
}
