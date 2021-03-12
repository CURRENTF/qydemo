package com.example.qydemo0.QYpack;

import org.json.JSONObject;

import java.util.Map;

public class Json2X {

    public static String Json2StringForHttpGet(Map<String, String> m){
        String s = "?";
        int cnt = 0;
        for (Map.Entry<String, String> i : m.entrySet()) {
            cnt++;
            s += i.getKey() + "=" + i.getValue();
            if(cnt < m.size()) s += "&";
        }
        return s;
    }

}
