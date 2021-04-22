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
            PostLoginMsg po = new PostLoginMsg();
            Log.d("hjt.password.encrypt", MD5encrypt.encrypt(password.toString()));
            po.execute(g.loginJson(username.toString(), MD5encrypt.encrypt(password.toString()), 0), C.login_url);
        }
    }

    class PostLoginMsg extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d("hjt.show", "1");
            String data = strings[0], url = strings[1];
            QYrequest htp = new QYrequest();
            return htp.post(data, url);
        }

        @Override
        protected void onPostExecute(String s) {
            JSONObject json = MsgProcess.msgProcess(s, true);
            Log.d("hjtLoginReturnMsg", s);
            if(json != null){
                ((LoginActivity) getActivity()).savMsg(json);
                Toast.makeText(getContext(), "登录成功", Toast.LENGTH_LONG).show();

                Intent intent = new Intent();
                intent.setClass(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else {
                Toast.makeText(getActivity(), MsgProcess.getWrongMsg(s), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
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