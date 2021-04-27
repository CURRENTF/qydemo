package com.example.qydemo0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import android.content.pm.PackageManager;
import android.view.GestureDetector;

import com.example.qydemo0.AiUnit.FaceFerActivity;
import com.example.qydemo0.DataTrans.FragmentDataForMain;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.DeviceInfo;
import com.example.qydemo0.QYpack.GestureListener;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.Widget.Dashboard;
import com.example.qydemo0.Widget.Home;
import com.example.qydemo0.Widget.Post;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.Widget.QYNavigation;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Constant C = Constant.mInstance;
    private GestureDetector gestureDetector = null;
    private GestureListener gestureListener = null;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //请求成功，获得权限，存储到本地
                Toast.makeText(this, "成功获取权限", Toast.LENGTH_LONG).show();
            } else {
                //请求被拒绝，提示用户
                Toast.makeText(this, "缺少权限App将无法正常使用", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        getPermission();

        SharedPreferences sp = getSharedPreferences(C.database, Context.MODE_PRIVATE);
        GlobalVariable.mInstance.readAllVar(sp);

        Log.d("hjt.uid", GlobalVariable.mInstance.uid);
        Log.d("hjt", "start LoginToken:" + GlobalVariable.mInstance.token);

        // 跳转登录
        if (!GlobalVariable.mInstance.tokenExisted) {
            Intent it = new Intent();
            it.setComponent(new ComponentName(this, LoginActivity.class));
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(it);
            finish();
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            return;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GlobalVariable.mInstance.fragmentDataForMain = new FragmentDataForMain();

        if(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson == null){
            GetUserInfo g = new GetUserInfo();
            g.execute();
        }

        LinearLayout mainLayout = findViewById(R.id.main_layout);
        Dashboard dashboard = new Dashboard(this);
        mainLayout.addView(dashboard);
        dashboard.setVisibility(View.GONE);
        Home home = new Home(this);
        mainLayout.addView(home);
        Post post = new Post(this);
        mainLayout.addView(post);
        post.setVisibility(View.GONE);
        QYNavigation navigation = new QYNavigation(this);
        View[] views = {post, home, dashboard};
        navigation.initView(views);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceInfo.dip2px(this, 50));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        navigation.setLayoutParams(layoutParams);
        RelativeLayout main = findViewById(R.id.main_main);
        main.addView(navigation);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        GlobalVariable.mInstance.tokenExisted = false;
        SharedPreferences sp = getSharedPreferences(C.database, Context.MODE_PRIVATE);
        GlobalVariable.mInstance.saveAllVar(sp);
        super.onDestroy();
    }

    void getPermission(){
        try {
            //检测是否有写的权限
            int permission0 = ActivityCompat.checkSelfPermission(this,
                    "android.permission.READ_EXTERNAL_STORAGE"),
                    permission1 = ActivityCompat.checkSelfPermission(this,
                            "android.permission.CAMERA"),
                    permission2 = ActivityCompat.checkSelfPermission(this,
                            "android.permission.RECORD_AUDIO"),
                    permission3 = ActivityCompat.checkSelfPermission(this,
                            "android.permission.WRITE_EXTERNAL_STORAGE");

            if (permission0 != PackageManager.PERMISSION_GRANTED || permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED
                    || permission3 != PackageManager.PERMISSION_GRANTED) {
                // 去申请读的权限，申请权限
                String[] t = new String[4];
                t[0] = Manifest.permission.READ_EXTERNAL_STORAGE;
                t[1] = Manifest.permission.CAMERA;
                t[2] = Manifest.permission.RECORD_AUDIO;
                t[3] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                ActivityCompat.requestPermissions(this, t, 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class GetUserInfo extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return htp.advanceGet(Constant.mInstance.userInfo_url, "Authorization", GlobalVariable.mInstance.token);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("hjtGetUserInfo", s);
            JSONObject json = MsgProcess.msgProcess(s, false, null);
            if(json != null){
                try {
                    if(json.getString("img_url").equals("null"))
                        json.put("img_url", Constant.mInstance.default_avatar);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                GlobalVariable.mInstance.fragmentDataForMain.userInfoJson = json;
                try {
                    GlobalVariable.mInstance.uid = json.getString("uid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}