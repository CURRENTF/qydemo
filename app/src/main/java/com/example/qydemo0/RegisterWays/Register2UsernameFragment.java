package com.example.qydemo0.RegisterWays;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.R;

import org.json.JSONObject;

public class Register2UsernameFragment extends Fragment {

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
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    class SendMsgToPhone extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = strings[0], url = strings[1], method = strings[2];
            url = url + "0/0/";
            QYrequest htp = new QYrequest();
            if(method.equals("post")){
                return htp.post(data, url);
            }
            else if(method.equals("get")) {

            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("hjtregister2", s);
            JSONObject json = MsgProcess.msgProcess(s);
            if(json != null){

            }
            super.onPostExecute(s);
        }
    }
}