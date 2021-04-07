package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.Widget.RenderQueueItem;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class RenderQueueActivity extends AppCompatActivity {

    private LinearLayout render_content;
    private List<RenderQueueItem> redner_items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_render_queue);
        render_content = (LinearLayout) findViewById(R.id.Render_content);
        for(int i=0;i<20;i++){
            RenderQueueItem render_item = new RenderQueueItem(this);
            try {
                render_item.init(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("img_url"),"2021.4.7","背景替换 赛博朋克滤镜",(float) i/20.0f);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            redner_items.add(render_item);
            render_content.addView(render_item);
        }
    }
}