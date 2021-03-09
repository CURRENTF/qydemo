package com.example.qydemo0.QYpack;

import android.util.Log;

import com.example.qydemo0.QYpack.Constant;

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

    Constant C = new Constant();

    /**
     * 同步请求
     */
    public String post(String data, String urll){
        final String[] msg = {""};
        final int[] status = {0};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();//创建单例
                RequestBody body = RequestBody.create(JSON, data);
                Request request = new Request.Builder()//创建请求
                        .url(urll)
                        .post(body)
                        .build();
                try {
                    Response response = okHttpClient.newCall(request).execute();//执行请求
                    String mContent = response.body().string();//得到返回响应，注意response.body().string() 只能调用一次！
//                    Log.d("hjt", mContent);
                    msg[0] = mContent;
                    status[0] = 1;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("hjt", e.toString());
                }
            }
        });
        thread.start();
        while (status[0] == 0);
        return msg[0];
    }

    OkHttpClient client = new OkHttpClient();

    public String post2(String json, String url) throws IOException {
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
