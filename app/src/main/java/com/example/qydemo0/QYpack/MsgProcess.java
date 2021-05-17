package com.example.qydemo0.QYpack;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MsgProcess {

    public static String getWrongMsg(String msg){
        JSONObject json = null;
        try {
            json = new JSONObject(msg);
            if(json.getInt("status") != Constant.mInstance.HTTP_OK){
                return json.getString("msg");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject msgProcess(String msg, Boolean status, String loc){
        Constant C = Constant.mInstance;
        if(status) Log.d("hjt.msgProcess:" + loc, msg);
        try {
            JSONObject json = new JSONObject(msg);
            if(json.getInt("status") == C.HTTP_OK){
                try {
                    json = new JSONObject(json.getString("data"));
                    return json;
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else {
                Log.e("hjtMsgProcess", json.getString("msg"));
            }
        } catch (JSONException e) {
            Log.e("hjt.MsgProcess", "jsonMsgProcessWrong");
            e.printStackTrace();
        }
        return null;
    }
    public static JSONArray msgProcessArr(String msg, Boolean status, String loc){
        Constant C = Constant.mInstance;
        if(status) Log.d("hjt.msgProcess.arr:" + loc, msg);
        try {
            JSONObject json = new JSONObject(msg);
            if(json.getInt("status") == C.HTTP_OK){
                try {
                    JSONArray t = new JSONArray(json.getString("data"));
                    return t;
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else {
                Log.e("hjtMsgProcess", json.getString("msg"));
            }
        } catch (JSONException e) {
            Log.e("hjt.MsgProcess", "jsonMsgProcessWrong");
            if(status) Log.e("hjt.MsgProgress", msg);
            e.printStackTrace();
        }
        return null;
    }


    public static boolean checkMsg(String msg, Boolean status, String location){
        try {
            if(status)
                Log.d("hjt.check.msg:" + location, msg);
            JSONObject json = new JSONObject(msg);
            if(json.getInt("status") == Constant.mInstance.HTTP_OK) return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}