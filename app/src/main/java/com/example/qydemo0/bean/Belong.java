package com.example.qydemo0.bean;

public class Belong {
    private int uid;
    private String username;
    private String img_url;

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Belong(int uid, String username, String img_url) {
        this.uid = uid;
        this.username = username;
        this.img_url = img_url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUid() {
        return uid;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getUsername() {
        return username;
    }

}
