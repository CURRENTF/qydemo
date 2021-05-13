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

public class ImageItem extends RelativeLayout {

    Activity activity;
    public View mView;
    TextView rank;
    ImageView medal, img;
    TextView userName, pass_rate;

    public ImageItem(Context context) {
        super(context);
        activity = (Activity) context;
        INFLATE();
    }

    void INFLATE(){
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.image_item, this, true);
        rank = mView.findViewById(R.id.rank);
        medal = mView.findViewById(R.id.medal);
        userName = mView.findViewById(R.id.name);
        img = mView.findViewById(R.id.pic);
        pass_rate = mView.findViewById(R.id.pass_rate);
    }

    void fill(JSONObject json){

    }
}
