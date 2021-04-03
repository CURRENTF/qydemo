package com.example.qydemo0;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.qydemo0.SwitchVideoModel;
import com.example.qydemo0.SampleVideo;
import com.example.qydemo0.adapter.CommentExpandAdapter;
import com.example.qydemo0.bean.Belong;
import com.example.qydemo0.bean.CommentBean;
import com.example.qydemo0.bean.CommentDetailBean;
import com.example.qydemo0.bean.ReplyDetailBean;
import com.example.qydemo0.view.CommentExpandableListView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by guoshuyu on 2017/6/18.
 * sampleVideo支持全屏与非全屏切换的清晰度，旋转，镜像等功能.
 */

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.post_detail_nested_scroll)
    NestedScrollView postDetailNestedScroll;

    //推荐使用StandardGSYVideoPlayer，功能一致
    //CustomGSYVideoPlayer部分功能处于试验阶段
    @BindView(R.id.detail_player)
    SampleVideo detailPlayer;

    @BindView(R.id.activity_detail_player)
    RelativeLayout activityDetailPlayer;

    private static final String TAG = "MainActivity1";
    private TextView bt_comment;
    private CommentExpandableListView expandableListView;
    private CommentExpandAdapter adapter;
    private CommentBean commentBean;
    private List<CommentDetailBean> commentsList;
    private BottomSheetDialog dialog;

    private String testJosn2 = "{\n" +
            "    \"status\": 200,\n" +
            "    \"msg\": \"Success\",\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"cid\": 4,\n" +
            "            \"text\": \"这个小姐姐是真的好看啊~\",\n" +
            "            \"like_num\": 2,\n" +
            "            \"created_time\": \"2021-03-27T21:22:24.087358\",\n" +
            "            \"is_public\": true,\n" +
            "            \"is_delete\": false,\n" +
            "            \"like\": true,\n" +
            "            \"belong\": {\n" +
            "                \"uid\": 1,\n" +
            "                \"username\": \"gsy666\",\n" +
            "                \"img_url\": \"http://qimg.hxnews.com/2018/0716/1531706889647.jpg\"\n" +
            "            },\n" +
            "            \"replies\": [\n" +
            "                {\n" +
            "                    \"cid\": 8,\n" +
            "                    \"text\": \"是啊是啊，好好看~\",\n" +
            "                    \"like_num\": 0,\n" +
            "                    \"created_time\": \"2021-03-27T21:56:54.080672\",\n" +
            "                    \"is_public\": true,\n" +
            "                    \"is_delete\": false,\n" +
            "                    \"like\": false,\n" +
            "                    \"belong\": {\n" +
            "                        \"uid\": 2,\n" +
            "                        \"username\": \"hjt666\",\n" +
            "                        \"img_url\": \"http://n.sinaimg.cn/ent/transform/w630h630/20180208/YrXA-fyrkuxs3657490.jpg\"\n" +
            "                    },\n" +
            "                    \"reply_to\": {\n" +
            "                        \"uid\": 1,\n" +
            "                        \"username\": \"gsy666\",\n" +
            "                        \"img_url\": \"http://qimg.hxnews.com/2018/0716/1531706889647.jpg\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"cid\": 9,\n" +
            "                    \"text\": \"嘻嘻嘻~\",\n" +
            "                    \"like_num\": 0,\n" +
            "                    \"created_time\": \"2021-03-27T22:05:33.351629\",\n" +
            "                    \"is_public\": true,\n" +
            "                    \"is_delete\": false,\n" +
            "                    \"like\": false,\n" +
            "                    \"belong\": {\n" +
            "                        \"uid\": 1,\n" +
            "                        \"username\": \"gsy666\",\n" +
            "                        \"img_url\": \"http://qimg.hxnews.com/2018/0716/1531706889647.jpg\"\n" +
            "                    },\n" +
            "                    \"reply_to\": {\n" +
            "                        \"uid\": 2,\n" +
            "                        \"username\": \"hjt666\",\n" +
            "                        \"img_url\": \"http://n.sinaimg.cn/ent/transform/w630h630/20180208/YrXA-fyrkuxs3657490.jpg\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"cid\": 6,\n" +
            "            \"text\": \"老婆！！！\",\n" +
            "            \"like_num\": 1,\n" +
            "            \"created_time\": \"2021-03-27T21:38:23.173259\",\n" +
            "            \"is_public\": true,\n" +
            "            \"is_delete\": false,\n" +
            "            \"like\": true,\n" +
            "            \"belong\": {\n" +
            "                \"uid\": 1,\n" +
            "                \"username\": \"gsy666\",\n" +
            "                \"img_url\": \"http://qimg.hxnews.com/2018/0716/1531706889647.jpg\"\n" +
            "            },\n" +
            "            \"replies\": []\n" +
            "        },\n" +
            "        {\n" +
            "            \"cid\": 10,\n" +
            "            \"text\": \"测试一下崩没崩\",\n" +
            "            \"like_num\": 0,\n" +
            "            \"created_time\": \"2021-03-28T15:29:59.968934\",\n" +
            "            \"is_public\": true,\n" +
            "            \"is_delete\": false,\n" +
            "            \"like\": false,\n" +
            "            \"belong\": {\n" +
            "                \"uid\": 1,\n" +
            "                \"username\": \"gsy666\",\n" +
            "                \"img_url\": \"http://qimg.hxnews.com/2018/0716/1531706889647.jpg\"\n" +
            "            },\n" +
            "            \"replies\": [\n" +
            "                {\n" +
            "                    \"cid\": 11,\n" +
            "                    \"text\": \"太好了，没崩！\",\n" +
            "                    \"like_num\": 0,\n" +
            "                    \"created_time\": \"2021-03-28T15:31:41.292740\",\n" +
            "                    \"is_public\": true,\n" +
            "                    \"is_delete\": false,\n" +
            "                    \"like\": false,\n" +
            "                    \"belong\": {\n" +
            "                        \"uid\": 2,\n" +
            "                        \"username\": \"hjt666\",\n" +
            "                        \"img_url\": \"http://n.sinaimg.cn/ent/transform/w630h630/20180208/YrXA-fyrkuxs3657490.jpg\"\n" +
            "                    },\n" +
            "                    \"reply_to\": {\n" +
            "                        \"uid\": 1,\n" +
            "                        \"username\": \"gsy666\",\n" +
            "                        \"img_url\": \"http://qimg.hxnews.com/2018/0716/1531706889647.jpg\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"cid\": 12,\n" +
            "                    \"text\": \"太好了，没崩！\",\n" +
            "                    \"like_num\": 0,\n" +
            "                    \"created_time\": \"2021-03-28T15:32:10.968833\",\n" +
            "                    \"is_public\": true,\n" +
            "                    \"is_delete\": false,\n" +
            "                    \"like\": false,\n" +
            "                    \"belong\": {\n" +
            "                        \"uid\": 2,\n" +
            "                        \"username\": \"hjt666\",\n" +
            "                        \"img_url\": \"http://n.sinaimg.cn/ent/transform/w630h630/20180208/YrXA-fyrkuxs3657490.jpg\"\n" +
            "                    },\n" +
            "                    \"reply_to\": {\n" +
            "                        \"uid\": 1,\n" +
            "                        \"username\": \"gsy666\",\n" +
            "                        \"img_url\": \"http://qimg.hxnews.com/2018/0716/1531706889647.jpg\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"cid\": 13,\n" +
            "                    \"text\": \"太好了，没崩！\",\n" +
            "                    \"like_num\": 0,\n" +
            "                    \"created_time\": \"2021-03-28T15:32:11.413643\",\n" +
            "                    \"is_public\": true,\n" +
            "                    \"is_delete\": false,\n" +
            "                    \"like\": false,\n" +
            "                    \"belong\": {\n" +
            "                        \"uid\": 2,\n" +
            "                        \"username\": \"hjt666\",\n" +
            "                        \"img_url\": \"http://n.sinaimg.cn/ent/transform/w630h630/20180208/YrXA-fyrkuxs3657490.jpg\"\n" +
            "                    },\n" +
            "                    \"reply_to\": {\n" +
            "                        \"uid\": 1,\n" +
            "                        \"username\": \"gsy666\",\n" +
            "                        \"img_url\": \"http://qimg.hxnews.com/2018/0716/1531706889647.jpg\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"cid\": 14,\n" +
            "                    \"text\": \"太好了，没崩！\",\n" +
            "                    \"like_num\": 0,\n" +
            "                    \"created_time\": \"2021-03-28T18:47:03.139941\",\n" +
            "                    \"is_public\": true,\n" +
            "                    \"is_delete\": false,\n" +
            "                    \"like\": false,\n" +
            "                    \"belong\": {\n" +
            "                        \"uid\": 2,\n" +
            "                        \"username\": \"hjt666\",\n" +
            "                        \"img_url\": \"http://n.sinaimg.cn/ent/transform/w630h630/20180208/YrXA-fyrkuxs3657490.jpg\"\n" +
            "                    },\n" +
            "                    \"reply_to\": {\n" +
            "                        \"uid\": 1,\n" +
            "                        \"username\": \"gsy666\",\n" +
            "                        \"img_url\": \"http://qimg.hxnews.com/2018/0716/1531706889647.jpg\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"cid\": 7,\n" +
            "            \"text\": \"神仙姐姐！！！\",\n" +
            "            \"like_num\": 0,\n" +
            "            \"created_time\": \"2021-03-27T21:39:43.643840\",\n" +
            "            \"is_public\": true,\n" +
            "            \"is_delete\": false,\n" +
            "            \"like\": false,\n" +
            "            \"belong\": {\n" +
            "                \"uid\": 2,\n" +
            "                \"username\": \"hjt666\",\n" +
            "                \"img_url\": \"http://n.sinaimg.cn/ent/transform/w630h630/20180208/YrXA-fyrkuxs3657490.jpg\"\n" +
            "            },\n" +
            "            \"replies\": []\n" +
            "        },\n" +
            "        {\n" +
            "            \"cid\": 5,\n" +
            "            \"text\": \"这个小姐姐爱了爱了！\",\n" +
            "            \"like_num\": 0,\n" +
            "            \"created_time\": \"2021-03-27T21:23:19.281322\",\n" +
            "            \"is_public\": true,\n" +
            "            \"is_delete\": false,\n" +
            "            \"like\": false,\n" +
            "            \"belong\": {\n" +
            "                \"uid\": 1,\n" +
            "                \"username\": \"gsy666\",\n" +
            "                \"img_url\": \"http://qimg.hxnews.com/2018/0716/1531706889647.jpg\"\n" +
            "            },\n" +
            "            \"replies\": []\n" +
            "        }\n" +
            "    ]\n" +
            "}";
    private boolean isPlay;
    private boolean isPause;
    private boolean isRelease;

    private OrientationUtils orientationUtils;

    private MediaMetadataRetriever mCoverMedia;

    private ImageView coverImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
        initView();
        String source1 = "https://file.yhf2000.cn/dash/hw.mp4/manifest.mpd";
        String name = "1080P";
        SwitchVideoModel switchVideoModel = new SwitchVideoModel(name, source1);

        String source2 = "https://file.yhf2000.cn/dash/hw.mp4/manifest.mpd";
        String name2 = "720P";
        SwitchVideoModel switchVideoModel2 = new SwitchVideoModel(name2, source2);

        String source3 = "https://file.yhf2000.cn/dash/hw.mp4/manifest.mpd";
        String name3 = "480P";
        SwitchVideoModel switchVideoModel3 = new SwitchVideoModel(name3, source3);

        String source4 = "https://file.yhf2000.cn/dash/hw.mp4/manifest.mpd";
        String name4 = "360P";
        SwitchVideoModel switchVideoModel4 = new SwitchVideoModel(name4, source4);

        List<SwitchVideoModel> list = new ArrayList<>();
        list.add(switchVideoModel);
        list.add(switchVideoModel2);
        list.add(switchVideoModel3);
        list.add(switchVideoModel4);

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

        loadFirstFrameCover(source1);

        Button btn_learn = findViewById(R.id.learn_dance);
        Button btn_free_dance = (Button) findViewById(R.id.free_dance);

        btn_learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, LearnDance.class);
                startActivity(intent);
            }
        });

        btn_free_dance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerActivity.this, FreeDance.class);
                startActivity(intent);
            }
        });

        //detailPlayer.
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

    /**
     * orientationUtils 和  detailPlayer.onConfigurationChanged 方法是用于触发屏幕旋转的
     */
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
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        commentsList = generateTestData();
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
    private List<CommentDetailBean> generateTestData(){
        Gson gson = new Gson();
        commentBean = gson.fromJson(testJosn2, CommentBean.class);
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
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());
                    CommentDetailBean detailBean = new CommentDetailBean(-1,commentContent,0,simpleDateFormat.format(date),
                            true,false,false,
                            new Belong(123,"拒绝者","http://5b0988e595225.cdn.sohucs.com/images/20190122/c26b0dbc2654438a9dbb93713b335b40.jpeg"),
                            null);
                    adapter.addTheCommentData(detailBean);
                    Toast.makeText(PlayerActivity.this,"评论成功",Toast.LENGTH_SHORT).show();

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

    /**
     * by moos on 2018/04/20
     * func:弹出回复框
     */
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
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());
                    ReplyDetailBean detailBean;
                    if(second_position!=-1) {
                        detailBean = new ReplyDetailBean(-1, replyContent, 0, simpleDateFormat.format(date), true, false, false, new Belong(-1,
                                "拒绝者", "http://5b0988e595225.cdn.sohucs.com/images/20190122/c26b0dbc2654438a9dbb93713b335b40.jpeg"),
                                new Belong(commentsList.get(position).getReplies().get(second_position).getBelong().getUid(),
                                        commentsList.get(position).getReplies().get(second_position).getBelong().getUsername(),
                                        commentsList.get(position).getReplies().get(second_position).getBelong().getImg_url()));
                    } else{
                        detailBean = new ReplyDetailBean(-1, replyContent, 0, simpleDateFormat.format(date), true, false, false, new Belong(-1,
                                "拒绝者", "http://5b0988e595225.cdn.sohucs.com/images/20190122/c26b0dbc2654438a9dbb93713b335b40.jpeg"),
                                null);
                    }
                    adapter.addTheReplyData(detailBean, position);
                    expandableListView.expandGroup(position);
                    Toast.makeText(PlayerActivity.this,"回复成功",Toast.LENGTH_SHORT).show();
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

}