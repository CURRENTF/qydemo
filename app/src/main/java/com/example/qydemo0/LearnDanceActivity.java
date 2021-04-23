package com.example.qydemo0;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.telephony.mbms.MbmsErrors;
import android.text.BoringLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.QYFile;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.SampleVideo;
import com.example.qydemo0.QYpack.SwitchVideoModel;
import com.example.qydemo0.QYpack.VideoClip;
import com.google.gson.JsonArray;
import com.example.qydemo0.Widget.Dashboard;
import com.example.qydemo0.entry.Image;
import com.google.gson.JsonObject;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.koushikdutta.ion.ImageViewBitmapInfo;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import moe.codeest.enviews.ENDownloadView;
import moe.codeest.enviews.ENPlayView;

import static android.view.View.GONE;
import static com.example.qydemo0.QYpack.VideoClip.getFromTime;

import com.example.qydemo0.AiUnit.FaceFer;

/**
 * sampleVideo支持全屏与非全屏切换的清晰度，旋转，镜像等功能.
 * Activity可以继承GSYBaseActivityDetail实现详情模式的页面
 * 或者参考DetailPlayer、DetailListPlayer实现
 * <p>
 * Created by guoshuyu on 2017/6/18.
 */

public class LearnDanceActivity extends Activity implements SurfaceHolder.Callback{

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
    String path_cur;
    private Boolean is_record;
    private Boolean is_compare;
    private List<List<Long>> wrong_time = new ArrayList<>();
    private List<List<Boolean>> wrong_id = new ArrayList<>();
    private int all_learn_depose_video_num = 0;
    private int cur_compare_id = 0;
    //private ProgressDialog progressDialog;
    private ProgressDialog dialog;
    SeekBar cur_process;
    List<Integer> opt = new ArrayList();
    private int learning_id = -1;
    private int segment_id = -1;
    RelativeLayout menu_op;
    ImageView arrow;
    private int cur_rid = -1;
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

    private int wid;

    RelativeLayout.LayoutParams people_all = new RelativeLayout.LayoutParams(350, 910);
    RelativeLayout.LayoutParams people_tiny = new RelativeLayout.LayoutParams(1,1);
    private JSONArray urls_jsonarry = new JSONArray();

    private QYrequest learn_request = new QYrequest();
    private  QYFile learn_file = new QYFile();

    boolean mirror_status = false;

    private ImageView[] wrong_kuang = new ImageView[10];

    private CVUnitClient mCVClient;
    private int startCode = -1;

    private TextView smile_word;

    private int cur_h = 480;

    private RelativeLayout.LayoutParams fill_tiny;

    private ImageView black_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setWindow();
        setContentView(R.layout.activity_learn_dance);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        smile_word = (TextView) findViewById(R.id.smile_word);

        dialog = new ProgressDialog(LearnDanceActivity.this);
        dialog.setCancelable(false);// 让dialog不能失去焦点，一直在最上层显示


        ArrayList<String> list = getIntent().getStringArrayListExtra("params");

        //学习项目id
        learning_id = Integer.valueOf(list.get(0));
        //work id
        wid = Integer.valueOf(list.get(1));
        //开始位置
        current_video_number = Integer.valueOf(list.get(2));

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
                int startCode = mCVClient.start();
                if(startCode==0){

                }
                else{
                    Log.i("whc123","init wrong!");
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
        new InitAllLearn().execute(wid);

    }

    private String getFer(Bitmap bitmap){
        String res = "";
        FaceInputSlot inputSlot = (FaceInputSlot) mCVClient.createInputSlot();
        inputSlot.setTargetBitmap(bitmap);
        FaceOutputSlot outputSlot = (FaceOutputSlot) mCVClient.createOutputSlot();
        mCVClient.process(inputSlot, outputSlot);
        FaceResultList faceList = outputSlot.getFaceList();
        List<FaceResult> faceResultList = new ArrayList<>();
        faceResultList = faceList.getFaceResultList();
        for (FaceResult faceResult: faceResultList) {
            res = faceResult.getExpression();
        }
        return res;
    }

    private void init_wrong_kuang(){
        wrong_kuang[0] = (ImageView) findViewById(R.id.head);
        wrong_kuang[1] = (ImageView) findViewById(R.id.body);
        wrong_kuang[2] = (ImageView) findViewById(R.id.left_hand);
        wrong_kuang[3] = (ImageView) findViewById(R.id.left_hand_2);
        wrong_kuang[4] = (ImageView) findViewById(R.id.right_hand);
        wrong_kuang[5] = (ImageView) findViewById(R.id.right_hand_2);
        wrong_kuang[6] = (ImageView) findViewById(R.id.left_leg);
        wrong_kuang[7] = (ImageView) findViewById(R.id.left_leg_2);
        wrong_kuang[8] = (ImageView) findViewById(R.id.right_leg);
        wrong_kuang[9] = (ImageView) findViewById(R.id.right_leg_2);
    }

