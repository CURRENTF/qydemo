package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SegmentGameChoiceActivity extends AppCompatActivity {

    private int[] segmentView = {R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segment_game_choice);
        init_segment_textView();
    }

    private void init_segment_textView() {
        for (int i = 0; i < 9; i++) {
            findViewById(segmentView[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SegmentGameChoiceActivity.this, GameActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

}