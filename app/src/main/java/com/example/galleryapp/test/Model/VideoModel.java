package com.example.galleryapp.test.Model;

public class VideoModel {

    String id;
    String path;
    String title;
    String size;
    String resolution;
    String duration;
    String displayName;
    String widthHeight;

    public VideoModel(String id, String path, String title, String size, String resolution, String duration, String displayName, String wh) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.size = size;
        this.resolution = resolution;
        this.duration = duration;
        this.displayName = displayName;
        this.widthHeight = wh;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getWidthHeight() {
        return widthHeight;
    }

    public void setWidthHeight(String widthHeight) {
        this.widthHeight = widthHeight;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        VideoModel videoModel = (VideoModel) obj;

        // Null-safe comparisons for all fields
        if (path != null ? !path.equals(videoModel.path) : videoModel.path != null) return false;
        if (title != null ? !title.equals(videoModel.title) : videoModel.title != null) return false;
        if (size != null ? !size.equals(videoModel.size) : videoModel.size != null) return false;
        if (resolution != null ? !resolution.equals(videoModel.resolution) : videoModel.resolution != null) return false;
        if (duration != null ? !duration.equals(videoModel.duration) : videoModel.duration != null) return false;
        if (displayName != null ? !displayName.equals(videoModel.displayName) : videoModel.displayName != null) return false;
        if (widthHeight != null ? !widthHeight.equals(videoModel.widthHeight) : videoModel.widthHeight != null) return false;

        return true;
    }


}
