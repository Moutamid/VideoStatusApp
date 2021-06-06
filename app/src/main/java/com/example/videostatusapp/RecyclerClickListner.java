package com.example.videostatusapp;

import android.view.View;

public interface RecyclerClickListner {
    void clickedDownload(View view, int position, String videoname, String url);

    void clickedFavourite(View view, int position, String videoname, String url);
}
