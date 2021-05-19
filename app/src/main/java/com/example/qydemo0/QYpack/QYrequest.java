package com.example.qydemo0.QYpack;

import android.os.AsyncTask;
import android.support.v4.media.session.IMediaControllerCallback;
import android.util.Log;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QYrequest {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    Constant C = Constant.mInstance;

    public String post(String data, String urll){
        OkHttpClient okHttpClient = new OkHttpClient();//创建单例
        RequestBody body = RequestBody.create(JSON, data);
        Request request = new Request.Builder()//创建请求
                .url(urll)
                .post(body)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();//执行请求

            if(response == null) return "null";
            String mContent = response.body().string();//得到返回响应，注意response.body().string() 只能调用一次！
            return mContent;
        } catch (IOException e) {
            Log.e("hjt", e.toString());
            return "";
        }
    }

    // GenerateJson.universe2Json("id","int",id,..)
    // advancePost(data, url, "Authorization", Glob.m.token)
    public String advancePost(String data, String... strings){
        int sz = strings.length;
        String url = strings[0];
        OkHttpClient okHttpClient = new OkHttpClient();//创建单例
        RequestBody requestBody = RequestBody.create(JSON, data);
        Request.Builder tmp = new Request.Builder()//创建请求
                .url(url)
                .post(requestBody);
        for(int i = 1; i < sz; i += 2){
            tmp.addHeader(strings[i], strings[i + 1]);
        }
        Request request = tmp.build();
        try {
            Response response = okHttpClient.newCall(request).execute();//执行请求

            if(response == null) return "null";
            String mContent = response.body().string();//得到返回响应，注意response.body().string() 只能调用一次！
            return mContent;
        } catch (IOException e) {
            Log.e("hjt", e.toString());
            return "";
        }
    }

    public String postWithFile(String filePath, String fileName, String url, String token){
        url += '?' + "token=" + token;
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(MediaType.parse("multipart/form-data"), new File(filePath)))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response == null) return "null";
        try {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("hjtPostWithFileWrong", "??");
            return "";
        }
    }


    public String get(String url){
        OkHttpClient okHttpClient = new OkHttpClient();//创建单例
        Request request = new Request.Builder()//创建请求
                .url(url)
                .get()
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();//执行请求
            if(response == null) return "null";
            String mContent = response.body().string();//得到返回响应，注意response.body().string() 只能调用一次！
            return mContent;
        } catch (IOException e) {
            Log.e("hjt", e.toString());
            return "";
        }
    }

    public String advanceGet(String... strings){
        int sz = strings.length;
        String url = strings[0];
        OkHttpClient okHttpClient = new OkHttpClient();//创建单例
        Request.Builder tmp = new Request.Builder()//创建请求
                .url(url)
                .get();
        for(int i = 1; i < sz; i += 2){
            tmp.addHeader(strings[i], strings[i + 1]);
        }
        Request request = tmp.build();
        try {
            Response response = okHttpClient.newCall(request).execute();//执行请求
            if(response == null) return "null";
            String mContent = response.body().string();//得到返回响应，注意response.body().string() 只能调用一次！
            return mContent;
        } catch (IOException e) {
            Log.e("hjt", e.toString());
            return "";
        }
    }

    public String put(String data, String url){
        OkHttpClient okHttpClient = new OkHttpClient();//创建单例
        RequestBody body = RequestBody.create(JSON, data);
        Request request = new Request.Builder()//创建请求
                .url(url)
                .put(body)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();//执行请求
            if(response == null) return "null";
            String mContent = response.body().string();//得到返回响应，注意response.body().string() 只能调用一次！
            return mContent;
        } catch (IOException e) {
//            e.printStackTrace();
            Log.e("hjt", e.toString());
            return "";
        }
    }

    public String advancePut(String data, String url, String... strings){
        int sz = strings.length;
        OkHttpClient okHttpClient = new OkHttpClient();//创建单例
        RequestBody body = RequestBody.create(JSON, data);
        Request.Builder tmp = new Request.Builder()//创建请求
                .url(url)
                .put(body);
        for(int i = 0; i < sz; i += 2){
            tmp.addHeader(strings[i], strings[i + 1]);
        }
        Request request = tmp.build();
        try {
            Response response = okHttpClient.newCall(request).execute();//执行请求
            if(response == null) return "null";
            String mContent = response.body().string();//得到返回响应，注意response.body().string() 只能调用一次！
            return mContent;
        } catch (IOException e) {
            Log.e("hjt", e.toString());
            return "";
        }
    }

    // get 时 data = null
    public String advanceMethod(String method, String data, String url, String... headers){
        int sz = headers.length;
        OkHttpClient okHttpClient = new OkHttpClient();//创建单例
        RequestBody body = null;
        Request.Builder tmp = null;
        if(!method.equals("GET")) body = RequestBody.create(JSON, data);
        if(method.equals("GET")){
            tmp = new Request.Builder()//创建请求
                    .url(url)
                    .get();
        }
        else {
            tmp = new Request.Builder()//创建请求
                    .url(url)
                    .method(method, body);
        }
        for(int i = 0; i < sz; i += 2){
            tmp.addHeader(headers[i], headers[i + 1]);
        }
        Request request = tmp.build();
        try {
            Response response = okHttpClient.newCall(request).execute();//执行请求

            if(response == null) return "null";
            String mContent = response.body().string();//得到返回响应，注意response.body().string() 只能调用一次！
            return mContent;
        } catch (IOException e) {
            Log.e("hjt", e.toString());
            return "";
        }
    }
}
