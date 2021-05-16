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
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SegmentChoiceActivity extends MyAppCompatActivity {

    LinearLayout all_main;

    LinearLayout.LayoutParams php = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    private int work_id;

    private QYrequest cur_request = new QYrequest();

    private List<String> intro = new ArrayList<>(),
                        upload_user = new ArrayList(),
                        useNum = new ArrayList<>(),
                        breakdownid = new ArrayList<>();

    SegmentChoiceItem sci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segment_choice);
        all_main = findViewById(R.id.content);
        ArrayList<String> list = getIntent().getStringArrayListExtra("params");
        work_id = Integer.valueOf(list.get(0));
        php.setMargins(10,10,10,10);
        new getBreakDown(SegmentChoiceActivity.this).execute(work_id);
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
                ArrayList<String > p1 = new ArrayList<>();
                p1.add(String.valueOf(work_id));
                p1.add(String.valueOf(breakdownid.get(Integer.valueOf(allPms[3]))));
                intent.putStringArrayListExtra("params", p1);
                startActivity(intent);
             }
        });
        all_main.addView(sci);
    }

    public class getBreakDown extends MyAsyncTask<Integer, Void, Boolean> {

        protected getBreakDown(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            try {
                JSONObject cur_res = new JSONObject(cur_request.advanceGet("https://api.yhf2000.cn/api/qingying/v1/work/breakdown/"+integers[0]+"/","Authorization", GlobalVariable.mInstance.token));
                if(!cur_res.getString("msg").equals("Success")) return false;
                Log.i("whc_cur_res", String.valueOf(cur_res));
                JSONArray bda = cur_res.getJSONArray("data");
                for(int i=0;i<bda.length();i++){
                    intro.add(bda.getJSONObject(i).getString("introduction"));
                    // 需要校准
                    upload_user.add(bda.getJSONObject(i).getJSONObject("belong").getString("username"));
                    useNum.add(bda.getJSONObject(i).getString("used_num"));
                    breakdownid.add(bda.getJSONObject(i).getString("id"));
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
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