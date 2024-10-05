package com.example.galleryapp;

public class Image {
    int id;
    String data;
    String name;

    public Image (int id, String data, String name) {
        this.id = id;
        this.data = data;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public String getName() {
        return name;
    }
}
