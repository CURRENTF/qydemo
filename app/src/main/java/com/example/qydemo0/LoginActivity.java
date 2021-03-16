package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.QYrequest;

import java.io.IOException;

import okhttp3.*;


public class LoginActivity extends AppCompatActivity {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    Constant C = Constant.mInstance;

    private int loginWays[] = {R.id.fragment_username_login, R.id.fragment_phone_login, R.id.fragment_email_login};
    private int registerWays[] = {R.id.fragment_email_register, R.id.fragment_username_register, R.id.fragment_username_register2};

    public class RegisterToLogin implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            hideRegisterFragment();
            showFragment(C.default_login_way);
            Button btn = findViewById(R.id.button_login_to_register);
            btn.setOnClickListener(new LoginToRegister());
            btn.setText(R.string.register);
        }
    }

    public class LoginToRegister implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            hideLoginFragment();
            showFragment(C.default_register_way);
            Button btn = findViewById(R.id.button_login_to_register);
            btn.setOnClickListener(new RegisterToLogin());
            btn.setText(R.string.login);
        }
    }

    protected void hideLoginFragment(){
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < loginWays.length; i++){
            Fragment f = fm.findFragmentById(loginWays[i]);
            fm.beginTransaction().hide(f).commitNow();
        }
    }

    protected void hideRegisterFragment(){
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < registerWays.length; i++){
            Fragment f = fm.findFragmentById(registerWays[i]);
            fm.beginTransaction().hide(f).commitNow();
        }
    }

    protected void showFragment(int frag_id){
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(frag_id);
        fm.beginTransaction().show(f).commitNow();
    }


    public void showRegister2UsernameFragment(){
        hideRegisterFragment();
        showFragment(C.default_register2_way);
        EditText phone = findViewById(R.id.edit_text_phone_verify);
        EditText ph = findViewById(R.id.edit_text_register_phone);
        phone.setText(ph.getText());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        hideLoginFragment(); hideRegisterFragment();
        showFragment(C.default_login_way);

        Button btn = findViewById(R.id.button_login_to_register);
        btn.setOnClickListener(new LoginToRegister());

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }



    }


}