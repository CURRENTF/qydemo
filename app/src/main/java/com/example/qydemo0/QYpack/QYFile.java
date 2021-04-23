package com.example.qydemo0.QYpack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.renderscript.ScriptGroup;
import android.util.Log;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class QYFile {
    public String hashFile(Uri uri, Activity ac){
        try {
            InputStream inputStream = ac.getContentResolver().openInputStream(uri);
            String h = hash(inputStream, Constant.mInstance.MAX_FILE_SIZE);
            return h;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String hashFileUrl(String file_url){
        File file = new File(file_url);
        try {
            InputStream inputStream = (InputStream) (new FileInputStream(file));
            String h = hash(inputStream, Constant.mInstance.MAX_FILE_SIZE);
            return h;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

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
        JSONObject jsonObject = MsgProcess.msgProcess(msg, false, null);
        Log.d("hjtQYFile.verify", msg);
        return jsonObject;
    }

    public Boolean uploadFileWithUri(String http_url, Uri file_uri, String token, Context context){
        String fileUrl = Uri2RealPath.getRealPathFromUri_AboveApi19(context, file_uri);
        QYrequest htp = new QYrequest();
        String msg = htp.postWithFile(fileUrl, "no.use", http_url, token);
        JSONObject jsonObject = MsgProcess.msgProcess(msg, false, null);
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
        Log.d("hjt.QYfile.upload", msg);
        return MsgProcess.checkMsg(msg, false, null);
    }

    // 返回 file_id
    public String uploadFileAllIn(String verify_url, String file_url, int file_type, String hash) {
        JSONObject json = verifyFileUpload(verify_url, file_type, hash);
        Log.d("hjt.uploadFileAllIn.json", json.toString());
        try {
            if(json.getBoolean("rapid_upload") || uploadFile(json.getString("upload_url"), file_url, json.getString("token")))
                return json.getString("file_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class ResultContract extends ActivityResultContract<Boolean, Uri> {

        public String params = "image";

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Boolean input) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(params + "/*");
            return intent;
        }

        @Override
        public Uri parseResult(int resultCode, @Nullable Intent intent) {
            if (intent != null) {
                return intent.getData();
            }
            else {
                Log.e("hjt.GetVideo.Null", "null");
                return null;
            }
        }
    }


}
