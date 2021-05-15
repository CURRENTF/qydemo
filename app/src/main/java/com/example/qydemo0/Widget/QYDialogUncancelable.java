package com.example.qydemo0.Widget;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;

public class QYDialogUncancelable extends  QYDIalog{

    public QYDialogUncancelable(Context context, int layoutResID, int[] listenedItem) {
        super(context, layoutResID, listenedItem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //提前设置Dialog的一些样式
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);//设置dialog显示居中
        //dialogWindow.setWindowAnimations();设置动画效果
        setContentView(layoutResID);

        setCanceledOnTouchOutside(false);//点击外部Dialog消失
        //遍历控件id添加点击注册
        for(int id:listenedItem){
            findViewById(id).setOnClickListener(this);
        }
    }
}
