package com.example.qydemo0.QYpack.Video;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYFile;
import com.example.qydemo0.QYpack.QYrequest;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONObject;

public class Work {
    public static Boolean post(VideoInfo videoInfo){
        String name = videoInfo.videoName;
        String intro = videoInfo.intro;
        String videoId = videoInfo.videoId;
        String coverId = videoInfo.coverId;
        String[] tags = videoInfo.tags;
        String classification = videoInfo.clas;
        QYrequest qYrequest = new QYrequest();
        String msg =  qYrequest.advancePost(GenerateJson.universeJson("name", name, "introduction", intro,
                "video", videoId, "cover", coverId, "tag", GenerateJson.listString(0, tags), "classification", classification),
                Constant.mInstance.work, "Authorization", GlobalVariable.mInstance.token);
        JSONObject jsonObject = MsgProcess.msgProcess(msg);
        if(jsonObject == null) return false;
        else return true;
    }
}
