package com.example.qydemo0.Widget;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.R;

public class WorkItem extends LinearLayout {

    public static int height = 120;

    private Context mContext = null;
    private View mView = null;
    ImageView cover = null;
    TextView name = null, intro = null, like = null, play = null;

    public WorkItem(Context context) {
        super(context);
        mContext = context;
        initDf();
    }

    private void initDf(){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.work_item, this, true);
    }

    public void init(String cover_url, String video_name, int like_num, int play_num, String intro){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.work_item, this, true);
        cover = mView.findViewById(R.id.cover);
        name = mView.findViewById(R.id.video_name);
        this.intro = mView.findViewById(R.id.text_intro);
        like = mView.findViewById(R.id.text_like_num);
        play = mView.findViewById(R.id.text_play_num);

        Img.url2imgViewRoundRectangle(cover_url, cover, mContext, 20);
        name.setText(video_name);
        like.setText(String.valueOf(like_num));
        play.setText(String.valueOf(play_num));
        this.intro.setText(intro);
    }
}
