package com.example.galleryapp.main.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.galleryapp.R;
import com.example.galleryapp.main.Activity.FavViewPictureActivity;
import com.example.galleryapp.main.Activity.ViewVideoActivity;
import com.example.galleryapp.main.Model.FavoriteDataHolder;
import com.example.galleryapp.main.Model.FavoriteModel;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {
    private Context context;
    //    private Cursor cursor;
    private ArrayList<FavoriteModel> favList;


    public FavoriteAdapter(Context context/*, Cursor cursor*/, ArrayList<FavoriteModel> favList) {
        this.context = context;
//        this.cursor = cursor;
        this.favList = favList;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gellery, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        if (position < 0 || position >= favList.size()) {
            return; // Safety check to avoid IndexOutOfBoundsException
        }
        FavoriteModel favorite = favList.get(position);
//        if (cursor.moveToPosition(position)) {
//            @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(FavDbHelper.COLUMN_PATH));
//            @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex("type"));
//            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));

//            favList.add(new FavoriteModel(path, type));
        Glide.with(context).load("file://" + favorite.getPath()).into(holder.thumbnail);
        holder.itemView.setOnClickListener(v -> {
            FavoriteDataHolder.getInstance().setFavoriteList(favList);

            Intent intent;
            if ("image".equals(favorite.getType())) {
                intent = new Intent(context, FavViewPictureActivity.class);
            } else {
                intent = new Intent(context, ViewVideoActivity.class);
            }
            intent.putExtra("position", position);
            context.startActivity(intent);

        });
//            holder.title.setText(title);
//            holder.typeIndicator.setText(type.equals("image") ? "Image" : "Video");
    }


    @Override
    public int getItemCount() {
        return favList.size();
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
//        TextView title, typeIndicator;

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.img_gallery_item);
//            title = itemView.findViewById(R.id.title);
//            typeIndicator = itemView.findViewById(R.id.typeIndicator);
        }

    }
}