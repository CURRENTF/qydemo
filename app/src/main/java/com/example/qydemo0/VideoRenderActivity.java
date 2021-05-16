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
import com.example.qydemo0.QYpack.WaveLoadDialog;
import com.example.qydemo0.Widget.QYDIalog;
import com.example.qydemo0.Widget.QYDialogUncancelable;
import com.example.qydemo0.bean.CallBackBean;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.gson.Gson;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.media.MediaMetadataRetriever;
import android.widget.RelativeLayout;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static android.telecom.DisconnectCause.LOCAL;
import static android.view.Gravity.*;
import static com.google.android.exoplayer2.scheduler.Requirements.NETWORK;
import org.json.JSONObject;

public class VideoRenderActivity extends AppCompatActivity {

    String free_dance_url = "";

    String clip_video_url = "";

    StandardGSYVideoPlayer videoPlayer;
    
    private WaveLoadDialog dialog;
    private QYDialogUncancelable dialog_loading;
    private AVLoadingIndicatorView avi;

    private int[] render_paras = {0,0,0};

    private Boolean isYuLan = false;

    private String render_img;

    private QYrequest cur_request = new QYrequest();
    private QYFile cur_file = new QYFile();

    private CVUnitClient mCVClient;
    private int startCode;

//    OrientationUtils orientationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_render);
//        final Intent intent = getIntent();
//        free_dance_url = intent.getStringExtra("free_dance_url");

        free_dance_url = "/sdcard/Pictures/QQ/【SPEC舞蹈】《Uh-Oh》-女团(G)I-DLE热单韩舞翻跳（单人版）.mp4";
        Log.e("free_dance_url",free_dance_url);

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
                        int[] render_paras_cur = new int[3];
                       render_paras_cur = popupWindowRight.getRenderParams();
                       render_paras[0] = render_paras_cur[0];
                        render_paras[1] = render_paras_cur[1];
                        render_paras[2] = render_paras_cur[2];
                        render_reset.setVisibility(View.GONE);
                        popupWindowRight.dismiss();
                        render_choice.setVisibility(View.VISIBLE);
                        isYuLan = true;
                        //showProgressDialog("提示","正在努力加载渲染预览视频...");
                        new SendRenderVideo().execute(clip_video_url);
                    }
                });

            }
        });

        Button btn_render = (Button) findViewById(R.id.start_render);
        btn_render.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("paras",""+render_paras[0]+" "+render_paras[1]+" "+render_paras[2]);
                //showProgressDialog("提示","加载中...");
                isYuLan = false;
                new SendRenderVideo().execute(free_dance_url);
            }
        });

