package com.example.galleryapp.main.Model;

import java.util.ArrayList;

public class ImageDataHolder {
    private static ImageDataHolder instance;
    private ArrayList<ImageModel> imageList; // Private member to hold data

    public ImageDataHolder() {
        imageList = new ArrayList<>();
    }

    // Public method to provide the single instance of the class
    public static synchronized ImageDataHolder getInstance() {
        if (instance == null) {
            instance = new ImageDataHolder();
        }
        return instance;
    }

    // Retrieve the list
    public ArrayList<ImageModel> getImageList() {
        return imageList;
    }

    // Store the list
    public void setImageList(ArrayList<ImageModel> imageList) {
        this.imageList = imageList;
    }

    // Retrieve from specific item from list
    public ImageModel getImageAt(int posi) {
        if (imageList != null && posi >= 0 && posi < imageList.size()) {
            return imageList.get(posi);
        }
        return null; // Return null if the position is invalid
    }

}
