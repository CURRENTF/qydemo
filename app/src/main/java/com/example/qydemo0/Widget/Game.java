package com.example.qydemo0.Widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.qydemo0.R;

public class Game extends RelativeLayout {

    Activity ac;
    View mView;

    public Game(Context context) {
        super(context);
        ac = (Activity) context;
        INFLATE();
        init();
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        ac = (Activity) context;
        INFLATE();
        init();
    }

    void INFLATE(){
        LayoutInflater inflater = (LayoutInflater)ac.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.fragment_game, this, true);
        challenge = mView.findViewById(R.id.challenge_mode);
        free = mView.findViewById(R.id.free_mode);
        tab = mView.findViewById(R.id.ranks);
        challenge_rank = mView.findViewById(R.id.challenge_rank);
        free_rank = mView.findViewById(R.id.free_rank);
        image_rank = mView.findViewById(R.id.img_rank);
    }

    Button challenge, free;
    Tab tab;
    LinearLayout challenge_rank, free_rank, image_rank;

    void init(){
        challenge.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

            }
        });
        free.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
            }
        });
        String[] texts = {"闯关", "自由", "难度"};
        View[] views = {challenge_rank, free_rank, image_rank};
        tab.init(texts, views, R.color.black_overlay, R.color.real_pink, 16);
    }
}
