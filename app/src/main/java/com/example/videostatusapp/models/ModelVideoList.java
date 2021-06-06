package com.example.videostatusapp.models;

public class ModelVideoList {
    private  int video_image;
    private  String video_name,url,category;

    public ModelVideoList(int video_image, String video_name, String url, String category) {
        this.video_image = video_image;
        this.video_name = video_name;
        this.url = url;
        this.category = category;
    }

    public int getVideo_image() {
        return video_image;
    }

    public String getVideo_name() {
        return video_name;
    }

    public String getUrl() {
        return url;
    }

    public String getCategory() {
        return category;
    }
}