    private void init_learn_pager(){
        try {
            new PostRecord().execute(learning_id, urls_jsonarry.getJSONObject(current_video_number).getInt("id"), 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

        try {
            detailPlayer.setUp(all_learn_video.get(current_video_number), true, urls_jsonarry.getJSONObject(current_video_number).getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

                if(is_learn && !is_compare){
                    detailPlayer.getCurrentPlayer().setIsTouchWiget(false);
                    detailPlayer.getCurrentPlayer().setIsTouchWigetFull(false);
                    Toast.makeText(getBaseContext(),"你有10秒钟的时间到达录制位置",Toast.LENGTH_SHORT).show();
                    AudioPlayer audioPlayer = null;
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
        });

        people_all.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        people_all.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        detailPlayer.setGSYVideoProgressListener(new GSYVideoProgressListener() {
            @Override
            public void onProgress(int progress, int secProgress, int currentPosition, int duration) {
                if(is_compare){
                    Log.d("hjt.in.play", "ok");
                    for(int k=0; k<expressions_sad.size(); k++){
                        if(currentPosition >= expressions_sad.get(k)*1000-500 && currentPosition <= expressions_sad.get(k)*1000+500){
                            smile_word.setText("请保持微笑呀！");
                            break;
                        }
                        else{
                            smile_word.setText("");
                        }
                    }

                    for(int i=0;i<wrong_time.size();i++) {
                        if (currentPosition > wrong_time.get(i).get(0)-500 && currentPosition < wrong_time.get(i).get(0) + wrong_time.get(i).get(1)){
                            if(detailPlayer.getSpeed()!=0.25f){
                                detailPlayer.getMspeed().setText("0.25倍速");
                                detailPlayer.getCurrentPlayer().setSpeedPlaying(0.25f, true);
                                Log.d("hjt.in.play", "change.it");

                                for(int k = 0; k < 2; k++){
                                    if(wrong_id.get(i).get(k)){
                                        wrong_kuang[k].setBackgroundResource(R.color.light_color);
                                    }
                                    else{
                                        wrong_kuang[k].setBackgroundResource(R.color.dark_color);
                                    }
                                }
                                for(int k = 2; k < 6; k++){
                                    if(wrong_id.get(i).get(k)){
                                        wrong_kuang[(k-2)*2+2].setBackgroundResource(R.color.light_color);
                                        wrong_kuang[(k-2)*2+1+2].setBackgroundResource(R.color.light_color);
                                    }
                                    else{
                                        wrong_kuang[(k-2)*2+2].setBackgroundResource(R.color.dark_color);
                                        wrong_kuang[(k-2)*2+1+2].setBackgroundResource(R.color.dark_color);
                                    }
                                }
                            }
                            break;
                        }
                        else{
                            for(int k=0;k<10;k++)
                                wrong_kuang[k].setBackgroundResource(R.color.dark_color);
                            if(detailPlayer.getSpeed()==0.25f){
                                detailPlayer.getMspeed().setText("1倍速");
                                detailPlayer.getCurrentPlayer().setSpeedPlaying(1f, true);
                            }
                        }
                    }
                }
            }
        });

        detailPlayer.getCurrentPlayer().startPlayLogic();

        RelativeLayout.LayoutParams fill_all_r = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int heightPixels = dm.heightPixels;
        RelativeLayout.LayoutParams fill_all = new RelativeLayout.LayoutParams((int) heightPixels*1280/720, heightPixels);
        fill_all.addRule(RelativeLayout.CENTER_HORIZONTAL);

        black_back = (ImageView) findViewById(R.id.black_back);
        fill_tiny = new RelativeLayout.LayoutParams(1,1);

        btn1 = (ImageView) findViewById(R.id.mirror_btn);
        btn2 = (ImageView) findViewById(R.id.next_video);
        btn3 = (ImageView) findViewById(R.id.learn_now);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_compare) {
                    if (!mirror_status) {
//                        btn1.setText("恢复");
                        mirror_status = true;
                        mSurfaceView.setLayoutParams(fill_all);
                        black_back.setLayoutParams(fill_all_r);
                    } else {
//                        btn1.setText("镜子");
                        mirror_status = false;
                        mSurfaceView.setLayoutParams(fill_tiny);
                        black_back.setLayoutParams(fill_tiny);
                    }
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!is_learn) {
                        new PostRecord().execute(learning_id, urls_jsonarry.getJSONObject(current_video_number).getInt("id"),2);
                    } else if (is_compare) {
                        stop_compare_video();
                        detailPlayer.setUp(all_learn_video.get(current_video_number), true, urls_jsonarry.getJSONObject(current_video_number).getString("name"));
                        detailPlayer.startPlayLogic();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_compare) {
                    if (!is_learn) {
                        is_learn = true;
                        init_learn_view();
                        detailPlayer.startPlayLogic();
                    }
                }
            }
        });

