package com.example.qydemo0.QYpack;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.qydemo0.DataTrans.FragmentDataForMain;
import com.example.qydemo0.MainActivity;
import com.example.qydemo0.R;

import java.lang.reflect.Field;
import java.util.Vector;


public class GlobalVariable {
    public static final GlobalVariable mInstance = new GlobalVariable();

    public boolean tokenExisted = false;
    public String token = "";
    public String uid = "";

    public boolean isRegisterTokenExisted = false;
    public String registerToken = "";

    public FragmentDataForMain fragmentDataForMain = null;
    public Context appContext = null;


    private GlobalVariable(){
        // should be empty?
    }

    Constant C = Constant.mInstance;

    public void readAllVar(SharedPreferences sp){
        token = sp.getString("token", "");
        uid = sp.getString("uid", "");
        tokenExisted = sp.getBoolean("tokenExisted", false);
        registerToken = sp.getString("registerToken", "");
        isRegisterTokenExisted = sp.getBoolean("isRegisterTokenExisted", false);
    }

    public void saveAllVar(SharedPreferences sp){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", token);
        editor.putString("uid", uid);
        editor.putBoolean("tokenExisted", tokenExisted);
        editor.putString("registerToken", registerToken);
        editor.putBoolean("isRegisterTokenExisted", isRegisterTokenExisted);
        editor.commit();
    }
}
