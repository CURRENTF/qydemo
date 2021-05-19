package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
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

import java.util.ArrayList;
import java.util.List;

import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
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

    private QYrequest cur_request = new QYrequest();

//    OrientationUtils orientationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_segment_pre_look);
        dialog = new ProgressDialog(SegmentPreLookActivity.this);
        dialog.setCancelable(false);// 让dialog不能失去焦点，一直在最上层显示
        ArrayList<String> list = getIntent().getStringArrayListExtra("params");

        wid = list.get(0);
        breakdown_id = list.get(1);

        new get_videos(this).execute();
        init_UI();
        //free_dance_url = "/sdcard/Pictures/QQ/【SPEC舞蹈】《Uh-Oh》-女团(G)I-DLE热单韩舞翻跳（单人版）.mp4";
//        videos_list.add(free_dance_url);
//        videos_list.add("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
//        videos_list.add("http://vjs.zencdn.net/v/oceans.mp4");
//        covers_list.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20171021%2Fa49af86fb5ee48f0a13d578ea793eb72.jpeg&refer=http%3A%2F%2F5b0988e595225.cdn.sohucs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1622962679&t=91aafd77adc99d87c21f364cc7a0378c");
//        covers_list.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20171021%2Fa49af86fb5ee48f0a13d578ea793eb72.jpeg&refer=http%3A%2F%2F5b0988e595225.cdn.sohucs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1622962679&t=91aafd77adc99d87c21f364cc7a0378c");
//        covers_list.add("https://gimg2.baidu.com/image_search/src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20171021%2Fa49af86fb5ee48f0a13d578ea793eb72.jpeg&refer=http%3A%2F%2F5b0988e595225.cdn.sohucs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1622962679&t=91aafd77adc99d87c21f364cc7a0378c");
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
        videoPlayer.getGSYVideoManager().stop();
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
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                ArrayList<String> params = new ArrayList<>();
                params.add(String.valueOf(integer));
                params.add(breakdown_id);
                params.add("0");
                params.add("1");
                intent.putStringArrayListExtra("params", params);
                startActivity(intent);
            }
        }
    }

    public class get_videos extends MyAsyncTask<Void, Void, Boolean>{

        protected get_videos(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                JSONObject cur_res = new JSONObject(cur_request.advanceGet("https://api.yhf2000.cn/api/qingying/v1/work/segment/"+breakdown_id+"/","Authorization", GlobalVariable.mInstance.token));
                Log.i("whc_cur_res", String.valueOf(cur_res));
                if(!cur_res.getString("msg").equals("Success")) return false;
                JSONArray cur_a_res = cur_res.getJSONArray("data");
                for(int i=0;i<cur_a_res.length();i++){
                    JSONObject video_urls = cur_a_res.getJSONObject(i).getJSONObject("video").getJSONObject("url");
                    if(!video_urls.isNull("1080P"))
                        videos_list.add(video_urls.getString("1080P"));
                    else if(!video_urls.isNull("720P"))
                        videos_list.add(video_urls.getString("720P"));
                    else if(!video_urls.isNull("480P"))
                        videos_list.add(video_urls.getString("480P"));
                    else if(!video_urls.isNull("360P"))
                        videos_list.add(video_urls.getString("360P"));
                    else if(!video_urls.isNull("自动"))
                        videos_list.add(video_urls.getString("自动"));
                    covers_list.add(cur_a_res.getJSONObject(i).getJSONObject("cover").getString("url"));
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean integer) {
            super.onPostExecute(integer);
            if(!integer){
                Toast.makeText(SegmentPreLookActivity.this, "出错啦！", Toast.LENGTH_SHORT).show();
            } else {
                init_player();
                restart_player(0);
                for(int i=0;i<videos_list.size();i++){
                    addSegment(i);
                }
                refreshKuang(0);
            }
        }
    }

}