        menu_op = findViewById(R.id.expand_menu);
        arrow = findViewById(R.id.menu_btn);
        shrink_menu_now();
    }


    void shrink_menu_now(){
        arrow.setImageResource(R.drawable.ic_down_arrow2);
        menu_op.setTranslationY(-DeviceInfo.dip2px(LearnDanceActivity.this, 253));

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
//        btn1.setVisibility(View.VISIBLE);
//        btn2.setVisibility(View.VISIBLE);
//        btn3.setVisibility(View.VISIBLE);
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
        btn2.setScaleX(1);
        is_compare = false;
        is_learn = false;
    }

    private void init_learn_view(){
        cur_process.setVisibility(GONE);
        changeSpeed.setVisibility(GONE);
        detailPlayer.setIs_double(false);
    }

    private void reset_learn_view(){
        cur_process.setVisibility(View.VISIBLE);
        changeSpeed.setVisibility(View.VISIBLE);
        detailPlayer.setIs_double(true);
    }

    private void go_to_next_segment(){
        try {
        new PostRecord().execute(learning_id, urls_jsonarry.getJSONObject(current_video_number).getInt("id"), 1);
        current_video_number++;
        if (current_video_number >= all_learn_depose_video_num) {
            Toast.makeText(LearnDanceActivity.this, "恭喜您！您已学会整支舞蹈", Toast.LENGTH_LONG);
            Intent intent = new Intent(LearnDanceActivity.this, MainActivity.class);
            startActivity(intent);
        }
            detailPlayer.setUp(all_learn_video.get(current_video_number), true, urls_jsonarry.getJSONObject(current_video_number).getString("name"));
            loadFirstFrameCover(all_learn_video.get(current_video_number).get(0).getUrl());
            detailPlayer.getCurrentPlayer().startPlayLogic();
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
    }

    private static Point findBestPreviewSizeValue(List<Camera.Size> sizeList){
        int bestX = 0;
        int bestY = 0;
        int size = 0;
        for (Camera.Size nowSize : sizeList){
            int newX = nowSize.width;
            int newY = nowSize.height;
            int newSize = Math.abs(newX * newX) + Math.abs(newY * newY);
            float ratio = (float) (newY * 1.0 / newX);
            if(newSize >= size && ratio != 0.75){//确保图片是16:9
                bestX  = newX;
                bestY = newY;
                size = newSize;
            }else if(newSize < size){
                continue;
            }
        }
        if(bestX > 0 && bestY > 0){
            return new Point(bestX,bestY);
        }
        return null;

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

    public static void loadCover(ImageView imageView, String url, Context context) {

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context)
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .frame(4000000)
                                .centerCrop()
                )
                .load(url)
                .into(imageView);

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
    public void loadFirstFrameCover(String url) {
        //可以参考Glide，内部也是封装了MediaMetadataRetriever
        Glide.with(this.getApplicationContext())
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .frame(1000000)
                                .centerCrop()
                                .error(R.drawable.logo)
                                .placeholder(R.drawable.logo))
                .load(url)
                .into(coverImageView);
    }

    public MediaMetadataRetriever getMediaMetadataRetriever(String url) {
        if (mCoverMedia == null) {
            mCoverMedia = new MediaMetadataRetriever();
        }
        mCoverMedia.setDataSource(url, new HashMap<String, String>());
        return mCoverMedia;
    }

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
            int cur_w = (int) (((float)(cur_h))/480.0f*640.0f);
            mSurfaceHolder.setFixedSize(cur_w, cur_h);//最高只能设置640x480
            mRecorder.setVideoSize(cur_w, cur_h);//最高只能设置640x480
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
            new SendUserDanceVideo().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        isRecording = false;
    }

    private void init_compare_video(){
        is_compare = true;
        reset_learn_view();
        btn2.setScaleX(-1);
        if(mirror_status){
            mirror_status = false;
            mSurfaceView.setLayoutParams(fill_tiny);
            black_back.setLayoutParams(fill_tiny);
        }
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
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
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
/*
    public void showProgressDialog(String title, String message) {
        if (progressDialog == null) {

            progressDialog = ProgressDialog.show(LearnDanceActivity.this, title,
                    message, true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }

        progressDialog.show();

    }
*/
    /*
     * 隐藏提示加载
     */
 /*   public void hideProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }
  */

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

    public class SendUserDanceVideo extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("提示");
            dialog.setMessage("正在努力生成对比视频。。。");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... video_path) {


            List<Bitmap> bitmaps = VideoClip.getFromTime(path_cur);
            for(int k=0;k<10;k++) {
                try {
                    Thread.sleep(500);
                    if(startCode==0) {
                        for (int i = 0; i < bitmaps.size(); i++) {
                            if (getFer(bitmaps.get(i)).equals("Sad")) {
                                expressions_sad.add(i);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Log.i("expression_res", String.valueOf(expressions_sad));

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

                String tid = res_json.getJSONObject("data").getString("tid");
                if(tid==null) return null;
                for(int i=0;i<20;i++){
                    Thread.sleep(1000);
                    JSONObject task_res = new JSONObject(learn_request.advanceGet(Constant.mInstance.task_url+"schedule/"+tid+"/",
                            "Authorization",GlobalVariable.mInstance.token));
                    Log.i("whc_233", String.valueOf(task_res));
                    String cur_schedule = task_res.getJSONObject("data").getString("schedule");
                    publishProgress(Integer.valueOf(cur_schedule.substring(0, cur_schedule.length()-1)));
                    if(cur_schedule.equals("100%") && task_res.getJSONObject("data").getJSONObject("data").getJSONObject("video_url").getJSONObject("url").has("自动")){
                        return task_res.getJSONObject("data").getJSONObject("data");
                    }
                }
                return null;
            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject resJson) {
            //hideProgressDialog();
            dialog.dismiss();
            if(resJson != null){
                try {
                    Log.d("hjt.return.msg.player", resJson.toString());
                    File file1 = new File(path_cur);
                    file1.delete();
                    detailPlayer.setUp(resJson.getJSONObject("video_url").getJSONObject("url").getString("自动"), true, "对比视频");
                    wrong_time.clear();
                    JSONArray wrong_time_json = resJson.getJSONObject("evaluation").getJSONArray("error");
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
                Toast.makeText(LearnDanceActivity.this,"出错啦！",Toast.LENGTH_LONG);
                Intent intent = new Intent(LearnDanceActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setProgress(values[0]);
        }


    }

    public class InitAllLearn extends AsyncTask<Integer, Void ,Boolean>{
        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if(aVoid)
                init_learn_pager();
            else
                Toast.makeText(LearnDanceActivity.this, "出错啦！", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            try {
                JSONObject res_json = new JSONObject(learn_request.advanceGet(Constant.mInstance.work_url+"breakdown/"+integers[0]+"/","Authorization",
                        GlobalVariable.mInstance.token));
                if(!res_json.has("msg") || !res_json.getString("msg").equals("Success")) return null;
                JSONObject res_data_json = res_json.getJSONObject("data");
                all_learn_depose_video_num = res_data_json.getInt("segment_num");
                urls_jsonarry = res_data_json.getJSONArray("segment_info");
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

    }

    public class PostRecord extends AsyncTask<Integer, Void, Integer[]>{
        @Override
        protected void onPostExecute(Integer[] ints) {
            super.onPostExecute(ints);
            Log.i("ints[1]",""+ints[1]);
            Log.i("ints[0]", ""+ints[0]);
            if(ints[0]==0){
                Toast.makeText(LearnDanceActivity.this, "出错啦！", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LearnDanceActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else{
                if(ints[1]==2){
                    Log.i("goto","gotogoto");
                    go_to_next_segment();
                }
            }
        }

        @Override
        protected Integer[] doInBackground(Integer... integers) {
            try {
            String[] rjs = {"learning", "int", ""+integers[0], "segment", "int", ""+integers[1], "status", "int", ""+integers[2]};
                JSONObject rjsr = new JSONObject(learn_request.advancePost(GenerateJson.universeJson2(rjs), Constant.mInstance.learn_url + "record/", "Authorization",
                        GlobalVariable.mInstance.token));
                Log.e("LearnDancePost", String.valueOf(rjsr));
                if(rjsr.getString("msg").equals("Success")){
                    Log.e("rjsr", String.valueOf(rjsr));
                    if(integers[2]==1){
                        cur_rid = rjsr.getJSONObject("data").getInt("rid");
                    }
                    Integer[] cur_input = {1,integers[2]};
                    return cur_input;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Integer[] cur_input1 = {0};
            return cur_input1;
        }
    }

}