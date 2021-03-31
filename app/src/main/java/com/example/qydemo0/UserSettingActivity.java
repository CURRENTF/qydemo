package com.example.qydemo0;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYFile;
import com.example.qydemo0.QYpack.QYUser;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.Uri2RealPath;
import com.example.qydemo0.Widget.QYDIalog;

import org.json.JSONException;
import org.json.JSONObject;

public class UserSettingActivity extends AppCompatActivity implements View.OnClickListener {

    TextView username, gender, sign;
    ImageView userAvatar;
    Uri avatar_uri = null;
    int[] avatar_button = {R.id.button_set_pic, R.id.button_upload_avatar};
    int[] username_view= {R.id.button_upload_username};
    int[] sign_view = {R.id.button_upload_sign};
    private QYDIalog qydIalog;
    ActivityResultLauncher launcher = registerForActivityResult(new QYFile.ResultContract(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if(result == null) return;
            setPhoto(result);
        }
    });

    public void setPhoto(Uri result){
        ((TextView)qydIalog.findViewById(R.id.setting_avatar_info)).setText("已选择图片"); // 针对组件进行findViewById
        ImageView img = qydIalog.findViewById(R.id.dialog_setting_avatar);
        img.setImageURI(result);
        avatar_uri = result;
    }

    void showGenderDialog(){
        String[] list = {"男", "女", "未知"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
        listDialog.setTitle("性别");
        listDialog.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gender.setText(list[which]);
                ChangeGender changeGender = new ChangeGender(); // 服务端改变
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
    void showAvatarDialog(){
        qydIalog = new QYDIalog(this, R.layout.setting_dialog_avatar, avatar_button);
        qydIalog.setOnCenterItemClickListener(new Avatar());
        qydIalog.show();
    }
    void showUsernameDialog(){
        qydIalog = new QYDIalog(this, R.layout.setting_dialog_username, username_view);
        qydIalog.setOnCenterItemClickListener(new Username());
        qydIalog.show();
    }
    void showSignDialog(){
        qydIalog = new QYDIalog(this, R.layout.setting_dialog_user_sign, sign_view);
        qydIalog.setOnCenterItemClickListener(new Sign());
        qydIalog.show();
    }


    // ac 的 onclick
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_gender:
                showGenderDialog();
                break;
            case R.id.setting_avatar:
                showAvatarDialog();
                break;
            case R.id.setting_username:
                showUsernameDialog();
                break;
            case R.id.setting_sign:
                showSignDialog();
                break;
        }
    }

    // 设置头像dialog onclick
    class Avatar implements QYDIalog.OnCenterItemClickListener{
        @Override
        public void OnCenterItemClick(QYDIalog dialog, View view) {
            switch (view.getId()){
                case R.id.button_set_pic:
                    launcher.launch(true);
                    break;
                case R.id.button_upload_avatar:
                    if(avatar_uri == null){ // 检查选图片
                        Toast.makeText(UserSettingActivity.this, "请选择图片", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    ChangeAvatar changeAvatar = new ChangeAvatar();
                    QYFile qyFile = new QYFile();
                    changeAvatar.execute(Uri2RealPath.getRealPathFromUri_AboveApi19(UserSettingActivity.this, avatar_uri), "0",
                            qyFile.hashFile(avatar_uri, UserSettingActivity.this));
                    dialog.dismiss();
                    break;
            }
        }
    }

    class Username implements QYDIalog.OnCenterItemClickListener{

        @Override
        public void OnCenterItemClick(QYDIalog dialog, View view) {
            switch (view.getId()){
                case R.id.button_upload_username:
                    ChangeUsername changeUsername = new ChangeUsername();
                    changeUsername.execute(((EditText)qydIalog.findViewById(R.id.edit_text_setting_username)).getText().toString());
                    dialog.dismiss();
                    break;
            }

        }
    }

    class Sign implements QYDIalog.OnCenterItemClickListener{

        @Override
        public void OnCenterItemClick(QYDIalog dialog, View view) {
            switch (view.getId()){
                case R.id.button_upload_sign:
                    ChangeUserSign changeUserSign = new ChangeUserSign();
                    changeUserSign.execute(((EditText)qydIalog.findViewById(R.id.edit_text_setting_sign)).getText().toString());
                    dialog.dismiss();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        ((LinearLayout)findViewById(R.id.setting_gender)).setOnClickListener(this);
        ((LinearLayout)findViewById(R.id.setting_avatar)).setOnClickListener(this);
        ((LinearLayout)findViewById(R.id.setting_username)).setOnClickListener(this);
        ((LinearLayout)findViewById(R.id.setting_sign)).setOnClickListener(this);
    }

    void writeInfo(){
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
    }

    @Override
    protected void onStart() {
        writeInfo();
        super.onStart();
    }


    /* --------internet---------*/

    class ChangeGender extends AsyncTask<Integer, Integer, String>{

        @Override
        protected String doInBackground(Integer... integers) {
            QYrequest qYrequest = new QYrequest();
            String[] data = {"gender", "int", String.valueOf(integers[0])};
            return qYrequest.advancePut(GenerateJson.universeJson2(data), Constant.mInstance.modifyUserInfo_url,
                    "Authorization", GlobalVariable.mInstance.token);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("hjt.changeGender", s);
            if(MsgProcess.checkMsg(s)) Toast.makeText(UserSettingActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
        }
    }

    class ChangeAvatar extends AsyncTask<String, Integer, Boolean>{

        // params: String file_url, int file_type, String hash
        @Override
        protected Boolean doInBackground(String... s) {
            QYFile qyFile = new QYFile();
            String res = qyFile.uploadFileAllIn(Constant.mInstance.file_upload_verify_url, s[0], Integer.parseInt(s[1]), s[2]);
            if(res != null)
                return QYUser.modify("img_id", "string", res);
            else return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                UpdateInfo info = new UpdateInfo();
                info.execute();
            }
            Log.d("hjt.set-avatar", String.valueOf(aBoolean));
            if(aBoolean) {
                Toast.makeText(UserSettingActivity.this, "头像修改成功", Toast.LENGTH_SHORT).show();
                ImageView ava = findViewById(R.id.image_setting_avatar);
//                ava.setImageURI(avatar_uri);
                Img.roundImgUri(UserSettingActivity.this, ava, avatar_uri);
            }
            else Toast.makeText(UserSettingActivity.this, "头像修改失败", Toast.LENGTH_SHORT).show();
            super.onPostExecute(aBoolean);
        }
    }

    class ChangeUsername extends AsyncTask<String, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            Log.d("hjt.username.show", strings[0]);
            return QYUser.modify("username", "string", strings[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                UpdateInfo info = new UpdateInfo();
                info.execute();
            }
            if(aBoolean) Toast.makeText(UserSettingActivity.this, "用户名修改成功", Toast.LENGTH_SHORT).show();
            else Toast.makeText(UserSettingActivity.this, "用户名修改失败", Toast.LENGTH_SHORT).show();
        }
    }

    class ChangeUserSign extends AsyncTask<String, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            return QYUser.modify("sign", "string", strings[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                UpdateInfo info = new UpdateInfo();
                info.execute();
            }
            if(aBoolean) Toast.makeText(UserSettingActivity.this, "个性签名修改成功", Toast.LENGTH_SHORT).show();
            else Toast.makeText(UserSettingActivity.this, "个性签名修改失败", Toast.LENGTH_SHORT).show();
        }
    }

    class UpdateInfo extends AsyncTask<String, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            return QYUser.refreshInfo();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean) writeInfo();
            super.onPostExecute(aBoolean);
        }
    }
}