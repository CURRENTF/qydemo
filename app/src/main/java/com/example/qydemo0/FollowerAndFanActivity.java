package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.Widget.LittleUserItem;
import com.example.qydemo0.Widget.QYScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FollowerAndFanActivity extends AppCompatActivity implements View.OnClickListener {

    TextView follow, fan;
    QYScrollView left, right;
    LinearLayout list_left, list_right;
    int switcher = 0;
    // 0 left, 1 right

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_and_fan);
        follow = findViewById(R.id.btn_follow);
        fan = findViewById(R.id.btn_fan);
        left = findViewById(R.id.view_follows);
        right = findViewById(R.id.view_fans);
        list_left = findViewById(R.id.list_follow);
        list_right = findViewById(R.id.list_fans);
        follow.setOnClickListener(this);
        fan.setOnClickListener(this);
        GetUserFans getUserFans = new GetUserFans();
        getUserFans.execute();
        GetUserFollows getUserFollows = new GetUserFollows();
        getUserFollows.execute();
    }

    @Override
    public void onClick(View view) {
        if(view == follow){
            if(switcher == 0) return;
            switcher = 0;
            Animation animation = AnimationUtils.loadAnimation(this
                    , R.anim.ani_right_translate_alpha_500ms);
            Animation animation2 = AnimationUtils.loadAnimation(this
                    , R.anim.ani_right_translate_in_alpha_500ms);
            follow.setTextColor(getColor(R.color.red));
            fan.setTextColor(getColor(R.color.black));
            right.startAnimation(animation);
            left.startAnimation(animation2);
            right.setVisibility(View.GONE);
            left.setVisibility(View.VISIBLE);
        }
        else {
            if(switcher == 1) return;
            switcher = 1;
            Animation animation = AnimationUtils.loadAnimation(this
                    , R.anim.ani_left_translate_alpha_500ms);
            Animation animation2 = AnimationUtils.loadAnimation(this
                    , R.anim.ani_left_translate_in_alpha_500ms);
            follow.setTextColor(getColor(R.color.black));
            fan.setTextColor(getColor(R.color.red));
            left.startAnimation(animation);
            right.startAnimation(animation2);
            right.setVisibility(View.VISIBLE);
            left.setVisibility(View.GONE);
        }
    }

    void write(JSONArray jsonArray, ViewGroup viewGroup){
        if(jsonArray == null){
            Log.e("hjt.user.s", "json_null");
        }
        else {
            for(int i = 0; i < jsonArray.length(); i++){
                try {
                    JSONObject json = jsonArray.getJSONObject(i);
                    LittleUserItem littleUserItem = new LittleUserItem(this);
                    littleUserItem.init(json);
                    if(viewGroup == list_right) littleUserItem.hideBtn();
                    viewGroup.addView(littleUserItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    class GetUserFans extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return htp.advanceGet(Constant.mInstance.user_fans + Json2X.Json2StringGet("ftype", "1"), "Authorization", GlobalVariable.mInstance.token);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("hjt.fans", s);
            if(!MsgProcess.checkMsg(s, true)) return;
            write(MsgProcess.msgProcessArr(s, true), list_right);
            super.onPostExecute(s);
        }
    }
    class GetUserFollows extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return htp.advanceGet(Constant.mInstance.user_fans + Json2X.Json2StringGet("ftype", "0"), "Authorization", GlobalVariable.mInstance.token);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("hjt.followers", s);
            write(MsgProcess.msgProcessArr(s, true), list_left);
            super.onPostExecute(s);
        }
    }
}