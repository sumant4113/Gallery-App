package com.example.galleryapp.main.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.galleryapp.R;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {
    private Context context;
    private Cursor cursor;

    public FavoriteAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex("path"));
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
            @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex("type"));

            holder.title.setText(title);
//            Picasso.get().load("file://" + path).into(holder.thumbnail); // Load from path
            Glide.with(context).load("file://"+ path).into(holder.thumbnail);
            holder.typeIndicator.setText(type.equals("image") ? "Image" : "Video");
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title, typeIndicator;

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.title);
            typeIndicator = itemView.findViewById(R.id.typeIndicator);
        }
    }
}
