package com.example.qydemo0;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qydemo0.Widget.QYDIalog;

import java.util.ArrayList;
import java.util.List;

public class CompareDialog {
    QYDIalog qydIalog;
    TextView con;
    public CompareDialog(Context context, String conn){
        qydIalog = new QYDIalog(context, R.layout.compare_dialog, new int[]{});
        qydIalog.show();
        con = qydIalog.findViewById(R.id.content);
        con.setText("您的得分是："+conn);
    }
}
