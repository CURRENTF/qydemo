package com.example.qydemo0.Widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.qydemo0.R;

import static java.lang.Math.min;

public class GameCheckPointItem extends ConstraintLayout {

    Activity activity;
    View mView;
    ImageView medal, src, lock;
    int resId;
    int[] stars_id = {R.id.star_0, R.id.star_1_0, R.id.star_1_1, R.id.star_2, R.id.star_3_0, R.id.star_3_1,
        R.id.star_4, R.id.star_5_0, R.id.star_5_1, R.id.star_6, R.id.star_7_0, R.id.star_7_1};
    ImageView[] stars;

    public GameCheckPointItem(@NonNull Context context, int resId) {
        super(context);
        stars = new ImageView[12];
        activity = (Activity) context;
        this.resId = resId;
        INFLATE();
    }

    void INFLATE(){
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.game_checkpoint_item, this, true);
        lock = mView.findViewById(R.id.lock);
        medal = mView.findViewById(R.id.medal);
        medal.setVisibility(GONE);
        src = mView.findViewById(R.id.img);
        src.setImageResource(resId);
        for(int i = 0; i < 8; i++){
            stars[i] = mView.findViewById(stars_id[i]);
            stars[i].setBackgroundColor(activity.getColor(R.color.star_dark));
        }
    }

    void setStarLine(int x){
        for(int i = 0; i < x; i++){
            if((i & 1) == 1){
                stars[3 * (i/2)].setBackgroundColor(activity.getColor(R.color.star_light));
            }
            else {
                stars[3 * (i/2) + 1].setBackgroundColor(activity.getColor(R.color.star_light));
                stars[3 * (i/2) + 2].setBackgroundColor(activity.getColor(R.color.star_light));
            }
        }
    }

    public void setStar(double p){
        int x = (int)(p * 9 + 0.5);
        setStar(min(x, 9));
    }

    public void setStar(int x){
        if(x == 0) lockSelf();
        if(x == 9){
            medal.setVisibility(VISIBLE);
            setStarLine(8);
        }
        else {
            setStarLine(x);
        }
    }

    public void lockSelf(){
        lock.setVisibility(VISIBLE);
        src.setAlpha((float) 0.5);
    }
}
