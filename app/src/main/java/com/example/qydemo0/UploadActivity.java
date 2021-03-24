package com.example.qydemo0;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.SHA256;
import com.example.qydemo0.QYpack.Uri2RealPath;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{

    protected SimpleExoPlayer player = null;
    Uri uri = null;
    URI urii = null;
    Constant C = Constant.mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //请求成功，获得权限，存储到本地
                Toast.makeText(this, "nc", Toast.LENGTH_LONG).show();
            } else {
                //请求被拒绝，提示用户
                Toast.makeText(this, "wuquanxian", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.READ_EXTERNAL_STORAGE");

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 去申请读的权限，申请权限
                String[] t = new String[1];
                t[0] = Manifest.permission.READ_EXTERNAL_STORAGE;
                ActivityCompat.requestPermissions(this, t, 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 重写onRequestPermissionsResult方法，监听用户点击了同意还是拒绝

        Button btn = findViewById(R.id.button_browse_file);
        btn.setOnClickListener(this);
        btn = findViewById(R.id.button_upload_selected_video);
        btn.setOnClickListener(this);
        player = new SimpleExoPlayer.Builder(getBaseContext()).build();
        PlayerView p = findViewById(R.id.player_for_upload_video);
        p.setPlayer(player);
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

    class UploadVideo extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            String filePath = strings[0], fileName = strings[1], url = strings[2], token = strings[3];
            QYrequest htp = new QYrequest();
            return htp.postWithFile(filePath, fileName, url, token);
        }

        @Override
        protected void onPostExecute(String msg) {
            Log.d("hjtupload", msg);
            JSONObject json = MsgProcess.msgProcess(msg);
            if(json != null){
                Toast.makeText(UploadActivity.this, "发送成功大概", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(msg);
        }
    }


    class HashThenPost extends AsyncTask<InputStream, Integer, String>{

        @Override
        protected String doInBackground(InputStream... inputStreams) {
            InputStream is = inputStreams[0];
            byte[] bytes = new byte[1024 * 1000 * 50];
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
            Log.e("hjtsha256", hash);
            QYrequest htp = new QYrequest();
            return htp.advancePost(GenerateJson.universeJson("file_type", "2", "hash", hash), C.file_upload_verify_url, "Authorization", GlobalVariable.mInstance.token);
        }
        @Override

        protected void onPostExecute(String s) {

            JSONObject json = MsgProcess.msgProcess(s);
            Log.d("hjtuploadmsg", s);
            if(json != null){
                try {
                    if(json.getBoolean("rapid_upload")){
                        Toast.makeText(UploadActivity.this, "该视频已存在", Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                EditText txt = findViewById(R.id.edit_text_file_name);
                UploadVideo t = new UploadVideo();
                try {
                    Log.e("hjtUri2RealPath", Uri2RealPath.getRealPathFromUri_AboveApi19(getApplicationContext(), uri));
                    t.execute(Uri2RealPath.getRealPathFromUri_AboveApi19(getApplicationContext(), uri), txt.getText().toString(), json.getString("upload_url"), json.getString("token"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Log.e("hjtUploadJsonNull", "??");
            }
            super.onPostExecute(s);
        }
    }
}