package com.example.qydemo0.Widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.qydemo0.R;

public class Tab extends LinearLayout {

    private Activity ac;
    public View mView;
    String[] texts;
    View[] views;
    TextView[] textViews;
    int padding_h = 50, padding_v = 10;

    public Tab(Context context) {
        super(context);
        ac = (Activity) context;
        expand_me();
    }

    public Tab(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ac = (Activity) context;
        expand_me();
    }

    void expand_me(){
        LayoutInflater inflater = (LayoutInflater)ac.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.tab, this, true);
    }

    int last_idx;
    Animation left_in, left_out, right_in, right_out;

    public void init(String[] texts, View[] views){
        left_out = AnimationUtils.loadAnimation(ac,
                R.anim.ani_left_translate_alpha_500ms);
        left_in = AnimationUtils.loadAnimation(ac, R.anim.ani_left_translate_in_alpha_500ms);
        right_in = AnimationUtils.loadAnimation(ac, R.anim.ani_right_translate_in_alpha_500ms);
        right_out = AnimationUtils.loadAnimation(ac, R.anim.ani_right_translate_alpha_500ms);
        this.texts = texts;
        this.views = views;
        LinearLayout container = mView.findViewById(R.id.txt);
        textViews = new TextView[texts.length];
        for(int i = 0; i < texts.length; i++){
            TextView txt = new TextView(ac);
            txt.setText(texts[i]);
            txt.setTextColor(ac.getColor(R.color.black));
            txt.setPadding(padding_h, padding_v, padding_h, padding_v);
            int finalI = i;
            txt.setOnClickListener(v -> {
                ((TextView)v).setTextColor(ac.getColor(R.color.real_pink));
                textViews[last_idx].setTextColor(ac.getColor(R.color.black));
                if(last_idx < finalI){
                    views[last_idx].startAnimation(left_out);
                    views[finalI].startAnimation(left_in);
                }
                else {
                    views[last_idx].startAnimation(right_out);
                    views[finalI].startAnimation(right_in);
                }
                views[last_idx].setVisibility(GONE);
                views[finalI].setVisibility(VISIBLE);
                last_idx = finalI;
            });
            textViews[i] = txt;
            container.addView(txt);
        }
        last_idx = 0;
        textViews[last_idx].setTextColor(ac.getColor(R.color.real_pink));
        views[last_idx].setVisibility(VISIBLE);
        for(int i = 1; i < views.length; i++) views[i].setVisibility(GONE);
    }

}
