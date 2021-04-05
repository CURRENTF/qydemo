package com.example.qydemo0;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qydemo0.QYpack.QYFile;
import com.example.qydemo0.utils.ImageSelector;
import com.example.qydemo0.QYpack.Img;
import com.google.android.exoplayer2.MediaItem;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class UploadPostActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener{

    GridLayout g = null;
    int[] imgs = {R.id.up_img1, R.id.up_img2, R.id.up_img3,
        R.id.up_img4, R.id.up_img4, R.id.up_img5, R.id.up_img6,
            R.id.up_img7, R.id.up_img8, R.id.up_img9};
    ImageView[] img_view = new ImageView[9];

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
        btn.setOnCheckedChangeListener(this);
        ImageView img = findViewById(imgs[img_pointer]);
        Img.url2imgViewRoundRectangle(getDrawable(R.drawable.add_f), img, this, 30);
        g = findViewById(R.id.grid_img_set);
        for(int i = 0; i < 9; i++){
            img = findViewById(imgs[i]);
            img.setOnClickListener(this);
            img_view[i] = img;
        }
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
    }

    void refreshPic(ArrayList<String> arr){

    }



}