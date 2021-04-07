package com.example.qydemo0.Widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.qydemo0.FollowerAndFanActivity;
import com.example.qydemo0.LearningListActivity;
import com.example.qydemo0.PlayerActivity;
import com.example.qydemo0.QYAdapter.ImageNetAdapter;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.Json2X;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.ShowProgressDialog;
import com.example.qydemo0.QYpack.TimeTool;
import com.example.qydemo0.R;
import com.example.qydemo0.SearchActivity;
import com.example.qydemo0.UploadActivity;
import com.example.qydemo0.UploadPostActivity;
import com.example.qydemo0.UserSettingActivity;
import com.example.qydemo0.bean.DataBean;
import com.example.qydemo0.ui.dashboard.DashboardFragment;
import com.example.qydemo0.ui.home.HomeFragment;
import com.example.qydemo0.ui.posts.PostsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class QYNavigation extends RelativeLayout implements View.OnClickListener {

    private Activity context;
    private View mView;
    LinearLayout posts, home, user;
    View a, b, c;
    JSONObject json = new JSONObject();


    private Activity getActivity(){
        return context;
    }

    public QYNavigation(@NonNull Context context) {
        super(context);
        this.context = (Activity) context;
        init();
    }

    public QYNavigation(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = (Activity) context;
        init();
    }

    void init() {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.navigation, this, true);

        posts = mView.findViewById(R.id.posts);
        home = mView.findViewById(R.id.home);
        user = mView.findViewById(R.id.user);
        posts.setId(View.generateViewId());
        home.setId(View.generateViewId());
        user.setId(View.generateViewId());
        try {
            json.put(String.valueOf(posts.getId()), 0);
            json.put(String.valueOf(home.getId()), 1);
            json.put(String.valueOf(user.getId()), 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        posts.setOnClickListener(this);
        home.setOnClickListener(this);
        user.setOnClickListener(this);
    }

    View[] views;

    public void initView(View[] views){
        this.views = views;
        last = home;
//        a = A; b = B; c = C; last = home;
        initAnim();
        TextView txt = home.findViewWithTag("txt");
        txt.setTextColor(getActivity().getColor(R.color.qy_purple));
        txt.setTextSize(14);
    }

    Animation l_out, l_in, r_out, r_in;

    void initAnim(){
        r_out = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_right_translate_alpha_500ms);
        r_in = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_right_translate_in_alpha_500ms);
        l_out = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_left_translate_alpha_500ms);
        l_in = AnimationUtils.loadAnimation(getActivity(), R.anim.ani_left_translate_in_alpha_500ms);
    }

    View last = null;



    @Override
    public void onClick(View v) {
        if(last == v) return;
        int id = v.getId();
        int last_id = last.getId();
        try {
            id = json.getInt(String.valueOf(id));
            last_id = json.getInt(String.valueOf(last_id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(id < last_id){
            views[id].startAnimation(r_in);
            views[last_id].startAnimation(r_out);
        }
        else {
            views[id].startAnimation(l_in);
            views[last_id].startAnimation(l_out);
        }
        TextView txt = v.findViewWithTag("txt");
        txt.setTextColor(getActivity().getColor(R.color.qy_purple));
        txt.setTextSize(14);
        txt = last.findViewWithTag("txt");
        txt.setTextColor(getActivity().getColor(R.color.gray));
        txt.setTextSize(12);
        views[id].setVisibility(VISIBLE);
        views[last_id].setVisibility(GONE);
        last = v;
    }


}
