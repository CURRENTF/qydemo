package com.example.qydemo0.Widget.ListItem;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.R;
import com.example.qydemo0.utils.DPIUtil;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class RenderQueueItem extends LinearLayout {

    public static int height = 120;

    private Context mContext = null;
    private View mView = null;
    ImageView cover = null;
    TextView name = null, text_choice = null;
    LinearProgressIndicator progressbar = null;

    public  RenderQueueItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public  RenderQueueItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initDf();
    }

    public RenderQueueItem(Context context) {
        this(context, null);
    }

    private void initDf(){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.render_queue_item, this, true);
    }

    public void init(String cover_url, String render_time, String render_params, float scheu){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.render_queue_item, this, true);
        cover = mView.findViewById(R.id.cover);
        name = mView.findViewById(R.id.video_time);
        text_choice = mView.findViewById(R.id.text_choice);
        progressbar = mView.findViewById(R.id.process_render);
        Img.url2imgViewRoundRectangle(cover_url, cover, mContext, 20);
        name.setText(render_time);
        text_choice.setText(render_params);
    }

}
