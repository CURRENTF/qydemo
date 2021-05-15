package com.example.qydemo0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.RelativeLayout;

import com.example.qydemo0.QYpack.AdvanceHttp;
import com.example.qydemo0.Widget.GameCheckPointItem;
import com.example.qydemo0.Widget.MyAppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameChallengeModeActivity extends MyAppCompatActivity {

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
            items[i] = new GameCheckPointItem(this, pic_id[i], i);
            containers[i].addView(items[i]);
        }
        Handler handler = new Handler(Looper.myLooper()){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg){
                JSONArray ja = (JSONArray) msg.obj;
                boolean first = true;
                for(int i = 0; i < ja.length(); i++){
                    try {
                        JSONObject json = ja.getJSONObject(i);
                        int star_num = json.getInt("star_num");
                        double star = json.getDouble("star");
                        if(star_num == 0 && first){
                            first = false;
                        }
                        else if(star_num == 0){
                            items[i].lockSelf();
                        }
                        else {
                            items[i].setStar(star_num / star);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                for(int j = ja.length(); j < 5; j++) items[j].lockSelf();
            }
        };
        AdvanceHttp.getGameChallengeStars(handler);
    }

}