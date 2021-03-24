package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.qydemo0.QYpack.GlobalVariable;

import org.json.JSONException;
import org.json.JSONObject;

public class UserSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
    }

    @Override
    protected void onStart() {
        try {
            JSONObject infoJson = GlobalVariable.mInstance.fragmentDataForMain.userInfoJson;
            ImageView userAvatar = findViewById(R.id.image_setting_avatar);
            Glide.with(this)
                    .load(infoJson.getString("img_url"))
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(userAvatar);
            TextView username = findViewById(R.id.text_setting_username);
            username.setText(infoJson.getString("username"));
            TextView gender = findViewById(R.id.user_setting_gender);
            gender.setText(GlobalVariable.mInstance.fragmentDataForMain.parseGender(infoJson.getInt("gender")));
            TextView sign = findViewById(R.id.user_setting_sign);
            sign.setText(infoJson.getString("sign"));
            TextView uid = findViewById(R.id.user_setting_uid);
            uid.setText(GlobalVariable.mInstance.uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onStart();
    }
}