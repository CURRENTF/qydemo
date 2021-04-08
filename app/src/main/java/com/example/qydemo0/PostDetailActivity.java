package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.Widget.PostItem;
import com.example.qydemo0.Widget.QYScrollView;

import org.json.JSONException;
import org.json.JSONObject;

public class PostDetailActivity extends AppCompatActivity {

    LinearLayout main;
    PostItem postItem;
    int like_op = -1, status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Bundle bundle = getIntent().getExtras();
        String s = bundle.getString("json");
        main = findViewById(R.id.main);
        try {
            JSONObject json = new JSONObject(s);
            postItem = new PostItem(this);
            postItem.init(json, true, false, true);
            if(json.getBoolean("like")) {
                postItem.like_img.setImageResource(R.drawable.like_gray);
                like_op = 1;
            }
            else {
                postItem.like_img.setImageResource(R.drawable.ic_like);
                like_op = -1;
            }
            main.addView(postItem);
        } catch (JSONException e) {
            Log.e("hjt.json.post.detail.wrong", "onCreate");
            e.printStackTrace();
        }
        postItem.like_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like_op *= -1;
                if(status == 0){
                    OPPost opPost = new OPPost();
                    opPost.execute(like_op);
                }
            }
        });
    }


    class OPPost extends AsyncTask<Integer, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(Integer... strings) {
            status = 1;
            QYrequest htp = new QYrequest();
            try {
                Log.d("hjt.post.pid", postItem.json.getString("pid"));
                return MsgProcess.checkMsg(htp.advancePut("{}", Constant.mInstance.post_url + "func/" + postItem.json.getString("pid") + "/" + strings[0] + "/",  "Authorization", GlobalVariable.mInstance.token), false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                 if(like_op == 1) postItem.like_img.setImageResource(R.drawable.like_gray);
                 else postItem.like_img.setImageResource(R.drawable.ic_like);
            }
            else {
                like_op *= -1;
                Toast.makeText(PostDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
            }
            status = 0;
        }
    }

}