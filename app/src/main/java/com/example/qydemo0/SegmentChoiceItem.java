package com.example.qydemo0;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.qydemo0.R;

import org.json.JSONObject;

public class SegmentChoiceItem extends LinearLayout {
    Activity activity;
    public View mView;
    TextView intro;
    TextView upload_user, useNum;

    public SegmentChoiceItem(Context context) {
        super(context);
        activity = (Activity) context;
        INFLATE();
    }

    void INFLATE(){
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.segment_choice_item, this, true);
        intro = mView.findViewById(R.id.intro);
        upload_user = mView.findViewById(R.id.upload_user);
        useNum = mView.findViewById(R.id.useNum);
    }

    void fill(JSONObject json){

    }
}
