package com.example.qydemo0;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.qydemo0.QYpack.AudioPlayer;
import com.example.qydemo0.QYpack.DeviceInfo;
import com.example.qydemo0.QYpack.KqwOneShot;
import com.example.qydemo0.QYpack.SampleVideo;
import com.example.qydemo0.QYpack.SwitchVideoModel;
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;
import com.example.qydemo0.Widget.QYDIalog;
import com.example.qydemo0.Widget.QYLoading;
import com.example.qydemo0.utils.SoundTipUtil;

import com.koushikdutta.ion.builder.Builders;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * sampleVideo支持全屏与非全屏切换的清晰度，旋转，镜像等功能.
 * Activity可以继承GSYBaseActivityDetail实现详情模式的页面
 * 或者参考DetailPlayer、DetailListPlayer实现
 * <p>
 * Created by guoshuyu on 2017/6/18.
 */

public class FreeDanceActivity extends MyAppCompatActivity implements SurfaceHolder.Callback{

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
    private int all_num = 1;
    String path_cur = "";
    private Boolean is_record;
    private Boolean is_compare;
    private QYLoading qyLoading;
    private AVLoadingIndicatorView avi;
    SeekBar cur_process;

    RelativeLayout menu_op;
    ImageView arrow;

    private AudioPlayer audioPlayer = null;

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

    private ImageView coverImageView, fullScreenView, black_back;

    private TextView changeSpeed;

    private Boolean is_video_input=true;

    private SurfaceView surf;

    private RelativeLayout.LayoutParams fill_all, fill_tiny, fill_all_r;

    boolean mirror_status = false;

    static private Handler handler;

    ImageView cover_start_icon;

    private KqwOneShot kqw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setWindow();
        setContentView(R.layout.activity_free_dance);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initViews();
        ButterKnife.bind(this);
        ArrayList<String> list = getIntent().getStringArrayListExtra("params");
        initLearnVideo(list);
        is_learn = false;

        is_record = false;

        is_compare = false;

        cur_process = (SeekBar) findViewById(R.id.progress);

        fullScreenView = (ImageView) findViewById(R.id.fullscreen);
        fullScreenView.setVisibility(GONE);

        black_back = (ImageView) findViewById(R.id.black_back);

        changeSpeed = (TextView) findViewById(R.id.change_speed);

        init_detail_player();

        surf = findViewById(R.id.sf_view);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int heightPixels = dm.heightPixels;
        Log.e("hjt.aaa", heightPixels + ".");
        fill_all = new RelativeLayout.LayoutParams((int) heightPixels * DeviceInfo.width(this) / DeviceInfo.height(this), heightPixels);
        fill_all.addRule(RelativeLayout.CENTER_HORIZONTAL);
        fill_all_r = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fill_tiny = new RelativeLayout.LayoutParams(1, 1);

