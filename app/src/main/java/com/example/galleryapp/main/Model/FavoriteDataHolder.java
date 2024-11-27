package com.example.galleryapp.main.Model;

import java.util.ArrayList;

public class FavoriteDataHolder {
    private static FavoriteDataHolder instance;
    private ArrayList<FavoriteModel> favoriteList;

    private FavoriteDataHolder() {
        favoriteList = new ArrayList<>();
    }

    public static synchronized FavoriteDataHolder getInstance() {
        if (instance == null) {
            instance = new FavoriteDataHolder();
        }
        return instance;
    }

    public ArrayList<FavoriteModel> getFavoriteList() {
        return favoriteList;
    }

    public void setFavoriteList(ArrayList<FavoriteModel> favoriteList) {
        this.favoriteList = favoriteList;
    }

    public FavoriteModel getFavoriteAt(int posi) {
        if (favoriteList != null && posi >= 0 && posi < favoriteList.size()) {
            return favoriteList.get(posi);
        }
        return null;
    }
}
