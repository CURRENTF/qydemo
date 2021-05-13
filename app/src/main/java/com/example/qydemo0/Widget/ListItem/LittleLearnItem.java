package com.example.qydemo0.Widget.ListItem;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.R;
import com.example.qydemo0.entry.Image;

import org.json.JSONException;
import org.json.JSONObject;

public class LittleLearnItem extends LinearLayoutItem {

    public static int height = 120;

    private Context mContext = null;
    public View mView = null;
    ImageView icon;
    TextView score, remark, ser;

    public LittleLearnItem(Context context) {
        super(context);
        mContext = context;
        initDf();
    }
    public LittleLearnItem(ViewGroup p, Activity a){
        super(a);
        mContext = a;
        mView = LayoutInflater.from(p.getContext())
                .inflate(R.layout.little_learn_item, p, false);
    }

    @Override
    public void fill(JSONObject json) {
        try {
            int i = json.getInt("i");
            init(json, i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initDf(){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.little_learn_item, this, true);
    }

    public void init(JSONObject jsonObject, int i){
        icon = mView.findViewById(R.id.medal);
        score = mView.findViewById(R.id.score);
        remark = mView.findViewById(R.id.remark);
        ser = mView.findViewById(R.id.record_serial);
        int scores = 0;
        try {
            Log.e("hjt.log.little.learn.item", jsonObject.toString());
            scores = jsonObject.getInt("avg_score");
            score.setText(String.valueOf(jsonObject.getInt("avg_score")));
            if(scores < 60){
                icon.setImageDrawable(mContext.getDrawable(R.drawable.ic__bronze_medal));
                remark.setText("继续努力啊");
            }
            else if(scores < 85){
                icon.setImageDrawable(mContext.getDrawable(R.drawable.ic__silver_medal));
                remark.setText("你做的不错");
            }
            else {
                icon.setImageDrawable(mContext.getDrawable(R.drawable.ic__gold_medal));
                remark.setText("你做的很棒");
            }
            remark.setMaxHeight(1);
            ser.setText(String.valueOf(i + 1));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
