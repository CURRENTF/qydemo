package com.example.qydemo0.QYpack;

import android.os.AsyncTask;
import android.support.v4.media.session.IMediaControllerCallback;
import android.util.Log;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
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
            String mContent = response.body().string();//得到返回响应，注意response.body().string() 只能调用一次！
            return mContent;
        } catch (IOException e) {
//            e.printStackTrace();
            Log.e("hjt", e.toString());
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
            String mContent = response.body().string();//得到返回响应，注意response.body().string() 只能调用一次！
            return mContent;
        } catch (IOException e) {
            Log.e("hjt", e.toString());
            return "";
        }
    }

    // 不知道怎么用
    public String post2(String json, String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

}
