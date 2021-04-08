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

    private CVUnitClient mCVClient;
    private Bitmap bitmap;

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
        mCVClient = CVUnit.getFaceFerClient
                (this.getApplicationContext()).addOnConnectionSucceedListener(new OnConnectionSucceedListener() {
            @Override
            public void onConnectionSucceed() {
                Log.i("TAG", " authorize connect: onConnectionSucceed");
            }
        }).addOnConnectionFailedListener(new OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Log.e("TAG", " authorize connect: onFailure: " + connectionResult.getErrorCode());
            }
        });

        mCVClient.initService(this, new ConnectionCallback() {
            @Override
            public void onServiceConnect() {
                Log.i("TAG", "initService: onServiceConnect");
                int startCode = mCVClient.start();
                if(startCode==0){
                    new test_face_fer().execute(bitmap);
                }
                else{
                    Log.i("whc123","init wrong!");
                }
            }

            @Override
            public void onServiceDisconnect() {
                Log.e("TAG", "initService: onServiceDisconnect: ");
            }
        });
    }

    public class test_face_fer extends AsyncTask<Bitmap, Void, Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            FaceInputSlot inputSlot = (FaceInputSlot) mCVClient.createInputSlot();
            inputSlot.setTargetBitmap(bitmaps[0]);
            FaceOutputSlot outputSlot = (FaceOutputSlot) mCVClient.createOutputSlot();
            mCVClient.process(inputSlot, outputSlot);
            FaceResultList faceList = outputSlot.getFaceList();
            List<FaceResult> faceResultList = faceList.getFaceResultList();
            for (FaceResult faceResult: faceResultList) {
                String expression = faceResult.getExpression();
                Log.i("cur_expression",expression);
            }
            if (mCVClient != null) {
                mCVClient.stop();
            }
            mCVClient.releaseService();
            mCVClient = null;
            return null;
        }
    }

}