package com.example.qydemo0.Widget.RankItem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.qydemo0.R;

import org.json.JSONObject;

public class FreeItem extends RelativeLayout {

    Activity activity;
    public View mView;
    TextView rank;
    ImageView medal;
    TextView userName, count;

    public FreeItem(Context context) {
        super(context);
        activity = (Activity) context;
        INFLATE();
    }

    void INFLATE(){
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.free_item, this, true);
        rank = mView.findViewById(R.id.rank);
        medal = mView.findViewById(R.id.medal);
        userName = mView.findViewById(R.id.name);
        count = mView.findViewById(R.id.count);
    }

    void fill(JSONObject json){

    }
}
