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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qydemo0.FreeDanceActivity;
import com.example.qydemo0.LearningListActivity;
import com.example.qydemo0.Manager.MyLinearLayoutManager;
import com.example.qydemo0.PlayerActivity;
import com.example.qydemo0.QYAdapter.EndlessRecyclerOnScrollListener;
import com.example.qydemo0.QYAdapter.ImageNetAdapter;
import com.example.qydemo0.QYAdapter.LinearLayoutAdapter;
import com.example.qydemo0.QYAdapter.LoadMoreAndRefreshWrapper;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.HttpCounter;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYFile;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.TimeTool;
import com.example.qydemo0.QYpack.Uri2RealPath;
import com.example.qydemo0.QYpack.UriUtil;
import com.example.qydemo0.R;
import com.example.qydemo0.SearchActivity;
import com.example.qydemo0.UploadActivity;
import com.example.qydemo0.Widget.ListItem.LinearLayoutItem;
import com.example.qydemo0.Widget.ListItem.WorkItem;
import com.example.qydemo0.bean.DataBean;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class Home extends RelativeLayout implements View.OnClickListener {

    private Activity context;
    public View mView;
    LinearLayoutAdapter itemAdapter;
    LoadMoreAndRefreshWrapper wrapper;

    Unbinder bind;
    public int startPos = 0, len = Constant.mInstance.MAX_UPDATE_LEN;
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
        //??????????????????????????????????????????????????????BannerImageAdapter
        ImageNetAdapter adapter = new ImageNetAdapter(DataBean.getTestData3());

        banner_ad.setAdapter(adapter)
                .addBannerLifecycleObserver((AppCompatActivity)getActivity())//???????????????????????????
                .setIndicator(new CircleIndicator(getActivity()))//???????????????
                .setOnBannerListener((data, position) -> {
                    Snackbar.make(banner_ad, ((DataBean) data).title, Snackbar.LENGTH_SHORT).show();
                });

        // ??????????????????

        itemAdapter = new LinearLayoutAdapter(new ArrayList<JSONObject>(), Constant.mInstance.WORK, getActivity());
        itemAdapter.setHasStableIds(true);
        wrapper = new LoadMoreAndRefreshWrapper(itemAdapter);
        RecyclerView recyclerView = mView.findViewById(R.id.works);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(wrapper);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                Log.d("hjt.o", "bottom");
                wrapper.setLoadState(wrapper.LOADING);
                GetUserRecommendation getUserRecommendation = new GetUserRecommendation((MyAppCompatActivity) getActivity());
                getUserRecommendation.execute();
            }
        });
        wrapper.setLoadState(wrapper.LOADING);
        GetUserRecommendation getUserRecommendation = new GetUserRecommendation((MyAppCompatActivity) getActivity());
        getUserRecommendation.execute();

        // ??????????????????

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
                Log.d("hjt.uri", result.toString());
                ArrayList<String> list = new ArrayList<>();
//                String path1 = Uri2RealPath.getRealPathFromUri_AboveApi19(getActivity(), result);
                String path2 = UriUtil.getPath(getActivity(), result);
                list.add("1"); list.add(path2);
                Log.i("??????", list.get(1));
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
                    Toast.makeText(getActivity(), "???????????????????????????BGM???~", Toast.LENGTH_LONG).show();
                    launcher.launch(true);
                }
        }
    }


    HttpCounter counter = new HttpCounter();
    class GetUserRecommendation extends MyAsyncTask<String, Integer, JSONArray> {

        protected GetUserRecommendation(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            Log.d("hjt.recommendation.info", counter.start + "," + counter.len);
            return MsgProcess.msgProcessArr(htp.advanceGet(Constant.mInstance.user_recommendation_url +
                            Json2X.Json2StringGet("start", String.valueOf(counter.start), "lens", String.valueOf(counter.len)),
                    "Authorization", GlobalVariable.mInstance.token), true, "recommendation");
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            if(jsonArray == null){
                Log.d("hjt.get.user.recommendation.fail", "null");
            }
            else {
                counter.inc(jsonArray.length());
                if(jsonArray.length() == 0) wrapper.setLoadState(wrapper.LOADING_END);
                else wrapper.setLoadState(wrapper.LOADING_COMPLETE);
                for(int i = 0; i < jsonArray.length(); i++){
                    try {
                        JSONObject json = (JSONObject) jsonArray.get(i);
                        itemAdapter.addData(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
