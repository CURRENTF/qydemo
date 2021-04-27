package com.example.qydemo0.Widget.ListItem;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import org.json.JSONObject;

public abstract class RelativeLayoutItem extends RelativeLayout {
    public RelativeLayoutItem(Context context) {
        super(context);
    }

    public RelativeLayoutItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeLayoutItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RelativeLayoutItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public abstract void fill(JSONObject json);
}
