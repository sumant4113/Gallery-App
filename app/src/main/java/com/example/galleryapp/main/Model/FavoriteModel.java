package com.example.galleryapp.main.Model;

public class FavoriteModel {
    private String id;
    private String path;
    private String type; // "image" or "video"
    private String title;

    public FavoriteModel(String id, String path, String type, String title) {
        this.id = id;
        this.path = path;
        this.type = type;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
