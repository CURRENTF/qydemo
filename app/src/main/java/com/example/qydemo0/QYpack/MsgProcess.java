package com.example.qydemo0.QYpack;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class MsgProcess {

    public static JSONObject msgProcess(String msg){
        Constant C = new Constant();
        try {
            JSONObject json = new JSONObject(msg);
            if(json.getInt("status") == C.HTTP_OK){
                json = new JSONObject(json.getString("data"));
                return json;
            }
            else {
                Log.e("hjt", json.getString("msg"));
            }
        } catch (JSONException e) {
            Log.e("hjt", "json");
            e.printStackTrace();
        }
        return null;
    }
}
