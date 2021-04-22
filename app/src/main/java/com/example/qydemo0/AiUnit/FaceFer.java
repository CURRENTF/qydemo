package com.example.qydemo0.AiUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.aiunit.common.protocol.face.FaceResult;
import com.aiunit.common.protocol.face.FaceResultList;
import com.aiunit.common.protocol.types.Rect2Pt;
import com.aiunit.vision.common.ConnectionCallback;
import com.aiunit.vision.face.FaceInputSlot;
import com.aiunit.vision.face.FaceOutputSlot;
import com.coloros.ocs.ai.cv.CVUnit;
import com.coloros.ocs.ai.cv.CVUnitClient;
import com.coloros.ocs.base.common.ConnectionResult;
import com.coloros.ocs.base.common.api.OnConnectionFailedListener;
import com.coloros.ocs.base.common.api.OnConnectionSucceedListener;

import org.json.JSONObject;

import java.util.List;

public class FaceFer {
    private CVUnitClient mCVClient;
    private int startCode = -1;

    public void init(Context app_context, Context cur_context) {
        mCVClient = CVUnit.getFaceFerClient
                (app_context).addOnConnectionSucceedListener(new OnConnectionSucceedListener() {
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

        mCVClient.initService(cur_context, new ConnectionCallback() {
            @Override
            public void onServiceConnect() {
                Log.i("TAG", "initService: onServiceConnect");
                int startCode = mCVClient.start();
                if(startCode==0){

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

    public void stop(){
        if (mCVClient != null) {
            mCVClient.stop();
        }
        mCVClient.releaseService();
        mCVClient = null;
    }

    public String getFerFromBitmap(Bitmap bitmap){

        String res = "";

        FaceInputSlot inputSlot = (FaceInputSlot) mCVClient.createInputSlot();
        inputSlot.setTargetBitmap(bitmap);
        FaceOutputSlot outputSlot = (FaceOutputSlot) mCVClient.createOutputSlot();
        mCVClient.process(inputSlot, outputSlot);
        FaceResultList faceList = outputSlot.getFaceList();
        List<FaceResult> faceResultList = faceList.getFaceResultList();
        for (FaceResult faceResult: faceResultList) {
            res = faceResult.getExpression();
        }
        return res;

    }

    public int getStartCode(){
        return this.startCode;
    }


}
