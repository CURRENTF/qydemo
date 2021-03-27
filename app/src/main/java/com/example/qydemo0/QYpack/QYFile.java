package com.example.qydemo0.QYpack;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class QYFile {
    public String hash(InputStream is, int size){
        byte[] bytes = new byte[size]; // 50MB
        int len = 0;
        try {
            len = is.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("hjt len", String.valueOf(len));
        byte[] b2 = new byte[len];
        for(int i = 0; i < len; i++) b2[i] = bytes[i];
        String hash = SHA256.hash(b2);
        return hash;
    }

    public JSONObject verifyFileUpload(String http_url, int file_type, String hash){
        QYrequest htp = new QYrequest();
        String msg = htp.advancePost(GenerateJson.universeJson("file_type", String.valueOf(file_type), "hash", hash),
                http_url, "Authorization", GlobalVariable.mInstance.token);
        JSONObject jsonObject = MsgProcess.msgProcess(msg);
        Log.d("hjtQYFile.verify", msg);
        return jsonObject;
    }

    public Boolean uploadFileWithUri(String http_url, Uri file_uri, String token, Context context){
        String fileUrl = Uri2RealPath.getRealPathFromUri_AboveApi19(context, file_uri);
        QYrequest htp = new QYrequest();
        String msg = htp.postWithFile(fileUrl, "no.use", http_url, token);
        JSONObject jsonObject = MsgProcess.msgProcess(msg);
        if(jsonObject == null) return false;
        else {
            try {
                if(jsonObject.getInt("code") == Constant.mInstance.HTTP_OK) return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public Boolean uploadFile(String http_url, String file_url, String token){
        QYrequest htp = new QYrequest();
        String msg = htp.postWithFile(file_url, "no.use", http_url, token);
        JSONObject jsonObject = MsgProcess.msgProcess(msg);
        Log.d("hjt.QYfile.upload", msg);
        if(jsonObject == null) return false;
        else {
            try {
                if(jsonObject.getInt("code") == Constant.mInstance.HTTP_OK) return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
