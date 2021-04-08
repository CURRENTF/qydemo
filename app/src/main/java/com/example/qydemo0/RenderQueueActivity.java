package com.example.qydemo0;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.view.CustomLinearLayout;
import com.example.qydemo0.utils.DPIUtil;
import com.example.qydemo0.view.LeftSlideView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

public class RenderQueueActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView = null;

    private MyAdapter mMyAdapter;

    private int tst1 = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test123);

        // 初始化转换工具
        DPIUtil.setDensity(getResources().getDisplayMetrics().density);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        View root = findViewById(R.id.root);
        if (root instanceof CustomLinearLayout) {
            CustomLinearLayout cll = (CustomLinearLayout) root;
            cll.setOnTouchListener(new CustomLinearLayout.OnTouchListener() {
                @Override
                public void doTouch(Point point) {
                    if (mMyAdapter != null) {
                        mMyAdapter.restoreItemView(point);
                    }
                }
            });
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(RenderQueueActivity.this));
        mMyAdapter = new MyAdapter(RenderQueueActivity.this, mRecyclerView);
        mRecyclerView.setAdapter(mMyAdapter);
    }

    public static class MyAdapter extends RecyclerView.Adapter {

        private Context mContext;

        private RecyclerView mRecyclerView;

        private LeftSlideView mLeftSlideView;

        private int ss=0;

        private QYrequest cur_request = new QYrequest();

        public MyAdapter(Context context, RecyclerView recyclerView) {
            this.mContext = context;
            this.mRecyclerView = recyclerView;
        }



        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            final LeftSlideView leftSlideView = new LeftSlideView(mContext);
            QYrequest cur_request = new QYrequest();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT
                    , DPIUtil.dip2px(100.f));

            leftSlideView.setLayoutParams(params);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View contentView = inflater.inflate(R.layout.render_content_layout, null);
            View menuView = inflater.inflate(R.layout.layout_menu, null);
            menuView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "点击删除按钮", Toast.LENGTH_SHORT).show();
                }
            });

            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearProgressIndicator params = (LinearProgressIndicator) view.findViewById(R.id.progress_render);
                    params.setProgress(50);
                    //Toast.makeText(mContext, "点击内容区域", Toast.LENGTH_SHORT).show();
                }
            });

            leftSlideView.addContentView(contentView);
            leftSlideView.addMenuView(menuView);
            leftSlideView.setRecyclerView(mRecyclerView);
            leftSlideView.setStatusChangeLister(new LeftSlideView.OnDelViewStatusChangeLister() {
                @Override
                public void onStatusChange(boolean show) {
                    if (show) {
                        // 如果编辑菜单在显示
                        mLeftSlideView = leftSlideView;
                    }
                }
            });


            return new MyVH(leftSlideView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TextView head = (TextView) holder.itemView.findViewById(R.id.render_params);
            head.setText(""+ss);
            ss++;
            TextView deletee = (TextView) holder.itemView.findViewById(R.id.btn_delete);
            deletee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notifyItemRemoved(position);
                    notifyAll();
                }
            });
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        /**
         * 还原itemView
         * @param point
         */
        public void restoreItemView(Point point) {
            if (mLeftSlideView != null) {

                int[] pos = new int[2];


                mLeftSlideView.getLocationInWindow(pos);

                int width = mLeftSlideView.getWidth();
                int height = mLeftSlideView.getHeight();

                // 触摸点在view的区域内，那么直接返回
                if (point.x >= pos[0] && point.y >= pos[1]
                        && point.x <= pos[0] + width && point.y <= pos[1] + height) {

                    return;
                }

                mLeftSlideView.resetDelStatus();
            }
        }

        public class getAllTask extends AsyncTask<Void, Void, Void>{
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String res = cur_request.advanceGet("https://api.yhf2000.cn/api/qingying/v1/task/"+
                            GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("uid")+"/",
                            "Authorization",GlobalVariable.mInstance.token);
                    JSONObject res_json = new JSONObject(res);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }

    }

    public static class MyVH extends RecyclerView.ViewHolder {

        public MyVH(@NonNull View itemView) {
            super(itemView);
        }
    }

}