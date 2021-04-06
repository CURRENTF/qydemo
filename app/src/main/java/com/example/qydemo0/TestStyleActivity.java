package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class TestStyleActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_style2);
        Button btn1 = findViewById(R.id.id1);
        Button btn2 = findViewById(R.id.id2);
        btn1.setOnClickListener(this);btn2.setOnClickListener(this);
        ImageView img = findViewById(R.id.id3);
        img.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        ImageView img = findViewById(R.id.id3);
        if(v.getId() == R.id.id1){
            Animation animation = AnimationUtils.loadAnimation(TestStyleActivity.this,R.anim.ani_right_translate_alpha_500ms);
            img.startAnimation(animation);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    img.setVisibility(View.INVISIBLE);
                }
            }, 500);
        }
        if(v.getId() == R.id.id3){
            Toast.makeText(this, "hh", Toast.LENGTH_SHORT).show();
        }
    }
}