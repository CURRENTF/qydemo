package com.example.qydemo0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.RelativeLayout;

import com.example.qydemo0.Widget.GameCheckPointItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameChallengeModeActivity extends AppCompatActivity {

    int[] cs_id = {R.id.imageView2, R.id.imageView, R.id.imageView3, R.id.imageView4, R.id.imageView5};
    int[] pic_id = {R.drawable.ic_a, R.drawable.ic_b, R.drawable.ic_c, R.drawable.ic_d, R.drawable.ic_e};
    RelativeLayout[] containers;
    GameCheckPointItem[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_challenge_mode);
        containers = new RelativeLayout[5];
        items = new GameCheckPointItem[5];
        for(int i = 0; i < 5; i++){
            containers[i] = findViewById(cs_id[i]);
            items[i] = new GameCheckPointItem(this, pic_id[i]);
            containers[i].addView(items[i]);
        }
        Handler handler = new Handler(Looper.myLooper()){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg){
                JSONArray ja = (JSONArray) msg.obj;
                for(int i = 0; i < ja.length(); i++){
                    try {
                        JSONObject json = ja.getJSONObject(i);
                        items[i].setStar(json.getInt("stars"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

}