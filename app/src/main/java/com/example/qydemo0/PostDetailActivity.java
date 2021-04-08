package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.qydemo0.Widget.PostItem;

import org.json.JSONException;
import org.json.JSONObject;

public class PostDetailActivity extends AppCompatActivity {

    LinearLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Bundle bundle = getIntent().getExtras();
        String s = bundle.getString("json");
        main = findViewById(R.id.main);
        try {
            JSONObject json = new JSONObject(s);
            PostItem postItem = new PostItem(this);
            postItem.init(json, true, false, true);
            main.addView(postItem);
        } catch (JSONException e) {
            Log.e("hjt.json.post.detail.wrong", "onCreate");
            e.printStackTrace();
        }
    }


}