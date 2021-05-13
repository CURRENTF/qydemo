package com.example.qydemo0.QYpack;

import android.content.Context;
import android.widget.TextView;

import com.example.qydemo0.GameContentActivity;
import com.example.qydemo0.R;
import com.example.qydemo0.Widget.QYDIalog;
import com.example.qydemo0.view.WaveProgressView;

public class WaveLoadDialog {

    QYDIalog qydIalog;
    WaveProgressView mCircleView;

    public WaveLoadDialog(Context context){
        qydIalog = new QYDIalog(context, R.layout.wave_progress_dialog, new int[]{R.id.wave_view});
        qydIalog.setCanceledOnTouchOutside(false);
    }

    public void start_progress(){
        qydIalog.show();
        mCircleView = (WaveProgressView) qydIalog.findViewById(R.id.wave_view);
        mCircleView.setmWaterLevel(0f);
        mCircleView.startWave();
    }

    public void set_progress(float cur_progress){
        mCircleView.setmWaterLevel(cur_progress);
    }

    public void stop_progress(){
        qydIalog.dismiss();
        mCircleView.stopWave();
        mCircleView = null;
    }

}
