package com.example.qydemo0.QYpack;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
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
                Toast.makeText(GlobalVariable.mInstance.appContext, json.getString("msg"), Toast.LENGTH_SHORT).show();
                Log.e("hjtMsgProcess", json.getString("msg"));
            }
        } catch (JSONException e) {
            Log.e("hjt.MsgProcess", "jsonMsgProcessWrong");
            e.printStackTrace();
        }
        return null;
    }
    public static JSONArray msgProcessArr(String msg){
        Constant C = Constant.mInstance;
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
//                Toast.makeText(GlobalVariable.mInstance.appContext, json.getString("msg"), Toast.LENGTH_SHORT).show();
                Log.e("hjtMsgProcess", json.getString("msg"));
            }
        } catch (JSONException e) {
            Log.e("hjt.MsgProcess", "jsonMsgProcessWrong");
            e.printStackTrace();
        }
        return null;
    }


    public static boolean checkMsg(String msg){
        try {
            JSONObject json = new JSONObject(msg);
            if(json.getInt("status") == Constant.mInstance.HTTP_OK) return true;
//                Toast.makeText(GlobalVariable.mInstance.appContext, json.getString("msg"), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


}