package com.example.qydemo0.QYpack;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class MsgProcess {

    public static JSONObject msgProcess(String msg){
        Constant C = Constant.mInstance;
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
}
