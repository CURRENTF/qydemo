package com.example.qydemo0.QYpack;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class GenerateJson {

    Constant C = Constant.mInstance;

    // mark is login pattern
    public String loginJson(String username, String password, int mark){
        Map<String, String> map = new HashMap<>();
        if(mark == 0) map.put(C.username, username);
        else if(mark == 1) map.put(C.email, username);
        else map.put(C.phone, username);
        map.put(C.password, MD5encrypt.encrypt(password));
        JSONObject json = new JSONObject(map);
        return json.toString();
    }

    public String registerPostJson(String username, String password, String phone){
        Map<String, String> map = new HashMap<String, String>();
        map.put(C.username, username);
        map.put(C.password, MD5encrypt.encrypt(password));
        map.put(C.phone, phone);
        JSONObject json = new JSONObject(map);
        return json.toString();
    }

    public static String phoneOnlyJson(String phone){
        String s = "{ \"info\":" + "\"" + phone + "\"" +  " }";
        return s;
    }



}
