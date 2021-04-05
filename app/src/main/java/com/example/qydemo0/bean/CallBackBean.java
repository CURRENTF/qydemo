package com.example.qydemo0.bean;

public class CallBackBean {
    private int Status;
    private String msg;

    public void setStatus(int status) {
        Status = status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return Status;
    }

    public String getMsg() {
        return msg;
    }
}
