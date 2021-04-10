package com.example.qydemo0;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.Widget.Dashboard;
import com.example.qydemo0.Widget.QYScrollView;
import com.example.qydemo0.Widget.RenderItem;
import com.example.qydemo0.view.CustomLinearLayout;
import com.example.qydemo0.utils.DPIUtil;
import com.example.qydemo0.view.LeftSlideView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RenderQueueActivity extends AppCompatActivity implements View.OnClickListener{

    QYScrollView left, right;
    LinearLayout l_left, l_right;
    TextView btn_left, btn_right;
    View last;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_render_queue);
        left = findViewById(R.id.view_progress);
        right = findViewById(R.id.view_finished);
        l_left = findViewById(R.id.list_progress);
        l_right = findViewById(R.id.list_finished);

        btn_left = findViewById(R.id.btn_progress);
        btn_right = findViewById(R.id.btn_finished);
        last = btn_left;
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        r_out = AnimationUtils.loadAnimation(this, R.anim.ani_right_translate_alpha_500ms);
        r_in = AnimationUtils.loadAnimation(this, R.anim.ani_right_translate_in_alpha_500ms);
        l_out = AnimationUtils.loadAnimation(this, R.anim.ani_left_translate_alpha_500ms);
        l_in = AnimationUtils.loadAnimation(this, R.anim.ani_left_translate_in_alpha_500ms);
        GetList getList = new GetList();
        getList.execute();
        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                GetList getList = new GetList();
                getList.execute();
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 5000);
    }


    Animation l_out, l_in, r_out, r_in;


    @Override
    public void onClick(View view) {
        if(view == last) return;
        if(view == btn_left){
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.GONE);
            btn_left.setTextColor(getColor(R.color.red));
            btn_right.setTextColor(getColor(R.color.black));
            left.startAnimation(l_in);
            right.startAnimation(l_out);
        }
        else {
            left.setVisibility(View.GONE);
            right.setVisibility(View.VISIBLE);
            btn_left.setTextColor(getColor(R.color.black));
            btn_right.setTextColor(getColor(R.color.red));
            left.startAnimation(r_out);
            right.startAnimation(r_in);
        }
        last = view;
    }

    class GetList extends AsyncTask<String, Integer, JSONArray>{

        @Override
        protected JSONArray doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return MsgProcess.msgProcessArr(htp.advanceGet(Constant.mInstance.task_url, "Authorization", GlobalVariable.mInstance.token), true);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.d("hjt.get.render.list.jsonArray", "null");
            }
            else {
                for(int i = 0; i < jsonArray.length(); i++){
                    RenderItem renderItem = new RenderItem(RenderQueueActivity.this);
                    try {
                        renderItem.init(jsonArray.getJSONObject(i));
                        renderItem.refresh();
                        if(renderItem.is_finished()){
                            l_right.addView(renderItem);
                        }
                        else {
                            l_left.addView(renderItem);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}