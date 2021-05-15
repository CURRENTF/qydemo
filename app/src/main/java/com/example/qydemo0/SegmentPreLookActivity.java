package com.example.qydemo0;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.aiunit.core.FrameData;
import com.aiunit.vision.common.ConnectionCallback;
import com.aiunit.vision.common.FrameInputSlot;
import com.aiunit.vision.common.FrameOutputSlot;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
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
import com.example.qydemo0.QYpack.SwitchVideoModel;
import com.example.qydemo0.QYpack.Uri2RealPath;
import com.example.qydemo0.QYpack.VideoClip;
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;
import com.example.qydemo0.bean.CallBackBean;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.gson.Gson;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.transitionseverywhere.extra.Scale;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.media.MediaMetadataRetriever;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static android.telecom.DisconnectCause.LOCAL;
import static android.view.Gravity.*;
import static com.google.android.exoplayer2.scheduler.Requirements.NETWORK;
import org.json.JSONObject;

public class SegmentPreLookActivity extends MyAppCompatActivity {

    String free_dance_url = "";


    StandardGSYVideoPlayer videoPlayer;
    String clip_video_url = "";

    private ProgressDialog dialog;

    private LinearLayout all_main;

    private int num = 0;

    private List<String > videos_list = new ArrayList<>();
    private List<String > covers_list = new ArrayList<>();

    private List<ImageView> img_view_list = new ArrayList<>();

    LinearLayout.LayoutParams php, phpC;
    private String wid, breakdown_id;

    private QYrequest cur_request;

//    OrientationUtils orientationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_segment_pre_look);
        dialog = new ProgressDialog(SegmentPreLookActivity.this);
        dialog.setCancelable(false);// 让dialog不能失去焦点，一直在最上层显示
        ArrayList<String> list = getIntent().getStringArrayListExtra("params");

        ArrayList<String> list1 = getIntent().getStringArrayListExtra("params1");

        ArrayList<String> list2 = getIntent().getStringArrayListExtra("params2");

        wid = list1.get(0);
        breakdown_id = list1.get(1);

        init_videoes_list(list, list2);
        init_UI();
        free_dance_url = "/sdcard/Pictures/QQ/【SPEC舞蹈】《Uh-Oh》-女团(G)I-DLE热单韩舞翻跳（单人版）.mp4";
//        videos_list.add(free_dance_url);
//        videos_list.add("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
//        videos_list.add("http://vjs.zencdn.net/v/oceans.mp4");
//        covers_list.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20171021%2Fa49af86fb5ee48f0a13d578ea793eb72.jpeg&refer=http%3A%2F%2F5b0988e595225.cdn.sohucs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1622962679&t=91aafd77adc99d87c21f364cc7a0378c");
//        covers_list.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20171021%2Fa49af86fb5ee48f0a13d578ea793eb72.jpeg&refer=http%3A%2F%2F5b0988e595225.cdn.sohucs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1622962679&t=91aafd77adc99d87c21f364cc7a0378c");
//        covers_list.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20171021%2Fa49af86fb5ee48f0a13d578ea793eb72.jpeg&refer=http%3A%2F%2F5b0988e595225.cdn.sohucs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1622962679&t=91aafd77adc99d87c21f364cc7a0378c");
        init_player();
        restart_player(0);

        for(int i=0;i<videos_list.size();i++){
            addSegment(i);
        }
        refreshKuang(0);
    }

    private void init_UI(){
        all_main = findViewById(R.id.segment_choice_list);
        Log.e("free_dance_url",free_dance_url);
        php = new LinearLayout.LayoutParams(200,200);
        php.setMargins(20,0,20,0);
        phpC = new LinearLayout.LayoutParams(300,300);
        phpC.setMargins(20,0,20,0);
        videoPlayer = (StandardGSYVideoPlayer) findViewById(R.id.video_player);
        findViewById(R.id.start_learn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new start_learn(SegmentPreLookActivity.this).execute();
            }
        });
    }
    private void init_videoes_list(ArrayList<String> url_list, ArrayList<String> cover_list){
        for(int i=0;i<url_list.size();i++){
            videos_list.add(url_list.get(i));
            covers_list.add(cover_list.get(i));
        }
    }

    private void init_player (){
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
    }

    private void restart_player(int ind){
        videoPlayer.setUp(videos_list.get(ind), true, "第"+(ind+1)+"段");
        videoPlayer.startPlayLogic();
    }

    private void addSegment(int lab){
        ImageView segment = new ImageView(this);
        segment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("whc__2", ""+lab);
                restart_player(lab);
                refreshKuang(lab);
            }
        });
        segment.setLayoutParams(php);
        segment.setScaleType(ImageView.ScaleType.CENTER_CROP);
        segment.setAlpha(0.5f);
        Glide.with(SegmentPreLookActivity.this)
                .load(covers_list.get(lab))
                .transform(/*new CenterInside(), */new RoundedCorners(20))
                .into(segment);
        all_main.addView(segment);
        img_view_list.add(segment);
    }

    private void refreshKuang(int ind){
        for(int i=0;i<img_view_list.size();i++){
            if(i==ind) {
                img_view_list.get(i).setLayoutParams(phpC);
                //img_view_list.get(i).setColorFilter(Color.parseColor("#FF4081"), PorterDuff.Mode.LIGHTEN);
            }
            else {
                img_view_list.get(i).setLayoutParams(php);
                //img_view_list.get(i).clearColorFilter();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private int new_learning_object(String work_id, String breakdown_id){
        String[] callTo = {"work", "int", ""+ work_id, "breakdown", "int", ""+breakdown_id};
        Log.e("callTo", GenerateJson.universeJson2(callTo));
        JSONObject rjs = null;
        try {
            rjs = new JSONObject(cur_request.advancePost(GenerateJson.universeJson2(callTo), Constant.mInstance.learn_url,"Authorization", GlobalVariable.mInstance.token));
            if(!rjs.getString("msg").equals("Success")) return -1;
            return rjs.getJSONObject("data").getInt("lid");
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public class start_learn extends MyAsyncTask<Void, Void, Integer> {
        protected start_learn(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return new_learning_object(wid, breakdown_id);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer==-1){
                Toast.makeText(SegmentPreLookActivity.this, "出错啦！", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(SegmentPreLookActivity.this, LearnDanceActivity.class);
                ArrayList<String> params = new ArrayList<>();
                params.add(String.valueOf(integer));
                params.add(wid);
                params.add("0");
                startActivity(intent);
            }
        }
    }

}