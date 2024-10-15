package com.example.galleryapp.main.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class VideoModel implements Parcelable {
    String id;
    String path;
    String title;
    String size;
    String resolution;
    String duration;
    String displayName;
    String widthHeight;
    String dataAdded;

    public VideoModel(String id, String path, String title, String size, String resolution, String duration, String displayName, String widthHeight, String dataAdded) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.size = size;
        this.resolution = resolution;
        this.duration = duration;
        this.displayName = displayName;
        this.widthHeight = widthHeight;
        this.dataAdded = dataAdded;
    }
    // Parcelable implementation
    protected VideoModel(Parcel in) {
        id = in.readString();
        path = in.readString();
        title = in.readString();
        size = in.readString();
        resolution = in.readString();
        duration = in.readString();
        displayName = in.readString();
        widthHeight = in.readString();
        dataAdded = in.readString();
    }


    public static final Creator<VideoModel> CREATOR = new Creator<VideoModel>() {
        @Override
        public VideoModel createFromParcel(Parcel in) {
            return new VideoModel(in);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(path);
        dest.writeString(title);
        dest.writeString(size);
        dest.writeString(resolution);
        dest.writeString(duration);
        dest.writeString(displayName);
        dest.writeString(widthHeight);
        dest.writeString(dataAdded);
    }

    public String getDataAdded() {
        return dataAdded;
    }

    public void setDataAdded(String dataAdded) {
        this.dataAdded = dataAdded;
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
        if (dataAdded != null? !dataAdded.equals(videoModel.dataAdded) : videoModel.dataAdded != null) return false;

        return true;
    }
}