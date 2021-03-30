package com.example.qydemo0.QYpack;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private int MIN_DISTANCE = 50;
    private Context context = null;
    public GestureListener(Context context){
        this.context = context;
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1.getX()-e2.getX()>MIN_DISTANCE){
            Toast.makeText(context,"左滑",Toast.LENGTH_SHORT).show();
        }else if(e2.getX()-e1.getX()>MIN_DISTANCE){
            Toast.makeText(context,"右滑",Toast.LENGTH_SHORT).show();
        }else if(e1.getY()-e2.getY()>MIN_DISTANCE){
            Toast.makeText(context,"上滑",Toast.LENGTH_SHORT).show();
        }else if(e2.getY()-e1.getY()>MIN_DISTANCE){
            Toast.makeText(context,"下滑",Toast.LENGTH_SHORT).show();
            Log.d("hjt", "OK");
        }
        return true;
    }

}
