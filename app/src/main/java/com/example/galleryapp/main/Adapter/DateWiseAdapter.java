package com.example.galleryapp.main.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.galleryapp.R;
import com.example.galleryapp.main.Model.ImageModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DateWiseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_DATE = 0;
    private static final int VIEW_TYPE_IMAGE = 1;

    private final List<Object> items;  // Mix = String Date and Image list

    public DateWiseAdapter(Map<String, List<ImageModel>> groupedData) {
        items = new ArrayList<>();

        for (Map.Entry<String, List<ImageModel>> entry : groupedData.entrySet()) {
            items.add(entry.getKey());  // Add date header
            items.addAll(entry.getValue());   // Add All images
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position) instanceof String) ? VIEW_TYPE_DATE : VIEW_TYPE_IMAGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gellery, parent, false);
            return new ImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_DATE) {
            String date = (String) items.get(position);
            ((HeaderViewHolder) holder).bind(date);
        } else {
            ImageModel model = (ImageModel) items.get(position);
            ((ImageViewHolder) holder).bind(model);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder for date headers
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView txtDateHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDateHeader = itemView.findViewById(R.id.txt_date);
        }

        void bind(String date) {
            txtDateHeader.setText(date);
        }
    }

    // ViewHolder for image items
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgGalleryItem;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgGalleryItem = itemView.findViewById(R.id.img_gallery_item);
        }

        void bind(ImageModel image) {
            Glide.with(itemView.getContext())
                    .load(image.getPath())
                    .into(imgGalleryItem);
        }
    }

}
