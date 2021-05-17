package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.HttpCounter;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.Widget.ListItem.PostItem;
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;
import com.example.qydemo0.Widget.QYScrollView;
import com.example.qydemo0.Widget.ListItem.WorkItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserDetailActivity extends MyAppCompatActivity implements View.OnClickListener {

    LinearLayout posts, works;
    int uid;
    TextView left, right, username, sign;
    QYScrollView all;
    ImageView avatar;
    int work_cnt = 0, post_cnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        posts = findViewById(R.id.posts);
        works = findViewById(R.id.works);
        all = findViewById(R.id.main_main);
        avatar = findViewById(R.id.avatar);
        sign = findViewById(R.id.sign);
        username = findViewById(R.id.username);
        left = findViewById(R.id.button_post);
        right = findViewById(R.id.button_work);
        Bundle bundle = getIntent().getExtras();
        uid = bundle.getInt("uid");
        if(uid == 0) uid = Integer.parseInt(bundle.getString("uid"));
        username.setText(bundle.getString("username"));
        String sign_tmp = bundle.getString("sign");
        if(sign_tmp != null)
            sign.setText(sign_tmp);
        Img.roundImgUrl(this, avatar, bundle.getString("avatar"));
        Log.d("hjt.get.uid", String.valueOf(uid));
        GetPosts getPosts = new GetPosts(UserDetailActivity.this);
        getPosts.execute();
        GetWorks getWorks = new GetWorks(UserDetailActivity.this);
        getWorks.execute();
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        all.setScanScrollChangedListener(new QYScrollView.ISmartScrollChangedListener() {
            @Override
            public void onScrolledToBottom() {
                GetPosts getPosts = new GetPosts(UserDetailActivity.this);
                getPosts.execute();
                GetWorks getWorks = new GetWorks(UserDetailActivity.this);
                getWorks.execute();
            }

            @Override
            public void onScrolledToTop() {

            }
        });
    }

    int switcher = 0;

    @Override
    public void onClick(View view) {
        if(view == left){
            if(switcher == 0) return;
            switcher = 0;
            right.setTextColor(getColor(R.color.black));
            left.setTextColor(getColor(R.color.red));
            posts.setVisibility(View.VISIBLE);
            works.setVisibility(View.GONE);
            Animation animation = AnimationUtils.loadAnimation(this
                    , R.anim.ani_right_translate_alpha_500ms);
            Animation animation2 = AnimationUtils.loadAnimation(this
                    , R.anim.ani_right_translate_in_alpha_500ms);
            posts.startAnimation(animation2);
            works.startAnimation(animation);
        }
        else if(view == right) {
            if(switcher == 1) return;
            switcher = 1;
            left.setTextColor(getColor(R.color.black));
            right.setTextColor(getColor(R.color.red));
            posts.setVisibility(View.GONE);
            works.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(this
                    , R.anim.ani_left_translate_alpha_500ms);
            Animation animation2 = AnimationUtils.loadAnimation(this
                    , R.anim.ani_left_translate_in_alpha_500ms);
            posts.startAnimation(animation);
            works.startAnimation(animation2);
        }
    }

    int w_startPos = 0;

    HttpCounter counter = new HttpCounter();
    class GetWorks extends MyAsyncTask<Integer, Integer, JSONArray> {


        protected GetWorks(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected JSONArray doInBackground(Integer... integers) {
            QYrequest htp = new QYrequest();
            return MsgProcess.msgProcessArr(htp.advanceGet(
                    Constant.mInstance.userWork_url + uid + "/" + Json2X.Json2StringGet("start", String.valueOf(counter.start)
                            , "lens", String.valueOf(counter.len)), "Authorization", GlobalVariable.mInstance.token
            ), false, null);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.e("hjt.user.other.posts", "null");
                return;
            }
            counter.inc(jsonArray.length());
            work_cnt += jsonArray.length();
            for(int i = 0; i < jsonArray.length(); i++){
                WorkItem workItem = new WorkItem(UserDetailActivity.this);
                try {
                    JSONObject json = jsonArray.getJSONObject(i);
                    JSONObject cover = json.getJSONObject("cover");
                    workItem.init(cover.getString("url"), json.getString("name"),
                            json.getInt("like_num"), json.getInt("play_num"),
                            json.getString("introduction"), json.getJSONObject("belong").getString("username"), json.getInt("id"));
                    works.addView(workItem);
                    works.addView(Img.linearLayoutDivideLine(UserDetailActivity.this));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    int p_startPos = 0, len = Constant.mInstance.MAX_UPDATE_LEN;

    HttpCounter counter2 = new HttpCounter();
    class GetPosts extends MyAsyncTask<Integer, Integer, JSONArray>{

        protected GetPosts(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected JSONArray doInBackground(Integer... integers) {
            QYrequest htp = new QYrequest();
            return MsgProcess.msgProcessArr(htp.advanceGet(
                    Constant.mInstance.post_url + "1/" + Json2X.Json2StringGet("user_id", String.valueOf(uid),
                            "start", String.valueOf(counter2.start), "lens", String.valueOf(counter2.len)), "Authorization", GlobalVariable.mInstance.token
            ), false, null);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.e("hjt.user.other.posts", "null");
                return;
            }
            counter2.inc(jsonArray.length());
            post_cnt += jsonArray.length();
            for(int i = 0; i < jsonArray.length(); i++){
                PostItem postItem = new PostItem(UserDetailActivity.this);
                try {
                    JSONObject json = jsonArray.getJSONObject(i);
                    postItem.init(json, false, true, false);
                    posts.addView(postItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}