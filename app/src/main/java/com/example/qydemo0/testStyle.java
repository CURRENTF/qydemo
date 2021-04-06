//package com.example.qydemo0;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Button;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//
//import com.aiunit.common.protocol.ocr.OCRItem;
//import com.aiunit.common.protocol.ocr.OCRItemCollection;
//import com.aiunit.common.protocol.types.Point;
//import com.aiunit.vision.common.ConnectionCallback;
//import com.aiunit.vision.ocr.OCRInputSlot;
//import com.aiunit.vision.ocr.OCROutputSlot;
//import com.coloros.ocs.ai.cv.CVUnit;
//import com.coloros.ocs.ai.cv.CVUnitClient;
//import com.coloros.ocs.base.common.ConnectionResult;
//import com.coloros.ocs.base.common.api.OnConnectionFailedListener;
//import com.coloros.ocs.base.common.api.OnConnectionSucceedListener;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.util.List;
//
//public class testStyle extends AppCompatActivity {
//    private CVUnitClient mCVClient;
//    private Button mBtnConnect;
//    String TAG = "MainActivity";
//    int startCode = -1;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        Log.i("onCreate", "onCreate");
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test_style);
//        requestPermissions();
//        mBtnConnect = (Button) findViewById(R.id.test);
//
//
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream("/sdcard/Pictures/吉林省17宣讲证书_1.jpg");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        Bitmap bitmap  = BitmapFactory.decodeStream(fis);
//        if (bitmap != null){
//            Log.i("bitmap is ok", "pass");
//        }
//
//        mCVClient = CVUnit.getOCRDetectorClient(this.getApplicationContext())
//                .addOnConnectionSucceedListener(new OnConnectionSucceedListener() {
//                    @Override
//                    public void onConnectionSucceed() {
//                        Log.i("TAG", " authorize connect: onConnectionSucceed");
//                    }
//                }).addOnConnectionFailedListener(new OnConnectionFailedListener() {
//                    @Override
//                    public void onConnectionFailed(ConnectionResult connectionResult) {
//                        Log.e("TAG", " authorize connect: onFailure: " + connectionResult.getErrorCode());
//                    }
//                });
//
//        mCVClient.initService(this, new ConnectionCallback() {
//            @Override
//            public void onServiceConnect() {
//                Log.i("TAG", "initService: onServiceConnect");
//                startCode = mCVClient.start();
//                Log.i("TAG", "initService: start success");
//            }
//
//            @Override
//            public void onServiceDisconnect() {
//                Log.e("TAG", "initService: onServiceDisconnect: ");
//            }
//        });
//
////
//        mBtnConnect.setOnClickListener( v -> {
//                    if (startCode == 0)
//                    {
//                        OCRInputSlot inputSlot = (OCRInputSlot) mCVClient.createInputSlot();
//                        inputSlot.setTargetBitmap(bitmap);
//
//                        OCROutputSlot outputSlot = (OCROutputSlot) mCVClient.createOutputSlot();
//
//                        mCVClient.process(inputSlot, outputSlot);
//                        OCRItemCollection ocrItemCollection = outputSlot.getOCRItemCollection();
//                        List<OCRItem> ocrItemList = ocrItemCollection.getOrcItemList();
//                        for (OCRItem ocrItem : ocrItemList) {
//                            List<Point> boundingBox = ocrItem.getBoundingBox();
//                            String text = ocrItem.getText();
//                            Log.i(TAG, String.valueOf(text));
//                        }
//
//                        if (mCVClient != null) {
//                            mCVClient.stop();
//                        }
//                        mCVClient.releaseService();
//                        mCVClient = null;
//                    } else {
//                        Log.i("startCode", String.valueOf(startCode));
//                    }
//                }
//        );
//
////        if (mCVClient != null) {
////            mCVClient.stop();
////        }
////
////        mCVClient.releaseService();
////        mCVClient = null;
//    }
//
//    private void requestPermissions() {
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//                int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                if (permission != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this, new String[] {
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.CAMERA
//                    }, 0x0010);
//                }
//            }
//        } catch (Exception ignored) {
//        }
//    }
//}