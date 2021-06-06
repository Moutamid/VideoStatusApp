package com.example.videostatusapp.models;

public class VideoList {

    public int id;
    public String video_url;
    public String video_name;


    public VideoList(int id, String video_url, String video_name) {
        this.id = id;
        this.video_url = video_url;
        this.video_name = video_name;

    }

    public int getId() {
        return id;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getVideo_name() {
        return video_name;
    }

}
