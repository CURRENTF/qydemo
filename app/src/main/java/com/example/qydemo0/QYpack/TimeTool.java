package com.example.qydemo0.QYpack;

public class TimeTool {
    public long lastTime = 0;
    public TimeTool(){
        lastTime = System.currentTimeMillis();
    }
    public Boolean checkFreq(){
        long nw = System.currentTimeMillis();
        if(nw - lastTime > Constant.mInstance.least_time){
            lastTime = nw;
            return true;
        }
        else  return  false;
    }

}
