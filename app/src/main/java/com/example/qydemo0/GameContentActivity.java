package com.example.qydemo0;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.qydemo0.Widget.QYDIalog;

public class GameContentActivity extends AppCompatActivity {

    private float cur_pro = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_content);
        init_intent();
        init_dialog();
    }

    private void init_intent(){
        findViewById(R.id.segmentModel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameContentActivity.this, SegmentGameChoiceActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.freeModel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameContentActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

    }

    private void init_dialog(){
        QYDIalog phb = new QYDIalog(GameContentActivity.this, R.layout.phb_dialog, new int[]{R.id.cg, R.id.zy, R.id.tp});
        phb.setOnCenterItemClickListener(new PHB());
        findViewById(R.id.phb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phb.show();
            }
        });
    }

    public class PHB implements QYDIalog.OnCenterItemClickListener{

        @Override
        public void OnCenterItemClick(QYDIalog dialog, View view) {
            switch (view.getId()){
                case R.id.cg:
                    Toast.makeText(GameContentActivity.this, "闯关模式排行榜", Toast.LENGTH_LONG).show();
                    break;
                case R.id.zy:
                    Toast.makeText(GameContentActivity.this, "自由模式排行榜", Toast.LENGTH_LONG).show();
                    break;
                case R.id.tp:
                    Toast.makeText(GameContentActivity.this, "图片难度排行榜", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

}
