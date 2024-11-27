package com.example.galleryapp.main.Model;

public class FavoriteModel  {
    private String path;
    private String type; // "image" or "video"

    public FavoriteModel(String path, String type) {
        this.path = path;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}