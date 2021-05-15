package com.example.qydemo0.Widget.RankItem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.qydemo0.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ChallengeItem extends RelativeLayout {

    Activity activity;
    public View mView;
    TextView rank;
    ImageView medal;
    TextView userName, stars;

    public ChallengeItem(Context context) {
        super(context);
        activity = (Activity) context;
        INFLATE();
    }

    void INFLATE(){
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.challenge_item, this, true);
        rank = mView.findViewById(R.id.rank);
        medal = mView.findViewById(R.id.medal);
        userName = mView.findViewById(R.id.name);
        stars = mView.findViewById(R.id.stars);
    }

    public void fill(JSONObject json, int loc){
        try {
            JSONObject user = json.getJSONObject("user");
            userName.setText(user.getString("username"));
            stars.setText(json.getString("star"));
            int rank_i = json.getInt("rank");
            if(loc <= rank_i){
                rank_i = loc;
            }
            else {
                mView.setVisibility(GONE);
                return;
            }
            if(rank_i > 3){
                medal.setVisibility(GONE);
                rank.setVisibility(VISIBLE);
                rank.setText(String.valueOf(rank_i));
            }
            else {
                if(rank_i == 2) medal.setImageResource(R.drawable.ic__silver_medal);
                else if(rank_i == 3) medal.setImageResource(R.drawable.ic__bronze_medal);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
