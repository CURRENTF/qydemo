package com.example.qydemo0.Widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;

import com.example.qydemo0.QYAdapter.GridViewAdapter;
import com.example.qydemo0.QYpack.AdvanceHttp;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.R;
import com.example.qydemo0.entry.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Category extends RelativeLayout {

    private Activity ac;
    View mView;
    GridView gridView;

    public Category(Context context) {
        super(context);
        ac = (Activity) context;
        INFLATE();
        init();
    }

    public Category(Context context, AttributeSet attrs) {
        super(context, attrs);
        ac = (Activity) context;
        INFLATE();
        init();
    }

    void INFLATE(){
        LayoutInflater inflater = (LayoutInflater)ac.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.fragment_category, this, true);
    }

    List<Map<String, Object>> data_list;

    public void init(){
        gridView = mView.findViewById(R.id.grid_view);
        data_list = new ArrayList<Map<String, Object>>();
        GridViewAdapter gridViewAdapter = new GridViewAdapter(ac, R.layout.category_item, data_list);
        gridView.setAdapter(gridViewAdapter);
        Handler handler = new Handler(Looper.myLooper()){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg){
                JSONArray ja = (JSONArray) msg.obj;
                for(int i = 0; i < ja.length(); i++){
                    Map<String, Object> map = new HashMap<String, Object>();
                    try {
                        JSONObject json = ja.getJSONObject(i);
                        map.put("image", json.getString("img_url"));
                        map.put("text", json.get("name"));
                        gridViewAdapter.addData(map);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        AdvanceHttp.getCategories(handler);
    }

}
