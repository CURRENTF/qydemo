package com.example.qydemo0;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.graphics.vector.PathNode;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.aiunit.core.FrameData;
import com.aiunit.vision.common.ConnectionCallback;
import com.aiunit.vision.common.FrameInputSlot;
import com.aiunit.vision.common.FrameOutputSlot;
import com.bumptech.glide.Glide;
import com.coloros.ocs.ai.cv.CVUnit;
import com.coloros.ocs.ai.cv.CVUnitClient;
import com.coloros.ocs.base.common.ConnectionResult;
import com.coloros.ocs.base.common.api.OnConnectionFailedListener;
import com.coloros.ocs.base.common.api.OnConnectionSucceedListener;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.QYFile;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.SampleVideo;
import com.example.qydemo0.QYpack.SwitchVideoModel;
import com.example.qydemo0.QYpack.Uri2RealPath;
import com.example.qydemo0.QYpack.VideoClip;
import com.example.qydemo0.R;
import com.example.qydemo0.Widget.QYDIalog;
import com.example.qydemo0.bean.CallBackBean;
import com.example.qydemo0.entry.Image;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.gson.Gson;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.media.MediaMetadataRetriever;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static android.telecom.DisconnectCause.LOCAL;
import static android.view.Gravity.*;
import static android.view.View.GONE;
import static com.google.android.exoplayer2.scheduler.Requirements.NETWORK;

import org.json.JSONObject;
import org.w3c.dom.Text;

