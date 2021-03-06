package com.example.qydemo0.Widget.ListItem;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qydemo0.PlayerActivity;
import com.example.qydemo0.PostDetailActivity;
import com.example.qydemo0.QYpack.DeviceInfo;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.QYUser;
import com.example.qydemo0.QYpack.TimeTool;
import com.example.qydemo0.R;
import com.example.qydemo0.UserDetailActivity;
import com.example.qydemo0.ViewImageActivity;
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PostItem extends LinearLayoutItem {

    private Context mContext = null;
    private Activity activity;
    public View mView = null;
    public ImageView avatar = null, post_avatar = null, like_img;
    ImageView work_cover, img_cover1, img_cover, single_img;
    GridLayout img_set = null;
    LinearLayout work = null, post_post = null;
    TextView username = null, post_username = null, post_time = null, post_content = null, work_name = null;
    TextView post_comment_num;
    Boolean filled = false;
    LinearLayout post_main;

    public PostItem(Context context) {
        super(context);
        mContext = context;
        activity = (Activity) context;
        initDf();
    }

    public void findViews(){
        img_set = mView.findViewById(R.id.post_img_layout);
        work = mView.findViewById(R.id.post_work);
        post_post = mView.findViewById(R.id.post_post);
        avatar = mView.findViewById(R.id.post_user_avatar);
        username = mView.findViewById(R.id.post_user_name);
        post_time = mView.findViewById(R.id.post_time);
        post_content = mView.findViewById(R.id.post_content);
        btn_follow = mView.findViewById(R.id.btn_follow);
        like_img = mView.findViewById(R.id.like_img);
        like_num = mView.findViewById(R.id.like_num);
        work_cover = mView.findViewById(R.id.post_work_cover);
        post_comment_num = mView.findViewById(R.id.post_comment_num);
        work_name = mView.findViewById(R.id.post_work_name);
        post_main = mView.findViewById(R.id.post_main);
        single_img = mView.findViewById(R.id.post_single_image);
    }

    public PostItem(ViewGroup parent, Activity activity){
        super(activity);
        this.mContext = activity;
        this.activity = activity;
        mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent,false);
        findViews();
    }

    @Override
    public void fill(JSONObject json) {
//        if(filled) return;
        try{
            boolean a = json.getBoolean("a"), b = json.getBoolean("b");
            if(json.has("is_detail")){
                init(json, a, b, json.getBoolean("is_detail"));
            }
            else init(json, a, b, true);
        }catch (JSONException e){
            init(json, true, true, true);
//            e.printStackTrace();
        }
    }

    private void initDf(){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.post_item, this, true);
        findViews();
    }


    public String post_json = null, img_json;
    int work_id = 0;
    public TextView btn_follow;

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
//            intent.setClass((Activity)mContext, DetailPostActivity.class);
            intent.putExtra("post", post_json);
        }
    }


    public TextView like_num;
    public void incLikes(){
        CharSequence t = like_num.getText();
        int k = Integer.parseInt(t.toString());
        like_num.setText(String.valueOf(k + 1));
    }
    public void decLikes(){
        CharSequence t = like_num.getText();
        int k = Integer.parseInt(t.toString());
        like_num.setText(String.valueOf(k - 1));
    }

    // mode:
    // 0 ????????????
    // 1 ??????+??????
    // 2 ??????+??????
    // 3 ??????+??????
    int mode = 0;
    public JSONObject json;

    public void init(JSONObject json){
//        Log.d("hjt.post", json.toString());
        this.json = json;
        mode = 0;
        try {
            if(!json.getString("post").equals("null")) mode = 2;
            else if(!json.getString("work").equals("null")) mode = 1;
            else if(!json.getString("img_set").equals("null")) mode = 3;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        filled = true;
        try {
            like_num.setText(String.valueOf(json.getInt("like_num")));
            TextView txt = post_comment_num;
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

        single_img.setVisibility(GONE);
        work.setVisibility(GONE);

        if(mode == 0){
//            work.setVisibility(GONE);
//            post_post.setVisibility(GONE);
            img_set.setVisibility(GONE);
        }
        else if(mode == 1){
            img_set.setVisibility(GONE);
            work.setVisibility(VISIBLE);
            try {
                JSONObject work = json.getJSONObject("work");
                work_id = work.getInt("id");
                JSONObject coverInfo = work.getJSONObject("cover");
                Img.url2imgViewRoundRectangle(coverInfo.getString("url"), work_cover, mContext, 40);
                work_name.setText(work.getString("name"));
                this.work.setOnClickListener(new GotoWork());
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
        else if(mode == 2){
//            img_set.setVisibility(GONE);
//            post_post.setVisibility(VISIBLE);
//            post_post.setOnClickListener(new GotoPostDetail());
//            cover = mView.findViewById(R.id.post_forward_video_cover);
//            post_username = mView.findViewById(R.id.post_forward_username);
//            try {
//                JSONObject post = json.getJSONObject("post");
//                post_json = post.toString();
//                JSONObject post_user = post.getJSONObject("belong");
//                post_username.setText(post_user.getString("name"));
//                TextView forward_text = mView.findViewById(R.id.post_forward_text);
//                forward_text.setText(post.getString("text"));
//                TextView forward_name = mView.findViewById(R.id.post_forward_name);
//                JSONObject coverInfo = post.getJSONObject("cover");
//                if(coverInfo.getString("url").equals("null")){
//                    cover.setVisibility(GONE);
//                    forward_name.setVisibility(GONE);
//                    return;
//                }
//                JSONObject coverInfo2 = post.getJSONObject("cover");
//                Img.url2imgViewRoundRectangle(coverInfo2.getString("url"), cover, mContext, 40);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
        else {
            try {
                JSONArray ja = json.getJSONArray("img_set");
                if(ja.length() == 1){
//                    LinearLayout.LayoutParams layoutParams =
//                            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceInfo.dip2px(mContext,200));
//                    ImageView img = new ImageView(mContext);
//                    img.setLayoutParams(layoutParams);
                    JSONObject jsonObj = (JSONObject) ja.get(0);
                    Img.url2imgViewRoundRectangle(jsonObj.getString("url"), single_img, mContext, 20);
//                    LinearLayout l = post_main;
//                    l.addView(img);
                    Img.setOnClickForView(single_img, (AppCompatActivity) mContext, ViewImageActivity.class, jsonObj.getString("url"));
                    single_img.setVisibility(VISIBLE);
                    img_set.removeAllViews();
                }
                else {
//                    if(mView == null) return;
//                    boolean flag = false;
//                    if(img_set == null){
//                        img_set = mView.findViewById(R.id.post_img_layout);
//                        if(img_set == null){
//                            Log.d("hjtsb", "666");
////                            flag = true;
////                            img_set = new GridLayout(mContext);
////                            ((LinearLayout)mView.findViewById(R.id.post_main)).addView(img_set);
////                            return;
//                        }
//                        if(mView == null){
//                            Log.d("hjtsb", "667");
//                        }
//                    }
                    img_set.setVisibility(VISIBLE);
                    img_set.removeAllViews();
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

    LinearLayout operation;

    public void init(JSONObject json, boolean a, boolean b, boolean is_detail){
        init(json);
        if(a) setIntentToDetail();
        if(b) setIntentToPostDetail();
        if(!is_detail){
//            operation = mView.findViewById(R.id.operation);
//            operation.setVisibility(GONE);
        }
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
                    intent.putExtra("sign", belong.getString("sign"));
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