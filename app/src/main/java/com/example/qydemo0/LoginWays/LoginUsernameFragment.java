package com.example.qydemo0.LoginWays;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.R;

import java.io.IOException;

import okhttp3.MediaType;

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
            Log.d("hjt", username.toString());
            Log.d("hjt", password.toString());
            GenerateJson g = new GenerateJson();
            QYrequest htp = new QYrequest();
            String msg = htp.post(g.loginJson(username.toString(), password.toString(), 0), C.login_url);
            Log.d("hjt", msg);
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