package com.example.qydemo0;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.qydemo0.QYpack.AudioPlayer;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.QYFile;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.SampleVideo;
import com.example.qydemo0.QYpack.SwitchVideoModel;
import com.google.gson.JsonObject;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import org.json.JSONException;
import org.json.JSONObject;

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
    private int[] wrong_time = {2700, 6900, 12000};
    private int[] wrong_id = {0, 1, 2};
    private int cur_compare_id = 0;
    private ProgressDialog progressDialog;
    ImageView people_img;
    SeekBar cur_process;
    List<Integer> opt = new ArrayList();

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

    private Button btn1,btn2,btn3;

    private ImageView coverImageView, fullScreenView;
    ENPlayView startVideo;
    ENDownloadView downloadView;

    private TextView changeSpeed;

    RelativeLayout.LayoutParams people_all = new RelativeLayout.LayoutParams(350, 910);
    RelativeLayout.LayoutParams people_tiny = new RelativeLayout.LayoutParams(1,1);

    private QYrequest learn_request = new QYrequest();
    private  QYFile learn_file = new QYFile();

    public LearnDanceActivity() throws IOException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setWindow();
        setContentView(R.layout.activity_learn_dance);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initViews();
        ButterKnife.bind(this);

        initLearnVideo();
        Log.i("hash",learn_file.hashFileUrl("/storage/emulated/0/Android/data/com.example.qydemo0/cache/videos/1617625252036.mp4"));
                opt.add(R.drawable.l0);
        opt.add(R.drawable.l1);

        is_learn = false;

        is_record = false;

        is_compare = false;

        cur_process = (SeekBar) findViewById(R.id.progress);

        fullScreenView = (ImageView) findViewById(R.id.fullscreen);
        fullScreenView.setVisibility(GONE);

        startVideo = (ENPlayView) findViewById(R.id.start);

        downloadView = (ENDownloadView) findViewById(R.id.loading);

        changeSpeed = (TextView) findViewById(R.id.change_speed);

        detailPlayer.setUp(all_learn_video.get(0), true, "韩国小姐姐的舞蹈视频");

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
                        audioPlayer = new AudioPlayer("https://downsc.chinaz.net/Files/DownLoad/sound1/202007/13182.mp3");
                        audioPlayer.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                System.out.println("hereherherhreh");
                                startRecord();
                                detailPlayer.onVideoResume();
                            }
                        });
                        audioPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                //new SleepNowThenPlay().execute();
                }
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
                if(is_learn && !is_compare){
                stopRecord();
                if((new File(path_cur)).isFile()) {
                        System.out.println("Oh yeah yes");
                    }
                }
            }

            @Override
            public void onClickStartError(String url, Object... objects) {
                super.onClickStartError(url, objects);
            }
        });

        people_img = (ImageView) findViewById(R.id.people_img);
        people_img.setScaleType(ImageView.ScaleType.FIT_XY);
        people_img.setLayoutParams(people_tiny);
        people_all.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        people_all.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        detailPlayer.setGSYVideoProgressListener(new GSYVideoProgressListener() {
            @Override
            public void onProgress(int progress, int secProgress, int currentPosition, int duration) {
                if(is_compare){
                    people_img.setLayoutParams(people_all);
                    for(int i=0;i<wrong_time.length;i++) {
                        if (currentPosition > wrong_time[i]-500 && currentPosition < wrong_time[i]+500 ){
                            if(detailPlayer.getSpeed()!=0.25f){
                                detailPlayer.getMspeed().setText("0.25倍速");
                                detailPlayer.getCurrentPlayer().setSpeedPlaying(0.25f, true);
                                people_img.setImageResource(opt.get(1));
                            }
                            break;
                        }
                        else{
                            if(detailPlayer.getSpeed()==0.25f){
                                detailPlayer.getMspeed().setText("1倍速");
                                detailPlayer.getCurrentPlayer().setSpeedPlaying(1f, true);
                                people_img.setImageResource(opt.get(0));
                            }
                        }
                    }
                }
            }
        });

        detailPlayer.getCurrentPlayer().startPlayLogic();

        SurfaceView surf = findViewById(R.id.sf_view);

        RelativeLayout.LayoutParams fill_all = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        RelativeLayout.LayoutParams fill_tiny = new RelativeLayout.LayoutParams(1,1);

        btn1 = (Button) findViewById(R.id.mirror_btn);
        btn2 = (Button) findViewById(R.id.next_video);
        btn3 = (Button) findViewById(R.id.learn_now);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_compare) {
                    if (btn1.getText().equals("镜子")) {
                        btn1.setText("恢复");
                        surf.setLayoutParams(fill_all);
                    } else {
                        btn1.setText("镜子");
                        surf.setLayoutParams(fill_tiny);
                    }
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_learn) {
                    current_video_number++;
                    if (current_video_number >= all_num) {
                        current_video_number = 0;
                    }
                    detailPlayer.setUp(all_learn_video.get(current_video_number), true, "韩国小姐姐的舞蹈视频");
                    loadFirstFrameCover(all_learn_video.get(current_video_number).get(0).getUrl());
                    detailPlayer.getCurrentPlayer().startPlayLogic();
                }else if(is_compare){
                    stop_compare_video();
                    detailPlayer.setUp(all_learn_video.get(current_video_number), true, "韩国小姐姐的舞蹈视频");
                    detailPlayer.startPlayLogic();
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
    }

    private void stop_compare_video(){
        btn2.setText("下一段");
        people_img.setLayoutParams(people_tiny);
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

    private void initLearnVideo() {
        String[] source = {"https://file.yhf2000.cn/dash/hw.mp4/manifest.mpd","http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4","http://vjs.zencdn.net/v/oceans.mp4","https://media.w3.org/2010/05/sintel/trailer.mp4"};
        for(int i=0;i<all_num;i++){
            SwitchVideoModel switchVideoModel = new SwitchVideoModel("1080P", source[i]);
            List<SwitchVideoModel> list = new ArrayList<SwitchVideoModel>();
            list.add(switchVideoModel);
            all_learn_video.add(list);
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
        SurfaceHolder holder = mSurfaceView.getHolder();// 取得holder
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.setKeepScreenOn(true);
        holder.addCallback(this); // holder加入回调接口
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
            mRecorder.setVideoEncodingBitRate(3200 * 1440);
            //设置录制的视频帧率,注意文档的说明:
            mRecorder.setVideoFrameRate(30);
            //设置要捕获的视频的宽度和高度
            mSurfaceHolder.setFixedSize(640, 480);//最高只能设置640x480
            mRecorder.setVideoSize(640, 480);//最高只能设置640x480
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
            showProgressDialog("提示","正在努力解析中，请稍等...");
            new SendUserDanceVideo().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        isRecording = false;
    }

    private void init_compare_video(){
        is_compare = true;
        reset_learn_view();
        btn2.setText("返回");
        people_img.setLayoutParams(people_all);
        people_img.setImageResource(R.drawable.l0);
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

    /*
     * 隐藏提示加载
     */
    public void hideProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

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

    public class SendUserDanceVideo extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... video_path) {
            JSONObject res = (JSONObject) learn_file.verifyFileUpload(Constant.mInstance.file_upload_verify_url,2,learn_file.hashFileUrl("/storage/emulated/0/Android/data/com.example.qydemo0/cache/videos/1617626033964.mp4"));
            String learn_video_id = "";
            try {
                if(!res.getBoolean("rapid_upload")){
                    if(!learn_file.uploadFile(Constant.mInstance.file_upload_callback_url, path_cur, res.getString("token"))){
                        return null;
                    }
                }

                learn_video_id = res.getString("file_id");


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String resJson) {
            if(resJson != ""){
                detailPlayer.setUp(all_learn_video.get(0), true, "对比视频");
                init_compare_video();
                detailPlayer.startPlayLogic();
                hideProgressDialog();
            }
        }

    }
}