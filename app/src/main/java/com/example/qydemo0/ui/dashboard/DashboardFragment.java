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
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.R;
import com.example.qydemo0.UserSettingActivity;

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
        if(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson == null){
            GetUserInfo g = new GetUserInfo();
            g.execute();
//            GetUserFans p = new GetUserFans();
//            p.execute();
        }
        else reWriteInfo(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson);
        ImageView img = getActivity().findViewById(R.id.button_user_setting);
        img.setOnClickListener(new ModifyUserInfo());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    class GetUserFans extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return htp.advance2Get(GenerateJson.universeJson("ftype", "1"), Constant.mInstance.user_fans, "Authorization", GlobalVariable.mInstance.token);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("hjtfan", s);
            super.onPostExecute(s);
        }
    }


    void reWriteInfo(JSONObject json){
        GlobalVariable.mInstance.fragmentDataForMain.userInfoJson = json;
        try {
            GlobalVariable.mInstance.uid = json.getString("uid");
        } catch (JSONException e) {
            Log.e("hjtUID", "不存在??");
            e.printStackTrace();
        }
        // 设置头像
        if(getActivity() == null) return;
        ImageView userAvatar = getActivity().findViewById(R.id.user_avatar);
        try {
            json.put("img_url", "https://file.yhf2000.cn/img/defult4.jpeg");
            if(getActivity() == null) return;
            Glide.with(getActivity())
                    .load(json.getString("img_url"))
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(userAvatar);
            if(getActivity() == null) return;
            TextView txt = getActivity().findViewById(R.id.text_username);
            txt.setText(json.getString("username"));
            json.put("sign", "Born to Dance");
            txt = getActivity().findViewById(R.id.text_user_sign);
            txt.setText(json.getString("sign"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            JSONObject json = MsgProcess.msgProcess(s);
            if(json != null){
                reWriteInfo(json);
            }
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