package com.example.qydemo0.Widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.qydemo0.FollowerAndFanActivity;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.ShowProgressDialog;
import com.example.qydemo0.R;
import com.example.qydemo0.UserDetailActivity;
import com.example.qydemo0.UserSettingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Dashboard extends RelativeLayout {

    private Activity context;
    private View mView;
    View work, post;

    private Activity getActivity(){
        return context;
    }

    public Dashboard(@NonNull Context context) {
        super(context);
        this.context = (Activity) context;
        init();
    }

    public Dashboard(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = (Activity) context;
        init();
    }

    void gotoUserDetail(){
        Intent intent = new Intent();
        intent.setClass(getActivity(), UserDetailActivity.class);
        try {
            intent.putExtra("uid", GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("uid"));
            intent.putExtra("username", GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("username"));
            intent.putExtra("avatar", GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("img_url"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getActivity().startActivity(intent);
    }

    void init(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.fragment_dashboard, this, true);
        View t = mView.findViewById(R.id.goto_fan_follow);
        work = mView.findViewById(R.id.work);
        post = mView.findViewById(R.id.post_linear);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), FollowerAndFanActivity.class);
                getActivity().startActivity(intent);
            }
        });
        work.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                gotoUserDetail();
            }
        });
        post.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                gotoUserDetail();
            }
        });
        if(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson == null){
            GetUserInfo g = new GetUserInfo();
            g.execute();
        }
        else reWriteInfo(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson);
        ImageView img = mView.findViewById(R.id.button_user_setting);
        img.setOnClickListener(new ModifyUserInfo());
        GetLastWork getLastWork = new GetLastWork();
        getLastWork.execute();
        GetLastPost getLastPost = new GetLastPost();
        getLastPost.execute();

        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                GetUserInfo getUserInfo =  new GetUserInfo();
                getUserInfo.execute();
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(runnable, 5000);
    }

    void reWriteInfo(JSONObject json){
        if(!getActivity().hasWindowFocus()) return;
        try {
            GlobalVariable.mInstance.uid = json.getString("uid");
        } catch (JSONException e) {
            Log.e("hjt.UID", "null");
            e.printStackTrace();
        }
        // 设置头像

        // 防止由于父亲销毁 RE
        if(getActivity() == null) return;

        ImageView userAvatar = mView.findViewById(R.id.user_avatar);
        TextView txt = mView.findViewById(R.id.text_username);

        if(getActivity() == null) return;
        String avatar_url, sign;
        Boolean a = false, b = false;
        try {
            avatar_url = json.getString("img_url");
            txt.setText(json.getString("username"));
            a = true;
        } catch (JSONException e) {
            avatar_url = Constant.mInstance.default_avatar;
        }

        if(avatar_url.equals("null")){
            avatar_url = Constant.mInstance.default_avatar;
            a = false;
        }


        if(getActivity() == null) return;

        try {
            sign = json.getString("sign");
            txt = mView.findViewById(R.id.text_user_sign);
            txt.setText(sign);
            b = true;
        } catch (JSONException e) {
            sign = "Born to Dance";
        }

        if(getActivity() == null) return;

        Img.roundImgUrl(getActivity(), userAvatar, avatar_url);

        try {
            if(!a) json.put("img_url", Constant.mInstance.default_avatar);
            if(!b) json.put("sign", "Born to Dance");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            TextView txt2 = mView.findViewById(R.id.text_fans);
            txt2.setText(String.valueOf(json.getInt("subscribe_num")));
            txt2 = mView.findViewById(R.id.text_followers);
            txt2.setText(String.valueOf(json.getInt("followers")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class GetLastWork extends AsyncTask<String, Integer, JSONArray>{

        TextView workName, like_num, play_num;
        ImageView img;
        GetLastWork(){
            workName = mView.findViewById(R.id.work_name);
            like_num = mView.findViewById(R.id.like_num_work);
            play_num = mView.findViewById(R.id.play_num_work);
            img = mView.findViewById(R.id.img1);
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return MsgProcess.msgProcessArr(
                    htp.advanceGet(Constant.mInstance.work_url + Json2X.Json2StringGet("start", "0", "lens", "1"),
                            "Authorization", GlobalVariable.mInstance.token),false
            );
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.e("hjt.last.work", "json_null");
                return;
            }
            try {
                JSONObject json = jsonArray.getJSONObject(0);
                workName.setText(json.getString("name"));
                like_num.setText(json.getString("like_num"));
                play_num.setText(json.getString("play_num"));
                Img.url2imgViewRoundRectangle(json.getJSONObject("cover").getString("url"), img, getActivity(), 10);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    class GetLastPost extends AsyncTask<String, Integer, JSONArray>{

        TextView postText, like_num, comment_num;
        ImageView img;
        GetLastPost(){
            postText = mView.findViewById(R.id.post_text);
            like_num = mView.findViewById(R.id.like_num_post);
            img = mView.findViewById(R.id.img2);
            comment_num = mView.findViewById(R.id.post_comment_num);
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return MsgProcess.msgProcessArr(
                    htp.advanceGet(Constant.mInstance.post_url + "1/" + Json2X.Json2StringGet("user_id", GlobalVariable.mInstance.uid, "start",
                            "0", "lens", "1"),
                            "Authorization", GlobalVariable.mInstance.token),false
            );
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.e("hjt.last.work", "json_null");
                return;
            }
            try {
                JSONObject json = jsonArray.getJSONObject(0);
                postText.setText(json.getString("text"));
                like_num.setText(json.getString("like_num"));
                comment_num.setText(json.getString("comment_num"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    class GetUserInfo extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return htp.advanceGet(Constant.mInstance.userInfo_url, "Authorization", GlobalVariable.mInstance.token);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("hjtGetUserInfo", s);
            JSONObject json = MsgProcess.msgProcess(s, true);
            if(json != null){
                GlobalVariable.mInstance.fragmentDataForMain.userInfoJson = json;
                reWriteInfo(json);
            }
        }
    }


    class ModifyUserInfo implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), UserSettingActivity.class);
            getActivity().startActivity(intent);
        }
    }
}
