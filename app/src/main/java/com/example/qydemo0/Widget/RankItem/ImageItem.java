package com.example.qydemo0.Widget.RankItem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.R;

import org.json.JSONException;
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

    public void fill(JSONObject json, int loc){
        try {
            JSONObject user = json.getJSONObject("belong");
            userName.setText(user.getString("username"));
            pass_rate.setText(json.getString("pass_rate"));
            int rank_i = loc;
//            if(loc <= rank_i){
//                rank_i = loc;
//            }
//            else {
//                mView.setVisibility(GONE);
//                return;
//            }
            if(rank_i > 3){
                medal.setVisibility(GONE);
                rank.setVisibility(VISIBLE);
                rank.setText(String.valueOf(rank_i));
            }
            else {
                if(rank_i == 2) medal.setImageResource(R.drawable.ic__silver_medal);
                else if(rank_i == 3) medal.setImageResource(R.drawable.ic__bronze_medal);
            }
            JSONObject img = json.getJSONObject("img");
            Img.url2imgViewRoundRectangle(img.getString("url"), this.img, activity, 10);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fill(JSONObject json){
        try {
            JSONObject user = json.getJSONObject("belong");
            userName.setText(user.getString("username"));
            pass_rate.setText(json.getString("pass_rate"));
            int rank_i = json.getInt("rank");
//            if(loc <= rank_i){
//                rank_i = loc;
//            }
//            else {
//                mView.setVisibility(GONE);
//                return;
//            }
            if(rank_i > 3){
                medal.setVisibility(GONE);
                rank.setVisibility(VISIBLE);
                rank.setText(String.valueOf(rank_i));
            }
            else {
                if(rank_i == 2) medal.setImageResource(R.drawable.ic__silver_medal);
                else if(rank_i == 3) medal.setImageResource(R.drawable.ic__bronze_medal);
            }
            JSONObject img = json.getJSONObject("img");
            Img.url2imgViewRoundRectangle(img.getString("url"), this.img, activity, 10);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
