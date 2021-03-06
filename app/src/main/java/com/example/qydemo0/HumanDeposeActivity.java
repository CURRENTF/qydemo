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
import com.example.qydemo0.QYpack.WaveLoadDialog;
import com.example.qydemo0.R;
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;
import com.example.qydemo0.Widget.QYDIalog;
import com.example.qydemo0.Widget.QYLoading;
import com.example.qydemo0.bean.CallBackBean;
import com.example.qydemo0.entry.Image;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.gson.Gson;
import com.koushikdutta.async.http.body.JSONObjectBody;
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

import org.json.JSONArray;
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

public class HumanDeposeActivity extends MyAppCompatActivity {

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

    private String wid="";

    private QYrequest cur_request = new QYrequest();

    private QYLoading qyLoading;

    private WaveLoadDialog waveLoadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_human_depose_acivity);
        ArrayList<String> list = getIntent().getStringArrayListExtra("params");
        init_player(list.get(0));
        wid = list.get(1);
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

        videoPlayer.setUp(video_url, true, "???????????????");

        //??????title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //???????????????
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //????????????
//            orientationUtils = new OrientationUtils(this, videoPlayer);
        //????????????????????????,????????????????????????????????????????????????
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //orientationUtils.resolveByClick();
            }
        });
        //????????????????????????
        videoPlayer.setIsTouchWiget(true);
        //????????????????????????
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
        if(depose_points.size()!=0) {
            Collections.sort(depose_points);

            if (depose_points.get(0) != 0) {
                depose_points.add(0, (double) 0);
            }

            if (depose_points.get(depose_points.size() - 1) != (double) (deposing_video_duration) / (double) (1000)) {
                depose_points.add((double) (deposing_video_duration) / (double) (1000));
            }
        } else {
            depose_points.add((double)0);
            depose_points.add((double) (deposing_video_duration) / (double) (1000));
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

        //????????????????????????canvas???????????????????????????
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

    public class sendParams extends MyAsyncTask<String, String, Boolean> {

        protected sendParams(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            waveLoadDialog = new WaveLoadDialog(HumanDeposeActivity.this);
            waveLoadDialog.start_progress();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            List<Double> res = getSegmentPoints();
            List<Double> s = res.subList(0, res.size()-1), e = res.subList(1, res.size());
            String ss = "", ee = "";
            for(int i=0;i<s.size();i++){
                ss += String.valueOf(s.get(i));
                ee += String.valueOf(e.get(i));
                if(i!=s.size()-1){
                    ss += ",";
                    ee += ",";
                }
            }

            Log.i("whc_ss", ss);
            Log.i("whc_ee", ee);

            //??????????????????

            String[] callToJson = {"start", "string", ss, "end", "string", ee, "intro", "string", strings[0]};
            Log.i("whc_human_depose", GenerateJson.universeJson2(callToJson));
            String res_send = cur_request.advancePost(GenerateJson.universeJson2(callToJson),
                    Constant.mInstance.work+"breakdown/"+wid+"/", "Authorization", GlobalVariable.mInstance.token);
            Log.i("whc_res_send", res_send);
            try {
                if(!(new JSONObject(res_send)).getString("msg").equals("Success")){
                    return false;
                } else {
                    JSONObject jo = new JSONObject(res_send);
                    String tid = jo.getJSONObject("data").getString("task_id");
                    for(int i=0;i<50;i++){
                        JSONObject task_res = new JSONObject(cur_request.advanceGet(Constant.mInstance.task_url+"schedule/"+tid+"/",
                                "Authorization",GlobalVariable.mInstance.token));
                        Log.i("whc_task", String.valueOf(task_res));
                        int cur_schedule = task_res.getJSONObject("data").getJSONObject("task").getInt("prog");
                        if(task_res.getJSONObject("data").getJSONObject("task").getInt("is_finish")==1){
                            return true;
                        } else {
                            publishProgress(cur_schedule==100?"99":String.valueOf(cur_schedule), task_res.getJSONObject("data").getJSONObject("task").getString("step"));
                        }
                        Thread.sleep(500);
                    }
                    return false;
                }
            } catch (JSONException | InterruptedException jsonException) {
                jsonException.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            waveLoadDialog.stop_progress();
            if (!aBoolean) {
                Log.i("whc__", "NO!");
                Toast.makeText(HumanDeposeActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
            } else {
                Log.i("whc__", "YES!");
                Toast.makeText(HumanDeposeActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            waveLoadDialog.set_progress(Float.valueOf(values[0])/100f, values[1]);
        }

    }

    class lsr implements QYDIalog.OnCenterItemClickListener{

        @Override
        public void OnCenterItemClick(QYDIalog dialog, View view) {
            switch (view.getId()){
                case R.id.upload:
                    String content = intro.getText().toString();
                    if(content.equals("")){
                        Toast.makeText(HumanDeposeActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(HumanDeposeActivity.this, content, Toast.LENGTH_SHORT).show();
                        new sendParams(HumanDeposeActivity.this).execute(content);
                    }
                    break;
                case R.id.cancel:
                    qydIalog.dismiss();
                    break;
            }
        }
    }
}