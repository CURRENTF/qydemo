package com.example.qydemo0.ui.home;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;

import com.example.qydemo0.LearningListActivity;
import com.example.qydemo0.PlayerActivity;
import com.example.qydemo0.QYAdapter.ImageNetAdapter;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.TimeTool;
import com.example.qydemo0.R;
import com.example.qydemo0.SearchActivity;
import com.example.qydemo0.UploadActivity;
import com.example.qydemo0.Widget.QYScrollView;
import com.example.qydemo0.Widget.WorkItem;
import com.example.qydemo0.bean.DataBean;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment implements View.OnClickListener {

    public LinearLayout scrollViewForVideos = null;
    public QYScrollView scrollView = null;
    Unbinder bind;
    public int startPos = 0, len = 20;
    TimeTool timeTool = new TimeTool();

    @BindView(R.id.banner_ad)
    Banner banner_ad;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        bind = ButterKnife.bind(this, root);
        //自定义的图片适配器，也可以使用默认的BannerImageAdapter
        ImageNetAdapter adapter = new ImageNetAdapter(DataBean.getTestData3());

        banner_ad.setAdapter(adapter)
                .addBannerLifecycleObserver(this)//添加生命周期观察者
                .setIndicator(new CircleIndicator(getActivity()))//设置指示器
                .setOnBannerListener((data, position) -> {
                    Snackbar.make(banner_ad, ((DataBean) data).title, Snackbar.LENGTH_SHORT).show();
                });
        Log.d("hjt.home_f", "create");

        return root;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d("hjt.home_f", "restore");
        scrollViewForVideos = getActivity().findViewById(R.id.home_scroll_for_video_cover);
        scrollView = getActivity().findViewById(R.id.scroll_home);
    }

    @Override
    public void onStart() {
        GetUserRecommendation getUserRecommendation = new GetUserRecommendation();
        getUserRecommendation.execute();

        scrollView.setScanScrollChangedListener(new QYScrollView.ISmartScrollChangedListener() {
            @Override
            public void onScrolledToBottom() {
                if(!timeTool.checkFreq()) return;
                GetUserRecommendation getUserRecommendation = new GetUserRecommendation();
                getUserRecommendation.execute();
                Log.d("hjt.scroll.bottom", "true");
                Log.d("hjt", "已添加");
            }

            @Override
            public void onScrolledToTop() {
                Log.d("hjt.scroll.top", "true");
            }
        });

        FloatingActionButton fbtn = getActivity().findViewById(R.id.button_add_my_video);
        fbtn.setOnClickListener(this);
        LinearLayout txt = getActivity().findViewById(R.id.button_search);
        txt.setOnClickListener(this);
        Button btn = getActivity().findViewById(R.id.button_image_learn_list);
        btn.setOnClickListener(this);

        super.onStart();
        Log.d("hjt.home_f", "start");

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_add_my_video:
                Intent intent = new Intent();
                intent.setClass(getActivity(), UploadActivity.class);
                startActivity(intent);
                break;

            case R.id.button_search:
                Intent intent2 = new Intent();
                intent2.setClass(getActivity(), SearchActivity.class);
                startActivity(intent2);
                break;
            case R.id.button_image_learn_list:
                Intent intent3 = new Intent();
                intent3.setClass(getActivity(), LearningListActivity.class);
                startActivity(intent3);
                break;
        }
    }

    class GetUserRecommendation extends AsyncTask<String, Integer, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            Log.d("hjt.recommendation.info", String.valueOf(startPos) + "," + String.valueOf(len));
            return MsgProcess.msgProcessArr(htp.advanceGet(Constant.mInstance.user_recommendation_url +
                            Json2X.Json2StringGet("start", String.valueOf(startPos), "lens", String.valueOf(len)),
                    "Authorization", GlobalVariable.mInstance.token), false);
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            startPos += len;
            if(jsonArray == null){
                Log.d("hjt.get.user.recommendation.fail", "null");
            }
            else {
                for(int i = 0; i < jsonArray.length(); i++){
                    WorkItem w = new WorkItem(getActivity());
                    try {
                        JSONObject j = (JSONObject) jsonArray.get(i);
                        JSONObject coverInfo = j.getJSONObject("cover");
                        w.init(coverInfo.getString("url"), j.getString("name"), j.getInt("like_num"),
                                j.getInt("play_num"), j.getString("introduction"));
                        w.id = j.getInt("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                    scrollViewForVideos.addView(w);
                    w.setOnClickListener(new SendWorkId());
                    scrollViewForVideos.addView(Img.linearLayoutDivideLine(getActivity()));
                }
            }
        }
    }

    class SendWorkId implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), PlayerActivity.class);
            intent.putExtra("id", ((WorkItem)v).id);
            startActivity(intent);
        }
    }
}