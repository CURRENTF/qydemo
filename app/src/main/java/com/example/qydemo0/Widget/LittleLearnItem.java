package com.example.qydemo0.Widget;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.R;
import com.example.qydemo0.entry.Image;

import org.json.JSONObject;

public class LittleLearnItem extends LinearLayout {

    public static int height = 120;

    private Context mContext = null;
    private View mView = null;
    ImageView icon;
    TextView score, remark, ser;

    public LittleLearnItem(Context context) {
        super(context);
        mContext = context;
        initDf();
    }

    private void initDf(){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.little_learn_item, this, true);
    }

    public void init(JSONObject jsonObject){
        icon = mView.findViewById(R.id.medal);
        score = mView.findViewById(R.id.score);
        remark = mView.findViewById(R.id.remark);
        ser = mView.findViewById(R.id.record_serial);

    }

}
