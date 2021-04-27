package com.example.qydemo0.Widget.ListItem;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import org.json.JSONObject;

public abstract class LinearLayoutItem extends LinearLayout {

    public LinearLayoutItem(Context context) {
        super(context);
    }

    public LinearLayoutItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LinearLayoutItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public abstract void fill(JSONObject json);
}