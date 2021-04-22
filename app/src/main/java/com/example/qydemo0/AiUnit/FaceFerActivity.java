package com.example.qydemo0.AiUnit;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.aiunit.common.protocol.face.FaceResult;
import com.aiunit.common.protocol.face.FaceResultList;
import com.aiunit.vision.common.ConnectionCallback;
import com.aiunit.vision.face.FaceInputSlot;
import com.aiunit.vision.face.FaceOutputSlot;
import com.coloros.ocs.ai.cv.CVUnit;
import com.coloros.ocs.ai.cv.CVUnitClient;
import com.coloros.ocs.base.common.ConnectionResult;
import com.coloros.ocs.base.common.api.OnConnectionFailedListener;
import com.coloros.ocs.base.common.api.OnConnectionSucceedListener;
import com.example.qydemo0.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class FaceFerActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private FaceFer ff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_fer);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("/sdcard/DCIM/Camera/IMG_20180615_075414.jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap  = BitmapFactory.decodeStream(fis);
        if(bitmap!=null) Log.i("bitmap","pass");
        ff.init(getApplicationContext(), this);
    }

//    public class test_face_fer extends AsyncTask<Bitmap, Void, Void>{
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//        }
//
//        @Override
//        protected Void doInBackground(Bitmap... bitmaps) {
//
//        }
//    }

}