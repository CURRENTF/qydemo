package com.example.qydemo0;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.aiunit.common.protocol.ocr.OCRItem;
import com.aiunit.common.protocol.ocr.OCRItemCollection;
import com.aiunit.common.protocol.types.Point;
import com.aiunit.core.FrameData;
import com.aiunit.vision.common.ConnectionCallback;
import com.aiunit.vision.common.FrameInputSlot;
import com.aiunit.vision.common.FrameOutputSlot;
import com.aiunit.vision.ocr.OCRInputSlot;
import com.aiunit.vision.ocr.OCROutputSlot;
import com.coloros.ocs.ai.cv.CVUnit;
import com.coloros.ocs.ai.cv.CVUnitClient;
import com.coloros.ocs.base.common.ConnectionResult;
import com.coloros.ocs.base.common.api.OnConnectionFailedListener;
import com.coloros.ocs.base.common.api.OnConnectionSucceedListener;
import com.example.qydemo0.R;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class testStyle extends AppCompatActivity {
    private CVUnitClient mCVClient;
    private Button mBtnConnect = null;
    String TAG = "testStyle";
    int startCode = -1;
    ImageView test_img;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("onCreate", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_style);
        requestPermissions();
        mBtnConnect = findViewById(R.id.test);
        //test_img = (ImageView) findViewById(R.id.test_img);

        FileInputStream fis = null;
        try {
            fis = new FileInputStream("/sdcard/DCIM/Camera/IMG_20180820_210432.jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap  = BitmapFactory.decodeStream(fis);
        if (bitmap != null){
            Log.i("bitmap is ok", "pass");
        }

        mCVClient = CVUnit.getVideoStyleTransferDetectorClient
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
                startCode = mCVClient.start();
                Log.i("TAG", "initService: start success");
            }

            @Override
            public void onServiceDisconnect() {
                Log.e("TAG", "initService: onServiceDisconnect: ");
            }
        });
//
        mBtnConnect.setOnClickListener( v -> {
                    if (startCode == 0)
                    {
                        new test_style().execute();
                    } else {
                        Log.i("startCode", String.valueOf(startCode));
                    }
                }
        );

//        if (mCVClient != null) {
//            mCVClient.stop();
//        }
//
//        mCVClient.releaseService();
//        mCVClient = null;
    }

    private void requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                    }, 0x0010);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public class test_style extends AsyncTask<Void, Void, Bitmap>{
        @Override
        protected void onPostExecute(Bitmap aVoid) {
            super.onPostExecute(aVoid);

            Log.i("here","stop");

            //test_img.setImageBitmap(aVoid);

            if (mCVClient != null) {
                mCVClient.stop();
            }
            mCVClient.releaseService();
            mCVClient = null;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            FrameInputSlot inputSlot = (FrameInputSlot) mCVClient.createInputSlot();
            inputSlot.setTargetBitmap(bitmap);
            FrameOutputSlot outputSlot = (FrameOutputSlot) mCVClient.createOutputSlot();
            mCVClient.process(inputSlot, outputSlot);
            FrameData frameData = outputSlot.getOutFrameData();
            System.out.println("look at here : " + frameData.getData());
            Log.i("length",""+frameData.getData().length);
            try {
                InputStream is = new ByteArrayInputStream(frameData.getData());
                OutputStream out = new FileOutputStream("/sdcard/1/123121312.jpg");
                int len=0;
                byte[] buff = new byte[1024];
                while ((len = is.read(buff)) != -1) {
                    out.write(buff, 0, len);
                }
                is.close();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap outImageBuffer = BitmapFactory.decodeByteArray(frameData.getData(), 0, frameData.getData().length);
            return null;
        }
    }

}