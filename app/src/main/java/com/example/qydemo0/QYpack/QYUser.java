package com.example.qydemo0.QYpack;

import android.os.AsyncTask;
import android.util.Log;

import com.example.qydemo0.Widget.QYScrollView;

import org.json.JSONObject;

public class QYUser {
    public static Boolean modify(String key, String type, String value){
        QYrequest htp = new QYrequest();
        String[] data = {key, type, value};
        String msg = htp.advancePut(GenerateJson.universeJson2(data), Constant.mInstance.userInfo_url,
                "Authorization", GlobalVariable.mInstance.token);
        Log.d("hjt.user.modify", msg);
        return MsgProcess.checkMsg(msg, false);
    }

    public static Boolean refreshInfo(){
        QYrequest qYrequest = new QYrequest();
        Log.d("hjt.qy.user.refresh", "1");
        String msg = qYrequest.advanceGet(Constant.mInstance.userInfo_url,
                "Authorization", GlobalVariable.mInstance.token);
        Log.d("hjt.qy.user.refresh", "2" + msg);
        JSONObject json =  MsgProcess.msgProcess(msg, false);
        if(json != null){
            Log.d("hjt.qy.user.refresh.info", "ok");
            GlobalVariable.mInstance.fragmentDataForMain.userInfoJson = json;
            return true;
        }
        else return false;
    }

    public static Boolean follow(int target_uid){
        QYrequest htp = new QYrequest();
        String[] data = {"target", "int", String.valueOf(target_uid)};
        return MsgProcess.checkMsg(htp.advancePost(GenerateJson.universeJson2(data), Constant.mInstance.follow_url, "Authorization", GlobalVariable.mInstance.token), false);
    }

}
