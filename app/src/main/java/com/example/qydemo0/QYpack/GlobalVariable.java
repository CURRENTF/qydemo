package com.example.qydemo0.QYpack;

public class GlobalVariable {
    public static final GlobalVariable mInstance = new GlobalVariable();

    public boolean tokenExisted = false;
    public String token = "";
    public String uid = "";

    public boolean isRegisterTokenExisted = false;
    public String RegisterToken = null;

    private GlobalVariable(){

    }
}
