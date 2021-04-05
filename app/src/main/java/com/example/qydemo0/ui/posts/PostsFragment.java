package com.example.qydemo0.ui.posts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.ui.node.ViewAdapter;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.qydemo0.R;
import com.example.qydemo0.UploadPostActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PostsFragment extends Fragment implements View.OnClickListener {

    private View root = null;
    int[] buttons = {R.id.add_post, R.id.button_post_recommendation, R.id.button_post_follow};


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_posts, container, false);
        return root;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        for(int i = 0; i < buttons.length; i++){
            View btn = root.findViewById(buttons[i]);
            btn.setOnClickListener(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        TextView t;
        switch (v.getId()){
            case R.id.add_post:
                Intent intent = new Intent();
                intent.setClass(getActivity(), UploadPostActivity.class);
                startActivity(intent);
                break;
            case R.id.button_post_recommendation:
                ((TextView)v).setTextColor(getResources().getColor(R.color.red));
                t = getActivity().findViewById(R.id.button_post_follow);
                t.setTextColor(getResources().getColor(R.color.black));

                break;
            case R.id.button_post_follow:
                ((TextView)v).setTextColor(getResources().getColor(R.color.red));
                t = getActivity().findViewById(R.id.button_post_recommendation);
                t.setTextColor(getResources().getColor(R.color.black));
                break;
        }
    }
}