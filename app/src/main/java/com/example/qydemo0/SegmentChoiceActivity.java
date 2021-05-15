package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.QYrequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SegmentChoiceActivity extends AppCompatActivity {

    LinearLayout all_main;

    List<String> segments = new ArrayList<>();

    LinearLayout.LayoutParams php = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    private int work_id;

    private QYrequest cur_request = new QYrequest();

    private List<String> intro = new ArrayList<>(),
                        upload_user = new ArrayList(),
                        useNum = new ArrayList<>(),
                        breakdownid = new ArrayList<>();

    private List<ArrayList<String> > videos_url = new ArrayList<>(), covers_url = new ArrayList<>();

    SegmentChoiceItem sci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segment_choice);
        all_main = findViewById(R.id.content);
        ArrayList<String> list = getIntent().getStringArrayListExtra("params");
        work_id = Integer.valueOf(list.get(0));
        php.setMargins(10,10,10,10);
        new getBreakDown().execute();
    }

    private void addNewView(String[] allPms){
        sci = new SegmentChoiceItem(SegmentChoiceActivity.this);
        sci.intro.setText(allPms[0]);
        sci.upload_user.setText("上传者：" + allPms[1]);
        sci.useNum.setText(allPms[2]);
        sci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SegmentChoiceActivity.this, SegmentPreLookActivity.class);
                intent.putStringArrayListExtra("params", videos_url.get(Integer.valueOf(allPms[3])));
                ArrayList<String > p1 = new ArrayList<>();
                p1.add(String.valueOf(work_id));
                p1.add(String.valueOf(breakdownid.get(Integer.valueOf(allPms[3]))));
                intent.putStringArrayListExtra("params1", p1);
                intent.putStringArrayListExtra("params2", covers_url.get(Integer.valueOf(allPms[3])));
                startActivity(intent);
             }
        });
        all_main.addView(sci);
    }

    public class getBreakDown extends AsyncTask<Integer, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Integer... integers) {
//            try {
//                JSONObject cur_res = new JSONObject(cur_request.advanceGet("https://api.yhf2000.cn/api/qingying/v1/work/breakdown/"+integers[0]+"/","Authorization", GlobalVariable.mInstance.token));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            intro.add("很良心的一个分段");
            upload_user.add("郭书宇");
            useNum.add("100");

            intro.add("很牛的一个分段");
            upload_user.add("郝继泰");
            useNum.add("80");

            intro.add("呕心沥血之作呀！");
            upload_user.add("尹浩飞");
            useNum.add("70");

            intro.add("着重分解卡点，让你体会最舒服的学习流程");
            upload_user.add("王昊晨");
            useNum.add("60");

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            for(int i=0;i<intro.size();i++){
                addNewView(new String[]{intro.get(i), upload_user.get(i), useNum.get(i), String.valueOf(i)});
            }
        }
    }

}