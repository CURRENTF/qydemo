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

    // [key, value, key, ...]
    public static String universeJson(String... strings){
        int sz = strings.length;
        Map<String, String> map = new HashMap<>();
        for(int i = 1; i < sz; i += 2){
            map.put(strings[i - 1], strings[i]);
        }
        return (new JSONObject(map)).toString();
    }

    //升级版本，需要标注类型
    // list = [string, int, bool, list]
    // [key, type_value, value, ...]
    // ["haoge_age", "int", "23"]
    // {"haoge_age":23}
    public static String universeJson2(String[] strings){
        int sz = strings.length;
        String res = "{";
        for(int i = 0; i < sz; i += 3){
            res += '"' + strings[i] + '"' + ':';
            if(strings[i + 1].equals("string"))
                res += '"' + strings[i + 2] + '"';
            else if(strings[i + 1].equals("int")) res += strings[i + 2];
            else res += strings[i + 2];
            if(i < sz - 3) res += ',';
        }
        res += '}';
        return res;
    }

    public static String listString(int startPos, String... strings){
        String s = "[";
        for(int i = startPos; i < strings.length; i++){
            s += '"';
            s += strings[i];
            s += '"';
            if(i != strings.length - 1) s += ',';
        }
        s += ']';
        return s;
    }

    public static String listStringWithSinglePoint(int startPos, String... strings){
        String s = "[";
        for(int i = startPos; i < strings.length; i++){
            s += '\'';
            s += strings[i];
            s += '\'';
            if(i != strings.length - 1) s += ',';
        }
        s += ']';
        return s;
    }
}
