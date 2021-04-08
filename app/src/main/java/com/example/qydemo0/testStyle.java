package com.example.qydemo0;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.example.qydemo0.QYpack.Img;
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
        test_img = (ImageView) findViewById(R.id.test_img);
        requestPermissions();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("/sdcard/DCIM/Camera/B612Kaji_20190213_142153_075.jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap  = BitmapFactory.decodeStream(fis);
        if(bitmap!=null) Log.i("bitmap","pass");
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
                int startCode = mCVClient.start();
                if(startCode==0){
                    new test_style().execute(bitmap);
                }
            }

            @Override
            public void onServiceDisconnect() {
                Log.e("TAG", "initService: onServiceDisconnect: ");
            }
        });

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

    public class test_style extends AsyncTask<Bitmap, Void, Bitmap> {
        @Override
        protected void onPostExecute(Bitmap aVoid) {
            super.onPostExecute(aVoid);

            Log.i("here", "stop");

            test_img.setImageBitmap(aVoid);
            //Log.i("save_img",Img.saveImg(aVoid,"hhh.jpg",testStyle.this));
            if (mCVClient != null) {
                mCVClient.stop();
            }
            mCVClient.releaseService();
            mCVClient = null;
        }

        @Override
        protected Bitmap doInBackground(Bitmap... voids) {
            FrameInputSlot inputSlot = (FrameInputSlot) mCVClient.createInputSlot();
            inputSlot.setTargetBitmap(voids[0]);
            FrameOutputSlot outputSlot = (FrameOutputSlot) mCVClient.createOutputSlot();
            mCVClient.process(inputSlot, outputSlot);
            FrameData frameData = outputSlot.getOutFrameData();
            byte[] outImageBuffer = frameData.getData();
            Log.d(TAG, "startProcess: " + outImageBuffer.length);
            Log.d(TAG, "startProcess outFrame.width = " + frameData.width);
            Log.d(TAG, "startProcess outFrame.height = " + frameData.height);

// RGB buffer.
            int[] colors = new int[outImageBuffer.length / 3];
            for (int j = 0; j < frameData.height; ++j) {

                for (int i = 0; i < frameData.width; ++i) {

                    int red = outImageBuffer[3 * (j * frameData.width + i)];

                    int green = outImageBuffer[3 * (j * frameData.width + i) + 1];

                    int blue = outImageBuffer[3 * (j * frameData.width  + i) + 2];

                    int alpha = 0xFF;

                    colors[j * frameData.width + i] = (alpha << 24) | (red << 16) | (green << 8) | (blue);
                }
            }

            Bitmap resultBmp = Bitmap.createBitmap(colors, frameData.width, frameData.height, Bitmap.Config.ARGB_8888);
            System.out.println(String.valueOf(resultBmp==null));
            return resultBmp;
        }
    }

}