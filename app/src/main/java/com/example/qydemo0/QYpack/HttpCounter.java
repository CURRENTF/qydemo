package com.example.qydemo0.QYpack;

public class HttpCounter {
    public int start, len;

    public HttpCounter(){
        start = 0; len = Constant.mInstance.MAX_UPDATE_LEN;
    }

    public void inc(int x){
        start += x;
    }

    public void clear(){
        start = 0;
    }
}
