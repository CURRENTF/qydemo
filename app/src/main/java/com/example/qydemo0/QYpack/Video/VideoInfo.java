package com.example.qydemo0.QYpack.Video;

public class VideoInfo {
    public String videoName = null, clas = null, intro = null, coverId = null, videoId = null;
    public String[] tags = null;
    VideoInfo(String vn, String cl, String intro, String[] ts){
        this.videoName = vn;
        this.clas = cl;
        this.intro = intro;
        tags = new String[ts.length];
        for(int i = 0; i < ts.length; i++) tags[i] = ts[i];
    }
}
