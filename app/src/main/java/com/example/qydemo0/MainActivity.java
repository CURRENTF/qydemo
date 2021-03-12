package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;

public class MainActivity extends AppCompatActivity {

    Constant C = Constant.mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sp = getSharedPreferences(C.database, Context.MODE_PRIVATE);
        GlobalVariable.mInstance.readAllVar(sp);

        Log.d("hjt", "start" + GlobalVariable.mInstance.token);

        if(!GlobalVariable.mInstance.tokenExisted){
            Intent it = new Intent();
            it.setComponent(new ComponentName(this, LoginActivity.class));
            startActivity(it);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sp = getSharedPreferences(C.database, Context.MODE_PRIVATE);
        GlobalVariable.mInstance.saveAllVar(sp);
        super.onDestroy();
    }
}