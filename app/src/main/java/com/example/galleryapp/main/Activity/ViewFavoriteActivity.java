package com.example.galleryapp.main.Activity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Adapter.FavoriteAdapter;
import com.example.galleryapp.main.sqlite.FavDbHelper;

public class ViewFavoriteActivity extends AppCompatActivity {

    private static final String TAG = "ViewFavoriteActivity";
    private RecyclerView rvFavItems;
    private FavoriteAdapter adapter;
    private FavDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_favorite);
        initView();
    }

    private void initView() {
        ImageView img_backBtn = findViewById(R.id.img_backBtn);
        rvFavItems = findViewById(R.id.rv_favItems);
        dbHelper = new FavDbHelper(this);
        rvFavItems.setLayoutManager(new GridLayoutManager(this, 3));

        img_backBtn.setOnClickListener(v -> onBackPressed());
        loadFavorites();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadFavorites() {
        Cursor cursor = dbHelper.getAllFavorite();

        if (adapter == null) {
            adapter = new FavoriteAdapter(this, cursor);
            rvFavItems.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}