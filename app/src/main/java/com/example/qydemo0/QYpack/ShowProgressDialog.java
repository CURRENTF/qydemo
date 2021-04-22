package com.example.qydemo0.QYpack;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ShowProgressDialog {
    public static ProgressDialog wait;

    public static void show(Context context, String msg) {
        wait = new ProgressDialog(context);
        //设置风格为圆形
        wait.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        wait.setTitle(null);
        wait.setIcon(null);
        //设置提示信息
        wait.setMessage(msg);
        wait.show();
    }

    public static void show(Context context, String msg, Thread thread) {
        final Thread th = thread;
        wait = new ProgressDialog(context);
        //设置风格为圆形
        wait.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        wait.setTitle(null);
        wait.setIcon(null);
        //设置提示信息
        wait.setMessage(msg);
        //设置是否可以通过返回键取消
        wait.setCancelable(false);
        wait.setIndeterminate(false);
        //设置取消监听
        wait.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                th.interrupt();
            }
        });
        wait.show();
    }
}
