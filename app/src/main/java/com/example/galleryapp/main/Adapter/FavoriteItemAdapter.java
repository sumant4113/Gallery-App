package com.example.galleryapp.main.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryapp.R;
import com.example.galleryapp.main.room.FavoriteItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FavoriteItemAdapter extends RecyclerView.Adapter<FavoriteItemAdapter.ViewHolder> {

    private List<FavoriteItem> favoriteItems;

    public FavoriteItemAdapter(List<FavoriteItem> favoriteItems) {
        this.favoriteItems = favoriteItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gellery, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteItem item = favoriteItems.get(position);


    }

    @Override
    public int getItemCount() {
        return favoriteItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageGalleryItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageGalleryItem = itemView.findViewById(R.id.img_gallery_item);
        }
    }
}