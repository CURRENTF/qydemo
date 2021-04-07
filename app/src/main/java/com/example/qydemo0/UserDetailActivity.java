package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.Widget.Post;
import com.example.qydemo0.Widget.PostItem;
import com.example.qydemo0.Widget.QYScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserDetailActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout posts, works;
    int uid;
    TextView left, right;
    QYScrollView all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        posts = findViewById(R.id.posts);
        works = findViewById(R.id.works);
        all = findViewById(R.id.main_main);
        Bundle bundle = getIntent().getExtras();
        uid = bundle.getInt("uid");
        GetPosts getPosts = new GetPosts();
        getPosts.execute();
    }

    @Override
    public void onClick(View view) {

    }

    class GetPosts extends AsyncTask<Integer, Integer, JSONArray>{

        @Override
        protected JSONArray doInBackground(Integer... integers) {
            QYrequest htp = new QYrequest();
            return MsgProcess.msgProcessArr(htp.advanceGet(
                    Constant.mInstance.post_url + "1/" + Json2X.Json2StringGet("user_id", String.valueOf(uid)), "Authorization", GlobalVariable.mInstance.token
            ), false);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.e("hjt.user.other.posts", "null");
                return;
            }
            for(int i = 0; i < jsonArray.length(); i++){
                PostItem postItem = new PostItem(UserDetailActivity.this);
                try {
                    JSONObject json = jsonArray.getJSONObject(i);
                    postItem.init(json);
                    posts.addView(postItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}