import butterknife.OnClick;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class HumanDeposeActivity extends AppCompatActivity {

    private List<List<SwitchVideoModel>> video_list = new ArrayList<>();

    long deposing_video_duration = 0;

    SampleVideo videoPlayer;

    long tinyChange = 40;

    DisplayMetrics dm;

    int width_px;

    List<Double> depose_points = new ArrayList<>();
    List<ImageView> depose_pointss = new ArrayList<>();

    long timeDown, timeUp;

    private EditText intro;

    private QYDIalog qydIalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_human_depose_acivity);
        ArrayList<String> list = getIntent().getStringArrayListExtra("params");
        init_player(list.get(0));
        //init_player("/sdcard/Pictures/QQ/res123.mp4");
        dm = getApplicationContext().getResources().getDisplayMetrics();
        width_px = dm.widthPixels;
        deposing_video_duration = videoPlayer.getGSYVideoManager().getDuration();
        ProgressDepose pd = new ProgressDepose(this);
        RelativeLayout allActivity = findViewById(R.id.activity_play);
        RelativeLayout.LayoutParams pd_params = new RelativeLayout.LayoutParams(-1, 10);
        pd_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        pd.setLayoutParams(pd_params);
        pd.setStateListAnimator(null);
        allActivity.addView(pd);
        ImageView backv = findViewById(R.id.back_video);
        backv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long cur_time = videoPlayer.getGSYVideoManager().getCurrentPosition();
                cur_time -= tinyChange;
                Log.i("whc__bac", "" + cur_time);
                videoPlayer.getCurrentPlayer().seekTo(cur_time);
            }
        });

        ImageView forwardv = findViewById(R.id.forward_video);
        forwardv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long cur_time = videoPlayer.getGSYVideoManager().getCurrentPosition();
                cur_time += tinyChange;
                Log.i("whc__bac", "" + cur_time);
                videoPlayer.getCurrentPlayer().seekTo(cur_time);
            }
        });
        Button bdp = findViewById(R.id.build_depose_point);
        bdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView depose_point = new ImageView(HumanDeposeActivity.this);
                depose_point.setImageResource(R.drawable.water);
                RelativeLayout.LayoutParams dp = new RelativeLayout.LayoutParams(100, 100);
                dp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                float a = (float) ((double) videoPlayer.getGSYVideoManager().getCurrentPosition() / (double) videoPlayer.getGSYVideoManager().getDuration());
                dp.leftMargin = (int) (a * width_px) - 60;
                Log.i("whc__", String.valueOf(dp.leftMargin));
                depose_point.setLayoutParams(dp);
                double current_time = (double) videoPlayer.getGSYVideoManager().getCurrentPosition() / (double) 1000;
                Log.i("whc_current_time", "" + videoPlayer.getGSYVideoManager().getCurrentPosition());
                if (!depose_points.contains(current_time)) {
                    depose_points.add(current_time);
                    allActivity.addView(depose_point);
                    depose_pointss.add(depose_point);

                    depose_point.setOnTouchListener(new View.OnTouchListener() {
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            switch (motionEvent.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    timeDown = System.currentTimeMillis();
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    timeUp = System.currentTimeMillis();
                                    if (timeUp - timeDown > 500) {
                                        for (int i = 0; i < depose_pointss.size(); i++) {
                                            if (depose_pointss.get(i) == depose_point) {
                                                allActivity.removeView(depose_point);
                                                depose_pointss.remove(i);
                                                depose_points.remove(i);
                                                break;
                                            }
                                        }
                                        vibrate();
                                    }
                                    break;
                                case MotionEvent.ACTION_UP:
//                                    timeUp = System.currentTimeMillis();
                                    int ind = -1;
                                    for (int i = 0; i < depose_pointss.size(); i++) {
                                        if (depose_pointss.get(i) == depose_point) {
                                            ind = i;
                                            break;
                                        }
                                    }


                                    videoPlayer.getCurrentPlayer().seekTo((long) (depose_points.get(ind) * 1000));
                                    Log.i("whc_seek", "" + (depose_points.get(ind) * 1000));
//                                    }
                                    break;
                            }
                            return true;
                        }
                    });
                }
            }
        });
        Button sendMessage = findViewById(R.id.send_message);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("whc__", "clicking");
                qydIalog = new QYDIalog(HumanDeposeActivity.this, R.layout.human_seg_intro, new int[]{R.id.upload, R.id.cancel});
                qydIalog.show();
                intro = qydIalog.findViewById(R.id.intro);
                qydIalog.setOnCenterItemClickListener(new lsr());
            }
        });
    }

    private void vibrate() {
//            Vibrator vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
//            vibrator.vibrate(100);
    }

    private void init_player(String video_url) {

        videoPlayer = findViewById(R.id.video_player);

        videoPlayer.setUp(video_url, true, "待分解视频");

        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //设置旋转
//            orientationUtils = new OrientationUtils(this, videoPlayer);
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //orientationUtils.resolveByClick();
            }
        });
        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true);
        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        videoPlayer.setVideoAllCallBack(new GSYSampleCallBack() {

            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                videoPlayer.onVideoPause();
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
                videoPlayer.startPlayLogic();
            }
        });
        videoPlayer.startPlayLogic();
        SeekBar cur_process = (SeekBar) findViewById(R.id.progress);
        cur_process.setVisibility(GONE);
        TextView switchSize = (TextView) findViewById(R.id.switchSize);
        switchSize.setVisibility(GONE);
        TextView current = (TextView) findViewById(R.id.current);
        current.setVisibility(GONE);
        TextView total = (TextView) findViewById(R.id.total);
        total.setVisibility(GONE);
        ImageView fullScreenView = (ImageView) findViewById(R.id.fullscreen);
        fullScreenView.setVisibility(GONE);
    }

    private List<Double> getSegmentPoints() {
        Collections.sort(depose_points);

        if(depose_points.get(0)!=0){
            depose_points.add(0, (double)0);
        }

        if(depose_points.get(depose_points.size()-1)!=(double)(deposing_video_duration)/(double)(1000)){
            depose_points.add((double)(deposing_video_duration)/(double)(1000));
        }
        return depose_points;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayer.getGSYVideoManager().stop();
    }

    class ProgressDepose extends View {
        Paint paintBlue, paintGray;

        public ProgressDepose(Context context) {
            super(context);
            paintBlue = new Paint();
            paintBlue.setColor(Color.BLUE);
            paintBlue.setStrokeJoin(Paint.Join.ROUND);
            paintBlue.setStrokeCap(Paint.Cap.ROUND);
            paintBlue.setStrokeWidth(10);

            paintGray = new Paint();
            paintGray.setColor(Color.GRAY);
            paintGray.setStrokeJoin(Paint.Join.ROUND);
            paintGray.setStrokeCap(Paint.Cap.ROUND);
            paintGray.setStrokeWidth(10);
        }

        long num = 0;

        //在这里我们将测试canvas提供的绘制图形方法
        @Override
        protected void onDraw(Canvas canvas) {
            long cur_time = videoPlayer.getGSYVideoManager().getCurrentPosition();
            float a = (float) cur_time / (float) deposing_video_duration;
            if (num % 100 == 0) Log.i("whc__", String.valueOf(depose_points));
            num++;
            canvas.drawLine(5, 0, a * width_px, 0, paintBlue);
            canvas.drawLine(a * width_px, 0, width_px, 0, paintGray);
            postInvalidate();
        }

    }

    public class sendParams extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            List<Double> res = getSegmentPoints();
            List<Double> s = res.subList(0, res.size()-1), e = res.subList(1, res.size());
            //发送人工分段
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean) {
                Log.i("whc__", "NO!");
            } else {
                Log.i("whc__", "YES!");
            }
        }
    }

    class lsr implements QYDIalog.OnCenterItemClickListener{

        @Override
        public void OnCenterItemClick(QYDIalog dialog, View view) {
            switch (view.getId()){
                case R.id.upload:
                    String content = intro.getText().toString();
                    if(content.equals("")){
                        Toast.makeText(HumanDeposeActivity.this, "输入简介不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(HumanDeposeActivity.this, content, Toast.LENGTH_SHORT).show();
                        //new sendParams().execute();
                    }
                    break;
                case R.id.cancel:
                    qydIalog.dismiss();
                    break;
            }
        }
    }

}
