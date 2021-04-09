package com.example.qydemo0;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.SampleVideo;
import com.example.qydemo0.QYpack.SwitchVideoModel;
import com.example.qydemo0.QYAdapter.CommentExpandAdapter;
import com.example.qydemo0.QYpack.TimeTool;
import com.example.qydemo0.Widget.QYScrollView;
import com.example.qydemo0.Widget.WorkItem;
import com.example.qydemo0.bean.Belong;
import com.example.qydemo0.bean.CallBackBean;
import com.example.qydemo0.bean.CommentBean;
import com.example.qydemo0.bean.CommentDetailBean;
import com.example.qydemo0.bean.ReplyDetailBean;
import com.example.qydemo0.bean.WorkBean;
import com.example.qydemo0.bean.WorkDataBean;
import com.example.qydemo0.view.CommentExpandableListView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import com.example.qydemo0.DataTrans.FragmentDataForMain;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by guoshuyu on 2017/6/18.
 * sampleVideo支持全屏与非全屏切换的清晰度，旋转，镜像等功能.
 */

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.post_detail_nested_scroll)
    QYScrollView postDetailNestedScroll;

    //推荐使用StandardGSYVideoPlayer，功能一致
    //CustomGSYVideoPlayer部分功能处于试验阶段
    @BindView(R.id.detail_player)
    SampleVideo detailPlayer;

    @BindView(R.id.activity_detail_player)
    RelativeLayout activityDetailPlayer;

    QYrequest work_request = new QYrequest();

    private static final String TAG = "MainActivity1";
    private TextView bt_comment;
    private CommentExpandableListView expandableListView;
    private CommentExpandAdapter adapter;
    private CommentBean commentBean;
    private List<CommentDetailBean> commentsList;
    private BottomSheetDialog dialog;
    private Boolean is_follow = false;

    private boolean isPlay;
    private boolean isPause;
    private boolean isRelease;
    private boolean isLikeWork = false, isDislikeWork = false;
    private TextView video_like_num, video_dislike_num;

    private OrientationUtils orientationUtils;

    private MediaMetadataRetriever mCoverMedia;

    private ImageView coverImageView,like_it,dislike_it;
    private Button isFollow, isCanceF;

    private Context context = this;

    private WorkBean work_bean = new WorkBean();

    private LinearLayout render_content;
    private List<WorkItem> render_items = new ArrayList<>();
    private QYrequest cur_request = new QYrequest();
    private QYScrollView post_detail_nested_scroll = null;
    TimeTool timeTool = new TimeTool();
    private int start_next = 0;
    private JSONObject player_urls = new JSONObject();
    private int breakdown_id = -1;
    private int work_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Bundle bundle = this.getIntent().getExtras();
        ButterKnife.bind(this);
//        QYScrollView qyscrollview_comment = (QYScrollView) findViewById(R.id.qyscrollview_comment);
        int wid = bundle.getInt("id");
        Log.d("hjt.wid", String.valueOf(wid));
        new GetCommentJson().execute(wid,0,20);
        new GetWorkJson().execute(wid);
        work_id = wid;
        render_content = (LinearLayout) findViewById(R.id.Render_content);
        post_detail_nested_scroll = (QYScrollView) findViewById(R.id.post_detail_nested_scroll);
        post_detail_nested_scroll.setScanScrollChangedListener(new QYScrollView.ISmartScrollChangedListener() {
            @Override
            public void onScrolledToBottom() {
                if(!timeTool.checkFreq()) return;
                new getRec().execute(wid,start_next,10);
                Log.d("hjt.scroll.bottom", "true");
            }

            @Override
            public void onScrolledToTop() {
                Log.d("hjt.scroll.top", "true");
            }
        });
