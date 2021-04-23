package com.example.qydemo0.Widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.example.qydemo0.FollowerAndFanActivity;
import com.example.qydemo0.FreeDanceActivity;
import com.example.qydemo0.LearningListActivity;
import com.example.qydemo0.PlayerActivity;
import com.example.qydemo0.QYAdapter.ImageNetAdapter;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYFile;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.ShowProgressDialog;
import com.example.qydemo0.QYpack.TimeTool;
import com.example.qydemo0.QYpack.Uri2RealPath;
import com.example.qydemo0.R;
import com.example.qydemo0.SearchActivity;
import com.example.qydemo0.UploadActivity;
import com.example.qydemo0.UserSettingActivity;
import com.example.qydemo0.bean.DataBean;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class Home extends RelativeLayout implements View.OnClickListener {

    private Activity context;
    private View mView;

    public LinearLayout scrollViewForVideos = null;
    public QYScrollView scrollView = null;
    Unbinder bind;
    public int startPos = 0, len = 20;
    TimeTool timeTool = new TimeTool();
    QYFile.ResultContract qyr = new QYFile.ResultContract();
    ActivityResultLauncher launcher;

    @BindView(R.id.banner_ad)
    Banner banner_ad;

    Button ezDance;

    private Activity getActivity(){
        return context;
    }

    public Home(@NonNull Context context) {
        super(context);
        this.context = (Activity) context;
        init();
    }

    public Home(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = (Activity) context;
        init();
    }

    void init(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.fragment_home, this, true);

        bind = ButterKnife.bind(this, mView);
        //自定义的图片适配器，也可以使用默认的BannerImageAdapter
        ImageNetAdapter adapter = new ImageNetAdapter(DataBean.getTestData3());

        banner_ad.setAdapter(adapter)
                .addBannerLifecycleObserver((AppCompatActivity)getActivity())//添加生命周期观察者
                .setIndicator(new CircleIndicator(getActivity()))//设置指示器
                .setOnBannerListener((data, position) -> {
                    Snackbar.make(banner_ad, ((DataBean) data).title, Snackbar.LENGTH_SHORT).show();
                });
        scrollViewForVideos = mView.findViewById(R.id.home_scroll_for_video_cover);
        scrollView = mView.findViewById(R.id.scroll_home);
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

        FloatingActionButton fbtn = mView.findViewById(R.id.button_add_my_video);
        fbtn.setOnClickListener(this);
        LinearLayout txt = mView.findViewById(R.id.button_search);
        txt.setOnClickListener(this);
        Button btn = mView.findViewById(R.id.button_image_learn_list);
        btn.setOnClickListener(this);

        launcher = ((AppCompatActivity)context).registerForActivityResult(qyr, new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if(result == null) return;
                Intent intent1 = new Intent();
                intent1.setClass(getActivity(), FreeDanceActivity.class);
                ArrayList<String> list = new ArrayList<>();
                list.add("1"); list.add(Uri2RealPath.getRealPathFromUri_AboveApi19(getActivity(), result));
                Log.i("音频", list.get(1));
                intent1.putExtra("params", list);
                getActivity().startActivity(intent1);
            }
        });
        ezDance = mView.findViewById(R.id.button_image_free_dance);
        ezDance.setOnClickListener(this);
    }

    void unbind(){
        bind.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_add_my_video:
                Intent intent = new Intent();
                intent.setClass(getActivity(), UploadActivity.class);
                getActivity().startActivity(intent);
                break;

            case R.id.button_search:
                Intent intent2 = new Intent();
                intent2.setClass(getActivity(), SearchActivity.class);
                getActivity().startActivity(intent2);
                break;
            case R.id.button_image_learn_list:
                Intent intent3 = new Intent();
                intent3.setClass(getActivity(), LearningListActivity.class);
                getActivity().startActivity(intent3);
                break;
            default:
                if(v == ezDance){
                    qyr.params = "audio";
                    launcher.launch(true);
                }
        }
    }

    class GetUserRecommendation extends AsyncTask<String, Integer, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            Log.d("hjt.recommendation.info", String.valueOf(startPos) + "," + String.valueOf(len));
            return MsgProcess.msgProcessArr(htp.advanceGet(Constant.mInstance.user_recommendation_url +
                            Json2X.Json2StringGet("start", String.valueOf(startPos), "lens", String.valueOf(len)),
                    "Authorization", GlobalVariable.mInstance.token), false, null);
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
                                j.getInt("play_num"), j.getString("introduction"), j.getJSONObject("belong").getString("username"), j.getInt("id"));
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
            getActivity().startActivity(intent);
        }
    }

}
