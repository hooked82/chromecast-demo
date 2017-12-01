package com.hookedroid.chromecastdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoModel implements Parcelable {

    private String videoUrl;
    private String thumbUrl;
    private String title;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(videoUrl);
        out.writeString(thumbUrl);
        out.writeString(title);
    }

    private void readFromParcel(Parcel in) {
        videoUrl = in.readString();
        thumbUrl = in.readString();
        title = in.readString();
    }

    public static final Parcelable.Creator<VideoModel> CREATOR
            = new Parcelable.Creator<VideoModel>() {
        @Override
        public VideoModel createFromParcel(Parcel in) {
            return new VideoModel(in);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[size];
        }
    };

    private VideoModel(Parcel in) {
        readFromParcel(in);
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
