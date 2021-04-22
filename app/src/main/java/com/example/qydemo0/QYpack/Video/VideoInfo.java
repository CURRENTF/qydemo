package com.example.qydemo0.QYpack.Video;

import android.util.Log;

import com.example.qydemo0.QYpack.GenerateJson;

public class VideoInfo {
    public String videoName = null, intro = null, coverId = null, videoId = null;
    public String[] tags = null;
    int classification;
    public VideoInfo(String vn, int cl, String intro, String[] ts){
        this.videoName = vn;
        this.classification = cl;
        this.intro = intro;
        tags = new String[ts.length];
        for(int i = 0; i < ts.length; i++) tags[i] = ts[i];
    }

    public static String checkMsg(String vn, String cl, String[] ts){
        if(vn.equals("")) return "视频名字不允许为空";
        if(cl.equals("")) return "视频类别不允许为空";
        if(ts.length == 0) return "至少添加一个tag";
        return null;
    }

    public String toData(){
        String[] data = {"name", "string", videoName, "introduction", "string", intro, "video_id", "string", videoId, "cover_id", "string", coverId,
            "tag", "string", GenerateJson.listStringWithSinglePoint(0, tags), "classification_id", "int", String.valueOf(classification), "is_public", "bool", "true"};
        Log.d("hjt.generateJson.list", GenerateJson.listStringWithSinglePoint(0, tags));
        return GenerateJson.universeJson2(data);
    }
}
