package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.donkingliang.imageselector.utils.ImageSelector;


public class UploadPostActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener{

    GridLayout g = findViewById(R.id.grid_img_set);
    int[] imgs = {R.id.up_img1, R.id.up_img2, R.id.up_img3,
        R.id.up_img4, R.id.up_img4, R.id.up_img5, R.id.up_img6,
            R.id.up_img7, R.id.up_img8, R.id.up_img9};
    ImageView[] img_view = new ImageView[9];

    int img_pointer = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);
        CompoundButton btn = findViewById(R.id.post_method);
        btn.setOnCheckedChangeListener(this);
        ImageView img = findViewById(imgs[img_pointer + 1]);
        img.setImageResource(R.drawable.ic_black_add);
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
        if(v.getId() == imgs[img_pointer + 1]){
            //限数量的多选(比如最多9张)
//            ImageSelector.builder()
//                    .useCamera(false) // 设置是否使用拍照
//                    .setSingle(false)  //设置是否单选
//                    .setMaxSelectCount(9) // 图片的最大选择数量，小于等于0时，不限数量。
//                    .setSelected(selected) // 把已选的图片传入默认选中。
//                    .canPreview(true) //是否可以预览图片，默认为true
//                    .start(this, REQUEST_CODE); // 打开相册

        }
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE && data != null) {
//            //获取选择器返回的数据
//            ArrayList<String> images = data.getStringArrayListExtra(
//                    ImageSelector.SELECT_RESULT);
//
//            /**
//             * 是否是来自于相机拍照的图片，
//             * 只有本次调用相机拍出来的照片，返回时才为true。
//             * 当为true时，图片返回的结果有且只有一张图片。
//             */
//            boolean isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false);
//        }
//    }
}