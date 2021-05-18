package com.example.qydemo0.Widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.example.qydemo0.R;
import com.race604.drawable.wave.WaveDrawable;

public class QYLoading {
    QYDialogUncancelable qydIalog;
    WaveDrawable mWaveDrawable ;

    public QYLoading(Context context){
        qydIalog = new QYDialogUncancelable(context, R.layout.qy_loading_dialog, new int[]{});
        mWaveDrawable = new WaveDrawable(context, R.mipmap.logo);
        mWaveDrawable.setIndeterminate(true);
        mWaveDrawable.setWaveAmplitude(50);
        mWaveDrawable.setWaveLength(50);
        mWaveDrawable.setWaveSpeed(10);
    }

    public void start_dialog(){
        qydIalog.show();
        ((ImageView)(qydIalog.findViewById(R.id.loadingIcon))).setImageDrawable(mWaveDrawable);
    }

    public void stop_dialog(){
        qydIalog.dismiss();
    }

}
