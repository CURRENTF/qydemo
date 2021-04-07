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
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.Widget.QYScrollView;

import org.json.JSONArray;

public class LearningListActivity extends AppCompatActivity implements View.OnClickListener {

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
            view_finished.startAnimation(animation);
            ChangeVisibility changeVisibility = new ChangeVisibility();
            changeVisibility.execute(switcher == 1);
        }
        else if(v.getId() == R.id.btn_finished) {
            if(switcher == 1) return;
            switcher = 1;
            progress.setTextColor(getColor(R.color.black));
            finished.setTextColor(getColor(R.color.red));
            Animation animation = AnimationUtils.loadAnimation(this,
                    R.anim.ani_left_translate_alpha_500ms);
            view_progress.startAnimation(animation);
            ChangeVisibility changeVisibility = new ChangeVisibility();
            changeVisibility.execute(switcher == 1);
        }
    }


    int startPos = 0, len = 20;
    class GetLearningListProgress extends AsyncTask<String, Integer, JSONArray>{

        @Override
        protected JSONArray doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return MsgProcess.msgProcessArr(htp.advanceGet(Constant.mInstance.learn_list_url + "1/"
                            + Json2X.Json2StringGet("start", String.valueOf(startPos), "lens", String.valueOf(len)),
                    "Authorization", GlobalVariable.mInstance.token), false);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.e("hjt.learn.list.progress", "null");
            }
            else {
                // todo
            }
        }
    }

    class ChangeVisibility extends AsyncTask<Boolean, Integer, Boolean>{

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
            if(aBoolean){
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