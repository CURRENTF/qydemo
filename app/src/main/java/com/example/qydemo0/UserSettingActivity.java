package com.example.qydemo0;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.AsyncQueryHandler;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;

import org.json.JSONException;
import org.json.JSONObject;

public class UserSettingActivity extends AppCompatActivity implements View.OnClickListener {

    TextView username, gender, sign;
    ImageView userAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);


    }

    @Override
    protected void onStart() {
        ((LinearLayout)findViewById(R.id.setting_gender)).setOnClickListener(this);
        try {
            JSONObject infoJson = GlobalVariable.mInstance.fragmentDataForMain.userInfoJson;
            userAvatar = findViewById(R.id.image_setting_avatar);
            Glide.with(this)
                    .load(infoJson.getString("img_url"))
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(userAvatar);
            username = findViewById(R.id.text_setting_username);
            username.setText(infoJson.getString("username"));
            gender = findViewById(R.id.user_setting_gender);
            gender.setText(GlobalVariable.mInstance.fragmentDataForMain.parseGender(infoJson.getInt("gender")));
            sign = findViewById(R.id.user_setting_sign);
            sign.setText(infoJson.getString("sign"));
            TextView uid = findViewById(R.id.user_setting_uid);
            uid.setText(GlobalVariable.mInstance.uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onStart();
    }

    void showGenderDialog(){
        String[] list = {"男", "女", "未知"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
        listDialog.setTitle("性别");
        listDialog.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gender.setText(list[which]);
                ChangeGender changeGender = new ChangeGender();
                changeGender.execute(which + 1);
                try {
                    GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.put("gender", which + 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        listDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_gender:
                showGenderDialog();
                break;
        }
    }

    /* --------internet---------*/

    class ChangeGender extends AsyncTask<Integer, Integer, String>{

        @Override
        protected String doInBackground(Integer... integers) {
            QYrequest qYrequest = new QYrequest();
            String[] data = {"gender", "int", String.valueOf(integers[0])};
            return qYrequest.advancePut(GenerateJson.universeJson2(data), Constant.mInstance.modifyUserInfo_url, "Authorization", GlobalVariable.mInstance.token);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("hjt.changeGender", s);
            if(MsgProcess.checkMsg(s)) Toast.makeText(UserSettingActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
        }
    }
}