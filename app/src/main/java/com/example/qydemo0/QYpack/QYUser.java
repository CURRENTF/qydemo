package com.example.qydemo0.QYpack;

import android.util.Log;

public class QYUser {
    public static Boolean modify(String key, String type, String value){
        QYrequest htp = new QYrequest();
        String[] data = {key, type, value};
        String msg = htp.advancePut(GenerateJson.universeJson2(data), Constant.mInstance.userInfo_url,
                "Authorization", GlobalVariable.mInstance.token);
        Log.d("hjt.user.modify", msg);
        return MsgProcess.checkMsg(msg);
    }
}
