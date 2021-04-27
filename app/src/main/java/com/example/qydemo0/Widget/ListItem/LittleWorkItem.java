package com.example.qydemo0.Widget.ListItem;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LittleWorkItem extends LinearLayoutItem {

    public static int height = 120;

    private Context mContext = null;
    private View mView = null;
    ImageView cover = null;
    TextView name = null, like = null, play = null;
    public int id = 0;

    public LittleWorkItem(Context context) {
        super(context);
        mContext = context;
        initDf();
    }
    public LittleWorkItem(ViewGroup parent, Activity activity){
        super(activity);
        mContext = activity;
        mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.little_work_item, this, true);
    }

    @Override
    public void fill(JSONObject json) {
        JSONObject cover = null;
        try {
            cover = json.getJSONObject("cover");
            init(cover.getString("url"), json.getString("name"), json.getInt("like_num"), json.getInt("play_num"));
            id = json.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initDf(){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.work_item, this, true);
    }

    public void init(String cover_url, String video_name, int like_num, int play_num){
//        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mView = inflater.inflate(R.layout.work_item, this, true);
        cover = mView.findViewById(R.id.cover);
        name = mView.findViewById(R.id.video_name);
        like = mView.findViewById(R.id.text_like_num);
        play = mView.findViewById(R.id.text_play_num);

        Img.url2imgViewRoundRectangle(cover_url, cover, mContext, 20);
        name.setText(video_name);
        like.setText(String.valueOf(like_num));
        play.setText(String.valueOf(play_num));
    }
}
