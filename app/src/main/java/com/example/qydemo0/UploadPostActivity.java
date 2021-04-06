package com.example.qydemo0;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYFile;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.utils.ImageSelector;
import com.example.qydemo0.QYpack.Img;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;


public class UploadPostActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener{

    GridLayout g = null;
    int[] imgs = {R.id.up_img1, R.id.up_img2, R.id.up_img3,
         R.id.up_img4, R.id.up_img5, R.id.up_img6,
            R.id.up_img7, R.id.up_img8, R.id.up_img9};
    ImageView[] img_view = new ImageView[9];
    TextView remain_num = null;
    TextView p;
    int switcher = 0;

    int img_pointer = 0;
    int RequestCode_pic = 233; // useless
    ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                //获取选择器返回的数据
                ArrayList<String> images = data.getStringArrayListExtra(
                        ImageSelector.SELECT_RESULT);
                refreshPic(images);
                for(String s : images){
                    Log.d("hjt.show.select.image", s);
                }
            }
        }
    });




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);
        CompoundButton btn = findViewById(R.id.post_method);
        remain_num = findViewById(R.id.add_post_remain_number);
        p = findViewById(R.id.add_post_text);
        p.addTextChangedListener(new TxtChange());
        btn.setOnCheckedChangeListener(this);
        ImageView img = findViewById(imgs[img_pointer]);
        Img.url2imgViewRoundRectangle(getDrawable(R.drawable.add_f), img, this, 30);
        g = findViewById(R.id.grid_img_set);
        for(int i = 0; i < 9; i++){
            img = findViewById(imgs[i]);
            img.setOnClickListener(this);
            img_view[i] = img;
        }
        FloatingActionButton fbtn = findViewById(R.id.button_upload_post);
        fbtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){

        }
        else {

        }
    }





    @Override
    public void onClick(View v) {
        if(v.getId() == imgs[img_pointer]){
            Intent intent = ImageSelector.builder()
                    .useCamera(false) // 设置是否使用拍照
                    .setSingle(false)  //设置是否单选
                    .setMaxSelectCount(9 - img_pointer) // 图片的最大选择数量，小于等于0时，不限数量。
                    .canPreview(true) //是否可以预览图片，默认为true
                    .start(this, RequestCode_pic); // 打开相册
            launcher.launch(intent);
        }
        else if(v.getId() == R.id.button_upload_post){
            if(switcher == 0){
                UploadImage uploadImage = new UploadImage();
                uploadImage.execute();
            }
        }
    }

    Set<String> s_url = new HashSet<>();
    void refreshPic(ArrayList<String> arr){
        int cnt = 0;
        for(int i = img_pointer; i < arr.size() + img_pointer; i++){
            if(s_url.contains(arr.get(i - img_pointer))) continue;
            img_view[i].setVisibility(View.VISIBLE);
            s_url.add(arr.get(i - img_pointer));
            Img.url2imgViewRoundRectangle(arr.get(i - img_pointer), img_view[img_pointer + cnt], this, 30);
            cnt++;
        }
        img_pointer += cnt;
        if(img_pointer >= 9) return;
        img_view[img_pointer].setVisibility(View.VISIBLE);
        img_view[img_pointer].setImageDrawable(getDrawable(R.drawable.add_f));
    }

    class TxtChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            remain_num.setText("还可以书写" + String.valueOf(Constant.mInstance.MAX_POST_TEXT_NUM - s.length()) + "字");
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length() > Constant.mInstance.MAX_POST_TEXT_NUM){
                int p = s.length() - Constant.mInstance.MAX_POST_TEXT_NUM;
                s.delete(s.length() - p, s.length());
            }
        }
    }

    class UploadImage extends AsyncTask<String, Integer, Vector<String>>{

        @Override
        protected Vector<String> doInBackground(String... strings) {
            QYFile qyFile = new QYFile();
            Vector<String> img_id = new Vector<>();
            for(String s : s_url){
                String t = Img.compressWithUrl(s, UploadPostActivity.this);
                String msg = qyFile.uploadFileAllIn(Constant.mInstance.file_upload_verify_url, t, 0, qyFile.hashFileUrl(t));
                img_id.add(msg);
            }
            return img_id;
        }

        @Override
        protected void onPostExecute(Vector<String> s) {
            String[] data = new String[s.size()];
            int i = 0;
            for(String id : s) data[i++] = id;
            String[] l = {"text", "string", p.getText().toString(),
                    "ap_img_set", "list", GenerateJson.listString(0, data),
                "is_public", "int", "1"};
            String json = GenerateJson.universeJson2(l);
            UploadPostImageType uploadPostImageType = new UploadPostImageType();
            uploadPostImageType.execute(json);
            Log.d("hjt.post.post.json", json);
        }
    }

    class UploadPostImageType extends AsyncTask<String, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return MsgProcess.checkMsg(htp.advancePost(strings[0], Constant.mInstance.post_url, "Authorization", GlobalVariable.mInstance.token), false);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean)
                Toast.makeText(UploadPostActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(UploadPostActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
        }
    }
}