package com.example.qydemo0.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qydemo0.QYAdapter.ImageNetAdapter;
import com.example.qydemo0.QYpack.GlobalVariable;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment implements View.OnClickListener {

    public LinearLayout scrollViewForVideos = null;
    public QYScrollView scrollView = null;
    Unbinder bind;

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
        for(int i = 0; i < GlobalVariable.mInstance.fragmentDataForMain.imgURLForHome.size(); i++){

        }
    }

    @Override
    public void onStart() {
//        String p = "no";
//        if(scrollViewForVideos != null) p = "yes";
//        Log.d("hjt.home_f", p);

        // 监听scroll的滑动
        scrollView.setScrollViewListener(new QYScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(QYScrollView scrollView, int l, int t, int oldl, int oldt) {
                if (oldt < t && ((t - oldt) > 50)) {// 向上
                    Log.e("wangly", "距离："+(oldt < t) +"---"+(t - oldt));
                    Log.e("TAG","向上滑动");
                    WorkItem w = new WorkItem(getActivity());
                    scrollViewForVideos.addView(w);
                    Log.d("hjt", "已添加");

                } else if (oldt > t && (oldt - t) > 15) {// 向下
                    Log.e("wangly", "距离："+(oldt > t) +"---"+(oldt - t));
                    Log.e("TAG"," 向下滑动");

                }
            }
        });

        Button btn = getActivity().findViewById(R.id.button_add_image);
        btn.setOnClickListener(this);
        FloatingActionButton fbtn = getActivity().findViewById(R.id.button_add_my_video);
        fbtn.setOnClickListener(this);
        LinearLayout txt = getActivity().findViewById(R.id.button_search);
        txt.setOnClickListener(this);

        super.onStart();
        Log.d("hjt.home_f", "start");

//        for(int i = 1; i <= Constant.mInstance.pre_items; i++){
//            WorkItem workItem = new WorkItem(getActivity());
//            int id = View.generateViewId();
//            workItem.setId(id);
//        }
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
            case R.id.button_add_image:
                WorkItem w = new WorkItem(getActivity());
//                w.init();
                scrollViewForVideos.addView(w);
                Log.d("hjt", "已添加");
                break;

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
        }
    }


}