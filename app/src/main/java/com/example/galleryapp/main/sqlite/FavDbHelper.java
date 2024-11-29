package com.example.galleryapp.main.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class FavDbHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "galleryApp.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_FAVORITES = "favorites";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TITLE = "title";

    public FavDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_FAVORITES + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_PATH + " TEXT, " +
                COLUMN_TYPE + " TEXT, " +
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

        db.insertWithOnConflict(TABLE_FAVORITES, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Remove Fav Item
    public void removeFav(String item_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_FAVORITES, COLUMN_ID + "=?", new String[]{item_id});
//        if (result == -1) {
//            Toast.makeText(context, "Delete DB Failed", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(context, "Remove SQLite", Toast.LENGTH_SHORT).show();
//        }
        db.close();
    }

    // checking item FAV OR NOT FAV
    public boolean isFavorite(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES,
                new String[]{COLUMN_ID},
                COLUMN_ID + " =?",
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

    public void updateFavPath(String id, String newPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PATH, newPath);

        db.update(TABLE_FAVORITES, cv, COLUMN_ID, new String[]{id});
        db.close();
    }

}
