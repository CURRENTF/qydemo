package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.HttpCounter;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;
import com.example.qydemo0.Widget.QYScrollView;
import com.example.qydemo0.Widget.ListItem.SmartItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LearningListActivity extends MyAppCompatActivity implements View.OnClickListener {

    LinearLayout list_progress, list_finished;
    QYScrollView view_progress, view_finished;
    TextView progress, finished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_list);
        list_progress = findViewById(R.id.learning_list_progress);
        list_finished = findViewById(R.id.learning_list_finished);
        view_progress = findViewById(R.id.view_progress);
        view_finished = findViewById(R.id.view_finished);
        progress = findViewById(R.id.btn_progress);
        finished = findViewById(R.id.btn_finished);
        GetLearningListProgress getLearningListProgress = new GetLearningListProgress(this);
        getLearningListProgress.execute();
        GetLearningListFinished getLearningListFinished = new GetLearningListFinished(this);
        getLearningListFinished.execute();
        progress.setOnClickListener(this);
        finished.setOnClickListener(this);

    }

    int switcher = 0;

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_progress){
            if(switcher == 0) return;
            switcher = 0;
            progress.setTextColor(getColor(R.color.red));
            finished.setTextColor(getColor(R.color.black));
            Animation animation = AnimationUtils.loadAnimation(this
                    , R.anim.ani_right_translate_alpha_500ms);
            Animation animation2 = AnimationUtils.loadAnimation(this
                    , R.anim.ani_right_translate_in_alpha_500ms);
            view_finished.startAnimation(animation);
            view_progress.startAnimation(animation2);
            ChangeVisibility changeVisibility = new ChangeVisibility(this);
            changeVisibility.execute(switcher == 1);
        }
        else if(v.getId() == R.id.btn_finished) {
            if(switcher == 1) return;
            switcher = 1;
            progress.setTextColor(getColor(R.color.black));
            finished.setTextColor(getColor(R.color.red));
            Animation animation = AnimationUtils.loadAnimation(this,
                    R.anim.ani_left_translate_alpha_500ms);
            Animation animation2 = AnimationUtils.loadAnimation(this,
                    R.anim.ani_left_translate_in_alpha_500ms);
            view_progress.startAnimation(animation);
            view_finished.startAnimation(animation2);
            ChangeVisibility changeVisibility = new ChangeVisibility(this);
            changeVisibility.execute(switcher == 1);
        }
    }


    int pr_startPos = 0, pr_len = Constant.mInstance.MAX_UPDATE_LEN;
    HttpCounter counter_1 = new HttpCounter();
    class GetLearningListProgress extends MyAsyncTask<String, Integer, JSONArray> {


        protected GetLearningListProgress(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return MsgProcess.msgProcessArr(htp.advanceGet(Constant.mInstance.learn_list_url + "1/"
                            + Json2X.Json2StringGet("start", String.valueOf(counter_1.start), "lens", String.valueOf(counter_1.len)),
                    "Authorization", GlobalVariable.mInstance.token), false, null);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.e("hjt.learn.list.progress", "null");
            }
            else {
                Log.d("hjt.learn", jsonArray.toString());
                counter_1.inc(jsonArray.length());
                for(int i = 0; i < jsonArray.length(); i++){
                    try {
                        JSONObject json = jsonArray.getJSONObject(i);
                        Log.d("hjt.smart.item", json.toString());
                        SmartItem smartItem = new SmartItem(LearningListActivity.this);
                        smartItem.init(json.getJSONObject("work_info"), json.getInt("record_num"),
                                json.getInt("segment_num"), json.getInt("avg_score"), json.getInt("lid"));
                        list_progress.addView(smartItem);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    HttpCounter counter = new HttpCounter();
    class GetLearningListFinished extends MyAsyncTask<String, Integer, JSONArray>{


        protected GetLearningListFinished(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return MsgProcess.msgProcessArr(htp.advanceGet(Constant.mInstance.learn_list_url + "2/"
                            + Json2X.Json2StringGet("start", String.valueOf(counter.start), "lens", String.valueOf(counter.len)),
                    "Authorization", GlobalVariable.mInstance.token), false,  null);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.e("hjt.learn.list.progress", "null");
            }
            else {
                counter.inc(jsonArray.length());
                for(int i = 0; i < jsonArray.length(); i++){
                    try {
                        JSONObject json = jsonArray.getJSONObject(i);
                        SmartItem smartItem = new SmartItem(LearningListActivity.this);
                        smartItem.init(json.getJSONObject("work_info"), json.getInt("record_num"),
                                json.getInt("segment_num"), json.getInt("avg_score"), json.getInt("lid"));
                        list_finished.addView(smartItem);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class ChangeVisibility extends MyAsyncTask<Boolean, Integer, Boolean>{

        protected ChangeVisibility(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            try {
                Thread.sleep(Constant.mInstance.ani_time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return booleans[0];
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(!aBoolean){
                view_progress.setVisibility(View.VISIBLE);
                view_finished.setVisibility(View.GONE);
            }
            else {
                view_progress.setVisibility(View.GONE);
                view_finished.setVisibility(View.VISIBLE);
            }
        }
    }

}