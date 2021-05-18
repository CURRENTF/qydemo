package com.example.qydemo0;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aiunit.common.protocol.face.FaceResult;
import com.aiunit.common.protocol.face.FaceResultList;
import com.aiunit.vision.common.ConnectionCallback;
import com.aiunit.vision.face.FaceInputSlot;
import com.aiunit.vision.face.FaceOutputSlot;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.coloros.ocs.ai.cv.CVUnit;
import com.coloros.ocs.ai.cv.CVUnitClient;
import com.coloros.ocs.base.common.ConnectionResult;
import com.coloros.ocs.base.common.api.OnConnectionFailedListener;
import com.coloros.ocs.base.common.api.OnConnectionSucceedListener;
import com.example.qydemo0.QYpack.AudioPlayer;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.DeviceInfo;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.KqwOneShot;
import com.example.qydemo0.QYpack.QYFile;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.SampleVideo;
import com.example.qydemo0.QYpack.SwitchVideoModel;
import com.example.qydemo0.QYpack.VideoClip;
import com.example.qydemo0.QYpack.WaveLoadDialog;
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;
import com.example.qydemo0.utils.SoundTipUtil;
import com.iflytek.cloud.b.b;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import moe.codeest.enviews.ENDownloadView;
import moe.codeest.enviews.ENPlayView;

import static android.view.View.GONE;

/**
 * sampleVideo支持全屏与非全屏切换的清晰度，旋转，镜像等功能.
 * Activity可以继承GSYBaseActivityDetail实现详情模式的页面
 * 或者参考DetailPlayer、DetailListPlayer实现
 * <p>
 * Created by guoshuyu on 2017/6/18.
 */

public class LearnDanceActivity extends MyAppCompatActivity implements SurfaceHolder.Callback{

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    //private Button btnStartStop;
    private boolean isRecording = false;//标记是否已经在录制
    private MediaRecorder mRecorder;//音视频录制类
    private Camera mCamera = null;//相机
    private Camera.Size mSize = null;//相机的尺寸
    private int mCameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;//默认后置摄像头
    private static final SparseIntArray orientations = new SparseIntArray();//手机旋转对应的调整角度
    private int current_video_number = 0;
    private Boolean is_learn;
    private int all_num = 4;
    String path_cur="";
    private Boolean is_record;
    private Boolean is_compare;
    private List<List<Long>> wrong_time = new ArrayList<>();
    private List<List<Boolean>> wrong_id = new ArrayList<>();
    private int all_learn_depose_video_num = 0;
    private int cur_compare_id = 0;
    //private ProgressDialog progressDialog;
    private WaveLoadDialog dialog;
    SeekBar cur_process;
    List<Integer> opt = new ArrayList();
    private int learning_id = -1;
    private int segment_id = -1;
    RelativeLayout menu_op;
    ImageView arrow;
    private int cur_rid = -1;

    private int is_normal;

    private boolean is_fac = false;

    private List<Integer> expressions_sad = new ArrayList<>();

    private List<List<SwitchVideoModel>> all_learn_video = new ArrayList<>();

    static {
        orientations.append(Surface.ROTATION_0, 90);
        orientations.append(Surface.ROTATION_90, 0);
        orientations.append(Surface.ROTATION_180, 270);
        orientations.append(Surface.ROTATION_270, 180);
    }

    @BindView(R.id.detail_player)
    SampleVideo detailPlayer;

    @BindView(R.id.activity_detail_player)
    RelativeLayout activityDetailPlayer;

    private boolean isPlay;
    private boolean isPause;
    private boolean isRelease;

    private OrientationUtils orientationUtils;

    private MediaMetadataRetriever mCoverMedia;

    private ImageView btn1,btn2,btn3;

    private ImageView coverImageView, fullScreenView;
    ENPlayView startVideo;
    ENDownloadView downloadView;

    private TextView changeSpeed;

    private int bid;

    RelativeLayout.LayoutParams people_all = new RelativeLayout.LayoutParams(350, 910);
    private JSONArray urls_jsonarry = new JSONArray();

    private QYrequest learn_request = new QYrequest();

    private  QYFile learn_file = new QYFile();

    boolean mirror_status = false;

    private ImageView[] wrong_kuang = new ImageView[10], human_icons = new ImageView[6];

    private CVUnitClient mCVClient;
    private int startCode = -1;

    private TextView smile_word;

    private RelativeLayout.LayoutParams fill_tiny;

    private LinearLayout human_iconss;

    private ImageView black_back;

    RelativeLayout.LayoutParams fill_all_r = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
    RelativeLayout.LayoutParams fill_all;

    Handler handler;

    ImageView cover_start_icon;

    private KqwOneShot kqw;

    private boolean pose_status = false;
    private PoseHuman phl,phr;

    private int[] wrong_kuang_resource = {R.id.head, R.id.body, R.id.left_hand, R.id.left_hand_2, R.id.right_hand,
            R.id.right_hand_2, R.id.left_leg, R.id.left_leg_2, R.id.right_leg, R.id.right_leg_2},
            human_icons_resource = {R.id.human_head, R.id.human_body, R.id.left_hands, R.id.right_hands, R.id.left_foots, R.id.right_foots};

    private WrongShow wrongShow;

    private CompareDialog cdg;

    private List<List<Integer> > spt = new ArrayList<>();

    private String nne, nnv, nnm, nni;

    AudioPlayer audioPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setWindow();
        setContentView(R.layout.activity_learn_dance);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getSupport();
        smile_word = (TextView) findViewById(R.id.smile_word);

        ArrayList<String> list = getIntent().getStringArrayListExtra("params");

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int heightPixels = dm.heightPixels;
        fill_all = new RelativeLayout.LayoutParams((int) heightPixels*1280/720, heightPixels);

        fill_all.addRule(RelativeLayout.CENTER_HORIZONTAL);
        fill_tiny = new RelativeLayout.LayoutParams(1,1);

        // 学习项目id
        learning_id = Integer.valueOf(list.get(0));
        // breakdown id
        bid = Integer.valueOf(list.get(1));
        // 开始位置
        current_video_number = Integer.valueOf(list.get(2));
        Log.i("whc_cvn", current_video_number+"");
        // 是否正常学习
        is_normal = Integer.valueOf(list.get(3));
        // 非正常学习参数
        if(is_normal==0){
            // 对比视频
            nnv = list.get(4);
            // 评估
            nne = list.get(5);
            // pose model
            nnm = list.get(6);
            // pose input
            nni = list.get(7);
        }

        human_iconss = findViewById(R.id.human_icons);
        human_iconss.setVisibility(GONE);

        mCVClient = CVUnit.getFaceFerClient
                (this.getApplicationContext()).addOnConnectionSucceedListener(new OnConnectionSucceedListener() {
            @Override
            public void onConnectionSucceed() {
                Log.i("TAG", " authorize connect: onConnectionSucceed");
            }
        }).addOnConnectionFailedListener(new OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Log.e("TAG", " authorize connect: onFailure: " + connectionResult.getErrorCode());
            }
        });

        mCVClient.initService(this, new ConnectionCallback() {
            @Override
            public void onServiceConnect() {
                Log.i("TAG", "initService: onServiceConnect");
                startCode = mCVClient.start();
                if (startCode == 0) {

                } else {
                    Log.i("whc123", "init wrong!");
                }
            }

            @Override
            public void onServiceDisconnect() {
                Log.e("TAG", "initService: onServiceDisconnect: ");
            }
        });

        init_wrong_kuang();
        initViews();
        ButterKnife.bind(this);
        init_pose_view();
        findViewById(R.id.humanPose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_humanPose();
            }
        });
        init_kqw();

        new InitAllLearn(LearnDanceActivity.this).execute(bid);

