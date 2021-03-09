package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;

public class MainActivity extends AppCompatActivity {

    Constant C = new Constant();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sp = getSharedPreferences(C.database, Context.MODE_PRIVATE);
        GlobalVariable.mInstance.readAllVar(sp);

        Log.d("hjt", "start" + GlobalVariable.mInstance.token);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onDestroy() {
        SharedPreferences sp = getSharedPreferences(C.database, Context.MODE_PRIVATE);
        GlobalVariable.mInstance.saveAllVar(sp);
        super.onDestroy();
    }
}