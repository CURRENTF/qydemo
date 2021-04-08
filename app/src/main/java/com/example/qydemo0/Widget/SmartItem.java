package com.example.qydemo0.Widget;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.R;
import com.example.qydemo0.entry.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SmartItem extends RelativeLayout implements View.OnClickListener{

    private Context mContext = null;
    private View mView = null;
    ImageView cover, medal;
    TextView video_name, learn_progress;
    ProgressBar progressBar;
    RelativeLayout layout;
    LinearLayout records;
    int lid;


    public SmartItem(Context context) {
        super(context);
        mContext = context;
        initDf();
    }

    private void initDf(){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.smart_item, this, true);
    }

    JSONObject info;

    public void init(JSONObject json, int record_num, int segment_num, int score, int lid){
        info = json;
        this.lid = lid;
//        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mView = inflater.inflate(R.layout.smart_item, this, true);
        layout = mView.findViewById(R.id.item_layout);
        cover = mView.findViewById(R.id.cover);
        medal = mView.findViewById(R.id.medal);
        video_name = mView.findViewById(R.id.work_name);
        learn_progress = mView.findViewById(R.id.txt_learn_progress);
        progressBar = mView.findViewById(R.id.learn_progress_bar);
        records = mView.findViewById(R.id.learn_info_d);

        try {
            video_name.setText(json.getString("name"));
            JSONObject cover_json = json.getJSONObject("cover");
            Img.url2imgViewRoundRectangle(cover_json.getString("url"), cover, mContext, 20);
            progressBar.setProgress((int)((float)record_num / segment_num * 100));
            learn_progress.setText(record_num + "/" + segment_num);
            if(score < 60) medal.setImageDrawable(mContext.getDrawable(R.drawable.ic__bronze_medal));
            else if(score < 85) medal.setImageDrawable(mContext.getDrawable(R.drawable.ic__silver_medal));
            else medal.setImageDrawable(mContext.getDrawable(R.drawable.ic__gold_medal));
            layout.setOnClickListener(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        GetRecords getRecords = new GetRecords();
        getRecords.execute();
    }

    int start = 0, len = 20;

    class GetRecords extends AsyncTask<String, Integer, JSONArray>{

        @Override
        protected JSONArray doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return MsgProcess.msgProcessArr(htp.advanceGet(Constant.mInstance.record_url + lid + "/" +
                    Json2X.Json2StringGet("start", String.valueOf(start), "lens", String.valueOf(len))), false);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.e("hjt.get.learn.record", "json_null");
            }
            else {
                for(int i = 0; i < jsonArray.length(); i++){
                    LittleLearnItem item = new LittleLearnItem(mContext);
//                    item.init()
                    // todo
                    records.addView(item);
                }
            }
        }
    }
}
