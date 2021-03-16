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

import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.R;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;
    public LinearLayout scrollViewForVideos = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }

    @Override
    public void onStart() {
        Button btn = getActivity().findViewById(R.id.button_add_image);
        btn.setOnClickListener(this);
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        scrollViewForVideos = getActivity().findViewById(R.id.home_scroll_for_video_cover);
    }

//    public int id = 1;

    @Override
    public void onClick(View v) {
//        AddImage addImage = new AddImage();
//        addImage.execute();
        ImageView img = new ImageView(getActivity());

        img.setBackgroundColor(getResources().getColor(R.color.black));

        Ion.with(img)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .load("https://file.yhf2000.cn/img/defult1.jpeg");

        scrollViewForVideos.addView(img);
        Log.d("hjt", "已添加图片");
    }

}