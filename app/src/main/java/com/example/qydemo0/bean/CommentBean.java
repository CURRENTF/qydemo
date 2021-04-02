package com.example.qydemo0.bean;

import java.util.List;

/**
 * Created by moos on 2018/4/20.
 */

public class CommentBean {
    private int status;
    private String msg;
    private List<CommentDetailBean> data;

    public int getStatus() {
        return status;
    }

    public List<CommentDetailBean> getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public void setData(List<CommentDetailBean> data) {
        this.data = data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
