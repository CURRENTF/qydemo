package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.qydemo0.QYpack.Img;


public class ViewImageActivity extends AppCompatActivity {

    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString("img");
        img = findViewById(R.id.img);
        Img.url2imgView(url, img, this);
    }

}