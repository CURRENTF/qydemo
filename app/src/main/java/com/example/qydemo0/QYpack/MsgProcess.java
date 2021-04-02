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

    static class ShowMsg extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            Looper.prepare();
            Toast.makeText(GlobalVariable.mInstance.appContext, strings[0], Toast.LENGTH_SHORT).show();
            Looper.loop();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


    public static JSONObject msgProcess(String msg, Boolean status){
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

                if(GlobalVariable.mInstance.appContext != null){
                    if(!status){
                        Looper.prepare();
                        Toast.makeText(GlobalVariable.mInstance.appContext, json.getString("msg"), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    else {
                        Toast.makeText(GlobalVariable.mInstance.appContext, json.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                }
                Log.e("hjtMsgProcess", json.getString("msg"));
            }
        } catch (JSONException e) {
            Log.e("hjt.MsgProcess", "jsonMsgProcessWrong");
            e.printStackTrace();
        }
        return null;
    }
    public static JSONArray msgProcessArr(String msg, Boolean status){
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

                if(GlobalVariable.mInstance.appContext != null){
                    if(!status){
                        Looper.prepare();
                        Toast.makeText(GlobalVariable.mInstance.appContext, json.getString("msg"), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    else {
                        Toast.makeText(GlobalVariable.mInstance.appContext, json.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                }
                Log.e("hjtMsgProcess", json.getString("msg"));
            }
        } catch (JSONException e) {
            Log.e("hjt.MsgProcess", "jsonMsgProcessWrong");
            e.printStackTrace();
        }
        return null;
    }


    public static boolean checkMsg(String msg, Boolean status){
        try {
            JSONObject json = new JSONObject(msg);
            if(json.getInt("status") == Constant.mInstance.HTTP_OK) return true;
            if(GlobalVariable.mInstance.appContext != null){
                if(!status){
                    Looper.prepare();
                    Toast.makeText(GlobalVariable.mInstance.appContext, json.getString("msg"), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                else {
                    Toast.makeText(GlobalVariable.mInstance.appContext, json.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


}