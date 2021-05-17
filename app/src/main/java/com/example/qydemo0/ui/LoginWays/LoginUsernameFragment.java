package com.example.qydemo0.ui.LoginWays;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qydemo0.LoginActivity;
import com.example.qydemo0.MainActivity;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.MD5encrypt;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.R;
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginUsernameFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_username, container, false);
    }


    private class Login implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            TextView tv_username = getActivity().findViewById(R.id.edit_text_login_username);
            CharSequence username = tv_username.getText();
            TextView tv_password = getActivity().findViewById(R.id.edit_text_login_password);
            CharSequence password = tv_password.getText();

            Log.d("hjt.username", username.toString());
            Log.d("hjt.password", password.toString());

            GenerateJson g = new GenerateJson();
            PostLoginMsg po = new PostLoginMsg((MyAppCompatActivity) getActivity());
            Log.d("hjt.password.encrypt", MD5encrypt.encrypt(password.toString()));
            po.execute(g.loginJson(username.toString(), MD5encrypt.encrypt(password.toString()), 0), C.login_url);
        }
    }

    class PostLoginMsg extends MyAsyncTask<String, Integer, String> {

        protected PostLoginMsg(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d("hjt.show", "1");
            String data = strings[0], url = strings[1];
            QYrequest htp = new QYrequest();
            return htp.post(data, url);
        }

        @Override
        protected void onPostExecute(String s) {
            JSONObject json = MsgProcess.msgProcess(s, false, null);
            Log.d("hjtLoginReturnMsg", s);
            if(json != null){
                try {
                    GlobalVariable.mInstance.token = json.getString("token");
                    GetUserInfo getUserInfo = new GetUserInfo((MyAppCompatActivity) getActivity());
                    getUserInfo.execute(json);
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "登录失败.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(getActivity(), MsgProcess.getWrongMsg(s), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }

    class GetUserInfo extends MyAsyncTask<JSONObject, Integer, JSONObject>{

        protected GetUserInfo(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected JSONObject doInBackground(JSONObject... jsonObjects) {
            QYrequest htp = new QYrequest();
            JSONObject json = MsgProcess.msgProcess(htp.advanceGet(Constant.mInstance.userInfo_url, "Authorization", GlobalVariable.mInstance.token), false, null);
            if(json == null){
                return null;
            }
            else {
                try {
                    jsonObjects[0].put("uid", json.getString("uid"));
                    GlobalVariable.mInstance.uid = json.getString("uid");
                    Log.e("global.uid0", GlobalVariable.mInstance.uid);
                    return jsonObjects[0];
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if(json != null){
                Log.e("global.uid1", GlobalVariable.mInstance.uid);
                ((LoginActivity) getActivity()).savMsg(json);
                Log.e("global.uid2", GlobalVariable.mInstance.uid);
                Toast.makeText(getContext(), "登录成功", Toast.LENGTH_LONG).show();

                Intent intent = new Intent();
                intent.setClass(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Button btn = getActivity().findViewById(R.id.button_login_username);
        btn.setOnClickListener(new Login());
    }

    Constant C = Constant.mInstance;
}