package com.example.qydemo0.bean;

import java.util.List;

/**
 * Created by moos on 2018/4/20.
 */

public class ReplyDetailBean {
    private int cid;
    private String text;
    private int like_num;
    private String created_time;
    private Boolean is_public;
    private Boolean is_delete;
    private Boolean like;
    private Belong belong;
    private Belong reply_to;

    public ReplyDetailBean(int cid, String text, int like_num, String created_time, Boolean is_public, Boolean is_delete, Boolean like, Belong belong, Belong reply_to) {
        this.cid = cid;
        this.text = text;
        this.like_num = like_num;
        this.created_time = created_time;
        this.is_public = is_public;
        this.is_delete = is_delete;
        this.like = like;
        this.belong = belong;
        this.reply_to = reply_to;
    }

    public Belong getBelong() {
        return belong;
    }

    public Boolean getIs_delete() {
        return is_delete;
    }

    public Boolean getIs_public() {
        return is_public;
    }

    public Boolean getLike() {
        return like;
    }

    public int getCid() {
        return cid;
    }

    public int getLike_num() {
        return like_num;
    }

    public String getCreated_time() {
        return created_time;
    }

    public Belong getReply_to() {
        return reply_to;
    }

    public String getText() {
        return text;
    }

    public void setBelong(Belong belong) {
        this.belong = belong;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public void setIs_delete(Boolean is_delete) {
        this.is_delete = is_delete;
    }

    public void setIs_public(Boolean is_public) {
        this.is_public = is_public;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }

    public void setLike_num(int like_num) {
        this.like_num = like_num;
    }

    public void setReply_to(Belong reply_to) {
        this.reply_to = reply_to;
    }

    public void setText(String text) {
        this.text = text;
    }

}
