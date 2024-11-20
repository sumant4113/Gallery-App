package com.example.galleryapp.main.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class FavDbHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "GalleryApp.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_FAVORITES = "favorites";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PATH = "path";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_TITLE = "title";

    public FavDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + DATABASE_NAME + " (" +
                COLUMN_ID + "TEXT PRIMARY KEY, " +
                COLUMN_PATH + " TEXT," +
                COLUMN_TYPE + " TEXT," +
                COLUMN_TITLE + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }

    public void addFav(String id, String path, String type, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_PATH, path);
        cv.put(COLUMN_TYPE, type);
        cv.put(COLUMN_TITLE, title); // Corrected to use COLUMN_TITLE

        db.insert(TABLE_FAVORITES, null, cv);
        db.close();
    }

    // Remove Fav Item
    public void removeFav(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, COLUMN_ID + "=?", new String[]{id});
        db.close();
    }

    // checking item FAV OR NOT FAV
    public boolean isFavorite(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES,
                new String[]{COLUMN_ID},
                COLUMN_ID + "=?",
                new String[]{id},
                null, null, null);
        boolean isFavorite = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isFavorite;
    }

    public Cursor getAllFavorite() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_FAVORITES, null, null, null, null, null, null);
    }

}
