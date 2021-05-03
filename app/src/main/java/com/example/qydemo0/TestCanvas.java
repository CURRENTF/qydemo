package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qydemo0.QYpack.GlobalVariable;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class TestCanvas extends AppCompatActivity {

    private String TAG = "ivw";
    private Toast mToast;
    private TextView textView;
    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    // 唤醒结果内容
    private String resultString;

    // 设置门限值 ： 门限值越低越容易被唤醒
    private TextView tvThresh;
    private SeekBar seekbarThresh;
    private final static int MAX = 3000;
    private final static int MIN = 0;
    private int curThresh = 1450;
    private String threshStr = "门限值：";
    private String keep_alive = "1";
    private String ivwNetMode = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_canvas);
        PoseHuman ph = new PoseHuman(this);
        RelativeLayout all_main = findViewById(R.id.testCanvas);
        RelativeLayout.LayoutParams php = new RelativeLayout.LayoutParams(500,500);
        php.addRule(RelativeLayout.CENTER_HORIZONTAL);
        php.addRule(RelativeLayout.CENTER_VERTICAL);
        ph.setLayoutParams(php);
        all_main.addView(ph);
    }
    private int[] points = {88,46,101,37,108,45,116,36,124,45};
    private boolean goRight = true;
    private boolean goBottom = true;
    private int xSpeed = 1;
    private int ySpeed = 1;
    public int[] getPoints(){
        //x坐标
        for(int i=0;i<5;i++) {
            if (points[i * 2] >= 500) {
                goRight = false;
            }
            if (points[i * 2] <= 0) {
                goRight = true;
            }
            if (goRight) {
                points[i * 2] += xSpeed;
            } else {
                points[i*2] -= xSpeed;
            }

            //y坐标
            if (points[i*2+1] >= 500) {
                goBottom = false;
            }
            if (points[i*2+1] <= 0) {
                goBottom = true;
            }
            if (goBottom) {
                points[i*2+1] += ySpeed;
            } else {
                points[i*2+1] -= ySpeed;
            }
        }
        return points;
    }


    public class PoseHuman extends View {
        private static final int ALPHA = 255;
        private int rectLeft = 0;
        private int rectTop = 0;
        private static final int rectWidth = 40;
        private static final int rectHeight = 40;
        private Paint mInnerPaint;
        private RectF mDrawRect;
        public int width;
        public int height;
        private int[] pointss;

        public PoseHuman(Context context) {
            super(context);
            mInnerPaint = new Paint();
            mDrawRect = new RectF();
            mInnerPaint.setARGB(ALPHA, 255, 0, 0);
            mInnerPaint.setAntiAlias(true);
            System.out.println("绘图初始化");
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            width = getMeasuredWidth();
            height = getMeasuredHeight();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            //mDrawRect.set(rectLeft, rectTop, rectLeft + rectWidth, rectTop + rectHeight);
            //canvas.drawRoundRect(mDrawRect, 0, 0, mInnerPaint);
            changePos();
            for(int i=0;i<4;i++)
                canvas.drawLine(points[i*2],points[i*2+1],points[i*2+2],points[i*2+3],mInnerPaint);
            changePos();
        }

        //改变位置
        protected void changePos() {
            //重新绘制,触发 onDraw()
            pointss = getPoints();
            postInvalidate();
        }

    }

}