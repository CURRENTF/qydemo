package com.example.qydemo0.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.R;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.util.Vector;

public class HomeFragment extends Fragment implements View.OnClickListener {

    public LinearLayout scrollViewForVideos = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        scrollViewForVideos = getActivity().findViewById(R.id.home_scroll_for_video_cover);
        for(int i = 0; i < GlobalVariable.mInstance.fragmentDataForMain.imgURLForHome.size(); i++){
            ImageView img = new ImageView(getActivity());
            Ion.with(img)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .load(GlobalVariable.mInstance.fragmentDataForMain.imgURLForHome.get(i));
            scrollViewForVideos.addView(img);
        }
    }

    @Override
    public void onStart() {
        Button btn = getActivity().findViewById(R.id.button_add_image);
        btn.setOnClickListener(this);
        super.onStart();
        scrollViewForVideos = getActivity().findViewById(R.id.home_scroll_for_video_cover);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        ImageView img = new ImageView(getActivity());
        Ion.with(img)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .load("https://file.yhf2000.cn/img/defult2.jpeg");
        scrollViewForVideos.addView(img);
        GlobalVariable.mInstance.fragmentDataForMain.imgURLForHome.add("https://file.yhf2000.cn/img/defult1.jpeg");
        Log.d("hjt", "已添加图片");
    }


}