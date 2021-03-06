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
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.qydemo0.QYAdapter.GridViewAdapter;
import com.example.qydemo0.QYAdapter.LittleGridViewAdapter;
import com.example.qydemo0.QYpack.AdvanceHttp;
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
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class UploadActivity extends MyAppCompatActivity implements View.OnClickListener{

    protected SimpleExoPlayer player = null;
    Uri videoUri = null;
    String videoUrl = null, coverUrl = null;
    GridLayout tagContainer = null;
    Set<Integer> idSet = new TreeSet<>();
    Set<String> tagSet = new TreeSet<>();
    Map<String, Integer> class_map = new HashMap<>();
    String[] classfi = null;
    TextView clas = null;
    JSONObject cover_json = null, video_json = null;
    PlayerView p;
    GridView class_list;
    LittleGridViewAdapter gridViewAdapter;

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
        p = findViewById(R.id.player_for_upload_video);
        p.setPlayer(player);

        ImageView addTag = findViewById(R.id.button_add_tag);
        addTag.setOnClickListener(this);

        clas = findViewById(R.id.text_class_upload);

        class_list = findViewById(R.id.class_grid);
        gridViewAdapter = new LittleGridViewAdapter(this, R.layout.little_category_item, new ArrayList<Map<String, Object>>(), clas);
        class_list.setAdapter(gridViewAdapter);

        GetClasInfo g = new GetClasInfo(UploadActivity.this);
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
                Toast.makeText(this, "??????????????????~", Toast.LENGTH_LONG).show();
                launcher.launch(true);
                break;
            case R.id.button_upload_selected_video:
                if(videoUri != null){
                    String video_name = ((EditText)findViewById(R.id.edit_text_file_name)).getText().toString(),
                            classification = ((TextView)findViewById(R.id.text_class_upload)).getText().toString();
                    String[] tags = new String[tagSet.size()];
                    String intro = ((EditText)findViewById(R.id.edit_text_introduction)).getText().toString();
                    int i = 0;
                    for(String s : tagSet){
                        tags[i++] = s;
                    }
                    ShowProgressDialog.show(UploadActivity.this, "????????????");
                    Handler handler = new Handler(Looper.myLooper()){
                        @SuppressLint("HandlerLeak")
                        @Override
                        public void handleMessage(@NonNull Message msg){
                            ShowProgressDialog.wait.dismiss();
                            if(msg.arg1 != AdvanceHttp.finish_code){
                                Toast.makeText(UploadActivity.this, msg.getData().getString("msg") + ".", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(UploadActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                                UploadActivity.this.finish();
                            }
                        }
                    };
                    AdvanceHttp.uploadWorkAllIn(handler, Uri2RealPath.getRealPathFromUri_AboveApi19(getApplicationContext(), videoUri),
                            this, new VideoInfo(video_name, class_map.get(classification), intro, tags));
                }
                else
                    Toast.makeText(UploadActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_add_tag:
                EditText tag = findViewById(R.id.edit_text_video_tag);
                String s = tag.getText().toString();
                if(s.length() == 0) Toast.makeText(UploadActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
                else if(idSet.size() == 5) Toast.makeText(UploadActivity.this, "????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                else if(tagSet.contains(s)) Toast.makeText(UploadActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                else {
                    tag.setText("");
                    TextView newTag = new TextView(this);
                    int pad = 5;
                    newTag.setPadding(pad, pad, pad, pad);
                    newTag.setText(s);
                    newTag.setBackground(ContextCompat.getDrawable(this, R.drawable.tag));
                    newTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
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
            p.setVisibility(View.VISIBLE);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }
    });

    class GetClasInfo extends MyAsyncTask<String, Integer, String> {

        protected GetClasInfo(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return htp.advanceGet(Constant.mInstance.getClas_url, "Authorization", GlobalVariable.mInstance.token);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("hjt.upload.get.class", s);
            JSONObject json = MsgProcess.msgProcess(s, false, null);
            if(json != null){
                try {
                    JSONArray ja = json.getJSONArray("classification");
                    classfi = new String[ja.length()];
                    for(int i = 0; i < ja.length(); i++){
//                        Log.d("hjt.show_data", ((JSONObject)ja.get(i)).toString());
                        classfi[i] = ((JSONObject)ja.get(i)).getString("name");
                        class_map.put(classfi[i], ((JSONObject)ja.get(i)).getInt("id"));
                    }

                    for(int i = 0; i < ja.length(); i++){
                        JSONObject js = ja.getJSONObject(i);
                        Map<String, Object> map = new HashMap<>();
                        map.put("text", js.getString("name"));
                        map.put("image", js.getString("img_url"));
                        gridViewAdapter.addData(map);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else Log.e("hjt.classification", "null");
        }
    }

    class UploadVideoInfo extends MyAsyncTask<VideoInfo, Integer, String>{


        protected UploadVideoInfo(MyAppCompatActivity activity) {
            super(activity);
        }

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
            if(MsgProcess.checkMsg(s, false, null)) {
                Toast.makeText(UploadActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                UploadActivity.this.finish();
            }
            else {
                Toast.makeText(UploadActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                UploadActivity.this.finish();
            }
        }
    }

    class UploadVideo extends MyAsyncTask<String, Integer, Boolean>{


        protected UploadVideo(MyAppCompatActivity activity) {
            super(activity);
        }

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

    class UploadCover extends MyAsyncTask<String, Integer, Boolean>{

        protected UploadCover(MyAppCompatActivity activity) {
            super(activity);
        }

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

    class HashVideo extends MyAsyncTask<InputStream, Integer, JSONObject>{

        protected HashVideo(MyAppCompatActivity activity) {
            super(activity);
        }

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
                        Toast.makeText(UploadActivity.this, "??????????????????", Toast.LENGTH_LONG).show();
                    }
                    else {
                        UploadVideo uploadVideo = new UploadVideo(UploadActivity.this);
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

    class HashCover extends MyAsyncTask<InputStream, Integer, JSONObject>{

        protected HashCover(MyAppCompatActivity activity) {
            super(activity);
        }

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
                    UploadCover uploadCover = new UploadCover(UploadActivity.this);
                    uploadCover.execute(jsonObject.getString("upload_url"), coverUrl, jsonObject.getString("token"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}