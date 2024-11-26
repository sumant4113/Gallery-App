package com.example.galleryapp.main.Model;

import java.util.ArrayList;

public class VideoDataHolder {

    private static VideoDataHolder instance;
    private ArrayList<VideoModel> videoList;
    public VideoDataHolder(){ videoList = new ArrayList<>();}

    public static synchronized VideoDataHolder getInstance() {

        if (instance == null) {
            instance = new VideoDataHolder();
        }

        return instance;
    }

    public ArrayList<VideoModel> getVideoList() {
        return videoList;
    }

    public void setVideoList(ArrayList<VideoModel> videoList) {
        this.videoList = videoList;
    }

    public VideoModel getVideoAt(int posi) {
        if (videoList != null && posi >= 0 && posi < videoList.size()) {
            return videoList.get(posi);
        }
        return null;
    }

}
