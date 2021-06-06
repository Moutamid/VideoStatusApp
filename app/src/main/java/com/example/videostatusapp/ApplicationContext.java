package com.example.videostatusapp;

import android.app.Application;

import com.downloader.PRDownloader;
import com.fxn.stash.Stash;

public class ApplicationContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PRDownloader.initialize(getApplicationContext());
        Stash.init(this);

    }
}
