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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qydemo0.Manager.MyLinearLayoutManager;
import com.example.qydemo0.QYAdapter.EndlessRecyclerOnScrollListener;
import com.example.qydemo0.QYAdapter.LinearLayoutAdapter;
import com.example.qydemo0.QYAdapter.LoadMoreAndRefreshWrapper;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.HttpCounter;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.TimeTool;
import com.example.qydemo0.R;
import com.example.qydemo0.UploadPostActivity;
import com.example.qydemo0.Widget.ListItem.PostItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Post extends RelativeLayout implements View.OnClickListener {

    private Activity context;
    public View mView;

    int[] buttons = {R.id.add_post, R.id.button_post_recommendation, R.id.button_post_follow};
    LinearLayout rc_layout, f_layout;
    int rc_startPos = 0, rc_len = Constant.mInstance.MAX_UPDATE_LEN;
    int switcher = 0;
    // 0 rc 1 f
    TimeTool timeTool = new TimeTool();

    int cnt_rc = 0, cnt_f = 0;

    LinearLayoutAdapter adapter_rec, adapter_follow;
    LoadMoreAndRefreshWrapper wrapper_rec, wrapper_follow;

    private MyAppCompatActivity getActivity(){
        return (MyAppCompatActivity) context;
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

    public void refresh(){
        adapter_follow.clearData();
        adapter_rec.clearData();
        counter.clear();
        GetFollowedPost a = new GetFollowedPost((MyAppCompatActivity) getActivity());
        GetRecommendationPost b = new GetRecommendationPost((MyAppCompatActivity) getActivity());
        a.execute();
        b.execute();
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
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.setMargins(0, 20, 0, 20);

        // ?????? RecyclerView ?????????
        RecyclerView rec = mView.findViewById(R.id.recy_rec);
        adapter_rec = new LinearLayoutAdapter(new ArrayList<>(), Constant.mInstance.POST, getActivity());
        adapter_rec.setHasStableIds(true);
        wrapper_rec = new LoadMoreAndRefreshWrapper(adapter_rec);

        rec.setLayoutManager(new LinearLayoutManager(getActivity()));
        rec.setNestedScrollingEnabled(false);
        rec.setItemAnimator(new DefaultItemAnimator());
        rec.setAdapter(wrapper_rec);
        rec.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                wrapper_rec.setLoadState(wrapper_rec.LOADING);
                GetRecommendationPost getRecommendationPost = new GetRecommendationPost(getActivity());
                getRecommendationPost.execute();
            }
        });

        RecyclerView follow = mView.findViewById(R.id.recy_like);
        adapter_follow = new LinearLayoutAdapter(new ArrayList<>(), Constant.mInstance.POST, getActivity());
        adapter_follow.setHasStableIds(true);
        wrapper_follow = new LoadMoreAndRefreshWrapper(adapter_follow);
        follow.setLayoutManager(new LinearLayoutManager(getActivity()));
        follow.setNestedScrollingEnabled(false);
        follow.setItemAnimator(new DefaultItemAnimator());
        follow.setAdapter(wrapper_follow);
        follow.addOnScrollListener(new EndlessRecyclerOnScrollListener() {

            @Override
            public void onLoadMore() {
                wrapper_follow.setLoadState(wrapper_follow.LOADING);
                GetFollowedPost getFollowedPost = new GetFollowedPost(getActivity());
                getFollowedPost.execute();
            }
        });

        GetRecommendationPost getRecommendationPost = new GetRecommendationPost(getActivity());
        getRecommendationPost.execute();
        GetFollowedPost getFollowedPost = new GetFollowedPost(getActivity());
        getFollowedPost.execute();
        counter_follow = new HttpCounter();
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
                f_layout.startAnimation(animation);
                Animation a2 = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_right_translate_in_alpha_500ms);
                rc_layout.startAnimation(a2);
                ChangeVisibility changeVisibility = new ChangeVisibility(getActivity());
                changeVisibility.execute(true);
                break;
            case R.id.button_post_follow:
                if (switcher == 1) break;
                switcher = 1;
                ((TextView)v).setTextColor(getResources().getColor(R.color.red));
                t = getActivity().findViewById(R.id.button_post_recommendation);
                t.setTextColor(getResources().getColor(R.color.black));
                Animation animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_left_translate_alpha_500ms);
                rc_layout.startAnimation(animation2);
                Animation a3 = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_left_translate_in_alpha_500ms);
                f_layout.startAnimation(a3);
                ChangeVisibility changeVisibility2 = new ChangeVisibility(getActivity());
                changeVisibility2.execute(false);
                break;
        }
    }

    int lastF_id = -1;
    HttpCounter counter_follow;
    class GetFollowedPost extends MyAsyncTask<String, Integer, JSONArray>{


        protected GetFollowedPost(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            Log.d("hjt.get.followed.post", "1");
            return MsgProcess.msgProcessArr(htp.advanceGet(Constant.mInstance.post_url + "0/"
                    + Json2X.Json2StringGet("start", String.valueOf(counter_follow.start), "lens", String.valueOf(counter_follow.len)),
                    "Authorization", GlobalVariable.mInstance.token), true, "followed.post");
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.d("hjt.get.follow.post", "null_json");
                return;
            }
            if(jsonArray.length() == 0){
                wrapper_follow.setLoadState(wrapper_rec.LOADING_END);
            }
            else {
                wrapper_follow.setLoadState(wrapper_rec.LOADING_COMPLETE);
            }
            counter_follow.inc(jsonArray.length());
            wrapper_follow.setLoadState(wrapper_rec.LOADING_END);
            cnt_f += jsonArray.length();
            for(int i = 0; i < jsonArray.length(); i++){
                // ?????????
//                try {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    PostItem postItem = new PostItem(getActivity());
//                    postItem.init(jsonObject, true, true, false);
////                    RelativeLayoutItem.LayoutParams layoutParams = new RelativeLayoutItem.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT);
////                    if(lastF_id != -1) layoutParams.addRule(RelativeLayoutItem.BELOW, lastF_id);
////                    else layoutParams.addRule(RelativeLayoutItem.ALIGN_PARENT_TOP);
//                    lastF_id = View.generateViewId();
//                    postItem.setId(lastF_id);
////                    postItem.setLayoutParams(layoutParams);
//                    f_layout.addView(postItem);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    return;
//                }

                // now
                try {
                    JSONObject json = jsonArray.getJSONObject(i);
                    json.put("a", "true");
                    json.put("b", "true");
                    json.put("is_detail", "false");
                    adapter_follow.addData(json);
                    wrapper_follow.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    int lastRc_id = -1;
    HttpCounter counter = new HttpCounter();
    class GetRecommendationPost extends MyAsyncTask<String, Integer, JSONArray>{

        protected GetRecommendationPost(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return MsgProcess.msgProcessArr(
                    htp.advanceGet(Constant.mInstance.post_recommendation_url + Json2X.Json2StringGet("start", String.valueOf(counter.start), "lens", String.valueOf(counter.len)),
                            "Authorization", GlobalVariable.mInstance.token), false, null
            );
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.d("hjt.get.recommendation.post", "null_json");
                return;
            }
            counter.inc(jsonArray.length());
            if(jsonArray.length() == 0){
                wrapper_rec.setLoadState(wrapper_rec.LOADING_END);
            }
            else {
                wrapper_rec.setLoadState(wrapper_rec.LOADING_COMPLETE);
            }
            cnt_rc += jsonArray.length();
            for(int i = 0; i < jsonArray.length(); i++){
                try {
                    // ?????????
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    PostItem postItem = new PostItem(getActivity());
//                    postItem.init(jsonObject, true, true, false);
////                    RelativeLayoutItem.LayoutParams layoutParams = new RelativeLayoutItem.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT);
////                    if(lastRc_id != -1) layoutParams.addRule(RelativeLayoutItem.BELOW, lastRc_id);
////                    else layoutParams.addRule(RelativeLayoutItem.ALIGN_PARENT_TOP);
////                    lastRc_id = View.generateViewId();
//                    postItem.setId(lastRc_id);
////                    postItem.setLayoutParams(layoutParams);
//                    rc_layout.addView(postItem);

                    // now
                    JSONObject json = jsonArray.getJSONObject(i);
                    json.put("a", "true");
                    json.put("b", "true");
                    adapter_rec.addData(json);

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    class ChangeVisibility extends MyAsyncTask<Boolean, Integer, Boolean>{

        protected ChangeVisibility(MyAppCompatActivity activity) {
            super(activity);
        }

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
                rc_layout.setVisibility(View.VISIBLE);
                f_layout.setVisibility(View.GONE);
            }
            else {
                rc_layout.setVisibility(View.GONE);
                f_layout.setVisibility(View.VISIBLE);
            }
        }
    }

}
