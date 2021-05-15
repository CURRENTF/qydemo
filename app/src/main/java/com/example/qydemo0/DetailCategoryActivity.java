package com.example.qydemo0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.qydemo0.QYAdapter.EndlessRecyclerOnScrollListener;
import com.example.qydemo0.QYAdapter.LinearLayoutAdapter;
import com.example.qydemo0.QYAdapter.LoadMoreAndRefreshWrapper;
import com.example.qydemo0.QYpack.AdvanceHttp;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.Widget.MyAppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailCategoryActivity extends MyAppCompatActivity {

    String name;
    RecyclerView main;
    List<JSONObject> dataList;
    int htp_pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_category);
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
//        Log.d("hjt.detailCate", name);
        main = findViewById(R.id.main);
        dataList = new ArrayList<>();
        LinearLayoutAdapter linearLayoutAdapter = new LinearLayoutAdapter(dataList, Constant.mInstance.WORK, this);
        LoadMoreAndRefreshWrapper loadMoreAndRefreshWrapper = new LoadMoreAndRefreshWrapper(linearLayoutAdapter);
        main.setAdapter(loadMoreAndRefreshWrapper);
        main.setLayoutManager(new LinearLayoutManager(this));
        Handler handler = new Handler(Looper.myLooper()){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg){
                JSONArray ja = (JSONArray) msg.obj;
                for(int i = 0; i < ja.length(); i++){
                    try {
                        JSONObject json = ja.getJSONObject(i);
                        linearLayoutAdapter.addData(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        main.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                AdvanceHttp.getCategoryWorks(handler, htp_pos, Constant.mInstance.MAX_UPDATE_LEN, name);
                htp_pos += Constant.mInstance.MAX_UPDATE_LEN;
            }
        });
        AdvanceHttp.getCategoryWorks(handler, htp_pos, Constant.mInstance.MAX_UPDATE_LEN, name);
        htp_pos += Constant.mInstance.MAX_UPDATE_LEN;
    }
}