        btn1 = findViewById(R.id.mirror_btn);
        btn2 = findViewById(R.id.next_video);
        btn3 = findViewById(R.id.learn_now);
        if (list.get(0).equals("1")) {
            is_video_input = false;
//            btn1.setText("恢复");
            surf.setLayoutParams(fill_all);
            black_back.setLayoutParams(fill_all_r);
        }
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mirror_status) {
                    do_mirror_close();
                } else {
                    do_mirror_open();
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_compare) {
                    stop_compare_video();
                    Intent intent = new Intent(FreeDanceActivity.this, VideoRenderActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("free_dance_url", path_cur);  // 传递参数，根据需要填写
                    startActivity(intent);
                }
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_start_it();
            }
        });

        menu_op = findViewById(R.id.expand_menu);
        arrow = findViewById(R.id.menu_btn);
        shrink_menu_now();

        init_kqw();
    }

    private void init_kqw(){
        handler = new Handler(Looper.myLooper()) {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String date = bundle.getString("msg");
                if(date.equals("【打开】镜子")){
                    SoundTipUtil.soundTip(getBaseContext(), "好的");
                    do_mirror_open();
                } else if(date.equals("【关闭】镜子")){
                    SoundTipUtil.soundTip(getBaseContext(), "好的");
                    do_mirror_close();
                } else if(date.equals("【开始】录制")){
                    SoundTipUtil.soundTip(getBaseContext(), "好的");
                    btn_start_it();
                }
            }
        };

        kqw = new KqwOneShot(this, handler);
        kqw.btn_grammar();
    }

    private void btn_start_it(){
        if (!is_compare) {
            if (!is_learn) {
                cover_start_icon.setVisibility(VISIBLE);
                is_learn = true;
                init_learn_view();
                detailPlayer.startPlayLogic();
            }
        } else {
//                    btn3.setText("开始");
            if (!is_video_input) {
                surf.setLayoutParams(fill_all);
                black_back.setLayoutParams(fill_all_r);
            }
            stop_compare_video();
            detailPlayer.setUp(all_learn_video.get(0), true, "韩舞小姐姐");
            detailPlayer.startPlayLogic();
        }
    }

    private void do_mirror_open(){
        if(!is_compare && is_video_input) {
                mirror_status = true;
                surf.setLayoutParams(fill_all);
                black_back.setLayoutParams(fill_all_r);
        }
    }

    private void do_mirror_close(){
        if(!is_compare && is_video_input) {
                mirror_status = false;
                surf.setLayoutParams(fill_tiny);
                black_back.setLayoutParams(fill_tiny);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        detailPlayer.setUp(all_learn_video.get(0), true, "自由舞");
    }

    void init_detail_player(){
        detailPlayer.setUp(all_learn_video.get(0), true, "自由舞");

        detailPlayer.setmSwitchSize(all_learn_video.get(0).get(0).getName());
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
                detailPlayer.startWindowFullscreen(FreeDanceActivity.this, true, true);
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
                    try {
                        audioPlayer = new AudioPlayer(FreeDanceActivity.this, R.raw.count_number_10);
                        audioPlayer.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                startRecord();
                                detailPlayer.onVideoResume();
                            }
                        });
                        audioPlayer.getMediaPlayer().setOnErrorListener(new MediaPlayer.OnErrorListener() {
                            @Override
                            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                                Log.e("whc_audio_error", "audio_wrong");
                                return false;
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
                if(is_learn && !is_compare){
                    stopRecord();
                }
            }

            @Override
            public void onClickStartError(String url, Object... objects) {
                super.onClickStartError(url, objects);
            }
        });

        detailPlayer.getCurrentPlayer().startPlayLogic();
    }

    void shrink_menu_now(){
        arrow.setImageResource(R.drawable.ic_down_arrow2);
        menu_op.setTranslationY(-DeviceInfo.dip2px(FreeDanceActivity.this, 253));

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

    private void show_record_result(){
        init_compare_video();
//        btn3.setText("重录");
        if(!is_video_input) {surf.setLayoutParams(fill_tiny);black_back.setLayoutParams(fill_tiny);}
        detailPlayer.setUp(path_cur,true,"");
        detailPlayer.startPlayLogic();
    }

    private void initLearnVideo(ArrayList<String> source) {
//        String[] source = {"https://file.yhf2000.cn/dash/hw.mp4/manifest.mpd"};
        if(source.get(0).equals("0")){
        for(int i=1;i<source.size();i+=2){
            SwitchVideoModel switchVideoModel = new SwitchVideoModel(source.get(i), source.get(i+1));
            List<SwitchVideoModel> list = new ArrayList<SwitchVideoModel>();
            list.add(switchVideoModel);
            all_learn_video.add(list);
        }
        }
        else{
            SwitchVideoModel switchVideoModel = new SwitchVideoModel("音频", source.get(1));
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
        cover_start_icon = findViewById(R.id.cover_start_icon);
        cover_start_icon.setVisibility(GONE);
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;}

    /**
     * 初始化相机
     */
    private void initCamera() {
        if (Camera.getNumberOfCameras() == 2) {
            mCamera = Camera.open(mCameraFacing);
        } else {
            mCamera = Camera.open();
        }

        FreeDanceActivity.CameraSizeComparator sizeComparator = new FreeDanceActivity.CameraSizeComparator();
        Camera.Parameters parameters = mCamera.getParameters();

        if (mSize == null) {
            Log.e("herher","herherhreher");
            List<Camera.Size> vSizeList = parameters.getSupportedPreviewSizes();
            Collections.sort(vSizeList, sizeComparator);

            for (int num = 0; num < vSizeList.size(); num++) {
                Camera.Size size = vSizeList.get(num);
                if (size.width >= 800 && size.height >= 480) {
                    this.mSize = size;
                    break;
                }
            }
//            for(int ii = 0; ii<vSizeList.size();ii++) Log.e("fill_width", ""+vSizeList.get(ii).width+"+"+vSizeList.get(ii).height);
            mSize.width = 1280;
            mSize.height = 720;

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
        kqw.btn_stop();
        if(audioPlayer!=null){
            audioPlayer.stop();
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
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            //设置video的编码格式
            mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            //设置录制的视频编码比特率
            mRecorder.setVideoEncodingBitRate(1000 * 1500);
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
            new waitForSave(this).execute(path_cur);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isRecording = false;

        cover_start_icon.setVisibility(GONE);

    }

    private void init_compare_video(){
        is_compare = true;
        reset_learn_view();
        if(mirror_status)
        {
            mirror_status = false;
            surf.setLayoutParams(fill_tiny);
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

    private class CameraSizeComparator implements Comparator<Camera.Size> {
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

    public class waitForSave extends MyAsyncTask<String, Void,Boolean> {

        protected waitForSave(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            qyLoading = new QYLoading(FreeDanceActivity.this);
            qyLoading.start_dialog();
        }

        @Override
        protected void onPostExecute(Boolean is_success) {
            super.onPostExecute(is_success);
            qyLoading.stop_dialog();
            if(!is_success){
                Toast.makeText(FreeDanceActivity.this,"出错啦,请重新录制",Toast.LENGTH_LONG).show();
            }
            else {
                show_record_result();
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            for(int i=0;i<10;i++)
            {
                try {
                    Thread.sleep(500);
                    File f=new File(strings[0]);
                    if(f.exists())
                    {
                        Log.i("whc_", "Yes");
                        return true;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

}