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
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYFile;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.SHA256;
import com.example.qydemo0.QYpack.ShowProgressDialog;
import com.example.qydemo0.QYpack.Uri2RealPath;
import com.example.qydemo0.QYpack.Video.VideoInfo;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{

    protected SimpleExoPlayer player = null;
    Uri uri = null;
    String realURL = null;
    Constant C = Constant.mInstance;
    GridLayout tagContainer = null;
    Set<Integer> idSet = new TreeSet<>();
    Set<String> tagSet = new TreeSet<>();
    String videoId = null, coverId = null;
    String[] classfi = null;
    AutoCompleteTextView clas = null;

    public static void loadCover(ImageView imageView, String url, Context context) {

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context)
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .frame(1000000)
                                .centerCrop()
                )
                .load(url)
                .into(imageView);
    }

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
        clas.setThreshold(1);//最多几个字符开始匹配
        clas.setDropDownHeight(800);//设置下拉菜单高度
        clas.setDropDownHorizontalOffset(0);//设置下路列表和文本框水平偏移
        clas.setDropDownVerticalOffset(900);//设置下拉列表和文本框垂直偏移
        clas.setDropDownWidth(500);//设置下拉列表宽度
        GetClasInfo g = new GetClasInfo();
        g.execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_browse_file:
                launcher.launch(true);
                break;
            case R.id.button_upload_selected_video:
                Log.d("hjt", "...");
                if(uri != null){
                    HashThenPost h = new HashThenPost();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        h.execute(inputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(UploadActivity.this, "未选择视频", Toast.LENGTH_SHORT).show();
                }
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

    ActivityResultLauncher launcher = registerForActivityResult(new ResultContract(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if(result == null) return;
            ((TextView)findViewById(R.id.edit_text_file_url)).setText(getResources().getString(R.string.get_file_now));
            MediaItem mediaItem = MediaItem.fromUri(result);
            uri = result;
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }
    });

    class ResultContract extends ActivityResultContract<Boolean, Uri> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Boolean input) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            return intent;
        }

        @Override
        public Uri parseResult(int resultCode, @Nullable Intent intent) {
            if (intent != null) {
                return intent.getData();
            }
            else {
                Log.e("hjt.GetVideo.Null", "null");
                return null;
            }
        }
    }

    class GetClasInfo extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return htp.advanceGet(Constant.mInstance.getClas_url, "Authorization", GlobalVariable.mInstance.token);
        }

        @Override
        protected void onPostExecute(String s) {
            JSONObject json = MsgProcess.msgProcess(s);
            if(json != null){
                try {
                    JSONArray ja = json.getJSONArray("classification");
                    classfi = new String[ja.length()];
                    for(int i = 0; i < ja.length(); i++){
                        classfi[i] = ((JSONObject)ja.get(i)).getString("name");
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
        protected String doInBackground(VideoInfo... videoInfos) {
            VideoInfo videoInfo = videoInfos[0];
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("hjt", s);
            super.onPostExecute(s);
        }
    }

    class UploadVideo extends AsyncTask<String, Integer, Boolean>{

        @Override
        protected void onPreExecute() {
            ShowProgressDialog.show(UploadActivity.this, "上传中");
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String filePath = strings[0], url = strings[1], token = strings[2];
            QYFile qyFile = new QYFile();
            return qyFile.uploadFile(url, filePath, token);
        }

        @Override
        protected void onPostExecute(Boolean s) {
            ShowProgressDialog.wait.dismiss();
            if(s){
                Toast.makeText(UploadActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                EditText videoName = findViewById(R.id.edit_text_file_name), videoIntro = findViewById(R.id.edit_text_introduction);
                UploadVideoInfo uploadVideoInfo = new UploadVideoInfo();
//                uploadVideoInfo.execute(videoName.getText().toString(), clas.getText().toString(), videoIntro.getText().toString(), getTags());
            }
        }
    }

    class UploadCover extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            String file_path = strings[0], http_url = strings[1];
            QYFile qyFile = new QYFile();
            return null;
        }
    }


    class HashThenPost extends AsyncTask<InputStream, Integer, JSONObject>{

        @Override
        protected void onPreExecute() {
            ShowProgressDialog.show(UploadActivity.this, "对视频进行hash处理");
        }

        @Override
        protected JSONObject doInBackground(InputStream... inputStreams) {
            InputStream is = inputStreams[0];

            QYFile qyFile = new QYFile();
            String hash = qyFile.hash(is, 1024 * 1000 * 50); // 50MB

            ShowProgressDialog.wait.setMessage("哈希完成");
            Log.e("hjtsha256", hash);

            return qyFile.verifyFileUpload(Constant.mInstance.file_upload_verify_url, 2, hash);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            ShowProgressDialog.wait.dismiss();
            if(json != null){
                try {
                    if(json.getBoolean("rapid_upload")){
                        Toast.makeText(UploadActivity.this, "该视频已存在", Toast.LENGTH_LONG).show();
                        return;
                    }
                    UploadVideo uploadVideo = new UploadVideo();
                    String fileUrl = Uri2RealPath.getRealPathFromUri_AboveApi19(getApplicationContext(), uri);
                    uploadVideo.execute(fileUrl, json.getString("upload_url"), json.getString("token"));
                    UploadCover uploadCover = new UploadCover();
                    uploadCover.execute(fileUrl, json.getString("upload_url"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Log.e("hjt.UploadAC.verify", "JSON_NULL");
            }
        }
    }
}