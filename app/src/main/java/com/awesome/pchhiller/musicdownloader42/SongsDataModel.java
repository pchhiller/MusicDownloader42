package com.awesome.pchhiller.musicdownloader42;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Daljit on 06-07-2017.
 */

public class SongsDataModel {
    @SerializedName("songName")
    @Expose
    private String songName;
    @SerializedName("songUrl")
    @Expose
    private String songUrl;

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public SongsDataModel(String songName, String songUrl)
    {
        this.songName=songName;
        this.songUrl=songUrl;
    }
}