//        Button btn_mode = (Button) findViewById(R.id.render_mode);
//        btn_mode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(btn_mode.getText().equals("快速")){
//                    render_paras[3] = 0;
//                    btn_mode.setText("精准");
//                } else {
//                    render_paras[3] = 1;
//                    btn_mode.setText("快速");
//                }
//            }
//        });

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

        //AI Unit
        mCVClient.initService(this, new ConnectionCallback() {
            @Override
            public void onServiceConnect() {
                Log.i("TAG", "initService: onServiceConnect");
                startCode = mCVClient.start();
            }

            @Override
            public void onServiceDisconnect() {
                Log.e("TAG", "initService: onServiceDisconnect: ");
            }
        });

    }

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
            long endTime = mid_time+2500 > total_time ? total_time : mid_time+2500;

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
        
    private void updatePlayer(String res_urls) {

        videoPlayer.setUp(res_urls,true,"渲染预览视频");
        videoPlayer.startPlayLogic();
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            if(resultCode == RESULT_OK&&data!=null){
                if(Build.VERSION.SDK_INT>=19){
                    render_img = handImage(data);
                }
                else{
                    render_img = handImageLow(data);
                }
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String handImage(Intent data){
        String path =null;
        Uri uri = data.getData();
        //根据不同的uri进行不同的解析
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+id;
                path = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                path = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            path = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            path = uri.getPath();
        }
        return path;
        //展示图片
        //displayImage(path);
    }


    //安卓小于4.4的处理方法
    private String handImageLow(Intent data){
        Uri uri = data.getData();
        return getImagePath(uri,null);
//        displayImage(path);
    }

    //content类型的uri获取图片路径的方法
    private String getImagePath(Uri uri,String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public Bitmap getStyleBitmap(Bitmap cur_bitmap){

        FrameInputSlot inputSlot = (FrameInputSlot) mCVClient.createInputSlot();
        inputSlot.setTargetBitmap(cur_bitmap);
        FrameOutputSlot outputSlot = (FrameOutputSlot) mCVClient.createOutputSlot();
        mCVClient.process(inputSlot, outputSlot);
        FrameData frameData = outputSlot.getOutFrameData();
        byte[] outImageBuffer = frameData.getData();

// RGB buffer.
        int[] colors = new int[outImageBuffer.length / 3];
        for (int j = 0; j < frameData.height; ++j) {

            for (int i = 0; i < frameData.width; ++i) {

                int red = outImageBuffer[3 * (j * frameData.width + i)];

                int green = outImageBuffer[3 * (j * frameData.width + i) + 1];

                int blue = outImageBuffer[3 * (j * frameData.width  + i) + 2];

                int alpha = 0xFF;

                colors[j * frameData.width + i] = (alpha << 24) | (red << 16) | (green << 8) | (blue);

            }
        }

        Bitmap resultBmp = Bitmap.createBitmap(colors, frameData.width, frameData.height, Bitmap.Config.ARGB_8888);
        System.out.println(String.valueOf(resultBmp==null));
        return resultBmp;
    }

    @Override
    protected void onDestroy() {
        if (mCVClient != null) {
            mCVClient.stop();
        }
        mCVClient.releaseService();
        mCVClient = null;
        super.onDestroy();
    }

    public class SendRenderVideo extends AsyncTask<String , String, String>{


        private String doBG(){
            int bg = render_paras[0], st = render_paras[2];
            if(bg==0) return null;
            String render_img_id = "";
            int[] imgs = {R.drawable.airam_dato_on_unsplash,R.drawable.christmas_3009949_1920,R.drawable.damiano_ferrante_unsplash,R.drawable.design_3289964,
                    R.drawable.efe_kurnaz_unsplash,R.drawable.halloween_72939_1280,R.drawable.jonathan_hanna_unsplash,R.drawable.lennon_cheng_unsplash,
                    R.drawable.painting_3135875_1920,R.drawable.space_4152623_1920, R.drawable.summer_4181783,R.drawable.abstract_2468874_1920};
            if (st == 1) {
                for (int o = 0; o < 5; o++) {
                    if (startCode == 0) {
                        if (bg == -2) {
                            Bitmap crs = getStyleBitmap(Img.getBitmapFromLocalUrl(render_img));
                            Log.i("whc_crs", String.valueOf(crs==null));
                            render_img = Img.saveImg(crs, "", VideoRenderActivity.this);
                        }
                        else {
                                render_img = Img.saveImg(getStyleBitmap(BitmapFactory.decodeResource(getResources(), imgs[bg-1])), "", VideoRenderActivity.this);
                        }
                        Log.e("style_img", render_img);
                        break;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                String cu = Img.compressWithUrl(render_img,VideoRenderActivity.this);
                render_img_id = cur_file.uploadFileAllIn(Constant.mInstance.file_upload_verify_url, cu, 0, cur_file.hashFileUrl(cu));
                Log.e("render_img_id_after_style",String.valueOf(render_img_id==null));
            } else {
                if (bg == -2) {
                    String cu = Img.compressWithUrl(render_img,VideoRenderActivity.this);
                    render_img_id = cur_file.uploadFileAllIn(Constant.mInstance.file_upload_verify_url, cu, 0, cur_file.hashFileUrl(cu));
                    Log.i("12312","成功上传！");
                } else {
                    try {
                            String cur_url = Img.saveImg(Img.getBitmapFormUri(VideoRenderActivity.this, Uri.parse("android.resource://"
                                    + getApplicationContext().getPackageName() + "/" + imgs[bg-1])), "", VideoRenderActivity.this);
                            cur_url = Img.compressWithUrl(cur_url, VideoRenderActivity.this);
                            render_img_id = cur_file.uploadFileAllIn(Constant.mInstance.file_upload_verify_url, cur_url, 0, cur_file.hashFileUrl(cur_url));


                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
            return render_img_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(isYuLan){
                dialog = new WaveLoadDialog(VideoRenderActivity.this);
                dialog.start_progress();
            }
            else{
                dialog_loading = new QYDialogUncancelable(VideoRenderActivity.this, R.layout.loading_dialog, new int[]{R.id.avi});
                dialog_loading.show();
                avi = (AVLoadingIndicatorView) dialog_loading.findViewById(R.id.avi);
                avi.smoothToShow();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            String will_do_url = strings[0];
            String render_img_id = null;
            VideoClip vp = new VideoClip();
            String cover_path = Img.saveImg(vp.getCoverFromVideo(will_do_url),"123",VideoRenderActivity.this);
            Log.e("cover_path", cover_path);
            String cover_id = cur_file.uploadFileAllIn(Constant.mInstance.file_upload_verify_url,
                    cover_path, 0,cur_file.hashFileUrl(cover_path));
            Log.e("cover_id", cover_id);
            if(cover_id==null) return null;
            String render_video_id = cur_file.uploadFileAllIn(Constant.mInstance.file_upload_verify_url,will_do_url, 2, cur_file.hashFileUrl(will_do_url));
            if(render_video_id!=null){

                render_img_id = doBG();

                Log.i("here",String.valueOf(render_img_id==null));
                    List<String> callToJson = new ArrayList<>();
                    callToJson.add("video");callToJson.add("string");callToJson.add(render_video_id);
                    callToJson.add("cover");callToJson.add("string");callToJson.add(cover_id);
                   if(render_paras[0]!=0){
                        callToJson.add("is_background");callToJson.add("bool");callToJson.add("true");
                        callToJson.add("img_id");callToJson.add("string");callToJson.add(render_img_id);
                    } else {
                        callToJson.add("is_background");callToJson.add("bool");callToJson.add("false");
                    }
//                    callToJson.add("mode");callToJson.add("bool");callToJson.add(render_paras[3]==1?"true":"false");
                    if(render_paras[1]!=0){
                        callToJson.add("is_filter");callToJson.add("bool");callToJson.add("true");
                        callToJson.add("filter_id");callToJson.add("string");callToJson.add(""+(render_paras[1]-1));
                    }
                    else{
                        callToJson.add("is_filter");callToJson.add("bool");callToJson.add("false");
                    }
                    System.out.println(GenerateJson.universeJson2(callToJson.toArray(new String[callToJson.size()])));
                    String res_json = cur_request.advancePost(GenerateJson.universeJson2(callToJson.toArray(new String[callToJson.size()])),Constant.mInstance.task_url+"rendering/", "Authorization", GlobalVariable.mInstance.token);
                    try {
                        JSONObject res_json_object = new JSONObject(res_json);
                        String tid = "";
                        Log.e("res_json_object", String.valueOf(res_json_object));
                        if(res_json_object.getString("msg").equals("Success")){
                            tid = res_json_object.getJSONObject("data").getString("tid");
                            if(!isYuLan) return "Success";
                        }
                        else {
                            return null;
                        }
                        for(int i=0;i<200;i++){
                            Thread.sleep(1000);
                            JSONObject res_json_rendered = new JSONObject(cur_request.advanceGet(Constant.mInstance.task_url+"schedule/"+tid+"/",
                                    "Authorization",GlobalVariable.mInstance.token));
                            Log.i("whc123",tid+" "+res_json_rendered.getJSONObject("data").getJSONObject("task").getInt("prog"));
                            int cur_schedule = res_json_rendered.getJSONObject("data").getJSONObject("task").getInt("prog");
                            if(res_json_rendered.getJSONObject("data").getJSONObject("task").getInt("is_finish")==1){
                                JSONObject cur_urls = res_json_rendered.getJSONObject("data").getJSONObject("data").getJSONObject("video_url").getJSONObject("url");
                                Log.i("whc_urls", String.valueOf(cur_urls));
                                if(cur_urls.has("1080P"))
                                    return cur_urls.getString("1080P");
                                if(cur_urls.has("720P"))
                                    return cur_urls.getString("720P");
                                if(cur_urls.has("480P"))
                                    return cur_urls.getString("480P");
                                if(cur_urls.has("360P"))
                                    return cur_urls.getString("360P");
                                if(cur_urls.has("自动"))
                                    return cur_urls.getString("自动");
                            } else {
                                publishProgress(cur_schedule==100?"99":String.valueOf(cur_schedule), res_json_rendered.getJSONObject("data").getJSONObject("task").isNull("step")?"":res_json_rendered.getJSONObject("data").getJSONObject("task").getString("step"));
                            }
                        }
                        return null;
                    } catch (JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //hideProgressDialog();
            if(s==null){
                Toast.makeText(VideoRenderActivity.this,"渲染出错，请重新尝试",Toast.LENGTH_LONG).show();
            } else {
                if (isYuLan) {
                    dialog.stop_progress();
                    Log.i("whc_url", s);
                    updatePlayer(s);
                } else {
                    avi.smoothToHide();
                    dialog_loading.dismiss();
                    Toast.makeText(VideoRenderActivity.this, "开始渲染，请到渲染列表查看进度", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(VideoRenderActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
            }
        @Override
        protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                dialog.set_progress(Integer.valueOf(values[0])/100f, values[1]);
            }

        }

}