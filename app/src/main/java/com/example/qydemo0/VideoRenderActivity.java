package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.coloros.ocs.ai.cv.CVUnitClient;
import com.example.qydemo0.QYpack.VideoClip;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import android.media.MediaMetadataRetriever;
import android.widget.RelativeLayout;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import static android.telecom.DisconnectCause.LOCAL;
import static android.view.Gravity.*;
import static com.google.android.exoplayer2.scheduler.Requirements.NETWORK;

public class VideoRenderActivity extends AppCompatActivity {

    String free_dance_url = "";

    String clip_video_url = "";

    StandardGSYVideoPlayer videoPlayer;

    private ProgressDialog progressDialog;

    private CVUnitClient mCVClient;

    private int[] render_paras = {0,0,0};

    private Boolean isYuLan = false;

//    OrientationUtils orientationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_render);
//        final Intent intent = getIntent();
//        free_dance_url = intent.getStringExtra("NAME");
        free_dance_url = "/sdcard/Movies/clip_out2.mp4";
        inti_clip_video();
        init_player();

        RelativeLayout.LayoutParams img_full = new RelativeLayout.LayoutParams(100,100);
        RelativeLayout.LayoutParams img_tiny = new RelativeLayout.LayoutParams(1,1);

        ImageView full_screen = (ImageView) findViewById(R.id.fullscreen);
        full_screen.setVisibility(View.GONE);

        ImageView render_choice = (ImageView) findViewById(R.id.render_choice);
        ImageView render_reset = (ImageView) findViewById(R.id.render_reset);

        render_reset.setVisibility(View.GONE);

        render_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                render_choice.setVisibility(View.GONE);
                PopupWindowRight popupWindowRight = new PopupWindowRight(VideoRenderActivity.this);
                popupWindowRight.showAtLocation(getWindow().getDecorView(), RIGHT | BOTTOM, 0, 0);
                render_reset.setVisibility(View.VISIBLE);

                render_reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        render_paras = popupWindowRight.getRenderParams();
                        render_reset.setVisibility(View.GONE);
                        popupWindowRight.dismiss();
                        render_choice.setVisibility(View.VISIBLE);
                        isYuLan = true;
                        showProgressDialog("提示","正在努力加载渲染预览视频...");
                        new SendClipVideo().execute(clip_video_url);
                    }
                });

            }
        });

        Button btn_render = (Button) findViewById(R.id.start_render);
        btn_render.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("paras",""+render_paras[0]+" "+render_paras[1]+" "+render_paras[2]);
                isYuLan = false;
                new SendClipVideo().execute(free_dance_url);
            }
        });

    }

//    public class tan extends AsyncTask<Void, Void, Void>{
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            showRightDialog();
//        }
//    }

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

//    private void hideRightDialog() {
//        popupWindowRight.dismiss();
//    }

    public long getDurationLong(String url,int type){
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

        VideoClip video_clip = new VideoClip();
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

            progressDialog = ProgressDialog.show(VideoRenderActivity.this, title,
                    message, true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }

        progressDialog.show();

    }

    public void hideProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

    public class SendClipVideo extends AsyncTask<String , Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            String will_do_url = strings[0];
            String res = "";
            /*
            请求结果
             */
            return res;
        }

        @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(isYuLan){
                    updatePlayer(s);
                    hideProgressDialog();
                } else {

                }
            }

        }
}