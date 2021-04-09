package com.example.qydemo0;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.AsyncTaskLoader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYFile;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.SHA256;
import com.example.qydemo0.QYpack.ShowProgressDialog;
import com.example.qydemo0.QYpack.Uri2RealPath;
import com.example.qydemo0.QYpack.Video.VideoInfo;
import com.example.qydemo0.QYpack.VideoClip;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{

    protected SimpleExoPlayer player = null;
    Uri videoUri = null;
    String videoUrl = null, coverUrl = null;
    GridLayout tagContainer = null;
    Set<Integer> idSet = new TreeSet<>();
    Set<String> tagSet = new TreeSet<>();
    Map<String, Integer> class_map = new HashMap<>();
    String[] classfi = null;
    AutoCompleteTextView clas = null;
    JSONObject cover_json = null, video_json = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        tagContainer = findViewById(R.id.tags);
        Button btn = findViewById(R.id.button_browse_file);
        btn.setOnClickListener(this);
        btn = findViewById(R.id.button_upload_selected_video);
        btn.setOnClickListener(this);

        player = new SimpleExoPlayer.Builder(getBaseContext()).build();
        PlayerView p = findViewById(R.id.player_for_upload_video);
        p.setPlayer(player);

        ImageView addTag = findViewById(R.id.button_add_tag);
        addTag.setOnClickListener(this);

        clas = findViewById(R.id.text_class_upload);
        clas.setThreshold(0);//最多几个字符开始匹配
        clas.setDropDownHeight(500);//设置下拉菜单高度
        clas.setDropDownHorizontalOffset(10);//设置下路列表和文本框水平偏移
        clas.setDropDownVerticalOffset(-500-200);//设置下拉列表和文本框垂直偏移
        clas.setDropDownWidth(800);//设置下拉列表宽度

        GetClasInfo g = new GetClasInfo();
        g.execute();

        GlobalVariable.mInstance.appContext = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_browse_file:
                qyr.params = "video";
                launcher.launch(true);
                break;
            case R.id.button_upload_selected_video:
                if(videoUri != null){
                    String video_name = ((EditText)findViewById(R.id.edit_text_file_name)).getText().toString(),
                            classification = ((EditText)findViewById(R.id.text_class_upload)).getText().toString();
                    String[] tags = new String[tagSet.size()];
                    String intro = ((EditText)findViewById(R.id.edit_text_introduction)).getText().toString();
                    int i = 0;
                    for(String s : tagSet){
                        tags[i++] = s;
                    }
                    String checkMsg = VideoInfo.checkMsg(video_name, classification, tags);
                    if(checkMsg != null){
                        Toast.makeText(this, checkMsg, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ShowProgressDialog.show(UploadActivity.this, "上传视频");
                    try {
                        HashVideo h = new HashVideo();
                        InputStream inputStream = getContentResolver().openInputStream(videoUri);
                        h.execute(inputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    VideoClip videoClip = new VideoClip();
                    videoUrl = Uri2RealPath.getRealPathFromUri_AboveApi19(getApplicationContext(), videoUri);
                    Bitmap cover = videoClip.getCoverFromVideo(videoUrl);
                    coverUrl = Img.saveImg(cover, String.valueOf(cover.hashCode()), UploadActivity.this);
                    try {
                        HashCover hashCover = new HashCover();
                        InputStream inputStream = new FileInputStream(new File(coverUrl));
                        hashCover.execute(inputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    UploadVideoInfo uploadVideoInfo = new UploadVideoInfo();
                    uploadVideoInfo.execute(new VideoInfo(video_name, class_map.get(classification), intro, tags));
                }
                else
                    Toast.makeText(UploadActivity.this, "未选择视频", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_add_tag:
                EditText tag = findViewById(R.id.edit_text_video_tag);
                String s = tag.getText().toString();
                if(s.length() == 0) Toast.makeText(UploadActivity.this, "请填写标签", Toast.LENGTH_SHORT).show();
                else if(idSet.size() == 5) Toast.makeText(UploadActivity.this, "不要添加太多呀，可以点击标签删除", Toast.LENGTH_SHORT).show();
                else if(tagSet.contains(s)) Toast.makeText(UploadActivity.this, "该标签已经存在", Toast.LENGTH_SHORT).show();
                else {
                    tag.setText("");
                    TextView newTag = new TextView(this);
                    int pad = 5;
                    newTag.setPadding(pad, pad, pad, pad);
                    newTag.setText(s);
                    newTag.setBackground(ContextCompat.getDrawable(this, R.drawable.tag));
                    newTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                    newTag.setTextColor(ContextCompat.getColor(this, R.color.exo_white));
                    Integer id = View.generateViewId();
                    idSet.add(id);
                    newTag.setId(id);
                    newTag.setOnClickListener(this);
                    tagSet.add(s);
                    tagContainer.addView(newTag);
                }
        }
        if(idSet.contains(v.getId())){
            idSet.remove(v.getId());
            tagSet.remove(((TextView)v).getText().toString());
            tagContainer.removeView(v);
        }
    }

    public String getTags(){
        String s = "";
        boolean first = true;
        for(String t : tagSet){
            if(!first) s += '.';
            s += t;
        }
        return s;
    }
    QYFile.ResultContract qyr = new QYFile.ResultContract();
    ActivityResultLauncher launcher = registerForActivityResult(qyr, new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if(result == null) return;
            ((TextView)findViewById(R.id.edit_text_file_url)).setText(getResources().getString(R.string.get_file_now));
            MediaItem mediaItem = MediaItem.fromUri(result);
            videoUri = result;
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }
    });

    class GetClasInfo extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return htp.advanceGet(Constant.mInstance.getClas_url, "Authorization", GlobalVariable.mInstance.token);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("hjt.upload.get.class", s);
            JSONObject json = MsgProcess.msgProcess(s, true);
            if(json != null){
                try {
                    JSONArray ja = json.getJSONArray("classification");
                    classfi = new String[ja.length()];
                    for(int i = 0; i < ja.length(); i++){
                        classfi[i] = ((JSONObject)ja.get(i)).getString("name");
                        class_map.put(classfi[i], ((JSONObject)ja.get(i)).getInt("id"));
                    }
                    ArrayAdapter<String> adapter =new ArrayAdapter<String>(UploadActivity.this, R.layout.auto_complete_textview, classfi);//适配器
                    clas.setAdapter(adapter);//设置适配器
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else Log.e("hjt.classification", "null");
        }
    }


    class UploadVideoInfo extends AsyncTask<VideoInfo, Integer, String>{


        @Override
        protected String doInBackground(VideoInfo... v) {
            VideoInfo videoInfo = v[0];
            while(cover_json == null || video_json == null);
            try {
                videoInfo.coverId = cover_json.getString("file_id");
                videoInfo.videoId = video_json.getString("file_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            QYrequest htp = new QYrequest();
            return htp.advancePost(videoInfo.toData(), Constant.mInstance.work_url, "Authorization", GlobalVariable.mInstance.token);
        }

        @Override
        protected void onPostExecute(String s) {
            ShowProgressDialog.wait.dismiss();
            Log.d("hjt.upload.video.info", s);
            if(MsgProcess.checkMsg(s, true)) Toast.makeText(UploadActivity.this, "成功上传", Toast.LENGTH_SHORT).show();
            else Toast.makeText(UploadActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
        }
    }


    class UploadVideo extends AsyncTask<String, Integer, Boolean>{


        @Override
        protected Boolean doInBackground(String... strings) {
            String filePath = strings[0], url = strings[1], token = strings[2];
            QYFile qyFile = new QYFile();
            return qyFile.uploadFile(url, filePath, token);
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if(s) Log.d("hjt.upload.video", "ok");
            else Log.d("hjt.upload.video", "wrong");
        }
    }

    class UploadCover extends AsyncTask<String, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            String file_path = strings[1], http_url = strings[0], token = strings[2];
            QYFile qyFile = new QYFile();
            return qyFile.uploadFile(http_url, file_path, token);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean) Log.d("hjt.upload.cover", "ok");
            else Log.d("hjt.upload.cover", "wrong");
        }
    }


    class HashVideo extends AsyncTask<InputStream, Integer, JSONObject>{

        @Override
        protected JSONObject doInBackground(InputStream... inputStreams) {
            InputStream is = inputStreams[0];

            QYFile qyFile = new QYFile();
            String hash = qyFile.hash(is, Constant.mInstance.MAX_FILE_SIZE);

            Log.e("hjt.hash.video", hash);

            return qyFile.verifyFileUpload(Constant.mInstance.file_upload_verify_url, 2, hash);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if(json != null){
                video_json = json;
                try {
                    if(json.getBoolean("rapid_upload")){
//                        Toast.makeText(UploadActivity.this, "该视频已存在", Toast.LENGTH_LONG).show();
                    }
                    else {
                        UploadVideo uploadVideo = new UploadVideo();
                        uploadVideo.execute(videoUrl, json.getString("upload_url"), json.getString("token"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Log.e("hjt.UploadAC.verify", "JSON_NULL");
            }
        }
    }

    class HashCover extends AsyncTask<InputStream, Integer, JSONObject>{

        @Override
        protected JSONObject doInBackground(InputStream... inputStreams) {
            InputStream is = inputStreams[0];
            QYFile qyFile = new QYFile();
            String hash = qyFile.hash(is, Constant.mInstance.MAX_FILE_SIZE);
            Log.e("hjt.hash.video.cover", hash);
            return qyFile.verifyFileUpload(Constant.mInstance.file_upload_verify_url, 0, hash);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Log.d("hjt.verify.cover", jsonObject.toString());
            if(jsonObject != null){
                cover_json = jsonObject;
                try {
                    UploadCover uploadCover = new UploadCover();
                    uploadCover.execute(jsonObject.getString("upload_url"), coverUrl, jsonObject.getString("token"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}