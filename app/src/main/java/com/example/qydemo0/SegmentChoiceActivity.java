package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SegmentChoiceActivity extends AppCompatActivity {

    LinearLayout all_main;

    List<String> segments = new ArrayList<>();

    LinearLayout.LayoutParams php = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segment_choice);
        all_main = findViewById(R.id.content);
        segments.add("12353");
        segments.add("1253");
        segments.add("153");
        php.setMargins(10,10,10,10);
        for(int i=0;i<segments.size();i++){
            addNewView("第"+(i+1)+"段");
        }
    }

    private void addNewView(String name){
        TextView t = new TextView(SegmentChoiceActivity.this);
        t.setText(name);
        t.setTextSize(30f);
        t.setTextColor(Color.GRAY);
        t.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        t.setBackgroundResource(R.drawable.btn_render_choice_on);
        t.setLayoutParams(php);
        all_main.addView(t);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SegmentChoiceActivity.this, SegmentPreLookActivity.class);
                startActivity(intent);
            }
        });
    }
}