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
    // 000000000011111111112222222
    // 012345678901234567890123456
    // 2021-04-08T05:00:20.105489
    public static String stringTime(String date){
//        return date;
        return date.substring(5, 7) + "月" + date.substring(8, 10) + "日" + date.substring(11, 19);
    }
}
