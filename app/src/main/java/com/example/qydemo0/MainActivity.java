package com.example.qydemo0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.qydemo0.DataTrans.FragmentDataForMain;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GestureListener;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.ui.dashboard.DashboardFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Constant C = Constant.mInstance;
    private GestureDetector gestureDetector = null;
    private GestureListener gestureListener = null;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //请求成功，获得权限，存储到本地
                Toast.makeText(this, "成功获取权限", Toast.LENGTH_LONG).show();
            } else {
                //请求被拒绝，提示用户
                Toast.makeText(this, "没有该权限将无法上传视频", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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

        SharedPreferences sp = getSharedPreferences(C.database, Context.MODE_PRIVATE);
        GlobalVariable.mInstance.readAllVar(sp);

        Log.d("hjt", GlobalVariable.mInstance.uid + "?");
        Log.d("hjt", "start LoginToken:" + GlobalVariable.mInstance.token);

        if(!GlobalVariable.mInstance.tokenExisted){
            Intent it = new Intent();
            it.setComponent(new ComponentName(this, LoginActivity.class));
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(it);
            finish();
        }

        GlobalVariable.mInstance.fragmentDataForMain = new FragmentDataForMain();
        // TODO: need delete maybe not

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if(getSupportActionBar() != null){
//            getSupportActionBar().hide();
//        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_category)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // 监听向下滑动 不太行， 应该是被scroll抓了
//        gestureListener = new GestureListener(this);
//        gestureDetector = new GestureDetector(this, gestureListener);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return gestureDetector.onTouchEvent(event);
//    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.d("hjtMain", "MainPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        GlobalVariable.mInstance.tokenExisted = false;
        SharedPreferences sp = getSharedPreferences(C.database, Context.MODE_PRIVATE);
        GlobalVariable.mInstance.saveAllVar(sp);
        Log.d("hjtMain", "MainDestroyed");
        super.onDestroy();
    }
}