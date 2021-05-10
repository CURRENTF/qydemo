package com.example.qydemo0.Widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class Game extends RelativeLayout {

    Activity ac;
    View mView;

    public Game(Context context) {
        super(context);
        ac = (Activity) context;
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        ac = (Activity) context;
    }

    void init(){

    }

}