//        phl.setPoints(trpl);
//      ]]4_now();
//        phr.setPoints(rawPointsLl);
//        phr.start_now();
    }

    private void getSupport(){
        LearnDanceActivity.CameraSizeComparator sizeComparator = new LearnDanceActivity.CameraSizeComparator();
        Camera cCamera = Camera.open();
        Camera.Parameters parameters = cCamera.getParameters();
        List<Camera.Size> vSizeList = parameters.getSupportedPreviewSizes();
        Collections.sort(vSizeList, sizeComparator);
        for(int i=0;i<vSizeList.size();i++){
            List<Integer> c = new ArrayList<>();
            c.add(vSizeList.get(i).height);
            c.add(vSizeList.get(i).width);
            spt.add(c);
        }
        Log.i("whc_support", String.valueOf(spt));
        cCamera.release();
    }

    private int[] getBestSupport(int h, int w){
        for(int i=spt.size()-1;i>=0;i--){
            if(spt.get(i).get(0) < h && (spt.get(i).get(1) + w)/2 < 4000){
                return new int[]{spt.get(i).get(1), spt.get(i).get(0)};
            }
        }
        return new int[]{176, 144};
    }

    private void init_kqw(){

        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String dataa = bundle.getString("msg");
                if(dataa.equals("【打开】镜子")) {
                    if(btn_mirrors(1) && !detailPlayer.getGSYVideoManager().isPlaying()) {
                        SoundTipUtil.soundTip(LearnDanceActivity.this, "好的");
                    }
                }
                else if(dataa.equals("【关闭】镜子")) {
                    if(btn_mirrors(0) && !detailPlayer.getGSYVideoManager().isPlaying()) {
                        SoundTipUtil.soundTip(LearnDanceActivity.this, "好的");
                    }
                }
                else if (dataa.equals("【开始】学习")) {
                    btn_start();
                    if (!is_compare) {
                        if (!is_learn) {
                            SoundTipUtil.soundTip(LearnDanceActivity.this, "好的");
                        }
                        }
                }
                else if (dataa.equals("【下】一段")){
                    if (!is_learn) {
                        try {
                            new PostRecord(LearnDanceActivity.this).execute(learning_id, urls_jsonarry.getJSONObject(current_video_number).getInt("id"),2);
                            SoundTipUtil.soundTip(LearnDanceActivity.this, "好的");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if (dataa.equals("【返】回")){
                    if (is_compare) {
                        stop_compare_video();
                        try {
                            detailPlayer.setUp(all_learn_video.get(current_video_number), true, urls_jsonarry.getJSONObject(current_video_number).getString("name"));
                            detailPlayer.startPlayLogic();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if (dataa.equals("【镜像】翻转")) {
                    detailPlayer.setmTransformSize(1);
                } else if (dataa.equals("【恢】复")) {
                    detailPlayer.setmTransformSize(0);
                }
            }
        };

        kqw = new KqwOneShot(this, handler);
        kqw.btn_grammar();
    }

    private void btn_humanPose(){

        if(is_compare){
            if(pose_status){
                phl.stop_now();
                phr.stop_now();
                pose_status = false;
            }
            else{
                phl.start_now();
                phr.start_now();
                pose_status=true;
            }
        }
        else{
            Toast.makeText(LearnDanceActivity.this, "该功能只在动作对比时可用！", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean btn_mirrors(int isi){
        if(!is_compare) {
            if (!mirror_status && isi == 1) {
//                        btn1.setText("恢复");
                mirror_status = true;
                mSurfaceView.setLayoutParams(fill_all);
                black_back.setLayoutParams(fill_all_r);
                return true;
            } else if(mirror_status && isi == 0){
//                        btn1.setText("镜子");
                mirror_status = false;
                mSurfaceView.setLayoutParams(fill_tiny);
                black_back.setLayoutParams(fill_tiny);
                return true;
            }
        }
        return false;
    }

    private void btn_start(){
        if (!is_compare) {
            if (!is_learn) {
                cover_start_icon.setVisibility(View.VISIBLE);
                detailPlayer.setmTransformSize(1);
                is_learn = true;
                init_learn_view();
                detailPlayer.startPlayLogic();
            }
        }
    }

    private void btn_nOb(){
        try {
            if (!is_learn) {
                new PostRecord(this).execute(learning_id, urls_jsonarry.getJSONObject(current_video_number).getInt("id"),2);
            } else if (is_compare) {
                stop_compare_video();
                btn2.setScaleX(1);
                detailPlayer.setUp(all_learn_video.get(current_video_number), true, urls_jsonarry.getJSONObject(current_video_number).getString("name"));
                detailPlayer.startPlayLogic();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getFer(Bitmap bitmap){
        Log.i("whc_bitmap", String.valueOf(bitmap==null));
        String res = "";
        FaceInputSlot inputSlot = (FaceInputSlot) mCVClient.createInputSlot();
        inputSlot.setTargetBitmap(bitmap);
        FaceOutputSlot outputSlot = (FaceOutputSlot) mCVClient.createOutputSlot();
        mCVClient.process(inputSlot, outputSlot);
        FaceResultList faceList = outputSlot.getFaceList();
        Log.i("whc_faceResult", String.valueOf(faceList));
        List<FaceResult> faceResultList = new ArrayList<>();
        faceResultList = faceList.getFaceResultList();
        for (FaceResult faceResult: faceResultList) {
            res = faceResult.getExpression();
        }
        return res;
    }

    private void init_wrong_kuang(){
        for(int i=0;i<10;i++)
            wrong_kuang[i] = (ImageView) findViewById(wrong_kuang_resource[i]);
        for(int i=0;i<6;i++)
            human_icons[i] = (ImageView) findViewById(human_icons_resource[i]);
    }

    private void init_learn_pager(){
        initLearnVideo();
        //Log.i("hash",learn_file.hashFileUrl("/storage/emulated/0/Android/data/com.example.qydemo0/cache/videos/1617625252036.mp4"));

        is_learn = false;

        is_record = false;

        is_compare = false;

        cur_process = (SeekBar) findViewById(R.id.progress);

        fullScreenView = (ImageView) findViewById(R.id.fullscreen);
        fullScreenView.setVisibility(GONE);

        startVideo = (ENPlayView) findViewById(R.id.start);

        downloadView = (ENDownloadView) findViewById(R.id.loading);

        changeSpeed = (TextView) findViewById(R.id.change_speed);

        Log.i("whc_current_init", ""+current_video_number);

//        try {
//            Log.i("whc_current_init", ""+current_video_number);
//            detailPlayer.setUp(all_learn_video.get(3), true, urls_jsonarry.getJSONObject(current_video_number).getString("name"));
//        } catch (JSONException e) {
//            Log.i("whc_current_init", "xxx"+current_video_number);
//            e.printStackTrace();
//        }

        //增加封面
        coverImageView = new ImageView(this);
        coverImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //coverImageView.setImageResource(R.mipmap.xxx1);
        detailPlayer.setThumbImageView(coverImageView);

        resolveNormalVideoUI();

        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, detailPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);

        detailPlayer.setIsTouchWiget(true);
        //detailPlayer.setIsTouchWigetFull(false);
        //关闭自动旋转
        detailPlayer.setRotateViewAuto(false);
        //打开  实现竖屏全屏动画
        detailPlayer.setShowFullAnimation(true);
        detailPlayer.setNeedLockFull(true);
        detailPlayer.setSeekRatio(1);
        //detailPlayer.setOpenPreView(false);
        detailPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                detailPlayer.startWindowFullscreen(LearnDanceActivity.this, true, true);
            }
        });

        detailPlayer.setVideoAllCallBack(new GSYSampleCallBack() {

            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                isPlay = true;
                detailPlayer.onVideoPause();
                detailPlayer.getCurrentPlayer().getCurrentPlayer().setIsTouchWiget(true);
                detailPlayer.getCurrentPlayer().setIsTouchWigetFull(true);

                if(is_normal==0 && is_compare && is_learn){
                    human_iconss.setVisibility(View.VISIBLE);
                    for(int i=0;i<6;i++) human_icons[i].setVisibility(View.VISIBLE);
                    btn2.setScaleX(-1);
                    wrongShow.start_show();
                }

                if(is_learn && !is_compare){
                    detailPlayer.getCurrentPlayer().setIsTouchWiget(false);
                    detailPlayer.getCurrentPlayer().setIsTouchWigetFull(false);
                    Toast.makeText(getBaseContext(),"你有10秒钟的时间到达录制位置",Toast.LENGTH_SHORT).show();
                    try {
                        audioPlayer = new AudioPlayer(LearnDanceActivity.this, R.raw.count_number_10);
                        audioPlayer.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                System.out.println("hereherherhreh");
                                startRecord();
                                detailPlayer.onVideoResume();
                            }
                        });
                        audioPlayer.getMediaPlayer().start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //new SleepNowThenPlay().execute();
                }
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
                smile_word.setText("");
                for(int k=0;k<10;k++)
                    wrong_kuang[k].setBackgroundResource(R.color.dark_color);
                for(int k=0;k<6;k++)
                    human_icons[k].setColorFilter(Color.parseColor("#aaaaaa"));
                if(is_learn && !is_compare){
                    stopRecord();
                    if((new File(path_cur)).isFile()) {
                        Log.i("whc233","用户视频已保存");}
                        else{
                                Log.i("whc233","用户视频保存失败");
                        }
                    }
                }

            @Override
            public void onClickStartError(String url, Object... objects) {
                super.onClickStartError(url, objects);
            }

            @Override
            public void onClickResume(String url, Object... objects) {
                super.onClickResume(url, objects);
                Log.i("whc_lll", "here");
            }
        });

        people_all.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        people_all.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        detailPlayer.setGSYVideoProgressListener(new GSYVideoProgressListener() {
//            @Override
//            public void onProgress(int progress, int secProgress, int currentPosition, int duration) {
//                //show_wrong();
//            }
//        });

        detailPlayer.getCurrentPlayer().startPlayLogic();

        black_back = (ImageView) findViewById(R.id.black_back);

        btn1 = (ImageView) findViewById(R.id.mirror_btn);
        btn2 = (ImageView) findViewById(R.id.next_video);
        btn3 = (ImageView) findViewById(R.id.learn_now);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!btn_mirrors(0)){
                    btn_mirrors(1);
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_nOb();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_start();
            }
        });

        menu_op = findViewById(R.id.expand_menu);
        arrow = findViewById(R.id.menu_btn);
        shrink_menu_now();

        try {
            new PostRecord(this).execute(learning_id, urls_jsonarry.getJSONObject(current_video_number).getInt("id"), 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void repeat(String video_url_json, String evaluation_json, String pose_model_json, String pose_input_json) throws JSONException {
        is_learn = true;
        is_compare = true;
        for(int k=0;k<10;k++)
            wrong_kuang[k].setBackgroundResource(R.color.dark_color);
        for(int k=0;k<6;k++)
            human_icons[k].setColorFilter(Color.parseColor("#aaaaaa"));

        Log.i("whc_eva", evaluation_json);

        Log.i("whc_video_url", (new JSONObject(video_url_json)).getJSONObject("url").getString("自动"));

        detailPlayer.setUp((new JSONObject(video_url_json)).getJSONObject("url").getString("自动"), true, "对比视频");
                    wrong_time.clear();
                    JSONArray wrong_time_json = (new JSONObject(evaluation_json)).getJSONArray("error");
                    wrong_id.clear();
                    for(int i=0;i<wrong_time_json.length();i++){
                        JSONObject wrong_time_json_item = wrong_time_json.getJSONObject(i);
                        List<Long> wrong_time_item = new ArrayList<>();
                        String str = wrong_time_json_item.getString("begin_time");
                        Log.d("hjt.learn.dance.begin_t", str);
                        wrong_time_item.add((long) (Double.parseDouble(str) * 1000.0));
                        String str2 = wrong_time_json_item.getString("end_time");
                        wrong_time_item.add((long) ((Double.parseDouble(str2) - Double.parseDouble(str))*1000.0));
                        int cur_wrong_id_2 = wrong_time_json_item.getInt("error_type");
                        List<Boolean> wrong_id_item = new ArrayList<>();
                        String s = Integer.toBinaryString(cur_wrong_id_2);
                        Integer s_int = Integer.valueOf(s);
                        for(int j = 6; j >0 ; j--){
                            wrong_id_item.add(s_int%10==1);
                            s_int /= 10;
                        }
                        wrong_time.add(wrong_time_item);
                        wrong_id.add(wrong_id_item);
                        Log.d("hjt.in.it", "?" + i);
                    }

        Log.e("wrong_time", String.valueOf(wrong_time));
        Log.e("wrong_id",String.valueOf(wrong_id));

        Log.d("hjt.out.it", "1");

        JSONArray pose_model = new JSONArray(pose_model_json);
        JSONArray pose_input = new JSONArray(pose_input_json);
        double[][][] a = new double[pose_model.length()][17][2];
        double[][][] b = new double[pose_input.length()][17][2];

        for(int i=0;i<pose_model.length();i++){
            JSONArray pm1 = pose_model.getJSONArray(i);
            for(int j=0;j<17;j++){
                a[i][j][0] = pm1.getJSONArray(j).getDouble(0);
                a[i][j][1] = pm1.getJSONArray(j).getDouble(1);
            }
        }

        for(int i=0;i<pose_input.length();i++){
            JSONArray pm1 = pose_input.getJSONArray(i);
            for(int j=0;j<17;j++){
                b[i][j][0] = pm1.getJSONArray(j).getDouble(0);
                b[i][j][1] = pm1.getJSONArray(j).getDouble(1);
            }
        }

        phr.setPoints(a);
        phl.setPoints(b);
        JSONArray t = (new JSONObject("evaluation_json")).getJSONArray("score");
        int r1 = 0;
        for(int y=1;y<7;y++){
            r1 = r1 + t.getJSONObject(y).getInt("value");
        }
        r1 /= 6;
        if(r1 < t.getJSONObject(0).getInt("value")) r1 = t.getJSONObject(0).getInt("value");
        cdg = new CompareDialog(LearnDanceActivity.this, ""+r1);
        init_compare_video();
        detailPlayer.getCurrentPlayer().startPlayLogic();
        //hideProgressDialog();
        for(int i=0;i<6;i++) human_icons[i].setVisibility(View.VISIBLE);
        Log.d("hjt.out.it", "2");
    }

    void shrink_menu_now(){
        arrow.setImageResource(R.drawable.ic_down_arrow2);
        menu_op.setTranslationY(-DeviceInfo.dip2px(LearnDanceActivity.this, 300));

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expand_menu_now();
            }
        });
    }

    void expand_menu_now(){
        arrow.setImageResource(R.drawable.ic_up_arrow2);
        menu_op.setTranslationY(-DeviceInfo.dip2px(this, 0));
        Animation animation = AnimationUtils.loadAnimation(this
                , R.anim.ani_down_translate_300ms);
        menu_op.startAnimation(animation);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shrink_menu_now();
            }
        });
    }

    private void stop_compare_video(){
        is_compare = false;
        is_learn = false;
        for(int i=0;i<6;i++) human_icons[i].setVisibility(GONE);
        for(int i=0;i<10;i++) wrong_kuang[i].setVisibility(GONE);
        detailPlayer.setSpeedPlaying(1.0f, true);
        detailPlayer.getMspeed().setText("1倍速");
        human_iconss.setVisibility(GONE);
        phl.stop_now();
        phr.stop_now();
        wrongShow.stop_show();
        reset_player();
    }

    private void reset_player(){
        detailPlayer.setSpeedPlaying(1.0f, true);
        detailPlayer.getMspeed().setText("1倍速");
        if(detailPlayer.getMchangeTransform().getText().equals("恢复")){
            detailPlayer.setmTransformSize(0);
        }
    }

    private void init_learn_view(){
        cur_process.setVisibility(GONE);
        changeSpeed.setVisibility(GONE);
        detailPlayer.setIs_double(false);
        detailPlayer.setmTransformSize(1);
    }

    private void reset_learn_view(){
        cur_process.setVisibility(View.VISIBLE);
        changeSpeed.setVisibility(View.VISIBLE);
        detailPlayer.setIs_double(true);
    }

    private void go_to_next_segment(){
        try {
            current_video_number++;
            if (current_video_number >= all_learn_depose_video_num) {
                Toast.makeText(LearnDanceActivity.this, "恭喜您！您已学会整支舞蹈！", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LearnDanceActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else {
                new PostRecord(this).execute(learning_id, urls_jsonarry.getJSONObject(current_video_number).getInt("id"), 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initLearnVideo() {
        try {
        for(int i=0;i<urls_jsonarry.length();i++){

            JSONObject urls_cur_item = urls_jsonarry.getJSONObject(i).getJSONObject("video").getJSONObject("url");

            SwitchVideoModel switchVideoModel;

            List<SwitchVideoModel> list = new ArrayList<>();

            if(urls_cur_item.has("1080P")){
                switchVideoModel = new SwitchVideoModel("1080P", urls_cur_item.getString("1080P"));
                list.add(switchVideoModel);
            }
            if(urls_cur_item.has("720P")){
                switchVideoModel = new SwitchVideoModel("720P", urls_cur_item.getString("720P"));
                list.add(switchVideoModel);
            }
            if(urls_cur_item.has("480P")){
                switchVideoModel = new SwitchVideoModel("480P", urls_cur_item.getString("480P"));
                list.add(switchVideoModel);
            }
            if(urls_cur_item.has("360P")){
                switchVideoModel = new SwitchVideoModel("360P", urls_cur_item.getString("360P"));
                list.add(switchVideoModel);
            }
            if(urls_cur_item.has("自动")){
                switchVideoModel = new SwitchVideoModel("自动", urls_cur_item.getString("自动"));
                list.add(switchVideoModel);
            }

            all_learn_video.add(list);

        }
        } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    private void setWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        // 设置竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 选择支持半透明模式,在有surfaceview的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
    }

    private void initViews() {
        mSurfaceView = (SurfaceView) findViewById(R.id.sf_view);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        Display display = getWindowManager().getDefaultDisplay();
//        screenWidth = display.getWidth();
//        screenHeight = display.getHeight();

        SurfaceHolder holder = mSurfaceView.getHolder();// 取得holder
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.setKeepScreenOn(true);
        holder.addCallback(this); // holder加入回调接口

        cover_start_icon = findViewById(R.id.cover_start_icon);
        cover_start_icon.setVisibility(GONE);
        for(int i=0;i<6;i++) human_icons[i].setVisibility(GONE);
    }

    /**
     * 初始化相机
     */
    private void initCamera() {
        if (Camera.getNumberOfCameras() == 2) {
            mCamera = Camera.open(mCameraFacing);
        } else {
            mCamera = Camera.open();
        }

        LearnDanceActivity.CameraSizeComparator sizeComparator = new LearnDanceActivity.CameraSizeComparator();
        Camera.Parameters parameters = mCamera.getParameters();

        if (mSize == null) {
            List<Camera.Size> vSizeList = parameters.getSupportedPreviewSizes();
            Collections.sort(vSizeList, sizeComparator);

            for (int num = 0; num < vSizeList.size(); num++) {
                Camera.Size size = vSizeList.get(num);

                if (size.width >= 800 && size.height >= 480) {
                    this.mSize = size;
                    break;
                }
            }
            mSize = vSizeList.get(0);

            List<String> focusModesList = parameters.getSupportedFocusModes();

            //增加对聚焦模式的判断
            if (focusModesList.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else if (focusModesList.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            mCamera.setParameters(parameters);
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int orientation = orientations.get(rotation);
        mCamera.setDisplayOrientation(orientation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRelease = true;
        if (isPlay) {
            getCurPlay().release();
        }
        //GSYPreViewManager.instance().releaseMediaPlayer();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
        if (mCoverMedia != null) {
            mCoverMedia.release();
            mCoverMedia = null;
        }
        if (mCVClient != null) {
            mCVClient.stop();
        }
        mCVClient.releaseService();
        mCVClient = null;
        kqw.btn_stop();
        if(!path_cur.equals("")){
        File file1 = new File(path_cur);
        if(file1.exists()) file1.delete();
        }
        if(audioPlayer!=null) audioPlayer.stop();
    }

    private GSYVideoPlayer getCurPlay() {
        if (detailPlayer.getFullWindowPlayer() != null) {
            return  detailPlayer.getFullWindowPlayer();
        }
        return detailPlayer;
    }


    private void resolveNormalVideoUI() {
        //增加title
        detailPlayer.getTitleTextView().setVisibility(GONE);
        detailPlayer.getBackButton().setVisibility(GONE);
    }


    /**
     * 这里只是演示，并不建议直接这么做
     * MediaMetadataRetriever最好做一个独立的管理器
     * 使用缓存
     * 注意资源的开销和异步等
     *
     * @param url
     */
//    public void loadFirstFrameCover(String url) {
//        //可以参考Glide，内部也是封装了MediaMetadataRetriever
//        Glide.with(this.getApplicationContext())
//                .setDefaultRequestOptions(
//                        new RequestOptions()
//                                .frame(1000000)
//                                .centerCrop();
//    }

    /**
     * 开始录制
     */
    private void startRecord() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder(); // 创建MediaRecorder
        }
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.unlock();
            mRecorder.setCamera(mCamera);
        }
        try {
            // 设置音频采集方式
            mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            //设置视频的采集方式
            mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            //设置文件的输出格式
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//aac_adif， aac_adts， output_format_rtp_avp， output_format_mpeg2ts ，webm
            //设置audio的编码格式
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //设置video的编码格式
            mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            //设置录制的视频编码比特率
            mRecorder.setVideoEncodingBitRate(1000 * 1500);
            //设置录制的视频帧率,注意文档的说明:
            mRecorder.setVideoFrameRate(30);
            //设置要捕获的视频的宽度和高度
            int[] r = getBestSupport(detailPlayer.getHeight(), detailPlayer.getWidth());
            Log.i("whc_r", ""+r[0]+"x"+r[1]);
            mSurfaceHolder.setFixedSize(r[0], r[1]);//最高只能设置640x480
            mRecorder.setVideoSize(r[0], r[1]);//最高只能设置640x480
            //设置记录会话的最大持续时间（毫秒）
            mRecorder.setMaxDuration(60 * 1000);
            mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            path_cur = getExternalCacheDir().getPath();
            if (path_cur != null) {
                File dir = new File(path_cur + "/videos");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                path_cur = dir + "/" + System.currentTimeMillis() + ".mp4";

                Log.i("path_cur",path_cur);

                //设置输出文件的路径
                mRecorder.setOutputFile(path_cur);
                //准备录制
                mRecorder.prepare();
                //开始录制
                mRecorder.start();
                isRecording = true;

                is_record = true;

                //btnStartStop.setText("停止");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录制
     */
    private void stopRecord() {
        try {
            //停止录制
            mRecorder.stop();
            is_record = false;
            //重置
            mRecorder.reset();
            //showProgressDialog("提示","正在努力解析中，请稍等...");
            new getFaceExpression(this).execute();
            new SendUserDanceVideo(this).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        isRecording = false;
        cover_start_icon.setVisibility(GONE);
    }

    private void init_compare_video(){
        is_compare = true;
        reset_learn_view();
        btn2.setScaleX(-1);
        for(int i=0;i<6;i++) human_icons[i].setVisibility(View.VISIBLE);
        wrongShow.start_show();
        human_iconss.setVisibility(View.VISIBLE);
        detailPlayer.setmTransformSize(0);
        reset_player();
    }

    /**
     * 释放MediaRecorder
     */
    private void releaseMediaRecorder() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.unlock();
                mCamera.release();
            }
        } catch (RuntimeException e) {
        } finally {
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
        mSurfaceHolder = holder;
        if (mCamera == null) {
            return;
        }
        try {
            //设置显示
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            releaseCamera();
            finish();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // surfaceDestroyed的时候同时对象设置为null
        if (isRecording && mCamera != null) {
            mCamera.lock();
        }
        mSurfaceView = null;
        mSurfaceHolder = null;
        releaseMediaRecorder();
        releaseCamera();
    }

    class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume();
        super.onResume();
        initCamera();
        isPause = false;
    }

    @Override
    public void onPause() {
        getCurPlay().onVideoPause();
        releaseCamera();
        super.onPause();
        isPause = true;
    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }

        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    public class getFaceExpression extends MyAsyncTask<Void, String, Boolean> {

        protected getFaceExpression(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected Boolean doInBackground(Void... avoid) {
            is_fac = false;
            List<Bitmap> bitmaps = VideoClip.getFromTime(path_cur);
            Log.i("whc_fer_num", bitmaps.size()+"");
            for(int k=0;k<10;k++) {
                try {
                    Thread.sleep(500);
                    if(startCode==0) {
                        Log.i("whc_ee", "startCode==0");
                        for (int i = 0; i < bitmaps.size(); i++) {
                            Log.i("whc_e", getFer(bitmaps.get(i)));
                            if (getFer(bitmaps.get(i)).equals("Sad")) {
                                expressions_sad.add(i);
                            }
                        }
                        //Log.i("whc_expressions", ""+expressions_sad);
                        return true;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Log.i("expression_res", String.valueOf(expressions_sad));

            return null;
        }

        @Override
        protected void onPostExecute(Boolean isi) {
            is_fac = true;
        }



    }

    public class SendUserDanceVideo extends MyAsyncTask<String, String, JSONObject[]> {

        protected SendUserDanceVideo(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btn_mirrors(0);
            dialog = new WaveLoadDialog(LearnDanceActivity.this);
            dialog.start_progress();
        }

        @Override
        protected JSONObject[] doInBackground(String... video_path) {
            try {String learn_dance_id = learn_file.uploadFileAllIn(Constant.mInstance.file_upload_verify_url, path_cur,
                    2,learn_file.hashFileUrl(path_cur));
            //Log.i("用户视频id", learn_dance_id);
            if(learn_dance_id == null) {
                Log.e("用户视频", "上传失败");
                return null;}
            String[] callToJson = {"record_id","int", ""+cur_rid,
                    "videoA","string", urls_jsonarry.getJSONObject(current_video_number).getJSONObject("video").getString("id"),
                    "videoB","string",learn_dance_id
            };
            Log.e("learn_json", GenerateJson.universeJson2(callToJson));
                JSONObject res_json = new JSONObject(learn_request.advancePost(GenerateJson.universeJson2(callToJson),
                        Constant.mInstance.task_url+"compare/","Authorization",GlobalVariable.mInstance.token));
                Log.i("whc_res_json", String.valueOf(res_json));
                String tid_pose = res_json.getJSONObject("data").getString("tid_pose"),
                        tid_merge = res_json.getJSONObject("data").getString("tid_merge");
                if(tid_pose==null||tid_merge==null) return null;
                int csc = 0;
                for(int i=0;i<100;i++){
                    JSONObject task_res = new JSONObject(learn_request.advanceGet(Constant.mInstance.task_url+"schedule/"+tid_pose+"/",
                            "Authorization",GlobalVariable.mInstance.token));
                    JSONObject task_res_merge = new JSONObject(learn_request.advanceGet(Constant.mInstance.task_url+"schedule/"+tid_merge+"/",
                            "Authorization",GlobalVariable.mInstance.token));
                    Log.i("whc_task", String.valueOf(task_res));
                    Log.i("whc_merge", String.valueOf(task_res_merge));
                    int cur_schedule = task_res.getJSONObject("data").getJSONObject("task").getInt("prog");
                    if(task_res.getJSONObject("data").getJSONObject("task").getInt("is_finish")==1 && task_res_merge.getJSONObject("data").getJSONObject("task").getInt("is_finish")==1&& task_res_merge.getJSONObject("data").getJSONObject("data").getJSONObject("video_url").getJSONObject("url").has("自动")){
                        for(int kk = 0;kk<10;kk++){
                            if(is_fac) break;
                            Thread.sleep(500);
                        }
                        is_fac = false;
                        return new JSONObject[]{task_res.getJSONObject("data").getJSONObject("data"), task_res_merge.getJSONObject("data").getJSONObject("data")};
                    } else {
                        if(cur_schedule!=0) {
                            if(csc>cur_schedule&&csc+2<100) csc += 2;
                            publishProgress(cur_schedule == 100 ? "99" : String.valueOf(Math.max(csc, cur_schedule)), task_res.getJSONObject("data").getJSONObject("task").getString("step"));
                        } else {
                            publishProgress(""+csc, "初始化中...");
                            csc += 2;
                        }
                    }
                    Thread.sleep(500);
                }
                return null;
            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject[] resJson) {
            dialog.stop_progress();
            if(resJson != null){
                try {
                    Log.d("hjt.return.msg.player", resJson.toString());
                    File file1 = new File(path_cur);
                    file1.delete();
                    detailPlayer.setUp(resJson[1].getJSONObject("video_url").getJSONObject("url").getString("自动"), true, "对比视频");
                    wrong_time.clear();
                    JSONArray wrong_time_json = resJson[0].getJSONObject("evaluation").getJSONArray("error");
                    wrong_id.clear();
                    for(int i=0;i<wrong_time_json.length();i++){
                        JSONObject wrong_time_json_item = wrong_time_json.getJSONObject(i);
                        List<Long> wrong_time_item = new ArrayList<>();
                        String str = wrong_time_json_item.getString("begin_time");
                        Log.d("hjt.learn.dance.begin_t", str);
                        wrong_time_item.add((long) (Double.parseDouble(str) * 1000.0));
                        String str2 = wrong_time_json_item.getString("end_time");
                        wrong_time_item.add((long) ((Double.parseDouble(str2) - Double.parseDouble(str))*1000.0));
                        int cur_wrong_id_2 = wrong_time_json_item.getInt("error_type");
                        List<Boolean> wrong_id_item = new ArrayList<>();
                        String s = Integer.toBinaryString(cur_wrong_id_2);
                        Integer s_int = Integer.valueOf(s);
                        for(int j = 6; j >0 ; j--){
                            wrong_id_item.add(s_int%10==1);
                            s_int /= 10;
                        }
                        wrong_time.add(wrong_time_item);
                        wrong_id.add(wrong_id_item);
                        Log.d("hjt.in.it", "?" + i);
                    }

                    Log.e("wrong_time", String.valueOf(wrong_time));
                    Log.e("wrong_id",String.valueOf(wrong_id));

                    Log.d("hjt.out.it", "1");

                    JSONArray pose_model = resJson[0].getJSONArray("pose_model");
                    JSONArray pose_input = resJson[0].getJSONArray("pose_input");

                    double[][][] a = new double[pose_model.length()][17][2];
                    double[][][] b = new double[pose_input.length()][17][2];

                    for(int i=0;i<pose_model.length();i++){
                        JSONArray pm1 = pose_model.getJSONArray(i);
                        for(int j=0;j<17;j++){
                            a[i][j][0] = pm1.getJSONArray(j).getDouble(0);
                            a[i][j][1] = pm1.getJSONArray(j).getDouble(1);
                        }
                    }

                    for(int i=0;i<pose_input.length();i++){
                        JSONArray pm1 = pose_input.getJSONArray(i);
                        for(int j=0;j<17;j++){
                            b[i][j][0] = pm1.getJSONArray(j).getDouble(0);
                            b[i][j][1] = pm1.getJSONArray(j).getDouble(1);
                        }
                    }

                    phr.setPoints(a);
                    phl.setPoints(b);

                    JSONArray t = resJson[0].getJSONObject("evaluation").getJSONArray("score");
                    int r1 = 0;
                    for(int y=1;y<7;y++){
                        r1 = r1 + t.getJSONObject(y).getInt("value");
                    }
                    r1 /= 6;
                    if(r1 < t.getJSONObject(0).getInt("value")) r1 = t.getJSONObject(0).getInt("value");
                    cdg = new CompareDialog(LearnDanceActivity.this, ""+r1);
                    init_compare_video();
                    detailPlayer.startPlayLogic();
                    //hideProgressDialog();
                    Log.d("hjt.out.it", "2");
                } catch (JSONException e) {
                    Log.d("hjt.json.wwww", "wwww");
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(LearnDanceActivity.this,"出错啦！",Toast.LENGTH_LONG).show();
                finish();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            dialog.set_progress(Float.valueOf(values[0])/100f, values[1]);
        }


    }

    public class InitAllLearn extends MyAsyncTask<Integer, Void ,Boolean>{
        protected InitAllLearn(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if(aVoid) {
                init_learn_pager();
            }
            else
                Toast.makeText(LearnDanceActivity.this, "出错啦！", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            try {
                Log.i("whc_bid", ""+bid);
                JSONObject res_json = new JSONObject(learn_request.advanceGet(Constant.mInstance.work_url+"segment/"+bid+"/","Authorization",
                        GlobalVariable.mInstance.token));
                Log.i("whc_res_json", String.valueOf(res_json));
                if(!res_json.has("msg") || !res_json.getString("msg").equals("Success")) return false;
                JSONArray res_data_json = res_json.getJSONArray("data");
                all_learn_depose_video_num = res_data_json.length();
                urls_jsonarry = res_data_json;
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

    }

    public class PostRecord extends MyAsyncTask<Integer, Void, Integer[]>{
        protected PostRecord(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected void onPostExecute(Integer[] ints) {
            super.onPostExecute(ints);
            if(ints[0]==0){
                Toast.makeText(LearnDanceActivity.this, "出错啦！", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LearnDanceActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else{
                Log.i("ints[1]",""+ints[1]);
                Log.i("ints[0]", ""+ints[0]);
                if(ints[1]==2){
                    Log.i("goto","gotogoto");
                    if(is_normal==1)
                        go_to_next_segment();
                }  else {
                    try {
                        detailPlayer.setUp(all_learn_video.get(current_video_number), true, urls_jsonarry.getJSONObject(current_video_number).getString("name"));
                        detailPlayer.getCurrentPlayer().startPlayLogic();
                        if(is_normal==0) {
                            try {
                                repeat(nnv, nne, nnm, nni);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected Integer[] doInBackground(Integer... integers) {
            try {
                Log.i("whc_status", integers[2] == 1 ? "开始segment" : "结束segment");
                String[] rjs = {"learning", "int", "" + integers[0], "segment", "int", "" + integers[1], "status", "int", "" + integers[2]};
                Log.i("whc_rjs", GenerateJson.universeJson2(rjs));
                JSONObject rjsr = new JSONObject(learn_request.advancePost(GenerateJson.universeJson2(rjs), Constant.mInstance.learn_url + "record/", "Authorization",
                        GlobalVariable.mInstance.token));
                Log.e("whc_LP", String.valueOf(rjsr));
                if (rjsr.getString("msg").equals("Success")) {
                    Log.e("rjsr", String.valueOf(rjsr));
                    if (integers[2] == 1) {
                        cur_rid = rjsr.getJSONObject("data").getInt("rid");
                    }
                    Integer[] cur_input = {1, integers[2]};
                    return cur_input;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Integer[] cur_input1 = {0};
            return cur_input1;
        }
    }

    private void init_pose_view(){
        phl = new PoseHuman(this);
        phr = new PoseHuman(this);
        RelativeLayout.LayoutParams phpl = new RelativeLayout.LayoutParams(600, 600);
        phpl.addRule(RelativeLayout.CENTER_VERTICAL);
        phpl.addRule(RelativeLayout.ALIGN_LEFT,R.id.centerTextView);
        RelativeLayout.LayoutParams phpr = new RelativeLayout.LayoutParams(600, 600);
        phpr.addRule(RelativeLayout.CENTER_VERTICAL);
        phpr.addRule(RelativeLayout.ALIGN_RIGHT,R.id.centerTextView);
        phl.setLayoutParams(phpl);
        phl.setBackgroundColor(Color.GRAY);
        phl.setAlpha(0.75f);
        activityDetailPlayer.addView(phl);
        phr.setLayoutParams(phpr);
        phr.setBackgroundColor(Color.GRAY);
        phr.setAlpha(0.75f);
        pose_status = false;
        phl.setVisibility(GONE);
        phr.setVisibility(GONE);
        wrongShow = new WrongShow(LearnDanceActivity.this);
        wrongShow.setLayoutParams(fill_tiny);
        activityDetailPlayer.addView(wrongShow);
        activityDetailPlayer.addView(phr);
    }

    public class PoseHuman extends View {
        private static final int ALPHA = 255;
        private Paint mInnerPaintRed, mInnerPaintBlue, mInnerPaintPur,
                pointsPaintRed,pointsPaintBlue,pointsPaintPur;
        public int width;
        public int height;
        private double[][][] raw_points;
        private List<List<Integer>> points = new ArrayList<>();
        private float total_time;
        private boolean isnull;
        private boolean is_playing = false;
        //private double cur_time = 0;

        public PoseHuman(Context context) {
            super(context);
            mInnerPaintRed = new Paint();
            mInnerPaintRed.setARGB(ALPHA, 255, 0, 0);
            mInnerPaintRed.setAntiAlias(true);
            mInnerPaintRed.setStrokeWidth(8f);

            mInnerPaintBlue = new Paint();
            mInnerPaintBlue.setARGB(ALPHA, 0, 0, 255);
            mInnerPaintBlue.setAntiAlias(true);
            mInnerPaintBlue.setStrokeWidth(8f);

            mInnerPaintPur = new Paint();
            mInnerPaintPur.setARGB(ALPHA, 255, 0, 255);
            mInnerPaintPur.setAntiAlias(true);
            mInnerPaintPur.setStrokeWidth(8f);

            pointsPaintRed = new Paint();
            pointsPaintRed.setARGB(ALPHA, 255, 0, 0);
            pointsPaintRed.setAntiAlias(true);
            pointsPaintRed.setStrokeWidth(10f);

            pointsPaintBlue = new Paint();
            pointsPaintBlue.setARGB(ALPHA, 0, 0, 255);
            pointsPaintBlue.setAntiAlias(true);
            pointsPaintBlue.setStrokeWidth(10f);

            pointsPaintPur = new Paint();
            pointsPaintPur.setARGB(ALPHA, 255, 0, 255);
            pointsPaintPur.setAntiAlias(true);
            pointsPaintPur.setStrokeWidth(10f);
            System.out.println("绘图初始化");
        }

        private double get_cur_time(){
            return (double)detailPlayer.getGSYVideoManager().getCurrentPosition()/(double)1000;
        }

        public void stop_now(){
            is_playing=false;
            this.setVisibility(GONE);
        }

        public void start_now(){
            is_playing=true;
            this.setVisibility(VISIBLE);
            postInvalidate();
        }

        public void getPoints() {
            double now_time = get_cur_time();
            if(now_time==-1) {
                stop_now();
            } else{
                Log.i("whc_total",""+now_time+" "+total_time);
                points = new ArrayList<>();
                if(now_time+0.25f<total_time) {
                    Log.i("whc_pos", "y");
                    int ii = (int) (now_time / 0.25f);
                    double wei_a = ((ii + 1) * 0.25f - now_time) / 0.25f;
                    double wei_b = (now_time - ii * 0.25f) / 0.25f;
                    for (int i = 0; i < 17; i++) {
                        List<Integer> cu = new ArrayList<>();
                        if(ii < raw_points.length && raw_points[ii][i][0]>=0&&raw_points[ii+1][i][0]>=0)
                            cu.add((int) (wei_a * raw_points[ii][i][0]*500 + wei_b * raw_points[ii + 1][i][0]*500 + 50));
                        else
                            cu.add(-1);
                        if(ii < raw_points.length && raw_points[ii][i][1]>=0&&raw_points[ii+1][i][1]>=0)
                            cu.add((int) (wei_a * raw_points[ii][i][1]*500 + wei_b * raw_points[ii + 1][i][1]*500 + 50));
                        else
                            cu.add(-1);
                        //Log.i("points",String.valueOf(cu));
                        points.add(cu);
                    }
                    Log.i("points",String.valueOf(points.get(0)));
                }
                else{
                    Log.i("whc_pos", "n");
                    stop_now();
                }
            }
        }

        public void setPoints(double[][][] d){
            this.raw_points = new double[d.length][17][2];
            System.arraycopy(d,0,this.raw_points,0,d.length);
            this.total_time = this.raw_points.length*0.25f-0.25f;
            Log.i("whc_raw_pol",""+raw_points.length);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            width = getMeasuredWidth();
            height = getMeasuredHeight();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            getPoints();
            if(is_playing) {
                if(points.get(0).get(0)!=-1)
                    canvas.drawCircle(points.get(0).get(0), points.get(0).get(1),15, pointsPaintPur);
                for(int i=1;i<17;i++){
                    if(points.get(i).get(0)!=-1 && points.get(i).get(1)!=-1)
                        canvas.drawCircle(points.get(i).get(0),points.get(i).get(1),15, i%2==1?pointsPaintRed:pointsPaintBlue);
                }
                Log.i("whc_points", ""+points.get(1).get(0)+" "+points.get(1).get(1));
                if(points.get(0).get(0)!=-1&&points.get(0).get(1)!=-1&&points.get(1).get(0)!=-1&&points.get(1).get(1)!=-1)canvas.drawLine(points.get(0).get(0), points.get(0).get(1), points.get(1).get(0), points.get(1).get(1), mInnerPaintRed);
                if(points.get(0).get(0)!=-1&&points.get(0).get(1)!=-1&&points.get(2).get(0)!=-1&&points.get(2).get(1)!=-1)canvas.drawLine(points.get(0).get(0), points.get(0).get(1), points.get(2).get(0), points.get(2).get(1), mInnerPaintBlue);
                if(points.get(0).get(0)!=-1&&points.get(0).get(1)!=-1&&points.get(5).get(0)!=-1&&points.get(5).get(1)!=-1)canvas.drawLine(points.get(0).get(0), points.get(0).get(1), points.get(5).get(0), points.get(5).get(1), mInnerPaintRed);
                if(points.get(0).get(0)!=-1&&points.get(0).get(1)!=-1&&points.get(6).get(0)!=-1&&points.get(6).get(1)!=-1)canvas.drawLine(points.get(0).get(0), points.get(0).get(1), points.get(6).get(0), points.get(6).get(1), mInnerPaintBlue);
                if(points.get(1).get(0)!=-1&&points.get(1).get(1)!=-1&&points.get(3).get(0)!=-1&&points.get(3).get(1)!=-1)canvas.drawLine(points.get(1).get(0), points.get(1).get(1), points.get(3).get(0), points.get(3).get(1), mInnerPaintRed);
                if(points.get(2).get(0)!=-1&&points.get(2).get(1)!=-1&&points.get(4).get(0)!=-1&&points.get(4).get(1)!=-1)canvas.drawLine(points.get(2).get(0), points.get(2).get(1), points.get(4).get(0), points.get(4).get(1), mInnerPaintBlue);
                if(points.get(5).get(0)!=-1&&points.get(5).get(1)!=-1&&points.get(6).get(0)!=-1&&points.get(6).get(1)!=-1)canvas.drawLine(points.get(5).get(0), points.get(5).get(1), points.get(6).get(0), points.get(6).get(1), mInnerPaintPur);
                if(points.get(5).get(0)!=-1&&points.get(5).get(1)!=-1&&points.get(7).get(0)!=-1&&points.get(7).get(1)!=-1)canvas.drawLine(points.get(5).get(0), points.get(5).get(1), points.get(7).get(0), points.get(7).get(1), mInnerPaintRed);
                if(points.get(5).get(0)!=-1&&points.get(5).get(1)!=-1&&points.get(11).get(0)!=-1&&points.get(11).get(1)!=-1)canvas.drawLine(points.get(5).get(0), points.get(5).get(1), points.get(11).get(0), points.get(11).get(1), mInnerPaintRed);
                if(points.get(6).get(0)!=-1&&points.get(6).get(1)!=-1&&points.get(8).get(0)!=-1&&points.get(8).get(1)!=-1)canvas.drawLine(points.get(6).get(0), points.get(6).get(1), points.get(8).get(0), points.get(8).get(1), mInnerPaintBlue);
                if(points.get(6).get(0)!=-1&&points.get(6).get(1)!=-1&&points.get(12).get(0)!=-1&&points.get(12).get(1)!=-1)canvas.drawLine(points.get(6).get(0), points.get(6).get(1), points.get(12).get(0), points.get(12).get(1), mInnerPaintBlue);
                if(points.get(7).get(0)!=-1&&points.get(7).get(1)!=-1&&points.get(9).get(0)!=-1&&points.get(9).get(1)!=-1)canvas.drawLine(points.get(7).get(0), points.get(7).get(1), points.get(9).get(0), points.get(9).get(1), mInnerPaintRed);
                if(points.get(8).get(0)!=-1&&points.get(8).get(1)!=-1&&points.get(10).get(0)!=-1&&points.get(10).get(1)!=-1)canvas.drawLine(points.get(8).get(0), points.get(8).get(1), points.get(10).get(0), points.get(10).get(1), mInnerPaintBlue);
                if(points.get(11).get(0)!=-1&&points.get(11).get(1)!=-1&&points.get(12).get(0)!=-1&&points.get(12).get(1)!=-1)canvas.drawLine(points.get(11).get(0), points.get(11).get(1), points.get(12).get(0), points.get(12).get(1), mInnerPaintPur);
                if(points.get(11).get(0)!=-1&&points.get(11).get(1)!=-1&&points.get(13).get(0)!=-1&&points.get(13).get(1)!=-1)canvas.drawLine(points.get(11).get(0), points.get(11).get(1), points.get(13).get(0), points.get(13).get(1), mInnerPaintRed);
                if(points.get(12).get(0)!=-1&&points.get(12).get(1)!=-1&&points.get(14).get(0)!=-1&&points.get(14).get(1)!=-1)canvas.drawLine(points.get(12).get(0), points.get(12).get(1), points.get(14).get(0), points.get(14).get(1), mInnerPaintBlue);
                if(points.get(13).get(0)!=-1&&points.get(13).get(1)!=-1&&points.get(15).get(0)!=-1&&points.get(15).get(1)!=-1)canvas.drawLine(points.get(13).get(0), points.get(13).get(1), points.get(15).get(0), points.get(15).get(1), mInnerPaintRed);
                if(points.get(14).get(0)!=-1&&points.get(14).get(1)!=-1&&points.get(16).get(0)!=-1&&points.get(16).get(1)!=-1)canvas.drawLine(points.get(14).get(0), points.get(14).get(1), points.get(16).get(0), points.get(16).get(1), mInnerPaintBlue);
                postInvalidate();
            }
        }

    }

    private void show_wrong(){
        long currentPosition = detailPlayer.getGSYVideoManager().getCurrentPosition();
        if (is_compare) {
                Log.d("hjt.in.play", "ok");
                for (int k = 0; k < expressions_sad.size(); k++) {
                    if (currentPosition >= expressions_sad.get(k) * 1000 - 500 && currentPosition <= expressions_sad.get(k) * 1000 + 500) {
                        smile_word.setText("请保持微笑呀！");
                        break;
                    } else {
                        smile_word.setText("");
                    }
                }

                for (int i = 0; i < wrong_time.size(); i++) {
                    if (currentPosition > wrong_time.get(i).get(0) - 10 && currentPosition < wrong_time.get(i).get(0) + wrong_time.get(i).get(1)) {
                        if (detailPlayer.getSpeed() != 0.5f) {
                            Log.i("whc_change", "0.5f");
                            detailPlayer.getMspeed().setText("0.5倍速");
                            detailPlayer.getCurrentPlayer().setSpeedPlaying(0.5f, true);
                        }
                            Log.d("hjt.in.play", "change.it");

                            for (int k = 0; k < 2; k++) {
                                if (wrong_id.get(i).get(k)) {
                                    wrong_kuang[k].setBackgroundResource(R.color.light_color);
                                    human_icons[k].setColorFilter(Color.parseColor("#FF5C5C"));
                                } else {
                                    wrong_kuang[k].setBackgroundResource(R.color.dark_color);
                                    human_icons[k].setColorFilter(Color.parseColor("#aaaaaa"));
                                }
                            }
                            for (int k = 2; k < 6; k++) {
                                if (wrong_id.get(i).get(k)) {
                                    wrong_kuang[(k - 2) * 2 + 2].setBackgroundResource(R.color.light_color);
                                    wrong_kuang[(k - 2) * 2 + 1 + 2].setBackgroundResource(R.color.light_color);
                                    human_icons[k].setColorFilter(Color.parseColor("#FF5C5C"));
                                } else {
                                    wrong_kuang[(k - 2) * 2 + 2].setBackgroundResource(R.color.dark_color);
                                    wrong_kuang[(k - 2) * 2 + 1 + 2].setBackgroundResource(R.color.dark_color);
                                    human_icons[k].setColorFilter(Color.parseColor("#aaaaaa"));
                                }
                            }
                        break;
                    } else {
                        for (int k = 0; k < 10; k++)
                            wrong_kuang[k].setBackgroundResource(R.color.dark_color);
                        for (int k = 0; k < 6; k++)
                            human_icons[k].setColorFilter(Color.parseColor("#aaaaaa"));
                        if (detailPlayer.getSpeed() == 0.5f) {
                            Log.i("whc_change", "1f");
                            detailPlayer.getMspeed().setText("1倍速");
                            detailPlayer.getCurrentPlayer().setSpeedPlaying(1f, true);
                        }
                    }
                }
        }
    }

    public class WrongShow extends View {

        private boolean iscon;

        public WrongShow(Context context) {
            super(context);
            iscon = false;
        }

        public void start_show(){
            iscon = true;
            postInvalidate();
        }

        public void stop_show(){
            iscon = false;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if(iscon) {
                Log.i("whc_wrong_show", "y");
                show_wrong();
                postInvalidate();
            }
        }

    }

}