package com.example.qydemo0.ui.posts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PostsFragment extends Fragment {

    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    ViewPager viewPager = null;
    PostAdapter postAdapter = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    String[] tabTxt = new String[]{"推荐", "关注"};


    // TODO finish this
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        postAdapter = new PostAdapter(this, )
        viewPager = view.findViewById(R.id.posts_pager);
        super.onViewCreated(view, savedInstanceState);
    }

    public class PostAdapter extends PagerAdapter {
        private List<Integer> list;
        private Context context;

        public PostAdapter(Context context, List<Integer> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            //支持的view总数
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv = new ImageView(context);
            //设置图片适应xy轴
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageResource(list.get(position));
            //增加子view到viewpager里
            container.addView(iv);
            return iv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //删除子view到viewpager里
            container.removeView((View) object);
        }
    }

}