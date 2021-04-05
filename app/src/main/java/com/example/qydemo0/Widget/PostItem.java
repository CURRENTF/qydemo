package com.example.qydemo0.Widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qydemo0.DetailPostActivity;
import com.example.qydemo0.PlayerActivity;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.R;
import com.google.android.exoplayer2.ui.PlayerView;
import com.koushikdutta.async.http.body.JSONArrayBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PostItem extends LinearLayout {

    private Context mContext = null;
    private View mView = null;
    ImageView avatar = null, post_avatar = null, fun_img = null, cover = null;
    GridLayout img_set = null;
    LinearLayout work = null, post_post = null;
    TextView username = null, post_username = null, post_time = null, post_content = null, work_name = null;



    public PostItem(Context context) {
        super(context);
        mContext = context;
        initDf();
    }

    private void initDf(){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.post_item, this, true);
    }


    String work_json = null, post_json = null, img_json;

    class GotoWork implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass((Activity)mContext, PlayerActivity.class);
            intent.putExtra("work", work_json);
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

    // mode:
    // 0 只有文字
    // 1 文字+作品
    // 2 文字+动态
    // 3 文字+图片

    public void init(JSONObject json){
        int mode = 0;
        if(json.has("post")) mode = 2;
        else if(json.has("work")) mode = 1;
        else if(json.has("img_set")) mode = 3;
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.post_item, this, true);
        img_set = mView.findViewById(R.id.post_img_layout);
        work = mView.findViewById(R.id.post_work);
        post_post = mView.findViewById(R.id.post_post);
        avatar = mView.findViewById(R.id.post_user_avatar);
        fun_img = mView.findViewById(R.id.fun_img);
        username = mView.findViewById(R.id.post_user_name);
        post_time = mView.findViewById(R.id.post_time);
        post_content = mView.findViewById(R.id.post_content);
        try {
            TextView txt = mView.findViewById(R.id.like_num);
            txt.setText(json.getInt("like_num"));
            txt = mView.findViewById(R.id.post_comment_num);
            txt.setText(json.getString("comment_num"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject user = json.getJSONObject("belong");
            Img.roundImgUrl((Activity)mContext, avatar, user.getString("img_url"));
            username.setText(user.getString("username"));
            post_time.setText(json.getString("created_time"));
            post_content.setText(json.getString("text"));
        } catch (JSONException e) {
            Log.d("hjt.post.item", "no belong");
            e.printStackTrace();
            return;
        }
        if(mode == 0){
            work.setVisibility(GONE);
//            post_post.setVisibility(GONE);
//            img_set.setVisibility(GONE);
        }
        else if(mode == 1){
            cover = mView.findViewById(R.id.post_work_cover);
            work_name = mView.findViewById(R.id.post_work_name);
            try {
                JSONObject work = json.getJSONObject("work");
                work_json = work.toString();
                Img.url2imgViewRoundRectangle(work.getString("cover_url"), cover, mContext, 40);
                work_name.setText(work.getString("name"));
                this.work.setOnClickListener(new GotoWork());
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
        else if(mode == 2){
            work.setVisibility(GONE);
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
                if(post.getString("cover_url").equals("null")){
                    cover.setVisibility(GONE);
                    forward_name.setVisibility(GONE);
                    return;
                }
                Img.url2imgViewRoundRectangle(post.getString("cover_url"), cover, mContext, 40);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            work.setVisibility(GONE);
            img_set.setVisibility(VISIBLE);
            try {
                JSONArray ja = json.getJSONArray("img_set");
                if(ja.length() == 1){
                    LinearLayout.LayoutParams layoutParams =
                            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
                    ImageView img = new ImageView(mContext);
                    img.setLayoutParams(layoutParams);
                    JSONObject jsonObj = (JSONObject) ja.get(0);
                    Img.url2imgViewRoundRectangle(jsonObj.getString("download_url"), img, mContext, 40);
                    LinearLayout l = mView.findViewById(R.id.post_main);
                    l.addView(img);
                }
                else {
                    for(int i = 0; i < ja.length(); i++){
                        ImageView img = new ImageView(mContext);
                        JSONObject j = (JSONObject) ja.get(i);
                        Img.url2imgViewRoundRectangle(j.getString("download_url"), img, mContext, 40);
                        img_set.addView(img);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
