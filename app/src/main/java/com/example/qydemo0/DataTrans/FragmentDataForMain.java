package com.example.qydemo0.DataTrans;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class FragmentDataForMain {
    public Vector<String> imgURLForHome = new Vector<String>();
//    public Map<String, String> userInfo = new HashMap<>();
    public JSONObject userInfoJson = null;
    public JSONArray userFans = null;
    public JSONArray userFollowers = null;
    public String parseGender(int x){
        if(x == 1) return "男";
        else if(x == 2) return "女";
        else return "未知";
    }
}