//        qyscrollview_comment.setScanScrollChangedListener(new QYScrollView.ISmartScrollChangedListener() {
//            @Override
//            public void onScrolledToBottom() {
//                if(!timeTool.checkFreq()) return;
//                new GetCommentJson().execute(wid,0,cur_comment_length);
//                cur_comment_length+=1;
//                Log.d("hjt.scroll.bottom", "true");
//            }
//
//            @Override
//            public void onScrolledToTop() {
//                Log.d("hjt.scroll.top", "true");
//            }
//        });

        new getRec().execute(wid,start_next,10);
    }

    private void init_button_and_pager(){
        ImageView btn_learn = (ImageView) findViewById(R.id.learn_dance);
        ImageView btn_free_dance = (ImageView) findViewById(R.id.free_dance);
        btn_learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_free_dance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, FreeDanceActivity.class);
                ArrayList<String> data1 = new ArrayList<String>();
                data1.add("0");
                try {
                    JSONObject cur_urls = player_urls;
                    if(cur_urls.has("1080P")) {
                        data1.add("1080P");
                        data1.add(cur_urls.getString("1080P"));
                    }
                    if(cur_urls.has("720P")) {
                        data1.add("720P");
                        data1.add(cur_urls.getString("720P"));
                    }
                    if(cur_urls.has("480P")) {
                        data1.add("480P");
                        data1.add(cur_urls.getString("480P"));
                    }
                    if(cur_urls.has("360P")) {
                        data1.add("360P");
                        data1.add(cur_urls.getString("360P"));
                    }
                    if(cur_urls.has("自动")) {
                        data1.add("自动");
                        data1.add(cur_urls.getString("自动"));
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putStringArrayListExtra("params", data1);
                startActivity(intent);

            }
        });

        LinearLayout detail_pager = findViewById(R.id.detail_page_above_container);
        LinearLayout comment_pager = findViewById(R.id.detail_page_comment_container);
        LinearLayout recall_pager = findViewById(R.id.recall_kuang);

        comment_pager.setVisibility(View.GONE);
        recall_pager.setVisibility(View.GONE);
        TextView intro = (TextView) findViewById(R.id.introduction);
        TextView comme = (TextView) findViewById(R.id.comment);
        intro.setTextColor(getColor(R.color.red));

        intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment_pager.setVisibility(View.GONE);
                recall_pager.setVisibility(View.GONE);
                post_detail_nested_scroll.setVisibility(View.VISIBLE);
                intro.setTextColor(getColor(R.color.red));
                comme.setTextColor(getColor(R.color.black));
            }
        });

        comme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post_detail_nested_scroll.setVisibility(View.GONE);
                comment_pager.setVisibility(View.VISIBLE);
                recall_pager.setVisibility(View.VISIBLE);
                comme.setTextColor(context.getResources().getColor(R.color.red));
                intro.setTextColor(context.getResources().getColor(R.color.black));
            }
        });
    }

    private void init_work_status(){
        like_it = findViewById(R.id.video_like);
        if(work_bean.getData().getIs_like()){
            isLikeWork = true;
            like_it.setColorFilter(Color.parseColor("#FF5C5C"));
        }
        like_it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(isLikeWork){
                //new WorkChange().execute(-1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try { String[] j = new String[0];
                        JSONObject res = new JSONObject(work_request.advancePut(GenerateJson.universeJson2(j),
                                Constant.mInstance.work+"func/"+work_id+"/-1/",
                                "Authorization", GlobalVariable.mInstance.token));

                            if(res.getString("msg").equals("Success")) {
                            isLikeWork = false;
                            work_bean.getData().setLike_num(work_bean.getData().getLike_num() - 1);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if(res.getString("msg").equals("Success"))
                                {
                                    if(work_bean.getData().getLike_num() == 0)  video_like_num.setText("");
                                    else video_like_num.setText(""+work_bean.getData().getLike_num());
                                    like_it.setColorFilter(Color.parseColor("#aaaaaa"));
                                }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            } else{
                //new WorkChange().execute(1);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try { String[] j = new String[0];
                        JSONObject res = new JSONObject(work_request.advancePut(GenerateJson.universeJson2(j),
                                Constant.mInstance.work+"func/"+work_id+"/1/",
                                "Authorization", GlobalVariable.mInstance.token));

                            if(res.getString("msg").equals("Success")) {
                                isLikeWork = true;
                                work_bean.getData().setLike_num(work_bean.getData().getLike_num() + 1);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if(res.getString("msg").equals("Success"))
                                        {
                                            video_like_num.setText(""+work_bean.getData().getLike_num());
                                            like_it.setColorFilter(Color.parseColor("#FF5C5C"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
            }
        });

        dislike_it = findViewById(R.id.video_dislike);
        if(work_bean.getData().getIs_dislike()){
            isDislikeWork = true;
            dislike_it.setColorFilter(Color.parseColor("#FF5C5C"));
        }
        dislike_it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDislikeWork){
                    //new WorkChange().execute(-2);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try { String[] j = new String[0];
                                JSONObject res = new JSONObject(work_request.advancePut(GenerateJson.universeJson2(j),
                                        Constant.mInstance.work+"func/"+work_id+"/-2/",
                                        "Authorization", GlobalVariable.mInstance.token));

                                if(res.getString("msg").equals("Success")) {
                                    isDislikeWork = false;
                                    work_bean.getData().setDislike_num(work_bean.getData().getDislike_num() - 1);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if(res.getString("msg").equals("Success"))
                                            {
                                                if(work_bean.getData().getDislike_num() == 0)  video_dislike_num.setText("");
                                                else video_dislike_num.setText(""+work_bean.getData().getDislike_num());
                                                dislike_it.setColorFilter(Color.parseColor("#aaaaaa"));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } else{
                    //new WorkChange().execute(2);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try { String[] j = new String[0];
                                JSONObject res = new JSONObject(work_request.advancePut(GenerateJson.universeJson2(j),
                                        Constant.mInstance.work+"func/"+work_id+"/2/",
                                        "Authorization", GlobalVariable.mInstance.token));

                                if(res.getString("msg").equals("Success")) {
                                    isDislikeWork = true;
                                    work_bean.getData().setDislike_num(work_bean.getData().getDislike_num() + 1);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if(res.getString("msg").equals("Success"))
                                            {
                                                video_dislike_num.setText(""+work_bean.getData().getDislike_num());
                                                dislike_it.setColorFilter(Color.parseColor("#FF5C5C"));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                }
            }
        });

        isFollow = (Button) findViewById(R.id.is_follow);
        isCanceF = (Button) findViewById(R.id.is_cancel_follow);

        if(work_bean.getData().getIs_follow()==null){
            isFollow.setVisibility(View.GONE);
            isCanceF.setVisibility(View.GONE);
        }
        else {
            if (work_bean.getData().getIs_follow()) {
                isCanceF.setVisibility(View.VISIBLE);
                isFollow.setVisibility(View.GONE);
            } else {
                isFollow.setVisibility(View.VISIBLE);
                isCanceF.setVisibility(View.GONE);
            }

            isFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new doFollow().execute();
                }
            });

            isCanceF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new cancelFollow().execute();
                }
            });
        }
    }

    private void init_content(String cur_title, String intros, int like_num, int dislike_num, int play_num, int comment_num){
        TextView detail_page_title = (TextView) findViewById(R.id.detail_page_title);
        detail_page_title.setText(cur_title);
        TextView detail_page_story = (TextView) findViewById(R.id.detail_page_story);
        detail_page_story.setText(intros);
        video_like_num = (TextView) findViewById(R.id.video_like_num);
        if(like_num != 0)
            video_like_num.setText(""+like_num);
        else
            video_like_num.setText("");
        video_dislike_num = (TextView) findViewById(R.id.video_dislike_num);
        if(dislike_num != 0)
            video_dislike_num.setText(""+dislike_num);
        else
            video_dislike_num.setText("");
        TextView video_play_num = (TextView) findViewById(R.id.video_play_num);
        if(play_num != 0)
            video_play_num.setText(""+play_num);
        else
            video_play_num.setText("");
        TextView video_comment_num = (TextView) findViewById(R.id.video_comment_num);
        if(comment_num != 0)
            video_comment_num.setText(""+comment_num);
        else
            video_comment_num.setText("");
        CircleImageView  head_img = (CircleImageView) findViewById(R.id.detail_page_userLogo);
        Glide.with(context)
                .load(work_bean.getData().getBelong().getImg_url())
                .transform(/*new CenterInside(), */new RoundedCorners(50)).into(head_img);
        TextView user_name = (TextView) findViewById(R.id.detail_page_userName);
        user_name.setText(work_bean.getData().getBelong().getUsername());
    }

    private void init_player(List<String> sources, List<String> list_name, String coverUrl){

        List<SwitchVideoModel> list = new ArrayList<>();
        for(int i=0;i<sources.size();i++){
            list.add(new SwitchVideoModel(list_name.get(i),sources.get(i)));
        }
        detailPlayer.setUp(list, true, "韩国小姐姐的舞蹈视频");

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
        detailPlayer.setLockLand(false);

        //打开  实现竖屏全屏动画
        detailPlayer.setShowFullAnimation(true);

        detailPlayer.setNeedLockFull(true);
        detailPlayer.setSeekRatio(1);
        //detailPlayer.setOpenPreView(false);
        detailPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //屏蔽，实现竖屏全屏
                //orientationUtils.resolveByClick();

                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                detailPlayer.startWindowFullscreen(PlayerActivity.this, true, true);
            }
        });

        detailPlayer.setVideoAllCallBack(new GSYSampleCallBack() {
            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                //开始播放了才能旋转和全屏
                //orientationUtils.setEnable(true);
                orientationUtils.setEnable(detailPlayer.isRotateWithSystem());
                isPlay = true;
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
            }

            @Override
            public void onClickStartError(String url, Object... objects) {
                super.onClickStartError(url, objects);
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
                //屏蔽，实现竖屏全屏
                //if (orientationUtils != null) {
                //orientationUtils.backToProtVideo();
                //}
            }
        });

        detailPlayer.setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                //屏蔽，实现竖屏全屏
                //if (orientationUtils != null) {
                //配合下方的onConfigurationChanged
                //orientationUtils.setEnable(!lock);
                //}
            }
        });

        loadFirstFrameCover(coverUrl);
    }

    private void init_work(String cur_Json){
        Gson gson = new Gson();
        work_bean = gson.fromJson(cur_Json, WorkBean.class);
        Log.i("whc123",""+work_bean.getMsg());
        List<String> lists = new ArrayList<>();
        List<String> list_name = new ArrayList<>();
        try {
            if(player_urls.has("1080P")) {
            lists.add(player_urls.getString("1080P"));
            list_name.add("1080P");
        }

        if(player_urls.has("720P")){
            lists.add(player_urls.getString("720P"));
            list_name.add("720P");
        }
        if(player_urls.has("480P")){
            lists.add(player_urls.getString("480P"));
            list_name.add("480P");
        }
        if(player_urls.has("360P")){
            lists.add(player_urls.getString("360P"));
            list_name.add("360P");
        }
        if(player_urls.has("自动")){
            lists.add(player_urls.getString("自动"));
            list_name.add("自动");
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("list_size", ""+player_urls);

        init_player(lists,list_name,work_bean.getData().getCover_url().getUrl());
        init_button_and_pager();
        init_content(work_bean.getData().getName(), work_bean.getData().getIntroduction(), work_bean.getData().getLike_num(),
                work_bean.getData().getDislike_num(), work_bean.getData().getPlay_num(), work_bean.getData().getComment_num());
        init_work_status();
    }

    private void do_like_btn(){
        if(isLikeWork){
            isLikeWork = false;
            work_bean.getData().setLike_num(work_bean.getData().getLike_num()-1);
            if(work_bean.getData().getLike_num() == 0)  video_like_num.setText("");
            else video_like_num.setText(""+work_bean.getData().getLike_num());
            like_it.setColorFilter(Color.parseColor("#aaaaaa"));
        } else{
            isLikeWork = true;
            work_bean.getData().setLike_num(work_bean.getData().getLike_num()+1);
            video_like_num.setText(""+work_bean.getData().getLike_num());
            like_it.setColorFilter(Color.parseColor("#FF5C5C"));
        }
    }

    private void do_dislike_btn(){
        if(isDislikeWork){
            isDislikeWork = false;
            work_bean.getData().setDislike_num(work_bean.getData().getDislike_num()-1);
            if(work_bean.getData().getDislike_num() == 0) video_dislike_num.setText("");
            else video_dislike_num.setText(""+work_bean.getData().getDislike_num());
            dislike_it.setColorFilter(Color.parseColor("#aaaaaa"));
        } else{
            isDislikeWork = true;
            work_bean.getData().setDislike_num(work_bean.getData().getDislike_num()+1);
            video_dislike_num.setText(""+work_bean.getData().getDislike_num());
            dislike_it.setColorFilter(Color.parseColor("#FF5C5C"));
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


    @Override
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume();
        super.onResume();
        isPause = false;
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            detailPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
        //竖屏全屏
//        orientationUtils.setEnable(false);
    }


    private GSYVideoPlayer getCurPlay() {
        if (detailPlayer.getFullWindowPlayer() != null) {
            return  detailPlayer.getFullWindowPlayer();
        }
        return detailPlayer;
    }


    private void resolveNormalVideoUI() {
        //增加title
        detailPlayer.getTitleTextView().setVisibility(View.GONE);
        detailPlayer.getBackButton().setVisibility(View.GONE);
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

        //原始方法
        /*final MediaMetadataRetriever mediaMetadataRetriever = getMediaMetadataRetriever(url);
        //获取帧图片
        if (getMediaMetadataRetriever(url) != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = mediaMetadataRetriever
                            .getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (bitmap != null && !isRelease) {
                                Debuger.printfLog("time " + System.currentTimeMillis());
                                //显示
                                coverImageView.setImageBitmap(bitmap);
                            }
                        }
                    });
                }
            }).start();
        }*/

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

    private void initView() {
        expandableListView = (CommentExpandableListView) findViewById(R.id.detail_page_lv_comment);
        bt_comment = (TextView) findViewById(R.id.detail_page_do_comment);
        bt_comment.setOnClickListener(this);
        initExpandableListView(commentsList);
    }

    /**
     * 初始化评论和回复列表
     */
    private void initExpandableListView(final List<CommentDetailBean> commentList){
        expandableListView.setGroupIndicator(null);
        //默认展开所有回复
        adapter = new CommentExpandAdapter(this, commentList);
        expandableListView.setAdapter(adapter);
        for(int i = 0; i<commentList.size(); i++){
            expandableListView.expandGroup(i);
        }
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                showReplyDialog(groupPosition, -1);
                return true;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                Toast.makeText(PlayerActivity.this,"点击了回复",Toast.LENGTH_SHORT).show();
                showReplyDialog(groupPosition,childPosition);
                return true;
            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //toast("展开第"+groupPosition+"个分组");

            }
        });

    }

    /**
     * by moos on 2018/04/20
     * func:生成测试数据
     * @return 评论数据
     */
    private List<CommentDetailBean> generateTestData(String commentJson){
        Gson gson = new Gson();
        commentBean = gson.fromJson(commentJson, CommentBean.class);
        List<CommentDetailBean> commentList = commentBean.getData();
        return commentList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.detail_page_do_comment){

            showCommentDialog();
        }
    }

    /**
     * by moos on 2018/04/20
     * func:弹出评论框
     */
    private void showCommentDialog(){
        dialog = new BottomSheetDialog(this);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        dialog.setContentView(commentView);
        /**
         * 解决bsd显示不全的情况
         */
        View parent = (View) commentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        commentView.measure(0,0);
        behavior.setPeekHeight(commentView.getMeasuredHeight());

        bt_comment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String commentContent = commentText.getText().toString().trim();
                if(!TextUtils.isEmpty(commentContent)){

                    //commentOnWork(commentContent);
                    dialog.dismiss();
                    new doComment().execute(commentContent);

                }else {
                    Toast.makeText(PlayerActivity.this,"评论内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && charSequence.length()>2){
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                }else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }

    private void success_commment(String commentContent, int cid) throws JSONException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        Log.i("raw_date",String.valueOf(date));
        CommentDetailBean detailBean = new CommentDetailBean(cid,commentContent,0,simpleDateFormat.format(date),
                true,false,false,
                new Belong(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getInt("uid"),
                        GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("username"), GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("img_url")),null);
        adapter.addTheCommentData(detailBean);
        Toast.makeText(PlayerActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
    }

    private void showReplyDialog(final int position, final int second_position){
        dialog = new BottomSheetDialog(this);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        if(second_position!=-1) {
            commentText.setHint("回复 " + commentsList.get(position).getReplies().get(second_position).getBelong().getUsername() + " 的评论:");
        }
        else{
            commentText.setHint("回复 " + commentsList.get(position).getBelong().getUsername() + " 的评论:");
        }
        dialog.setContentView(commentView);
        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String replyContent = commentText.getText().toString().trim();
                if(!TextUtils.isEmpty(replyContent)){
                    dialog.dismiss();
                    new doReply().execute(""+position, ""+second_position, replyContent);
                }else {
                    Toast.makeText(PlayerActivity.this,"回复内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && charSequence.length()>2){
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                }else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }

    private void success_reply(String replyContent, int second_position, int position, int cid) throws JSONException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        ReplyDetailBean detailBean;
        if(second_position!=-1) {
            detailBean = new ReplyDetailBean(-1, replyContent, 0, simpleDateFormat.format(date), true, false, false, new Belong(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getInt("uid"),
                    GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("username"), GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("img_url")),
                    new Belong(commentsList.get(position).getReplies().get(second_position).getBelong().getUid(),
                            commentsList.get(position).getReplies().get(second_position).getBelong().getUsername(),
                            commentsList.get(position).getReplies().get(second_position).getBelong().getImg_url()));
        } else{
            detailBean = new ReplyDetailBean(cid, replyContent, 0, simpleDateFormat.format(date), true, false, false, new Belong(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getInt("uid"),
                    GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("username"), GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("username")),
                    null);
        }
        adapter.addTheReplyData(detailBean, position);
        expandableListView.expandGroup(position);
        Toast.makeText(PlayerActivity.this,"回复成功",Toast.LENGTH_SHORT).show();
    }

    public class GetWorkJson extends AsyncTask<Integer, Void, String>{

        @Override
        protected String doInBackground(Integer... idd) {
            String res = work_request.advanceGet(Constant.mInstance.work+idd[0]+"/", "Authorization", GlobalVariable.mInstance.token);
            Log.i("workJson",res);
            return res;
        }

        @Override
        protected void onPostExecute(String cur_work_json) {
            super.onPreExecute();
            try {
                JSONObject player_urls1 = new JSONObject(cur_work_json);
                Log.e("whc_player_urls1", String.valueOf(player_urls1));
                player_urls = player_urls1.getJSONObject("data").getJSONObject("video").getJSONObject("url");
                Log.e("player_urls", String.valueOf(player_urls));
                try{
                breakdown_id = player_urls.getJSONObject("data").getJSONArray("breakdown").getJSONObject(0).getInt("id");
                Log.e("breakdown_id", ""+breakdown_id);}
                catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }init_work(cur_work_json);
            new WorkChange().execute(0);
        }
    }

    public class GetCommentJson extends AsyncTask<Integer, Void, String>{

        @Override
        protected String doInBackground(Integer... ints) {
            String[] ss = new String[0];
            String res = work_request.advanceMethod("GET",GenerateJson.universeJson2(ss),Constant.mInstance.comment+"0/"+ints[0]+"/?start="+ints[1]+"&lens="+ints[2], "Authorization", GlobalVariable.mInstance.token);
            Log.i("commentJson",res);
            //Log.i("token",""+ GlobalVariable.mInstance.token);
            return res;
        }

        @Override
        protected void onPostExecute(String cur_work_json) {
            super.onPreExecute();
            commentsList = generateTestData(cur_work_json);
            initView();
        }
    }

    public class WorkChange extends AsyncTask<Integer, Void, Integer>{

        @Override
        protected Integer doInBackground(Integer... ope) {

            String[] j = new String[0];
            JSONObject res = null;
            try {
                res = new JSONObject(work_request.advancePut(GenerateJson.universeJson2(j),
                        Constant.mInstance.work+"func/"+work_bean.getData().getId()+"/"+ope[0]+"/",
                        "Authorization", GlobalVariable.mInstance.token));
            if(res.getString("msg").equals("Success"))
                return ope[0];
            else
                return 404;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 404;
        }

        @Override
        protected void onPostExecute(Integer res_int) {
            super.onPostExecute(res_int);
            if(res_int==404)
                Toast.makeText(PlayerActivity.this,"WRONG!!!",Toast.LENGTH_LONG).show();
            else {
                if(res_int == 1 || res_int == -1){
                    do_like_btn();
                }
                else if(res_int == 2 || res_int == -2){
                    do_dislike_btn();
                }
            }
        }
    }

    public class doComment extends AsyncTask<String, Void, String[]>{
        @Override
        protected void onPostExecute(String... contentt) {
            super.onPostExecute(contentt);
            try {
                success_commment(contentt[0], Integer.valueOf(contentt[1]));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String[] doInBackground(String... strings) {
            String[] callToJson = {"text", "string",strings[0]};
            String res = work_request.advancePost(GenerateJson.universeJson2(callToJson),
                    Constant.mInstance.comment+"0/"+work_id+"/", "Authorization", GlobalVariable.mInstance.token);
            try {
                JSONObject res_jsonobj = new JSONObject(res);
                Log.i("comment_callback",res);
                if(res_jsonobj.getString("msg").equals("Success")) {
                    String[] res_reply_all = {strings[0], String.valueOf(res_jsonobj.getJSONObject("data").getInt("cid"))};
                    return res_reply_all;
                }
                else
                    return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
                return null;
        }
    }

    public class doReply extends AsyncTask<String, Void, String[]>{
        @Override
        protected void onPostExecute(String[] contentt) {
            super.onPostExecute(contentt);
            try {
                success_reply(contentt[2],Integer.valueOf(contentt[1]),Integer.valueOf(contentt[0]),Integer.valueOf(contentt[3]));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String[] doInBackground(String... strings) {
            String res = "";
            if (Integer.valueOf(strings[1]) != -1) {
                String[] call_to_json = {"top", "int", "" + commentsList.get(Integer.valueOf(strings[0])).getCid(), "reply", "int", "" + commentsList.get(Integer.valueOf(strings[0])).getReplies().get(Integer.valueOf(strings[1])).getCid(), "text", "string", strings[2]};
                res = work_request.advancePost(GenerateJson.universeJson2(call_to_json),
                        Constant.mInstance.comment + "0/" + work_bean.getData().getId() + "/", "Authorization", GlobalVariable.mInstance.token);
            } else {
                String[] call_to_json = {"top", "int", "" + commentsList.get(Integer.valueOf(strings[0])).getCid(), "reply", "int", "" + commentsList.get(Integer.valueOf(strings[0])).getCid(), "text", "string", strings[2]};
                res = work_request.advancePost(GenerateJson.universeJson2(call_to_json),
                        Constant.mInstance.comment + "0/" + work_bean.getData().getId() + "/", "Authorization", GlobalVariable.mInstance.token);
            }
            try {
                if ((new JSONObject(res)).getString("msg").equals("Success")) {
                    String[] res_to_reply = {strings[0], strings[1], strings[2], String.valueOf((new JSONObject(res)).getJSONObject("data").getInt("cid"))};
                    return res_to_reply;
                }
                else
                    return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class doFollow extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {

            String[] callJson = {"target","int",""+ work_bean.getData().getBelong().getUid()};
            String res = work_request.advancePost(GenerateJson.universeJson2(callJson),
                    Constant.mInstance.userFollow_url, "Authorization", GlobalVariable.mInstance.token);
            Log.i("关注", res);
            Gson gson = new Gson();
            CallBackBean call_back_bean = gson.fromJson(res, CallBackBean.class);
            return call_back_bean.getMsg().equals("Success");

        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if (aVoid) {
                isFollow.setVisibility(View.GONE);
                isCanceF.setVisibility(View.VISIBLE);
            }
        }
    }

    public class cancelFollow extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            String[] callJson = {"target","int",""+ work_bean.getData().getBelong().getUid()};
            String res = work_request.advanceMethod("DELETE",GenerateJson.universeJson2(callJson),
                    Constant.mInstance.userFollow_url, "Authorization", GlobalVariable.mInstance.token);
            Log.i("取消关注", res);
            Gson gson = new Gson();
            CallBackBean call_back_bean = gson.fromJson(res, CallBackBean.class);
            return call_back_bean.getMsg().equals("Success");
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if (aVoid) {
                isCanceF.setVisibility(View.GONE);
                isFollow.setVisibility(View.VISIBLE);
            }
        }
    }

    public class getRec extends AsyncTask<Integer, Void, JSONArray>{
        @Override
        protected void onPostExecute(JSONArray aVoid) {
            super.onPostExecute(aVoid);
            start_next += 10;
            for(int i=0;i<aVoid.length();i++)
            {
                JSONObject cur_json_object = null;
                try {
                    cur_json_object = aVoid.getJSONObject(i);
                    WorkItem render_item = new WorkItem(PlayerActivity.this);
                    render_item.init(cur_json_object.getJSONObject("cover").getString("url"),cur_json_object.getString("name"),
                            cur_json_object.getInt("like_num"),cur_json_object.getInt("play_num"),
                            cur_json_object.getString("introduction"), cur_json_object.getJSONObject("belong").getString("username"), cur_json_object.getInt("id"));
                    render_items.add(render_item);
                    render_content.addView(render_item);
                    JSONObject finalCur_json_object = cur_json_object;
                    render_item.findViewById(R.id.work_item_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setClass(PlayerActivity.this, PlayerActivity.class);
                            try {
                                intent.putExtra("id", finalCur_json_object.getInt("id"));
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected JSONArray doInBackground(Integer... ints) {
            JSONObject res_json = null;
            try {
                res_json = new JSONObject(cur_request.advanceGet("https://api.yhf2000.cn/api/qingying/v1/recommendation/work/"+ints[0]+"/?start="+ints[1]+"&lens="+ints[2],"Authorization", GlobalVariable.mInstance.token));
                return(res_json.getJSONArray("data"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class readyToJumpToLearn extends AsyncTask<Void, Void, Integer>{

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(integer != null){
                if(integer == -1) Toast.makeText(PlayerActivity.this, "该舞蹈目前不可学习，请稍后再试", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(PlayerActivity.this, LearnDanceActivity.class);
                ArrayList<String> data1 = new ArrayList<String>();
                data1.add(""+integer);
                data1.add(""+work_id);
                data1.add("0");
                intent.putStringArrayListExtra("params", data1);
                startActivity(intent);
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            String[] callTo = {"work", "int", ""+ work_id, "breakdown", "int", ""+breakdown_id};
            try {
                JSONObject rjs = new JSONObject(cur_request.advancePost(GenerateJson.universeJson2(callTo), Constant.mInstance.learn_url,"Authorization", GlobalVariable.mInstance.token));
                if(rjs.getString("msg").equals("Success")){
                    return rjs.getJSONObject("data").getInt("lid");
                } else {
                    return -1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}