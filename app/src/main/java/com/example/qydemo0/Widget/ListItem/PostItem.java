package com.example.qydemo0.Widget.ListItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qydemo0.DetailPostActivity;
import com.example.qydemo0.PlayerActivity;
import com.example.qydemo0.PostDetailActivity;
import com.example.qydemo0.QYpack.DeviceInfo;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYUser;
import com.example.qydemo0.QYpack.TimeTool;
import com.example.qydemo0.R;
import com.example.qydemo0.UserDetailActivity;
import com.example.qydemo0.ViewImageActivity;
import com.google.android.exoplayer2.ui.PlayerView;
import com.koushikdutta.async.http.body.JSONArrayBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PostItem extends LinearLayoutItem {

    private Context mContext = null;
    private Activity activity;
    public View mView = null;
    public ImageView avatar = null, post_avatar = null, cover = null, like_img;
    GridLayout img_set = null;
    LinearLayout work = null, post_post = null;
    TextView username = null, post_username = null, post_time = null, post_content = null, work_name = null;
    Boolean filled = false;

    public PostItem(Context context) {
        super(context);
        mContext = context;
        initDf();
    }

    public PostItem(ViewGroup parent, Activity activity){
        super(activity);
        this.mContext = activity;
        this.activity = activity;
        mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent,false);
    }

    @Override
    public void fill(JSONObject json) {
        if(filled) return;
        try{
            boolean a = json.getBoolean("a"), b = json.getBoolean("b");
            init(json, a, b, true);
            filled = true;
        }catch (JSONException e){
            init(json, true, true, true);
//            e.printStackTrace();
        }
    }

    private void initDf(){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.post_item, this, true);
    }


    public String post_json = null, img_json;
    int work_id = 0;
    TextView btn_follow;

    class GotoWork implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass((Activity)mContext, PlayerActivity.class);
            intent.putExtra("id", work_id);
            ((Activity)mContext).startActivity(intent);
        }
    }
    class GotoPostDetail implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass((Activity)mContext, DetailPostActivity.class);
            intent.putExtra("post", post_json);
        }
    }
    public class Follow extends AsyncTask<Integer, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... integers) {
            return QYUser.follow(integers[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                btn_follow.setText("已关注");
            }
            else {
                Toast.makeText(mContext, "关注失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // mode:
    // 0 只有文字
    // 1 文字+作品
    // 2 文字+动态
    // 3 文字+图片
    int mode = 0;
    public JSONObject json;

    public void init(JSONObject json){
        if(filled) return;
        filled = true;
        this.json = json;
        mode = 0;
        try {
            if(!json.getString("post").equals("null")) mode = 2;
            else if(!json.getString("work").equals("null")) mode = 1;
            else if(!json.getString("img_set").equals("null")) mode = 3;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        img_set = mView.findViewById(R.id.post_img_layout);
        work = mView.findViewById(R.id.post_work);
        post_post = mView.findViewById(R.id.post_post);
        avatar = mView.findViewById(R.id.post_user_avatar);
        username = mView.findViewById(R.id.post_user_name);
        post_time = mView.findViewById(R.id.post_time);
        post_content = mView.findViewById(R.id.post_content);
        btn_follow = mView.findViewById(R.id.btn_follow);
        like_img = mView.findViewById(R.id.like_img);
        try {
            if(json.getString("follow").equals("true")){
                btn_follow.setText("已关注");
            }
            else if(json.getString("follow").equals("false")) {
                btn_follow.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        Follow follow = new Follow();
                        try {
                            follow.execute(json.getJSONObject("belong").getInt("uid"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            else {
                btn_follow.setText("");
            }
            TextView txt = mView.findViewById(R.id.like_num);
            txt.setText(String.valueOf(json.getInt("like_num")));
            txt = mView.findViewById(R.id.post_comment_num);
            txt.setText(json.getString("comment_num"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject user = json.getJSONObject("belong");
            Img.roundImgUrl((Activity)mContext, avatar, user.getString("img_url"));
            username.setText(user.getString("username"));
            post_time.setText(TimeTool.stringTime(json.getString("created_time")));
            if(json.getString("text").equals("")){
                post_content.setVisibility(GONE);
            }
            else
                post_content.setText(json.getString("text"));
        } catch (JSONException e) {
            Log.d("hjt.post.item", "no belong");
            e.printStackTrace();
            return;
        }
        if(mode == 0){
//            work.setVisibility(GONE);
//            post_post.setVisibility(GONE);
            img_set.setVisibility(GONE);
        }
        else if(mode == 1){
            img_set.setVisibility(GONE);
            cover = mView.findViewById(R.id.post_work_cover);
            work_name = mView.findViewById(R.id.post_work_name);
            work.setVisibility(VISIBLE);
            try {
                JSONObject work = json.getJSONObject("work");
                work_id = work.getInt("id");
                JSONObject coverInfo = work.getJSONObject("cover");
                Img.url2imgViewRoundRectangle(coverInfo.getString("url"), cover, mContext, 40);
                work_name.setText(work.getString("name"));
                this.work.setOnClickListener(new GotoWork());
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
        else if(mode == 2){
            img_set.setVisibility(GONE);
            post_post.setVisibility(VISIBLE);
            post_post.setOnClickListener(new GotoPostDetail());
            cover = mView.findViewById(R.id.post_forward_video_cover);
            post_username = mView.findViewById(R.id.post_forward_username);
            try {
                JSONObject post = json.getJSONObject("post");
                post_json = post.toString();
                JSONObject post_user = post.getJSONObject("belong");
                post_username.setText(post_user.getString("name"));
                TextView forward_text = mView.findViewById(R.id.post_forward_text);
                forward_text.setText(post.getString("text"));
                TextView forward_name = mView.findViewById(R.id.post_forward_name);
                JSONObject coverInfo = post.getJSONObject("cover");
                if(coverInfo.getString("url").equals("null")){
                    cover.setVisibility(GONE);
                    forward_name.setVisibility(GONE);
                    return;
                }
                JSONObject coverInfo2 = post.getJSONObject("cover");
                Img.url2imgViewRoundRectangle(coverInfo2.getString("url"), cover, mContext, 40);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                JSONArray ja = json.getJSONArray("img_set");
                if(ja.length() == 1){
                    LinearLayout.LayoutParams layoutParams =
                            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceInfo.dip2px(mContext,200));
                    ImageView img = new ImageView(mContext);

                    img.setLayoutParams(layoutParams);
                    JSONObject jsonObj = (JSONObject) ja.get(0);
                    Img.url2imgViewRoundRectangle(jsonObj.getString("url"), img, mContext, 20);
                    LinearLayout l = mView.findViewById(R.id.post_main);
                    l.addView(img);
                    Img.setOnClickForView(img, (AppCompatActivity) mContext, ViewImageActivity.class, jsonObj.getString("url"));
                }
                else {
                    img_set.setVisibility(VISIBLE);
                    for(int i = 0; i < ja.length(); i++){
                        ImageView img = new ImageView(mContext);
                        JSONObject j = (JSONObject) ja.get(i);
                        Img.url2imgViewRoundRectangle(j.getString("url"), img, mContext, 20);
                        LinearLayout.LayoutParams layoutParams =
                                new LinearLayout.LayoutParams(DeviceInfo.dip2px(mContext, 98), DeviceInfo.dip2px(mContext, 98));
                        layoutParams.setMargins(DeviceInfo.dip2px(mContext, 5),DeviceInfo.dip2px(mContext, 5),
                                DeviceInfo.dip2px(mContext, 5),DeviceInfo.dip2px(mContext, 5));
                        img.setLayoutParams(layoutParams);
                        Img.setOnClickForView(img, (AppCompatActivity)mContext, ViewImageActivity.class, j.getString("url"));
                        img_set.addView(img);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void init(JSONObject json, boolean a, boolean b, boolean is_detail){
        init(json);
        if(a) setIntentToDetail();
        if(b) setIntentToPostDetail();
    }

    public int getQYHeight(){
        if(mode == 0) return 70 + 70 + 40;
        else if(mode == 1) return 70 + 230 + 40;
        else if(mode == 2) return 70 + 40 + 14 + 200 + 30 + 10 + 40;
        else return 70 + 300;
    }

    public void setIntentToDetail(){
        if(json == null) return;
        avatar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass((Activity)mContext, UserDetailActivity.class);
                try {
                    JSONObject belong = json.getJSONObject("belong");
                    intent.putExtra("uid", belong.getInt("uid"));
                    intent.putExtra("username", belong.getString("username"));
                    intent.putExtra("avatar", belong.getString("img_url"));
                    ((Activity)mContext).startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void setIntentToPostDetail(){
        if(json == null) return;
        mView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass((Activity)mContext, PostDetailActivity.class);
                intent.putExtra("json", json.toString());
                ((Activity)mContext).startActivity(intent);
            }
        });
    }
}