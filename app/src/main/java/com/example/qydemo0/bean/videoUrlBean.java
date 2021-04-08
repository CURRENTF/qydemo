package com.example.qydemo0.bean;

import org.json.JSONObject;

public class videoUrlBean {
    private String id;
    private JSONObject url;

    public String getId() {
        return id;
    }

    public JSONObject getUrl() {
        return url;
    }

    public void setUrl(JSONObject url) {
        this.url = url;
    }

    public void setId(String id) {
        this.id = id;
    }
}
