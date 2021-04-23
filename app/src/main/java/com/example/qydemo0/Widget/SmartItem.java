package com.example.qydemo0.Widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.qydemo0.LearnDanceActivity;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.R;
import com.example.qydemo0.entry.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SmartItem extends RelativeLayout implements View.OnClickListener{

    private Context mContext = null;
    private View mView = null;
    ImageView cover, medal, btn_expand;
    TextView video_name, learn_progress;
    ProgressBar progressBar;
    RelativeLayout layout;
    LinearLayout records;
    int lid;
    String wid;

    public SmartItem(Context context) {
        super(context);
        mContext = context;
        initDf();
    }

    private void initDf(){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.smart_item, this, true);
    }
    private Activity getActivity(){
        return (Activity) mContext;
    }

    JSONObject info;

    public void init(JSONObject json, int record_num, int segment_num, int score, int lid){
        info = json;
        this.lid = lid;
        layout = mView.findViewById(R.id.item_layout);
        cover = mView.findViewById(R.id.cover);
        medal = mView.findViewById(R.id.medal);
        video_name = mView.findViewById(R.id.work_name);
        learn_progress = mView.findViewById(R.id.txt_learn_progress);
        progressBar = mView.findViewById(R.id.learn_progress_bar);
        records = mView.findViewById(R.id.learn_info_d);
        btn_expand = mView.findViewById(R.id.btn_expand);

        try {
            wid = json.getString("id");
            video_name.setText(json.getString("name"));
            JSONObject cover_json = json.getJSONObject("cover");
            Img.url2imgViewRoundRectangle(cover_json.getString("url"), cover, mContext, 20);
            progressBar.setProgress((int)((float)record_num / segment_num * 100));
            learn_progress.setText(record_num + "/" + segment_num);
            if(score < 60) medal.setImageDrawable(mContext.getDrawable(R.drawable.ic__bronze_medal));
            else if(score < 85) medal.setImageDrawable(mContext.getDrawable(R.drawable.ic__silver_medal));
            else medal.setImageDrawable(mContext.getDrawable(R.drawable.ic__gold_medal));
            btn_expand.setOnClickListener(this);
            layout.setOnClickListener(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    int record_len = 0;
    int start = 0, len = 20;
    int flag = 0;

    @Override
    public void onClick(View v) {
        if(v == btn_expand) flag = 1;
        else flag = 2;
        GetRecords getRecords = new GetRecords();
        getRecords.execute();
    }


    class GetRecords extends AsyncTask<String, Integer, JSONArray>{

        @Override
        protected JSONArray doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            Log.d("hjt.get.record", lid + ".");
            return MsgProcess.msgProcessArr(htp.advanceGet(Constant.mInstance.record_url + lid + "/" +
                    Json2X.Json2StringGet("start", String.valueOf(start), "lens", String.valueOf(len)), "Authorization", GlobalVariable.mInstance.token), false);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.e("hjt.get.learn.record", "json_null");
            }
            else {
                record_len = jsonArray.length();
                Log.d("hjt.learn_item.len", record_len + ".");
                if(flag == 2){
                    try {
                        if(record_len == 0){
                            record_len = 1;
                        }
                        else {
                            JSONObject json = jsonArray.getJSONObject(record_len - 1);
                            if(json.getInt("status") == 2) record_len ++;
                        }
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), LearnDanceActivity.class);
                        ArrayList<String> list = new ArrayList<>();
                        list.add(String.valueOf(lid));
                        list.add(String.valueOf(wid));
                        list.add(String.valueOf(record_len));
                        intent.putExtra("params", list);
                        getActivity().startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                for(int i = 0; i < jsonArray.length(); i++){
                    LittleLearnItem item = new LittleLearnItem(mContext);
                    try {
                        item.init(jsonArray.getJSONObject(i), i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    records.addView(item);
                }
            }
        }
    }
}