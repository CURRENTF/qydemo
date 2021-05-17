package com.example.qydemo0.QYpack;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aiunit.vision.utils.gesture.Hand;
import com.example.qydemo0.QYpack.Video.VideoInfo;
import com.example.qydemo0.Widget.QYScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;


public class AdvanceHttp {

    static String null_data = "{}";
    public static int finish_code = 114154;

    public static void getMyPosts(Handler handler, int startPos, int len){
        new Thread(new Runnable() {
            @Override
            public void run() {
                QYrequest htp = new QYrequest();
                Message msg = new Message();
                String res = htp.advanceMethod(
                        "GET", null_data, Constant.mInstance.post_url + "1/" +
                                Json2X.Json2StringGet(
                                        "user_id", GlobalVariable.mInstance.uid,
                                        "start", String.valueOf(startPos),
                                        "lens", String.valueOf(len)),
                        "Authorization", GlobalVariable.mInstance.token);
                msg.obj = MsgProcess.msgProcessArr(res, false, null);
                handler.sendMessage(msg);
            }
        }).start();
    }
    public static void getMyWorks(Handler handler, int startPos, int len){
        new Thread(new Runnable() {
            @Override
            public void run() {
                QYrequest htp = new QYrequest();
                Message msg = new Message();
                String res = htp.advanceGet(Constant.mInstance.work_url + Json2X.Json2StringGet("start", String.valueOf(startPos), "lens", String.valueOf(len)),
                        "Authorization", GlobalVariable.mInstance.token);
                msg.obj = MsgProcess.msgProcessArr(res, false, null);
                handler.sendMessage(msg);
            }
        }).start();
    }
    public static void getMyRenders(Handler handler, int startPos, int len){
        new Thread(new Runnable() {
            @Override
            public void run() {
                QYrequest htp = new QYrequest();
                Message msg = new Message();
                String res = htp.advanceGet(Constant.mInstance.task_url, "Authorization", GlobalVariable.mInstance.token);
                Log.i("hjt.get.render", res);
                msg.obj = MsgProcess.msgProcessArr(res, false, null);
                handler.sendMessage(msg);
            }
        }).start();
    }
    public static void getCategories(Handler handler){
        new Thread(new Runnable() {
            @Override
            public void run() {
                QYrequest htp = new QYrequest();
                Message msg = new Message();
                String res = htp.advanceGet(Constant.mInstance.getClas_url, "Authorization", GlobalVariable.mInstance.token);
                Log.e("hjt", res);
                JSONObject json = MsgProcess.msgProcess(res, false, null);
                try {
                    JSONArray ja = json.getJSONArray("classification");
                    msg.obj = ja;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static void getCategoryWorks(Handler handler, int startPos, int len, String name){
        new Thread(new Runnable() {
            @Override
            public void run() {
                QYrequest htp = new QYrequest();
                String[] data = {"text", "string", name, "start", "int", String.valueOf(startPos), "lens", "int", String.valueOf(len)};
                String res = htp.advancePost(GenerateJson.universeJson2(data), Constant.mInstance.work_c_url, "Authorization", GlobalVariable.mInstance.token);
                Log.d("hjt.out", res);
                JSONArray ja = MsgProcess.msgProcessArr(res, false, null);
                Message msg = new Message();
                msg.obj = ja;
                handler.sendMessage(msg);
            }
        }).start();
    }
    public static void getGameChallengeStars(Handler handler){
        new Thread(new Runnable() {
            @Override
            public void run() {
                QYrequest htp = new QYrequest();
                String res = htp.advanceGet(Constant.mInstance.challenge_url, "Authorization", GlobalVariable.mInstance.token);
                Log.d("hjt.challenge", res);
                JSONArray ja = MsgProcess.msgProcessArr(res, false, null);
                Message msg = new Message();
                msg.obj = ja;
                handler.sendMessage(msg);
            }
        }).start();
    }
    public static void postGameFreeImage(Handler handler, String file_id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                QYrequest htp = new QYrequest();
                String res = htp.advancePost(GenerateJson.universeJson("img", file_id), Constant.mInstance.postImage_url, "Authorization", GlobalVariable.mInstance.token);
                Message msg = new Message();
                msg.arg1 = MsgProcess.checkMsg(res, false, null) ? 1 : 0;
                handler.sendMessage(msg);
            }
        }).start();
    }
    public static void getGameRank(Handler handler, int type){
        new Thread(new Runnable() {
            @Override
            public void run() {
                QYrequest htp = new QYrequest();
                String res = htp.advanceGet(Constant.mInstance.gameRank_url + type + '/', "Authorization", GlobalVariable.mInstance.token);
                Log.d("hjt.gameRank", res);
                Message msg = new Message();
                JSONObject json = MsgProcess.msgProcess(res, false, null);
                if(type == 1 || type == 2){
                    try {
                        JSONArray ja = json.getJSONArray("top");
                        JSONObject t = json.getJSONObject("user");
                        ja.put(t);
                        msg.obj = ja;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        JSONArray ja = json.getJSONArray("top");
                        JSONArray t = json.getJSONArray("user");
                        for(int i = 0; i < t.length(); i++){
                            ja.put(t.getJSONObject(i));
                        }
                        msg.obj = ja;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public static void uploadWorkAllIn(Handler finish, String video_path, Context context, VideoInfo info){
        new Thread(new Runnable() {
            @Override
            public void run() {
                QYFile qyFile = new QYFile();
                String video_id = qyFile.uploadFileAllIn(video_path, QYFile.VideoCode);
                Message msg = new Message();
                msg.arg1 = 1;
                Bundle str = new Bundle();
                if(video_id == null){
                    msg.arg1 = 0;
                    str.putString("msg", "视频上传失败");
                    msg.setData(str);
                    finish.sendMessage(msg);
                    return;
                }
                VideoClip videoClip = new VideoClip();
                Bitmap cover = videoClip.getCoverFromVideo(video_path);
                String coverUrl = Img.saveImg(cover, String.valueOf(cover.hashCode()), context);
                String cover_id = qyFile.uploadFileAllIn(coverUrl, QYFile.ImageCode);
                if(cover_id == null){
                    msg.arg1 = 1;
                    str.putString("msg", "封面上传失败");
                    msg.setData(str);
                    finish.sendMessage(msg);
                    return;
                }
                QYrequest htp = new QYrequest();
                info.coverId = cover_id;
                info.videoId = video_id;
                String res = htp.advancePost(info.toData(), Constant.mInstance.work_url,
                        "Authorization", GlobalVariable.mInstance.token);
                Log.d("hjt.params", info.toData() + '\n' +  Constant.mInstance.work_url+ '\n' +
                        "Authorization"+ '\n' +   GlobalVariable.mInstance.token);
                if(MsgProcess.checkMsg(res, false, null)){
                    msg.arg1 = finish_code;
                    finish.sendMessage(msg);
                }
                else {
                    msg.arg1 = 2;
                    Log.d("hjt.wrong.upload.work", res);
                    str.putString("msg", MsgProcess.getWrongMsg(res));
                    msg.setData(str);
                    finish.sendMessage(msg);
                }
            }
        }).start();
    }
}
