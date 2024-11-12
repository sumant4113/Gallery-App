package com.example.galleryapp.main.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FavoriteItem.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {

    private static AppDataBase instance;

    public abstract FavoriteItemDao favoriteItemDao();

    // Singleton patten to avoid multiple database instances
    public static synchronized AppDataBase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDataBase.class, "favorite_items_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}