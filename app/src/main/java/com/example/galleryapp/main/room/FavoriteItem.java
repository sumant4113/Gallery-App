package com.example.galleryapp.main.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_items")
public class FavoriteItem {

    @PrimaryKey(autoGenerate = true)
    private int id;// unique Id Auto generated by room

    private String uri;
    private boolean isFavorite;

    public FavoriteItem(String uri, boolean isFavorite) {
        this.uri = uri;
        this.isFavorite = isFavorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
