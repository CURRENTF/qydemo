package com.example.qydemo0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.content.pm.PackageManager;
import android.view.GestureDetector;

import com.example.qydemo0.DataTrans.FragmentDataForMain;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.DeviceInfo;
import com.example.qydemo0.QYpack.GestureListener;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.WaveLoadDialog;
import com.example.qydemo0.Widget.Category;
import com.example.qydemo0.Widget.Dashboard;
import com.example.qydemo0.Widget.Game;
import com.example.qydemo0.Widget.Home;
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;
import com.example.qydemo0.Widget.Post;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.Widget.QYLoading;
import com.example.qydemo0.Widget.QYNavigation;
import com.googlecode.mp4parser.authoring.tracks.TextTrackImpl;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.squareup.haha.perflib.Main;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MyAppCompatActivity {

    Constant C = Constant.mInstance;
    private GestureDetector gestureDetector = null;
    private GestureListener gestureListener = null;
    private QYrequest work_request = new QYrequest();

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

    ImageView img;
    PoseHuman inn;

    RelativeLayout activityDetailPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        GlobalVariable.mInstance.appContext = getApplicationContext();
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
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
                String[] t = new String[10];
                t[0] = Manifest.permission.READ_EXTERNAL_STORAGE;
                t[1] = Manifest.permission.CAMERA;
                t[2] = Manifest.permission.RECORD_AUDIO;
                t[3] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                t[4] = Manifest.permission.LOCATION_HARDWARE;
                t[5] = Manifest.permission.READ_PHONE_STATE;
                t[6] = Manifest.permission.WRITE_SETTINGS;
                t[7] = Manifest.permission.READ_CONTACTS;
                t[8] = Manifest.permission.ACCESS_COARSE_LOCATION;
                t[9] = Manifest.permission.ACCESS_FINE_LOCATION;
                ActivityCompat.requestPermissions(this, t, 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("global.uid4", GlobalVariable.mInstance.uid);

        SharedPreferences sp = getSharedPreferences(C.database, Context.MODE_PRIVATE);
        GlobalVariable.mInstance.readAllVar(sp);
        Log.d("hjt.uid", GlobalVariable.mInstance.uid + "?");

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

        Log.d("hjt.uid2", GlobalVariable.mInstance.uid + "?");

        GlobalVariable.mInstance.fragmentDataForMain = new FragmentDataForMain();

        StringBuffer param = new StringBuffer();

        //IflytekAPP_id为我们申请的Appid
        param.append("appid=4f537480");
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE+"="+ SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(GlobalVariable.mInstance.appContext, param.toString());

//        inn = new PoseHuman(MainActivity.this);
//        activityDetailPlayer = new RelativeLayout(MainActivity.this);
//        activityDetailPlayer = findViewById(R.id.main_main);
//        RelativeLayout.LayoutParams phpl = new RelativeLayout.LayoutParams(800, 800);
//        phpl.addRule(RelativeLayout.CENTER_VERTICAL);
//        phpl.addRule(RelativeLayout.ALIGN_LEFT,R.id.centerTextView);
//        inn.setLayoutParams(phpl);
//        inn.setBackgroundColor(Color.GRAY);
//        inn.setAlpha(0.75f);
//        activityDetailPlayer.addView(inn);
        Button btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                inn.setPoints(new double[][]{{0.4685792349726776, 0.019125683060109287}, {0.4986338797814208, 0.0}, {0.43579234972677594, 0.00273224043715847}, {0.5396174863387978, 0.04918032786885246}, {0.39480874316939896, 0.05737704918032786}, {0.6215846994535519, 0.22677595628415298}, {0.3346994535519126, 0.24316939890710382}, {0.7199453551912569, 0.39344262295081966}, {0.2800546448087432, 0.4371584699453552}, {0.5806010928961749, 0.4808743169398907}, {0.4030054644808743, 0.49726775956284147}, {0.6079234972677596, 0.6775956284153005}, {0.4139344262295082, 0.6994535519125683}, {0.6215846994535519, 0.9999999999999999}, {0.39480874316939896, 0.9918032786885245}, {-1, -1}, {-1, -1}});
                new GetTestData(MainActivity.this).execute();
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        GlobalVariable.mInstance.appContext = this;

        DeviceInfo.info(this);

        if(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson == null){
            GetUserInfo g = new GetUserInfo(this);
            g.execute();
        }

        LinearLayout mainLayout = findViewById(R.id.main_layout);

        dashboard = new Dashboard(this);
        mainLayout.addView(dashboard);
        dashboard.setVisibility(View.GONE);

        home = new Home(this);
        mainLayout.addView(home);

        post = new Post(this);
        mainLayout.addView(post);
        post.setVisibility(View.GONE);

        category = new Category(this);
        mainLayout.addView(category);
        category.setVisibility(View.GONE);

        game = new Game(this);
        mainLayout.addView(game);
        game.setVisibility(View.GONE);

        navigation = new QYNavigation(this);
        View[] views = {post, category, home, game, dashboard};
        navigation.initView(views);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceInfo.dip2px(this, 50));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        navigation.setLayoutParams(layoutParams);
        RelativeLayout main = findViewById(R.id.main_main);
        main.addView(navigation);
    }

    Dashboard dashboard;
    Home home;
    Post post;
    Category category;
    Game game;
    QYNavigation navigation;
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("hjt.main.resume", "Resume!");
        dashboard.refresh();
        game.refreshRank();
