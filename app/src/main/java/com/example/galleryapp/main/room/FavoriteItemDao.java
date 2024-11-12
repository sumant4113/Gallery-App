package com.example.galleryapp.main.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteItemDao {

    @Insert
    void insert(FavoriteItem item);

    // Get all favoriteItems
    @Query("SELECT * FROM favorite_items")
    List<FavoriteItem> getAllFavorite();

    // Update
    @Query("UPDATE favorite_items SET isFavorite = :isFavorite WHERE uri = :uri")
    void updateFavoriteStatus(String uri, boolean isFavorite);

    // DELETE
    @Query("DELETE FROM favorite_items WHERE uri = :uri")
    void deleteFavoriteItem(String uri);

}
