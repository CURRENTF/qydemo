package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.qydemo0.DataTrans.FragmentDataForMain;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Constant C = Constant.mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sp = getSharedPreferences(C.database, Context.MODE_PRIVATE);
        GlobalVariable.mInstance.readAllVar(sp);

        Log.d("hjt", "start LoginToken:" + GlobalVariable.mInstance.token);

        if(!GlobalVariable.mInstance.tokenExisted){
            Intent it = new Intent();
            it.setComponent(new ComponentName(this, LoginActivity.class));
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(it);
            finish();
        }

        GlobalVariable.mInstance.fragmentDataForMain = new FragmentDataForMain();
        // TODO: need delete

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
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
//        GlobalVariable.mInstance.tokenExisted = false;
        SharedPreferences sp = getSharedPreferences(C.database, Context.MODE_PRIVATE);
        GlobalVariable.mInstance.saveAllVar(sp);
        super.onDestroy();
    }
}