package com.example.qydemo0.Widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.qydemo0.FollowerAndFanActivity;
import com.example.qydemo0.LearningListActivity;
import com.example.qydemo0.PlayerActivity;
import com.example.qydemo0.QYAdapter.ImageNetAdapter;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.ShowProgressDialog;
import com.example.qydemo0.QYpack.TimeTool;
import com.example.qydemo0.R;
import com.example.qydemo0.SearchActivity;
import com.example.qydemo0.UploadActivity;
import com.example.qydemo0.UploadPostActivity;
import com.example.qydemo0.UserSettingActivity;
import com.example.qydemo0.bean.DataBean;
import com.google.android.exoplayer2.text.tx3g.Tx3gDecoder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class Post extends RelativeLayout implements View.OnClickListener {

    private Activity context;
    private View mView;

    int[] buttons = {R.id.add_post, R.id.button_post_recommendation, R.id.button_post_follow};
    LinearLayout rc_layout, f_layout;
    QYScrollView rc_view, f_view;
    int rc_startPos = 0, rc_len = 20;
    int switcher = 0;
    // 0 rc 1 f
    TimeTool timeTool = new TimeTool();

    int cnt_rc = 0, cnt_f = 0;
    TextView placeholder1, placeholder2;

    private Activity getActivity(){
        return context;
    }

    public Post(@NonNull Context context) {
        super(context);
        this.context = (Activity) context;
        init();
    }

    public Post(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = (Activity) context;
        init();
    }


    void init() {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.fragment_posts, this, true);

        for(int i = 0; i < buttons.length; i++){
            View btn = mView.findViewById(buttons[i]);
            btn.setOnClickListener(this);
        }

        rc_layout = mView.findViewById(R.id.rela_layout_posts_recommendation);
        f_layout = mView.findViewById(R.id.rela_layout_posts_follow);
        rc_view = mView.findViewById(R.id.view_posts_recommendation);
        f_view = mView.findViewById(R.id.view_posts_follow);
        placeholder1 = new TextView(getActivity());
        placeholder2 = new TextView(getActivity());
        placeholder1.setText("空空如也");
        placeholder2.setText("空空如也");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.setMargins(0, 20, 0, 20);
        rc_layout.addView(placeholder1, layoutParams);
        f_layout.addView(placeholder2, layoutParams);
        rc_view.setScanScrollChangedListener(new QYScrollView.ISmartScrollChangedListener() {
            @Override
            public void onScrolledToBottom() {
                if(!timeTool.checkFreq()) return;
                GetRecommendationPost getRecommendationPost = new GetRecommendationPost();
                getRecommendationPost.execute();
            }
            @Override
            public void onScrolledToTop() {
                Log.d("hjt.scroll.top", "true");
            }
        });
        f_view.setScanScrollChangedListener(new QYScrollView.ISmartScrollChangedListener() {
            @Override
            public void onScrolledToBottom() {
                if(!timeTool.checkFreq()) return;
                GetFollowedPost getFollowedPost = new GetFollowedPost();
                getFollowedPost.execute();
            }
            @Override
            public void onScrolledToTop() {
                Log.d("hjt.scroll.top", "true");
            }
        });
        GetRecommendationPost getRecommendationPost = new GetRecommendationPost();
        getRecommendationPost.execute();
        GetFollowedPost getFollowedPost = new GetFollowedPost();
        getFollowedPost.execute();
    }

    @Override
    public void onClick(View v) {
        TextView t;
        switch (v.getId()){
            case R.id.add_post:
                Intent intent = new Intent();
                intent.setClass(getActivity(), UploadPostActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.button_post_recommendation:
                if(switcher == 0) break;
                switcher = 0;
                ((TextView)v).setTextColor(getActivity().getColor(R.color.red));
                t = getActivity().findViewById(R.id.button_post_follow);
                t.setTextColor(getActivity().getColor(R.color.black));
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_right_translate_alpha_500ms);
                f_view.startAnimation(animation);
                Animation a2 = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_right_translate_in_alpha_500ms);
                rc_view.startAnimation(a2);
                ChangeVisibility changeVisibility = new ChangeVisibility();
                changeVisibility.execute(true);
                break;
            case R.id.button_post_follow:
                if (switcher == 1) break;
                switcher = 1;
                ((TextView)v).setTextColor(getResources().getColor(R.color.red));
                t = getActivity().findViewById(R.id.button_post_recommendation);
                t.setTextColor(getResources().getColor(R.color.black));
                Animation animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_left_translate_alpha_500ms);
                rc_view.startAnimation(animation2);
                Animation a3 = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_left_translate_in_alpha_500ms);
                f_view.startAnimation(a3);
                ChangeVisibility changeVisibility2 = new ChangeVisibility();
                changeVisibility2.execute(false);
                break;
        }
    }

    int lastF_id = -1;
    class GetFollowedPost extends AsyncTask<String, Integer, JSONArray>{

        @Override
        protected JSONArray doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            Log.d("hjt.get.followed.post", "1");
            return MsgProcess.msgProcessArr(htp.advanceGet(Constant.mInstance.post_url + "0/", "Authorization", GlobalVariable.mInstance.token), false, null);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.d("hjt.get.follow.post", "null_json");
                return;
            }
            if(jsonArray.length() > 0 && cnt_f == 0){
                f_layout.removeView(placeholder2);
            }
            cnt_f += jsonArray.length();
            for(int i = 0; i < jsonArray.length(); i++){
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    PostItem postItem = new PostItem(getActivity());
                    postItem.init(jsonObject, true, true, false);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT);
//                    if(lastF_id != -1) layoutParams.addRule(RelativeLayout.BELOW, lastF_id);
//                    else layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    lastF_id = View.generateViewId();
                    postItem.setId(lastF_id);
//                    postItem.setLayoutParams(layoutParams);
                    f_layout.addView(postItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    int lastRc_id = -1;
    class GetRecommendationPost extends AsyncTask<String, Integer, JSONArray>{
        @Override
        protected JSONArray doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return MsgProcess.msgProcessArr(
                    htp.advanceGet(Constant.mInstance.post_recommendation_url + Json2X.Json2StringGet("start", String.valueOf(rc_startPos), "lens", String.valueOf(rc_len)),
                            "Authorization", GlobalVariable.mInstance.token), false, null
            );
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.d("hjt.get.recommendation.post", "null_json");
                return;
            }
            if(jsonArray.length() > 0 && cnt_rc == 0){
                rc_layout.removeView(placeholder1);
            }
            cnt_rc += jsonArray.length();
            for(int i = 0; i < jsonArray.length(); i++){
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    PostItem postItem = new PostItem(getActivity());
                    postItem.init(jsonObject, true, true, false);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT);
//                    if(lastRc_id != -1) layoutParams.addRule(RelativeLayout.BELOW, lastRc_id);
//                    else layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//                    lastRc_id = View.generateViewId();
                    postItem.setId(lastRc_id);
//                    postItem.setLayoutParams(layoutParams);
                    rc_layout.addView(postItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    class ChangeVisibility extends AsyncTask<Boolean, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            try {
                Thread.sleep(Constant.mInstance.ani_time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return booleans[0];
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                rc_view.setVisibility(View.VISIBLE);
                f_view.setVisibility(View.GONE);
            }
            else {
                rc_view.setVisibility(View.GONE);
                f_view.setVisibility(View.VISIBLE);
            }
        }
    }

}