//        post.refresh();
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

    class GetUserInfo extends MyAsyncTask<String, Integer, String> {

        protected GetUserInfo(MyAppCompatActivity activity) {
            super(activity);
        }

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

    class GetTestData extends MyAsyncTask<String, Integer, String> {

        protected GetTestData(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONObject res = null;
            try {
                res = new JSONObject(work_request.advanceGet(Constant.mInstance.learn_url+"record/3/?start=0&lens=1", "Authorization", GlobalVariable.mInstance.token));
                Log.i("whc_ind_res", String.valueOf(res));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "1";
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }

    public class PoseHuman extends View {
        private static final int ALPHA = 255;
        private Paint mInnerPaintRed, mInnerPaintBlue, mInnerPaintPur,
                pointsPaintRed,pointsPaintBlue,pointsPaintPur;
        public int width;
        public int height;
        private int[][] points = new int[17][2];

        public PoseHuman(Context context) {
            super(context);
            mInnerPaintRed = new Paint();
            mInnerPaintRed.setARGB(ALPHA, 255, 0, 0);
            mInnerPaintRed.setAntiAlias(true);
            mInnerPaintRed.setStrokeWidth(8f);

            mInnerPaintBlue = new Paint();
            mInnerPaintBlue.setARGB(ALPHA, 0, 0, 255);
            mInnerPaintBlue.setAntiAlias(true);
            mInnerPaintBlue.setStrokeWidth(8f);

            mInnerPaintPur = new Paint();
            mInnerPaintPur.setARGB(ALPHA, 255, 0, 255);
            mInnerPaintPur.setAntiAlias(true);
            mInnerPaintPur.setStrokeWidth(8f);

            pointsPaintRed = new Paint();
            pointsPaintRed.setARGB(ALPHA, 255, 0, 0);
            pointsPaintRed.setAntiAlias(true);
            pointsPaintRed.setStrokeWidth(10f);

            pointsPaintBlue = new Paint();
            pointsPaintBlue.setARGB(ALPHA, 0, 0, 255);
            pointsPaintBlue.setAntiAlias(true);
            pointsPaintBlue.setStrokeWidth(10f);

            pointsPaintPur = new Paint();
            pointsPaintPur.setARGB(ALPHA, 255, 0, 255);
            pointsPaintPur.setAntiAlias(true);
            pointsPaintPur.setStrokeWidth(10f);
            System.out.println("绘图初始化");
        }

        public void setPoints(double[][] d) {
            for (int i = 0; i < d.length; i++) {
                points[i][0] = (int)(d[i][0]*500);
                points[i][1] = (int)(d[i][1]*500);
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            width = getMeasuredWidth();
            height = getMeasuredHeight();
        }

        @Override
        protected void onDraw(Canvas canvas) {
                if(points[0][0]!=-1)
                    canvas.drawCircle(points[0][0], points[0][1],15, pointsPaintPur);
                for(int i=1;i<17;i++){
                    Log.i("points", ""+points[i][0]+" "+points[i][1]);
                    if(points[i][0]!=-1 && points[i][1]!=-1)
                        canvas.drawCircle(points[i][0],points[i][1],15, i%2==1?pointsPaintRed:pointsPaintBlue);
                }
                if(points[0][0]!=-1&&points[0][1]!=-1&&points[1][0]!=-1&&points[1][1]!=-1)canvas.drawLine(points[0][0], points[0][1], points[1][0], points[1][1], mInnerPaintRed);
                if(points[0][0]!=-1&&points[0][1]!=-1&&points[2][0]!=-1&&points[2][1]!=-1)canvas.drawLine(points[0][0], points[0][1], points[2][0], points[2][1], mInnerPaintBlue);
                if(points[0][0]!=-1&&points[0][1]!=-1&&points[5][0]!=-1&&points[5][1]!=-1)canvas.drawLine(points[0][0], points[0][1], points[5][0], points[5][1], mInnerPaintRed);
                if(points[0][0]!=-1&&points[0][1]!=-1&&points[6][0]!=-1&&points[6][1]!=-1)canvas.drawLine(points[0][0], points[0][1], points[6][0], points[6][1], mInnerPaintBlue);
                if(points[1][0]!=-1&&points[1][1]!=-1&&points[3][0]!=-1&&points[3][1]!=-1)canvas.drawLine(points[1][0], points[1][1], points[3][0], points[3][1], mInnerPaintRed);
                if(points[2][0]!=-1&&points[2][1]!=-1&&points[4][0]!=-1&&points[4][1]!=-1)canvas.drawLine(points[2][0], points[2][1], points[4][0], points[4][1], mInnerPaintBlue);
                if(points[5][0]!=-1&&points[5][1]!=-1&&points[6][0]!=-1&&points[6][1]!=-1)canvas.drawLine(points[5][0], points[5][1], points[6][0], points[6][1], mInnerPaintPur);
                if(points[5][0]!=-1&&points[5][1]!=-1&&points[7][0]!=-1&&points[7][1]!=-1)canvas.drawLine(points[5][0], points[5][1], points[7][0], points[7][1], mInnerPaintRed);
                if(points[5][0]!=-1&&points[5][1]!=-1&&points[11][0]!=-1&&points[11][1]!=-1)canvas.drawLine(points[5][0], points[5][1], points[11][0], points[11][1], mInnerPaintRed);
                if(points[6][0]!=-1&&points[6][1]!=-1&&points[8][0]!=-1&&points[8][1]!=-1)canvas.drawLine(points[6][0], points[6][1], points[8][0], points[8][1], mInnerPaintBlue);
                if(points[6][0]!=-1&&points[6][1]!=-1&&points[12][0]!=-1&&points[12][1]!=-1)canvas.drawLine(points[6][0], points[6][1], points[12][0], points[12][1], mInnerPaintBlue);
                if(points[7][0]!=-1&&points[7][1]!=-1&&points[9][0]!=-1&&points[9][1]!=-1)canvas.drawLine(points[7][0], points[7][1], points[9][0], points[9][1], mInnerPaintRed);
                if(points[8][0]!=-1&&points[8][1]!=-1&&points[10][0]!=-1&&points[10][1]!=-1)canvas.drawLine(points[8][0], points[8][1], points[10][0], points[10][1], mInnerPaintBlue);
                if(points[11][0]!=-1&&points[11][1]!=-1&&points[12][0]!=-1&&points[12][1]!=-1)canvas.drawLine(points[11][0], points[11][1], points[12][0], points[12][1], mInnerPaintPur);
                if(points[11][0]!=-1&&points[11][1]!=-1&&points[13][0]!=-1&&points[13][1]!=-1)canvas.drawLine(points[11][0], points[11][1], points[13][0], points[13][1], mInnerPaintRed);
                if(points[12][0]!=-1&&points[12][1]!=-1&&points[14][0]!=-1&&points[14][1]!=-1)canvas.drawLine(points[12][0], points[12][1], points[14][0], points[14][1], mInnerPaintBlue);
                if(points[13][0]!=-1&&points[13][1]!=-1&&points[15][0]!=-1&&points[15][1]!=-1)canvas.drawLine(points[13][0], points[13][1], points[15][0], points[15][1], mInnerPaintRed);
                if(points[14][0]!=-1&&points[14][1]!=-1&&points[16][0]!=-1&&points[16][1]!=-1)canvas.drawLine(points[14][0], points[14][1], points[16][0], points[16][1], mInnerPaintBlue);
                postInvalidate();
        }
        }

    }