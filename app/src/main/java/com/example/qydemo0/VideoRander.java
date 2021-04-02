package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.aiunit.vision.common.ConnectionCallback;
import com.coloros.ocs.ai.cv.CVUnit;
import com.coloros.ocs.ai.cv.CVUnitClient;
import com.coloros.ocs.base.common.ConnectionResult;
import com.coloros.ocs.base.common.api.OnConnectionFailedListener;
import com.coloros.ocs.base.common.api.OnConnectionSucceedListener;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import android.media.MediaMetadataRetriever;

import static android.telecom.DisconnectCause.LOCAL;
import static com.google.android.exoplayer2.scheduler.Requirements.NETWORK;

public class VideoRander extends AppCompatActivity {

    String free_dance_url = "";

    String clip_video_url = "";

    StandardGSYVideoPlayer videoPlayer;

    private ProgressDialog progressDialog;

    private CVUnitClient mCVClient;

//    OrientationUtils orientationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_rander);
        final Intent intent = getIntent();
        free_dance_url = intent.getStringExtra("FreeDanceUrl");
        inti_clip_video();
        init_player();

        mCVClient = CVUnit.getVideoStyleTransferDetectorClient
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
            }

            @Override
            public void onServiceDisconnect() {
                Log.e("TAG", "initService: onServiceDisconnect: ");
            }
        });
    }

//    private void init_spinner(){
//        ArrayAdapter<String> adpter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,ctype);
//        adpter.setDropDownViewResource(android.R.layout.simple_spinner_item);
//
//        //获取Spinner组件,
//        Spinner spinner = (Spinner) findViewById(R.id.backgoudChange);
//        spinner.setAdapter(adpter);
//
//        //获取选中列的值。
//        String str = spinner.getSelectedItem().toString();
//        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
//    }

    private long getDurationLong(String url,int type){
        String duration = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //如果是网络路径
            if(type == NETWORK){
                retriever.setDataSource(url,new HashMap<String, String>());
            }else if(type == LOCAL){//如果是本地路径
                retriever.setDataSource(url);
            }
            duration = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception ex) {
            Log.d("nihao", "获取音频时长失败");
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                Log.d("nihao", "释放MediaMetadataRetriever资源失败");
            }
        }
        if(!TextUtils.isEmpty(duration)){
            return Long.parseLong(duration);
        }else{
            return 0;
        }
    }

    private void inti_clip_video() {

        long total_time = getDurationLong(free_dance_url, LOCAL);
        long mid_time = total_time/2;

        videoClip video_clip = new videoClip();
        clip_video_url = getExternalCacheDir().getPath();
        if (clip_video_url != null) {
            File dir = new File(clip_video_url + "/videos");
            if (!dir.exists()) {
                dir.mkdir();
            }
            clip_video_url = dir + "/" + System.currentTimeMillis() + ".mp4";
            long startTime = mid_time-2500 < 0 ? 0 : mid_time-2500;
            long endTime = mid_time+1500 > total_time ? total_time : mid_time+1500;

            try {
                video_clip.clip(free_dance_url, clip_video_url, startTime, endTime);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void init_player (){

            videoPlayer = (StandardGSYVideoPlayer) findViewById(R.id.video_player);

            videoPlayer.setUp(clip_video_url, true, "渲染视频预览");

            //增加封面
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.logo);
            videoPlayer.setThumbImageView(imageView);
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
            videoPlayer.setIsTouchWiget(false);
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
        }

        private void testChangeBackgrond(){

        }

    private void testLvJing(){

    }

    private void testChangeStyle(){



    }
        
        private void updatePlayer(String YuLanUrl){
        videoPlayer.setUp(YuLanUrl, true, "渲染视频预览");
        videoPlayer.startPlayLogic();
        }

    public void showProgressDialog(String title, String message) {
        if (progressDialog == null) {

            progressDialog = ProgressDialog.show(VideoRander.this, title,
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
        
        public class SendClipVideo extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... strings) {
                String cur_total_url = strings[0];
                String res = "";
                /*
                请求
                 */
                return res;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

        }
}