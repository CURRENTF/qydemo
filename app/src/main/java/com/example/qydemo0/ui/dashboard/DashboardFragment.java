package com.example.qydemo0.ui.dashboard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.R;
import com.example.qydemo0.UserSettingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DashboardFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
        if(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson == null){
            GetUserInfo g = new GetUserInfo();
            g.execute();
        }
        else reWriteInfo(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson);
        ImageView img = getActivity().findViewById(R.id.button_user_setting);
        img.setOnClickListener(new ModifyUserInfo());
    }


    void reWriteInfo(JSONObject json){
        GlobalVariable.mInstance.fragmentDataForMain.userInfoJson = json;
        try {
            GlobalVariable.mInstance.uid = json.getString("uid");
        } catch (JSONException e) {
            Log.e("hjt.UID", "null");
            e.printStackTrace();
        }
        // 设置头像

        // 防止由于父亲销毁 RE
        if(getActivity() == null) return;

        ImageView userAvatar = getActivity().findViewById(R.id.user_avatar);
        TextView txt = getActivity().findViewById(R.id.text_username);

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
            txt = getActivity().findViewById(R.id.text_user_sign);
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
            TextView txt2 = getActivity().findViewById(R.id.text_followers);
            txt2.setText(String.valueOf(json.getInt("subscribe_num")));
            txt2 = getActivity().findViewById(R.id.text_fans);
            txt2.setText(String.valueOf(json.getInt("followers")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void writeFans(JSONArray ja) {
        if(getActivity() == null) return;
        GlobalVariable.mInstance.fragmentDataForMain.userFans = ja;
        TextView txt = getActivity().findViewById(R.id.text_fans);
        txt.setText(String.valueOf(ja.length()));
    }
    void writeFollowers(JSONArray ja){
        if(getActivity() == null) return;
        GlobalVariable.mInstance.fragmentDataForMain.userFollowers = ja;
        TextView txt = getActivity().findViewById(R.id.text_followers);
        txt.setText(String.valueOf(ja.length()));
    }

    class GetUserInfo extends AsyncTask<String, Integer, String>{

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
                reWriteInfo(json);
            }
        }
    }
    class GetUserFans extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return htp.advanceGet(Constant.mInstance.user_fans + Json2X.Json2StringGet("ftype", "1"), "Authorization", GlobalVariable.mInstance.token);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("hjt.fans", s);
            if(!MsgProcess.checkMsg(s, true)) return;
            writeFans(MsgProcess.msgProcessArr(s, true));
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
            writeFollowers(MsgProcess.msgProcessArr(s, true));
            super.onPostExecute(s);
        }
    }


    class ModifyUserInfo implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), UserSettingActivity.class);
            startActivity(intent);
        }
    }
}