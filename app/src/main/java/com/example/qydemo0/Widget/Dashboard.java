package com.example.qydemo0.Widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.qydemo0.FollowerAndFanActivity;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.ShowProgressDialog;
import com.example.qydemo0.R;
import com.example.qydemo0.UserSettingActivity;
import com.example.qydemo0.ui.dashboard.DashboardFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class Dashboard extends RelativeLayout {

    private Activity context;
    private View mView;

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

    void init(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.fragment_dashboard, this, true);
        View t = mView.findViewById(R.id.goto_fan_follow);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), FollowerAndFanActivity.class);
                getActivity().startActivity(intent);
            }
        });
        if(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson == null){
            GetUserInfo g = new GetUserInfo();
            g.execute();
        }
        else reWriteInfo(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson);
        ImageView img = mView.findViewById(R.id.button_user_setting);
        img.setOnClickListener(new ModifyUserInfo());
    }

    void reWriteInfo(JSONObject json){
        ShowProgressDialog.show(getActivity(), "加载用户信息");
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
        ShowProgressDialog.wait.dismiss();
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
