package com.example.qydemo0.AiUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.aiunit.core.FrameData;
import com.aiunit.vision.common.ConnectionCallback;
import com.aiunit.vision.common.FrameInputSlot;
import com.aiunit.vision.common.FrameOutputSlot;
import com.coloros.ocs.ai.cv.CVUnit;
import com.coloros.ocs.ai.cv.CVUnitClient;
import com.coloros.ocs.base.common.ConnectionResult;
import com.coloros.ocs.base.common.api.OnConnectionFailedListener;
import com.coloros.ocs.base.common.api.OnConnectionSucceedListener;

public class ChangeVideoStyle {

    private CVUnitClient mCVClient;

    public void initClient(Context context) {

        mCVClient = CVUnit.getVideoStyleTransferDetectorClient
                (context).addOnConnectionSucceedListener(new OnConnectionSucceedListener() {
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

        mCVClient.initService(context, new ConnectionCallback() {
            @Override
            public void onServiceConnect() {
                Log.i("TAG", "initService: onServiceConnect");
                int startCode = mCVClient.start();
            }

            @Override
            public void onServiceDisconnect() {
                Log.e("TAG", "initService: onServiceDisconnect: ");
            }
        });

    }

    public Bitmap getChangeStyleImage(Bitmap bitmap) {
        FrameInputSlot inputSlot = (FrameInputSlot) mCVClient.createInputSlot();
        inputSlot.setTargetBitmap(bitmap);
        FrameOutputSlot outputSlot = (FrameOutputSlot) mCVClient.createOutputSlot();
        mCVClient.process(inputSlot, outputSlot);
        FrameData frameData = outputSlot.getOutFrameData();
        byte[] outImageBuffer = frameData.getData();
        stopClient();
        return BitmapFactory.decodeByteArray(outImageBuffer, 0, outImageBuffer.length);
    }

    private void stopClient(){
        if (mCVClient != null) {
            mCVClient.stop();
        }

        mCVClient.releaseService();
        mCVClient = null;
    }

}
