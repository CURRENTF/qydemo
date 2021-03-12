package com.example.qydemo0.RegisterWays;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.qydemo0.LoginActivity;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterUsernameFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_username, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Button btn = getActivity().findViewById(R.id.button_register_username);
        btn.setOnClickListener(new Register());
    }

    Constant C = Constant.mInstance;

    public void getRegisterTokenAndUid(JSONObject json){
        try {
            GlobalVariable.mInstance.registerToken = json.getString("token");
            GlobalVariable.mInstance.uid = json.getString("uid");
            GlobalVariable.mInstance.isRegisterTokenExisted = true;
            Log.d("hjt", GlobalVariable.mInstance.registerToken);
        } catch (JSONException e){
            Log.d("hjt", e.getMessage());
        }
    }

    public class Register implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            EditText et_username = getActivity().findViewById(R.id.edit_text_register_username);
            EditText et_password = getActivity().findViewById(R.id.edit_text_register_password);
            EditText et_phone = getActivity().findViewById(R.id.edit_text_register_phone);

            CharSequence username = et_username.getText();
            CharSequence password = et_password.getText();
            CharSequence phone = et_phone.getText();

            Log.d("hjt", username.toString());
            Log.d("hjt", password.toString());

            GenerateJson g = new GenerateJson();
            postRegisterMsg po = new postRegisterMsg();
            po.execute(g.registerPostJson(username.toString(), password.toString(), phone.toString()), C.register_url, "post");

        }
    }


    public void toRegister2(){
        ((LoginActivity) getActivity()).showRegister2UsernameFragment();
    }

    class postRegisterMsg extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = strings[0], url = strings[1], method = strings[2];
            QYrequest htp = new QYrequest();
            if(method.equals("post")) {
                return htp.post(data, url);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("hjt", s);
            JSONObject json = MsgProcess.msgProcess(s);
            if(json != null){
                getRegisterTokenAndUid(json);
                toRegister2();
            }
            else Log.e("hjt", "register1wrong");
            super.onPostExecute(s);
        }
    }

}