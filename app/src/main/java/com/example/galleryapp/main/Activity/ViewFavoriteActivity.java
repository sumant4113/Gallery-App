package com.example.galleryapp.main.Activity;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryapp.R;
import com.example.galleryapp.main.Adapter.FavoriteAdapter;
import com.example.galleryapp.main.sqlite.FavDbHelper;

public class ViewFavoriteActivity extends AppCompatActivity {


    private RecyclerView rvFavItems;
    private FavoriteAdapter adapter;
    private FavDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_favorite);

        initView();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initView() {
        rvFavItems = findViewById(R.id.rv_favItems);


    }

    private void loadFavorites() {
        dbHelper = new FavDbHelper(this);
        Cursor cursor = dbHelper.getAllFavorite();
        adapter = new FavoriteAdapter(this, cursor);
        rvFavItems.setAdapter(adapter);
    }
}