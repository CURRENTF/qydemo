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

    // key, value
    public static String Json2StringGet(String... strings){
        int sz = strings.length;
        String s = "?";
        for(int i = 0; i < sz; i += 2){
            s += strings[i] + "=" + strings[i + 1];
            if(i != sz - 2) s += '&';
        }
        return s;
    }

}
