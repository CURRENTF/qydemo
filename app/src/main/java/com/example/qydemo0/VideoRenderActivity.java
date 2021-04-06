package com.example.qydemo0;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.QYFile;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.VideoClip;
import com.example.qydemo0.bean.CallBackBean;
import com.google.gson.Gson;
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

    private ProgressDialog progressDialog;

    private int[] render_paras = {0,0,0};

    private Boolean isYuLan = false;

    private String render_img;

    private QYrequest cur_request = new QYrequest();
    private QYFile cur_file = new QYFile();

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
                showProgressDialog("提示","加载中...");
                isYuLan = false;
                new SendRenderVideo().execute(free_dance_url);
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

//    //根据路径展示图片的方法
//    private void displayImage(String imagePath){
//        if (imagePath != null){
//            Log.i("img_path",imagePath);
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//            test_img.setImageBitmap(bitmap);
//        }else{
//            Toast.makeText(this,"fail to set image",Toast.LENGTH_SHORT).show();
//        }
//    }

    public class SendRenderVideo extends AsyncTask<String , Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            String will_do_url = strings[0];

            JSONObject res_json = cur_file.verifyFileUpload(Constant.mInstance.file_upload_verify_url,2,cur_file.hashFileUrl(will_do_url));
            int render_video_id = -1;
            try {
                if(!res_json.getBoolean("rapid_upload")){
                    try {
                        if(!cur_file.uploadFile(Constant.mInstance.file_upload_callback_url, will_do_url, res_json.getString("token"))){
                            return null;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                render_video_id = res_json.getInt("file_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String img_id,img_mode,lj_id;
            int render_img_id = -1;
                if(render_paras[0] == -2){

                        JSONObject res_json1 = cur_file.verifyFileUpload(Constant.mInstance.file_upload_verify_url,0,cur_file.hashFileUrl(render_img));

                    try {
                        if(!res_json1.getBoolean("rapid_upload")){
                            if(!cur_file.uploadFile(Constant.mInstance.file_upload_callback_url, render_img, res_json1.getString("token"))){
                                return null;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        render_img_id = res_json1.getInt("file_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            Boolean choice1 = render_paras[0]==0?false:true,
                    choice2 = render_paras[1]==0?false:true;
            String[] callToJson = {"video", "int", ""+render_video_id,
                    "is_background", "bool", choice1?"true":"false",
            "img_id", choice1?"string":"int", choice1?(""+render_img_id):"null",
            "mode", choice1?"bool":"int", choice1?(""+"false"):"null", /*这里加渲染模型！！！！！！！！！！！*/
            "is_filter","bool",choice2?"true":"false",
            "filter_id", choice1?"string":"int", choice1?(""+(render_paras[1]-1)):"null"
            };
            String resJson = cur_request.advancePost(GenerateJson.universeJson2(callToJson), Constant.mInstance.task_url+"rendering/", "Authorization", GlobalVariable.mInstance.token);

            try {
                String tid;
                JSONObject ress_json = new JSONObject(resJson);
                if(ress_json.getString("msg").equals("Success")){
                    tid = ress_json.getJSONObject("data").getString("tid");
                }
                else return null;
                while(true){
                    Thread.sleep(500);
                    String render_res = cur_request.advancePost(GenerateJson.universeJson("tid",tid),Constant.mInstance.task_url+"task/",
                            "Authorization", GlobalVariable.mInstance.token);
                    JSONObject render_res_json = new JSONObject(render_res);
                    if(!isYuLan){
                        if(render_res_json.getInt("schedule")!=-1){
                            return "success";
                        }
                    }
                    if(render_res_json.getInt("schedule") == 100){
                        return render_res_json.getJSONObject("data").getJSONObject("video_url").getString("1080P");
                    }
                }
            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            hideProgressDialog();
                if(isYuLan){
                    if(s!=null){
                        updatePlayer(s);
                    }
                    else{
                        Toast.makeText(VideoRenderActivity.this,"预览失败",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(VideoRenderActivity.this,"开始渲染，请到渲染列表查看进度", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(VideoRenderActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

        }
}