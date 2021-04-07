package com.example.qydemo0.ui.posts;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.ui.node.ViewAdapter;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.DeviceInfo;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.TimeTool;
import com.example.qydemo0.R;
import com.example.qydemo0.TestStyleActivity;
import com.example.qydemo0.UploadPostActivity;
import com.example.qydemo0.Widget.PostItem;
import com.example.qydemo0.Widget.QYScrollView;
import com.example.qydemo0.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PostsFragment extends Fragment implements View.OnClickListener {

    private View root = null;
    int[] buttons = {R.id.add_post, R.id.button_post_recommendation, R.id.button_post_follow};
    RelativeLayout rc_layout, f_layout;
    QYScrollView rc_view, f_view;
    int rc_startPos = 0, rc_len = 20;
    int switcher = 0;// 0 rc 1 f
    TimeTool timeTool = new TimeTool();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_posts, container, false);
        return root;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        for(int i = 0; i < buttons.length; i++){
            View btn = root.findViewById(buttons[i]);
            btn.setOnClickListener(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        rc_layout = root.findViewById(R.id.rela_layout_posts_recommendation);
        f_layout = root.findViewById(R.id.rela_layout_posts_follow);
        rc_view = root.findViewById(R.id.view_posts_recommendation);
        f_view = root.findViewById(R.id.view_posts_follow);
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
                startActivity(intent);
                break;
            case R.id.button_post_recommendation:
                if(switcher == 0) break;
                switcher = 0;
                ((TextView)v).setTextColor(getResources().getColor(R.color.red));
                t = getActivity().findViewById(R.id.button_post_follow);
                t.setTextColor(getResources().getColor(R.color.black));
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_right_translate_alpha_500ms);
                f_view.startAnimation(animation);
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
            return MsgProcess.msgProcessArr(htp.advanceGet(Constant.mInstance.post_url + "0/", "Authorization", GlobalVariable.mInstance.token), false);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.d("hjt.get.follow.post", "null_json");
                return;
            }
            for(int i = 0; i < jsonArray.length(); i++){
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    PostItem postItem = new PostItem(getActivity());
                    postItem.init(jsonObject);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT);
                    if(lastF_id != -1) layoutParams.addRule(RelativeLayout.BELOW, lastF_id);
                    else layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    lastF_id = View.generateViewId();
                    postItem.setId(lastF_id);
                    postItem.setLayoutParams(layoutParams);
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
                            "Authorization", GlobalVariable.mInstance.token), false
            );
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.d("hjt.get.recommendation.post", "null_json");
                return;
            }
            for(int i = 0; i < jsonArray.length(); i++){
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    PostItem postItem = new PostItem(getActivity());
                    postItem.init(jsonObject);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT);
                    if(lastRc_id != -1) layoutParams.addRule(RelativeLayout.BELOW, lastRc_id);
                    else layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    lastRc_id = View.generateViewId();
                    postItem.setId(lastRc_id);
                    postItem.setLayoutParams(layoutParams);
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