package com.example.qydemo0;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.SHA256;
import com.example.qydemo0.QYpack.ShowProgressDialog;
import com.example.qydemo0.QYpack.Uri2RealPath;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
            intent.setType("video/*");
            return intent;
        }

        @Override
        public Uri parseResult(int resultCode, @Nullable Intent intent) {
            if (intent != null) {
                return intent.getData();
            }
            else {
                Toast.makeText(UploadActivity.this, "未选择视频", Toast.LENGTH_SHORT).show();
                Log.e("hjtGetVideoNull", "oh");
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
            else Log.e("hjtclassification", "null");
        }
    }

    class UploadVideoInfo extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            String name = strings[0], clas = strings[1],  intro = strings[2];
            QYrequest htp = new QYrequest();
            return htp.advancePost(GenerateJson.universeJson("name", name, "introduction", intro, "video", videoId, "cover", coverId, "tag", GenerateJson.listString(3, strings), "classfication", clas), Constant.mInstance.work, "Authorization", GlobalVariable.mInstance.token);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("hjt", s);
            super.onPostExecute(s);
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

    class UploadVideo extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            String filePath = strings[0], fileName = strings[1], url = strings[2], token = strings[3], file_id = strings[4];
            QYrequest htp = new QYrequest();
            return htp.postWithFile(filePath, fileName, url, token);
        }

        @Override
        protected void onPostExecute(String msg) {
            Log.d("hjtupload", msg);
            JSONObject json = null;
            try {
                json = new JSONObject(msg);
                ShowProgressDialog.wait.dismiss();
                if(json.getInt("code") == C.HTTP_OK) {
                    Toast.makeText(UploadActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                    EditText videoName = findViewById(R.id.edit_text_file_name), videoIntro = findViewById(R.id.edit_text_introduction);
                    // TODO
                    String[] t = new String[tagSet.size()];
                    UploadVideoInfo uploadVideoInfo = new UploadVideoInfo();
                    uploadVideoInfo.execute(videoName.getText().toString(), clas.getText().toString(), videoIntro.getText().toString(), getTags());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    class HashThenPost extends AsyncTask<InputStream, Integer, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ShowProgressDialog.show(UploadActivity.this, "对视频进行hash处理");
        }

        @Override
        protected String doInBackground(InputStream... inputStreams) {
            InputStream is = inputStreams[0];
            byte[] bytes = new byte[1024 * 1000 * 50]; // 50MB
            int len = 0;
            try {
                len = is.read(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("hjt len", String.valueOf(len));
            byte[] b2 = new byte[len];
            for(int i = 0; i < len; i++) b2[i] = bytes[i];
            String hash = SHA256.hash(b2);
            ShowProgressDialog.wait.setMessage("哈希完成");
            Log.e("hjtsha256", hash);
            QYrequest htp = new QYrequest();
            return htp.advancePost(GenerateJson.universeJson("file_type", "2", "hash", hash), C.file_upload_verify_url, "Authorization", GlobalVariable.mInstance.token);
        }
        @Override

        protected void onPostExecute(String s) {
            ShowProgressDialog.wait.setMessage("文件处理完成");
            JSONObject json = MsgProcess.msgProcess(s);
            Log.d("hjtuploadmsg", s);
            if(json != null){
                try {
                    if(json.getBoolean("rapid_upload")){
                        ShowProgressDialog.wait.dismiss();
                        Toast.makeText(UploadActivity.this, "该视频已存在", Toast.LENGTH_LONG).show();
                        return;
                    }
                    EditText txt = findViewById(R.id.edit_text_file_name);
                    UploadVideo t = new UploadVideo();
                    realURL = Uri2RealPath.getRealPathFromUri_AboveApi19(getApplicationContext(), uri);
                    Log.e("hjtUri2RealPath", realURL);
                    t.execute(Uri2RealPath.getRealPathFromUri_AboveApi19(getApplicationContext(), uri), txt.getText().toString(), json.getString("upload_url"), json.getString("token"), json.getString("file_id"));
                } catch (JSONException e) {
                    ShowProgressDialog.wait.dismiss();
                    e.printStackTrace();
                }
            }
            else {
                ShowProgressDialog.wait.dismiss();
                Log.e("hjtUploadJsonNull", "??");
            }
            super.onPostExecute(s);
        }
    }
}