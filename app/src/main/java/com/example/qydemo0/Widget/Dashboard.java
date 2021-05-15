package com.example.qydemo0.Widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.qydemo0.FollowerAndFanActivity;
import com.example.qydemo0.Manager.FullyLinearLayoutManager;
import com.example.qydemo0.Manager.MyLinearLayoutManager;
import com.example.qydemo0.QYAdapter.EndlessRecyclerOnScrollListener;
import com.example.qydemo0.QYAdapter.LinearLayoutAdapter;
import com.example.qydemo0.QYAdapter.LoadMoreAndRefreshWrapper;
import com.example.qydemo0.QYAdapter.RelativeLayoutAdapter;
import com.example.qydemo0.QYpack.AdvanceHttp;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYFile;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.ShowProgressDialog;
import com.example.qydemo0.R;
import com.example.qydemo0.RenderQueueActivity;
import com.example.qydemo0.UserDetailActivity;
import com.example.qydemo0.UserSettingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Dashboard extends RelativeLayout {

    private MyAppCompatActivity context;
    public View mView;
    private Tab tab;
    QYScrollView scrollView;

    private MyAppCompatActivity getActivity(){
        return context;
    }

    public Dashboard(@NonNull Context context) {
        super(context);
        this.context = (MyAppCompatActivity) context;
        init();
    }

    public Dashboard(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = (MyAppCompatActivity) context;
        init();
    }

//    void gotoUserDetail(){
//        Intent intent = new Intent();
//        intent.setClass(getActivity(), UserDetailActivity.class);
//        try {
//            intent.putExtra("uid", GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("uid"));
//            intent.putExtra("username", GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("username"));
//            intent.putExtra("avatar", GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("img_url"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        getActivity().startActivity(intent);
//    }

    RecyclerView[] l = new RecyclerView[3];
    LoadMoreAndRefreshWrapper[] w = new LoadMoreAndRefreshWrapper[3];
    LinearLayoutAdapter work;
    LinearLayoutAdapter post;
    RelativeLayoutAdapter render;

    void init(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.fragment_dashboard, this, true);
        View t = mView.findViewById(R.id.goto_fan_follow);
        Intent intent = new Intent();
        t.setOnClickListener(view -> {
            intent.setClass(getActivity(), FollowerAndFanActivity.class);
            getActivity().startActivity(intent);
        });
        if(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson == null){
            GetUserInfo g = new GetUserInfo(getActivity());
            g.execute();
        }
        else reWriteInfo(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson);
        ImageView img = mView.findViewById(R.id.button_user_setting);
        img.setOnClickListener(new ModifyUserInfo());

        l[0] = mView.findViewById(R.id.l0);
        l[1] = mView.findViewById(R.id.l1);
        l[2] = mView.findViewById(R.id.l2);
        tab = mView.findViewById(R.id.tab);
        String[] s = {"作品", "动态", "渲染"};
        tab.init(s, l);
        tab.setTextSize(15);
        work = new LinearLayoutAdapter(new ArrayList<>(), Constant.mInstance.WORK, getActivity());
        post = new LinearLayoutAdapter(new ArrayList<>(), Constant.mInstance.POST, getActivity());
        post.setHasStableIds(true);
        render = new RelativeLayoutAdapter(new ArrayList<>(), Constant.mInstance.RENDER, getActivity());
        w[0] = new LoadMoreAndRefreshWrapper(work);
        w[1] = new LoadMoreAndRefreshWrapper(post);
        w[2] = new LoadMoreAndRefreshWrapper(render);
        int[] startPos = {0, 0, 0};
        int len = Constant.mInstance.MAX_UPDATE_LEN;
        for(int i = 0; i < 3; i++){
            l[i].setAdapter(w[i]);
            l[i].setHasFixedSize(false);
            l[i].setLayoutManager(new LinearLayoutManager(getActivity()));
            int finalI = i;
            EndlessRecyclerOnScrollListener TT = new EndlessRecyclerOnScrollListener() {
                @Override
                public void onLoadMore() {
                    w[finalI].setLoadState(w[finalI].LOADING);
                    Log.d("hjt.kk", "donw");
                    Handler handler = new Handler(Looper.getMainLooper()){
                        @SuppressLint("HandlerLeak")
                        @Override
                        public void handleMessage(@NonNull Message msg){
                            super.handleMessage(msg);
                            JSONArray arr = (JSONArray) msg.obj;
                            if(arr == null) return;
                            if(arr.length() == 0 || startPos[finalI] > 10)
                                w[finalI].setLoadState(w[finalI].LOADING_END);
                            else
                                w[finalI].setLoadState(w[finalI].LOADING_COMPLETE);
                            for(int i = 0; i < arr.length(); i++){
                                try {
                                    JSONObject jsonObject = arr.getJSONObject(i);
                                    if(finalI == 0)
                                        work.addData(jsonObject);
                                    else if(finalI == 1)
                                        post.addData(jsonObject);
                                    else{
                                        jsonObject.put("dashboard", true);
                                        render.addData(jsonObject);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    if(finalI == 0){
                        AdvanceHttp.getMyWorks(handler, startPos[finalI], len);
                        startPos[finalI] += len;
                    } else if(finalI == 1){
                        AdvanceHttp.getMyPosts(handler, startPos[finalI], len);
                        startPos[finalI] += len;
                    } else {
                        if(startPos[finalI] > 0){
                            w[finalI].setLoadState(w[finalI].LOADING_END);
                            return;
                        }
                        AdvanceHttp.getMyRenders(handler, startPos[finalI], len);
                        startPos[finalI] += len;
                    }
                }
            };
            l[i].addOnScrollListener(TT);
            TT.onLoadMore();
        }
    }

    void reWriteInfo(JSONObject json){
//        if(!getActivity().hasWindowFocus()) return;
//        Log.d("hjt.no_focus", "true");
        try {
            GlobalVariable.mInstance.uid = json.getString("uid");
        } catch (JSONException e) {
            Log.e("hjt.UID", "null");
            e.printStackTrace();
        }
        // 设置头像

        // 防止由于父亲销毁 RE
        if(getActivity() == null) return;

        ImageView userAvatar = mView.findViewById(R.id.user_avatar);
        TextView txt = mView.findViewById(R.id.text_username);

        if(getActivity() == null) return;
        String avatar_url, sign;
        Boolean a = false, b = false;
        try {
            avatar_url = json.getString("img_url");
            txt.setText(json.getString("username"));
            a = true;
        } catch (JSONException e) {
            avatar_url = Constant.mInstance.default_avatar;
        }

        if(avatar_url.equals("null")){
            avatar_url = Constant.mInstance.default_avatar;
            a = false;
        }


        if(getActivity() == null) return;

        try {
            sign = json.getString("sign");
            txt = mView.findViewById(R.id.text_user_sign);
            txt.setText(sign);
            b = true;
        } catch (JSONException e) {
            sign = "Born to Dance";
        }

        if(getActivity() == null) return;

        Img.roundImgUrl(getActivity(), userAvatar, avatar_url);

        try {
            if(!a) json.put("img_url", Constant.mInstance.default_avatar);
            if(!b) json.put("sign", "Born to Dance");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            TextView txt2 = mView.findViewById(R.id.text_fans);
            txt2.setText(String.valueOf(json.getInt("subscribe_num")));
            txt2 = mView.findViewById(R.id.text_followers);
            txt2.setText(String.valueOf(json.getInt("followers")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    class GetUserInfo extends MyAsyncTask<String, Integer, String> {

        protected GetUserInfo(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return htp.advanceGet(Constant.mInstance.userInfo_url, "Authorization", GlobalVariable.mInstance.token);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("hjtGetUserInfo", s);
            JSONObject json = MsgProcess.msgProcess(s, false, null);
            if(json != null){
                try {
                    if(json.getString("img_url").equals("null"))
                        json.put("img_url", Constant.mInstance.default_avatar);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                GlobalVariable.mInstance.fragmentDataForMain.userInfoJson = json;
//                Log.d("hjt.user_json", json.toString());
                reWriteInfo(json);
            }
        }
    }


    class ModifyUserInfo implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), UserSettingActivity.class);
            getActivity().startActivity(intent);
        }
    }


}
