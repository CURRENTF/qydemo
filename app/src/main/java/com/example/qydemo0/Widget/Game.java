package com.example.qydemo0.Widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qydemo0.FreeDanceActivity;
import com.example.qydemo0.GameActivity;
import com.example.qydemo0.GameChallengeModeActivity;
import com.example.qydemo0.QYpack.AdvanceHttp;
import com.example.qydemo0.QYpack.QYFile;
import com.example.qydemo0.QYpack.Uri2RealPath;
import com.example.qydemo0.R;
import com.example.qydemo0.Widget.RankItem.ChallengeItem;
import com.example.qydemo0.Widget.RankItem.FreeItem;
import com.example.qydemo0.Widget.RankItem.ImageItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Game extends RelativeLayout {

    Activity ac;
    View mView;

    public Game(Context context) {
        super(context);
        ac = (Activity) context;
        INFLATE();
        init();
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        ac = (Activity) context;
        INFLATE();
        init();
    }

    void INFLATE(){
        LayoutInflater inflater = (LayoutInflater)ac.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.fragment_game, this, true);
        challenge = mView.findViewById(R.id.challenge_mode);
        free = mView.findViewById(R.id.free_mode);
        tab = mView.findViewById(R.id.ranks);
        challenge_rank = mView.findViewById(R.id.challenge_rank);
        free_rank = mView.findViewById(R.id.free_rank);
        image_rank = mView.findViewById(R.id.img_rank);
    }

    Button challenge, free;
    FloatingActionButton upload;
    Tab tab;
    LinearLayout challenge_rank, free_rank, image_rank;
    ActivityResultLauncher launcher;

    void init(){
        challenge.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ac, GameChallengeModeActivity.class);
                ac.startActivity(intent);
            }
        });
        free.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                ArrayList<String> params = new ArrayList<>();
                params.add("1");
                intent.putStringArrayListExtra("GameParams", params);
                intent.setClass(ac, GameActivity.class);
                ac.startActivity(intent);
            }
        });
        String[] texts = {"闯关", "自由", "难度"};
        View[] views = {challenge_rank, free_rank, image_rank};
        tab.init(texts, views, R.color.black_overlay, R.color.real_pink, 16);
        upload = mView.findViewById(R.id.add_image);
        QYFile.ResultContract qyr = new QYFile.ResultContract();
        qyr.params = "image";
        launcher = ((AppCompatActivity)ac).registerForActivityResult(qyr, new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if(result == null) return;
                String realPath = Uri2RealPath.getRealPathFromUri_AboveApi19(ac, result);
                Intent intent = new Intent();
                intent.setClass(ac, GameActivity.class);
                ArrayList<String> list = new ArrayList<>();
                list.add("2");
                list.add(realPath);
                intent.putStringArrayListExtra("GameParams", list);
                ac.startActivity(intent);
//                UploadImage uploadImage = new UploadImage();
//                uploadImage.execute(realPath);
            }
        });
        upload.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Toast.makeText(ac, "请选择一张想要上传的图片呀~", Toast.LENGTH_LONG).show();
                launcher.launch(true);
            }
        });
        Handler challengeHandler = new Handler(Looper.myLooper()){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg){
                JSONArray ja = (JSONArray) msg.obj;
//                Log.d("hjt.c", ja.toString());
                for(int i = 0; i < ja.length(); i++){
                    ChallengeItem challengeItem = new ChallengeItem(ac);
                    try {
                        challengeItem.fill(ja.getJSONObject(i), i + 1);
                        challenge_rank.addView(challengeItem);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        AdvanceHttp.getGameRank(challengeHandler, 1);
        Handler freeHandler = new Handler(Looper.myLooper()){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg){
                JSONArray ja = (JSONArray) msg.obj;
//                Log.d("hjt.c", ja.toString());
                for(int i = 0; i < ja.length(); i++){
                    FreeItem freeItem = new FreeItem(ac);
                    try {
                        freeItem.fill(ja.getJSONObject(i), i + 1);
                        free_rank.addView(freeItem);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        AdvanceHttp.getGameRank(freeHandler, 2);
        Handler imageHandler = new Handler(Looper.myLooper()){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg){
                JSONArray ja = (JSONArray) msg.obj;
                Log.d("hjt.c", ja.toString());
                for(int i = 0; i < Math.min(ja.length(), 10); i++){
                    ImageItem imageItem = new ImageItem(ac);
                    try {
                        imageItem.fill(ja.getJSONObject(i), i + 1);
                        image_rank.addView(imageItem);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                for(int i = Math.min(ja.length(), 10); i < ja.length(); i++){
                    ImageItem imageItem = new ImageItem(ac);
                    try {
                        imageItem.fill(ja.getJSONObject(i));
                        image_rank.addView(imageItem);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        AdvanceHttp.getGameRank(imageHandler, 3);
    }

    class UploadImage extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            String path = strings[0];
            QYFile qyFile = new QYFile();
            String file_id = qyFile.uploadFileAllIn(path, QYFile.ImageCode);
            return file_id;
        }

        @Override
        protected void onPostExecute(String file_id) {
//            Log.d("hjt.file_id", file_id);
            Handler handler = new Handler(Looper.myLooper()){
                @SuppressLint("HandlerLeak")
                @Override
                public void handleMessage(@NonNull Message msg){
                    if(msg.arg1 == 1){
                        Toast.makeText(ac, "上传成功", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ac, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            AdvanceHttp.postGameFreeImage(handler, file_id);
        }
    }
}
