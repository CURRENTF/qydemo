package com.example.qydemo0.ui.RegisterWays;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qydemo0.LoginActivity;
import com.example.qydemo0.MainActivity;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MD5encrypt;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.R;
import com.example.qydemo0.Widget.Dashboard;
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register2UsernameFragment extends Fragment {

    Constant C = Constant.mInstance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register2_username, container, false);
    }

    @Override
    public void onStart() {
        Button btn = getActivity().findViewById(R.id.button_send_register_code);
        btn.setOnClickListener(new btn());
        btn = getActivity().findViewById(R.id.button_finish_register);
        btn.setOnClickListener(new btn());
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    class btn implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            EditText et_phone = getActivity().findViewById(R.id.edit_text_register_phone);
            CharSequence ph = et_phone.getText();
            switch (v.getId()){
                case R.id.button_send_register_code:
                    SendMsgToPhone po = new SendMsgToPhone((MyAppCompatActivity) getActivity());
                    po.execute(GenerateJson.phoneOnlyJson(ph.toString()), C.verify_url);
                    break;
                case R.id.button_finish_register:
                    EditText code = getActivity().findViewById(R.id.edit_text_verify_code);
                    CharSequence code_content = code.getText();
                    VerifyCode vc = new VerifyCode((MyAppCompatActivity) getActivity());
                    vc.execute(ph.toString(), code_content.toString(), C.verify_url);
                    break;
            }
        }
    }


    Handler handler = new Handler();

    class SendMsgToPhone extends MyAsyncTask<String, Integer, String> {

        protected SendMsgToPhone(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(String... strings) {
            String data = strings[0], url = strings[1];
            QYrequest htp = new QYrequest();
            url = url + "0/0/";
            return htp.post(data, url);
        }

        @Override
        protected void onPostExecute(String s) {
            if(MsgProcess.checkMsg(s, true, "regis2")){
                Toast.makeText(getActivity(), "验证码已发送", Toast.LENGTH_LONG).show();
                Button btn = getActivity().findViewById(R.id.button_send_register_code);
                final int[] cnt = {0};
                int p = 120;
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        btn.setText("已发送(" + (p - cnt[0]) + "s)");
                        cnt[0]++;
                        if(p - cnt[0] > 1) handler.postDelayed(this, 1000);
                    }
                };
                handler.post(runnable);
            }
            else {
                Toast.makeText(getActivity(), MsgProcess.getWrongMsg(s), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }

    class VerifyCode extends MyAsyncTask<String, Integer, String> {

        protected VerifyCode(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(String... strings) {
            Map<String, String> map = new HashMap<>();
            map.put("info", strings[0]);
            map.put("code", strings[1]);
            QYrequest htp = new QYrequest();
            String url = strings[2] + "0/0/";
            url += Json2X.Json2StringForHttpGet(map);
            return htp.get(url);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("hjt.register.2.verify", s);
            if(MsgProcess.checkMsg(s, true, "re2")){
                PostLoginMsg postLoginMsg = new PostLoginMsg((MyAppCompatActivity) getActivity());
                postLoginMsg.execute();
            }
            super.onPostExecute(s);
        }
    }

    class PostLoginMsg extends MyAsyncTask<String, Integer, String> {

        protected PostLoginMsg(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            Log.d("hjt.password.encrypt", MD5encrypt.encrypt(((LoginActivity)getActivity()).password));
            return htp.post(GenerateJson.universeJson("username", ((LoginActivity)getActivity()).username,
                    "password", MD5encrypt.encrypt(((LoginActivity)getActivity()).password)), Constant.mInstance.login_url);
        }

        @Override
        protected void onPostExecute(String s) {
            JSONObject json = MsgProcess.msgProcess(s, true, "re2user");
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
}