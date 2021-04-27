package com.example.qydemo0.Widget.ListItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qydemo0.PlayerActivity;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.Video.Work;
import com.example.qydemo0.R;

import org.json.JSONException;
import org.json.JSONObject;

public class WorkItem extends LinearLayoutItem {


    public Context mContext = null;
    private View mView = null;
    ImageView cover = null;
    TextView name = null, intro = null, like = null, play = null, uploader = null;
    public int id = 0;

    private Activity getActivity(){
        return (Activity) mContext;
    }

    public WorkItem(Context context) {
        super(context);
        mContext = context;
        initDf();
    }

    // RecyclerView传入
    public WorkItem(ViewGroup parent, Activity activity){
        super(activity);
        mContext = activity;
        mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.work_item, this, true);
    }

    private void initDf(){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.work_item, this, true);
    }

    public void fill(JSONObject json){
        try {
            JSONObject coverInfo = json.getJSONObject("cover");
            init(coverInfo.getString("url"), json.getString("name"),
                    json.getInt("like_num"), json.getInt("play_num"),
                    json.getString("introduction"), json.getJSONObject("belong").getString("username"), json.getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return;
    }

    public void init(String cover_url, String video_name, int like_num, int play_num, String intro, String uploader_name, int id){
        cover = mView.findViewById(R.id.cover);
        name = mView.findViewById(R.id.video_name);
        this.intro = mView.findViewById(R.id.text_intro);
        like = mView.findViewById(R.id.text_like_num);
        play = mView.findViewById(R.id.text_play_num);
        uploader = mView.findViewById(R.id.uploader);

        Img.url2imgViewRoundRectangle(cover_url, cover, mContext, 20);
        name.setText(video_name);
        like.setText(String.valueOf(like_num));
        play.setText(String.valueOf(play_num));
        uploader.setText(uploader_name);
        this.intro.setText(intro);
        this.id = id;
        setOnClick();
    }

    public void setOnClick(){
        mView.setOnClickListener(new SendWorkId());
    }

    class SendWorkId implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), PlayerActivity.class);
            Log.e("hjt.id", String.valueOf(((WorkItem)v).id));
            intent.putExtra("id", ((WorkItem)v).id);
            getActivity().startActivity(intent);
        }
    }
}