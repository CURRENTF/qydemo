package com.example.qydemo0.QYpack;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;


public class AdvanceHttp {

    static String null_data = "{}";

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
                String res = htp.advanceGet(Constant.mInstance.render_progress_url + Json2X.Json2StringGet("start", String.valueOf(startPos), "lens", String.valueOf(len)),
                        "Authorization", GlobalVariable.mInstance.token);
                msg.obj = MsgProcess.msgProcessArr(res, false, null);
                handler.sendMessage(msg);
            }
        }).start();
    }
}
