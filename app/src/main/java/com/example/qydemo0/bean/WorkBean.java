package com.example.qydemo0.bean;

public class WorkBean {
    private String msg;
    private int id;
    private WorkDataBean data = new WorkDataBean();

    public int getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }

    public WorkDataBean getData() {
        return data;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(WorkDataBean data) {
        this.data = data;
    }

}
