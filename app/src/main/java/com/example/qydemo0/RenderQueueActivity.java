package com.example.qydemo0;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.view.CustomLinearLayout;
import com.example.qydemo0.utils.DPIUtil;
import com.example.qydemo0.view.CustomTextView;
import com.example.qydemo0.view.LeftSlideView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class RenderQueueActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView = null;

    private MyAdapter mMyAdapter;

    private JSONArray res_json_array1;

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

        new getAllTask().execute();

    }

    public static class MyAdapter extends RecyclerView.Adapter {

        private Context mContext;

        private RecyclerView mRecyclerView;

        private LeftSlideView mLeftSlideView;

        private JSONArray res_json_array;

        private int ss=0;

        private QYrequest cur_request = new QYrequest();

        String[] lj = {"锐化滤镜","边缘滤镜","高斯模糊滤镜","轮廓滤镜","浮雕滤镜"
                ,"复古滤镜","铅笔彩","卡通滤镜","赛博朋克滤镜","美白磨皮滤镜"};

        TextView head;
        TextView params;

        public MyAdapter(Context context, RecyclerView recyclerView, JSONArray in_res_json_array) {
            this.mContext = context;
            this.mRecyclerView = recyclerView;
            this.res_json_array = in_res_json_array;
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
                    Log.e("whc_view", ""+view.getId());
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
            try {
                JSONObject cur_json = res_json_array.getJSONObject(position);
                //holder. head.setText(cur_json.getString("created_time"));
                TextView params = (TextView) holder.itemView.findViewById(R.id.render_params);
                String txt_params = "";
                if(cur_json.getJSONObject("args").getBoolean("is_background")){
                    txt_params += "背景替换 ";
                }
                if(cur_json.getJSONObject("args").getBoolean("is_filter")){
                    txt_params += lj[cur_json.getJSONObject("args").getInt("filter_id")];
                }
                params.setText(txt_params);
                ImageView render_cover = (ImageView) holder.itemView.findViewById(R.id.cover);
                Log.i("url",cur_json.getJSONObject("cover").getString("url"));
                Glide.with(mContext)
                        .load(cur_json.getJSONObject("cover").getString("url"))
                        .transform(/*new CenterInside(), */new RoundedCorners(50)).into(render_cover);
            TextView deletee = (TextView) holder.itemView.findViewById(R.id.btn_delete);

            } catch (JSONException e) {
            e.printStackTrace();
        }
        }

        @Override
        public int getItemCount() {
            return res_json_array.length();
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

        public class updateItems extends AsyncTask<Void, Void, Void>{
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }

            @Override
            protected Void doInBackground(Void... voids) {

                return null;
            }
        }

    }

    public static class MyVH extends RecyclerView.ViewHolder {

        CustomTextView head_title;
        TextView timee;
        ImageView img_cover;
        LinearProgressIndicator progress_here;
        public MyVH(@NonNull View itemView) {
            super(itemView);
            head_title = (CustomTextView) itemView.findViewById(R.id.content);
            timee = (TextView) itemView.findViewById(R.id.render_params);
            img_cover = (ImageView) itemView.findViewById(R.id.cover);
            progress_here = (LinearProgressIndicator) itemView.findViewById(R.id.process_render);
        }
    }

    public class getAllTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(RenderQueueActivity.this));
            mMyAdapter = new MyAdapter(RenderQueueActivity.this, mRecyclerView, res_json_array1);
            mRecyclerView.setAdapter(mMyAdapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String res = "{\"status\":200,\"msg\":\"Success\",\"data\":[{\"tid\":\"0b4dba4e-98a5-11eb-bb07-a11125b44791\",\"schedule\":\"25%\",\"args\":\"{\\\"video\\\": \\\"ffe96d866f3ba915af608b46a0caf9c035423505004bcbcd6b08a3c6feeaa0b3\\\", \\\"cover\\\": \\\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": false}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/a4/49/a4494be963f2190657aafdf968d99f8c886d1520dcd968fc973d1345f412409d-xInQuq.use\",\"id\":\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\"},\"created_time\":\"2021-04-09T04:00:19.162825\"},{\"tid\":\"0c1537d8-988a-11eb-bb07-a11125b44791\",\"schedule\":\"100%\",\"args\":\"{\\\"video\\\": \\\"9309ca86a5b4f7770a8c111056d98683d93cab6d485e07f36bf3e601180b54f1\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"1\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-09T00:47:04.061985\"},{\"tid\":\"1237a55c-98ac-11eb-bb07-a11125b44791\",\"schedule\":\"25%\",\"args\":\"{\\\"video\\\": \\\"96347dc50e7c7dce6eb21e7be066fc98951438e5861fedbf977309525bbd5c38\\\", \\\"cover\\\": \\\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"8\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/a4/49/a4494be963f2190657aafdf968d99f8c886d1520dcd968fc973d1345f412409d-xInQuq.use\",\"id\":\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\"},\"created_time\":\"2021-04-09T04:50:37.239693\"},{\"tid\":\"18aaa310-98b3-11eb-bb07-a11125b44791\",\"schedule\":\"100%\",\"args\":\"{\\\"video\\\": \\\"5b2e17f228081f2c87dda6596d7c28f29a573e5585729e6c7488c3a1d4d0c8d1\\\", \\\"cover\\\": \\\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": false}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/a4/49/a4494be963f2190657aafdf968d99f8c886d1520dcd968fc973d1345f412409d-xInQuq.use\",\"id\":\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\"},\"created_time\":\"2021-04-09T05:40:54.538317\"},{\"tid\":\"1bf692ec-986b-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"1fbdd40765a5bfec0671e75bf6333eb89df077cb33ad6045e47eee014c8316a3\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"6\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T21:05:36.302951\"},{\"tid\":\"1fd0a2da-9853-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"44a893d829c54df5877bd5a9810c4f68013867b8917b9786091f274cd8589f03\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"a54bc1a9268acbb19e789681e0bda24dd424d67932441480e22f9edcafb502e2\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T18:13:54.843476\"},{\"tid\":\"2b1078a0-98b2-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"daac16a81a4d96a18c5d9b308424acbbd842e058b87aea4669437ab405c47c7e\\\", \\\"cover\\\": \\\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/a4/49/a4494be963f2190657aafdf968d99f8c886d1520dcd968fc973d1345f412409d-xInQuq.use\",\"id\":\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\"},\"created_time\":\"2021-04-09T05:34:15.906062\"},{\"tid\":\"2c3beace-9872-11eb-bb07-a11125b44791\",\"schedule\":\"100%\",\"args\":\"{\\\"video\\\": \\\"cb7311366d6ababbe24fe76740d6807df789d2e9041fc0ea1050cc80de0eb86b\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T21:56:10.077962\"},{\"tid\":\"37909ce4-9863-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"af6b7a45b05978b80719d940274adffd6c95a7dc0654a577f47e59af4dc8e507\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"a54bc1a9268acbb19e789681e0bda24dd424d67932441480e22f9edcafb502e2\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T20:09:06.637162\"},{\"tid\":\"3af43b6a-98a0-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"e96e8ce3124e119e266ca2269479165369d13257896ae4f6d4db44076bc2898d\\\", \\\"cover\\\": \\\"f5f4c70b21419313a0da2d0950fec69e1c6f5f68e387bbe0ce414f92b1c8256e\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/3f/8f/3f8f096a4294d486025bd79ce1a7b7c55ea1b516f890d9b2535fa9bce7aa7fc2-ycDCiL.use\",\"id\":\"f5f4c70b21419313a0da2d0950fec69e1c6f5f68e387bbe0ce414f92b1c8256e\"},\"created_time\":\"2021-04-09T03:25:51.626391\"},{\"tid\":\"489c7aee-989f-11eb-bb07-a11125b44791\",\"schedule\":\"100%\",\"args\":\"{\\\"video\\\": \\\"e5c492d198d1ac53fde2beeb618a20af179f8af430de5cd52ef76b5588a1776b\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-09T03:19:05.039904\"},{\"tid\":\"4ef748b4-9864-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"a223e8da848c955352240d493588ac5530fc468e0c7bce68235e4d22e3d195e0\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"a54bc1a9268acbb19e789681e0bda24dd424d67932441480e22f9edcafb502e2\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"6\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T20:16:55.395047\"},{\"tid\":\"51ffa09c-98af-11eb-bb07-a11125b44791\",\"schedule\":\"25%\",\"args\":\"{\\\"video\\\": \\\"14eb5f3382dca0578320c8d911db8502d3311afca31eafb1ed881d777b5ba0f9\\\", \\\"cover\\\": \\\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/a4/49/a4494be963f2190657aafdf968d99f8c886d1520dcd968fc973d1345f412409d-xInQuq.use\",\"id\":\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\"},\"created_time\":\"2021-04-09T05:13:52.737429\"},{\"tid\":\"54848ece-989e-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"7c764fc1a632ed8ad2157b15f7569dccb2fcc3369c6ffcf5e79b0f06c95ab28b\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-09T03:12:15.519213\"},{\"tid\":\"569684d2-98b3-11eb-bb07-a11125b44791\",\"schedule\":\"100%\",\"args\":\"{\\\"video\\\": \\\"f7a3124e7d8f8245c56ee7e72571a486600d03f77466642cfd1ea74f21c7c40c\\\", \\\"cover\\\": \\\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": false}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/a4/49/a4494be963f2190657aafdf968d99f8c886d1520dcd968fc973d1345f412409d-xInQuq.use\",\"id\":\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\"},\"created_time\":\"2021-04-09T05:42:38.426185\"},{\"tid\":\"5a68db0a-9863-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"5f9b365e7fb719fbf94f903362cfab640acb0aac82e9364fbc1502cc28d7271e\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"a54bc1a9268acbb19e789681e0bda24dd424d67932441480e22f9edcafb502e2\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T20:10:05.100542\"},{\"tid\":\"6164640a-9855-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"cebe516927b41317f3d135cf281e43dfe1f8503942c7d1ecff0ec004b6525e6f\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"76d68331eca1259dc1b9eeadc10acd325e437f9916a0d7bead48d7c7d9720458\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T18:30:03.857554\"},{\"tid\":\"68c71340-9874-11eb-bb07-a11125b44791\",\"schedule\":\"100%\",\"args\":\"{\\\"video\\\": \\\"01176c536a25f3f0ddf917a45bd7fff5acbdc61af4ad1663e827dfbbf3a0d806\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"1\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T22:12:10.649311\"},{\"tid\":\"6eba1b14-9863-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"5ff67e1d9acccb4bb6b5fc9c1296550beab75cf23563b8c607f00b5530530447\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"a54bc1a9268acbb19e789681e0bda24dd424d67932441480e22f9edcafb502e2\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T20:10:39.183759\"},{\"tid\":\"6f7b550e-987c-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"3daf35ab2c21b75f5a8367f7895ddf36f1bd8f2e238f893678715c6f78be500a\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": false}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T23:09:37.869100\"},{\"tid\":\"73319066-98ac-11eb-bb07-a11125b44791\",\"schedule\":\"125%\",\"args\":\"{\\\"video\\\": \\\"08cf694728102bb000770805504478ed08c92dd4ba162f2d6fa376602f6cdc5f\\\", \\\"cover\\\": \\\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"2\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/a4/49/a4494be963f2190657aafdf968d99f8c886d1520dcd968fc973d1345f412409d-xInQuq.use\",\"id\":\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\"},\"created_time\":\"2021-04-09T04:53:19.939157\"},{\"tid\":\"73747d38-98a0-11eb-bb07-a11125b44791\",\"schedule\":\"100%\",\"args\":\"{\\\"video\\\": \\\"e96e8ce3124e119e266ca2269479165369d13257896ae4f6d4db44076bc2898d\\\", \\\"cover\\\": \\\"f5f4c70b21419313a0da2d0950fec69e1c6f5f68e387bbe0ce414f92b1c8256e\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/3f/8f/3f8f096a4294d486025bd79ce1a7b7c55ea1b516f890d9b2535fa9bce7aa7fc2-ycDCiL.use\",\"id\":\"f5f4c70b21419313a0da2d0950fec69e1c6f5f68e387bbe0ce414f92b1c8256e\"},\"created_time\":\"2021-04-09T03:27:26.416451\"},{\"tid\":\"79ff5728-9868-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"e5c492d198d1ac53fde2beeb618a20af179f8af430de5cd52ef76b5588a1776b\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T20:46:45.577020\"},{\"tid\":\"7a645b6e-98a4-11eb-bb07-a11125b44791\",\"schedule\":\"25%\",\"args\":\"{\\\"video\\\": \\\"45e5b08c6a4da9de1d976788ac2e4ed56edc0730d127b299d8762a17ea705e75\\\", \\\"cover\\\": \\\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/a4/49/a4494be963f2190657aafdf968d99f8c886d1520dcd968fc973d1345f412409d-xInQuq.use\",\"id\":\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\"},\"created_time\":\"2021-04-09T03:56:16.043593\"},{\"tid\":\"832e6704-9862-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"ce6e7e7666a0e9726929f93870031d31b92415a9278d8c1da93c676aa6d8b4b7\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"22ca8d7e0c864b0a3f88da9e86212b37755b1648e99bba49b196ca4b7654575e\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"2\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T20:04:04.004533\"},{\"tid\":\"888196a6-989c-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"59dcb60c75fd52fb4778593097f183fa61310d89f4053585e5002a2cbc3cd521\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-09T02:59:23.749648\"},{\"tid\":\"8d80aef0-98ae-11eb-bb07-a11125b44791\",\"schedule\":\"23%\",\"args\":\"{\\\"video\\\": \\\"99e8d8941fa0ee9788c64a5ff5339734a072b1fcf13deadf01adcae4e1eb4178\\\", \\\"cover\\\": \\\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"8\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/a4/49/a4494be963f2190657aafdf968d99f8c886d1520dcd968fc973d1345f412409d-xInQuq.use\",\"id\":\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\"},\"created_time\":\"2021-04-09T05:08:23.071272\"},{\"tid\":\"90682aa4-9853-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"175836c2d3114023238aaf712f024e5cba553ce6c834344666ca8949b0a32012\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"a54bc1a9268acbb19e789681e0bda24dd424d67932441480e22f9edcafb502e2\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"6\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T18:17:03.743638\"},{\"tid\":\"93bdca8e-9852-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"61bd22dbf9aa26b7e2b0b2c096800bc4170120f3cfbb68c653f94bd4198626b5\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"a54bc1a9268acbb19e789681e0bda24dd424d67932441480e22f9edcafb502e2\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T18:09:59.841414\"},{\"tid\":\"a7cf66ca-98a9-11eb-bb07-a11125b44791\",\"schedule\":\"25%\",\"args\":\"{\\\"video\\\": \\\"8093ca71aec04dd2959f845c737bed6a86c762749e3b7cf88680a7085b0059b0\\\", \\\"cover\\\": \\\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/a4/49/a4494be963f2190657aafdf968d99f8c886d1520dcd968fc973d1345f412409d-xInQuq.use\",\"id\":\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\"},\"created_time\":\"2021-04-09T04:33:19.724375\"},{\"tid\":\"c13eda50-9872-11eb-bb07-a11125b44791\",\"schedule\":\"100%\",\"args\":\"{\\\"video\\\": \\\"b16ec8cf650889715671e0d480e5037d782c2e8606c1c9b2b50c5eda032b7734\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T22:00:20.077686\"},{\"tid\":\"c17c3e98-9874-11eb-bb07-a11125b44791\",\"schedule\":\"100%\",\"args\":\"{\\\"video\\\": \\\"dcee81976ccc6f4e62068406b8ad67a31f14ee67015a1c78b002050ff9585002\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"1\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T22:14:39.475277\"},{\"tid\":\"c5b68c4e-98af-11eb-bb07-a11125b44791\",\"schedule\":\"211%\",\"args\":\"{\\\"video\\\": \\\"bb1dc7b1efafd60372ae55bdbd66bca72d815cc63d5f7627cfade21fc80827fa\\\", \\\"cover\\\": \\\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"7\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/a4/49/a4494be963f2190657aafdf968d99f8c886d1520dcd968fc973d1345f412409d-xInQuq.use\",\"id\":\"9802a72bf4d5c86b7af0d5983006db45109d39ed49354a973f38346ba9019060\"},\"created_time\":\"2021-04-09T05:17:06.873321\"},{\"tid\":\"c7e0112e-989c-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"3dc0b0c73ff292c012eb9deaf973cbfc12a186d1368500f8625a8a65639a54bf\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-09T03:01:10.066043\"},{\"tid\":\"ca31d71a-9873-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"ad81f3bae6431517e297b5e0bdc1fca93e9105c5426ef727905b8550a1e272fe\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": false}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T22:07:44.589626\"},{\"tid\":\"e96303f0-9852-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"87acad62943c5a743a1bfd45ac93e76bd0a0de253d18120a38c6d42b4545975a\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"a54bc1a9268acbb19e789681e0bda24dd424d67932441480e22f9edcafb502e2\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T18:12:23.531527\"},{\"tid\":\"ec58bac2-98a3-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"9d5af8139e4ee851de9116cfafa1accdc8224072416f5ce236c89c8fa9f755fe\\\", \\\"cover\\\": \\\"f5f4c70b21419313a0da2d0950fec69e1c6f5f68e387bbe0ce414f92b1c8256e\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/3f/8f/3f8f096a4294d486025bd79ce1a7b7c55ea1b516f890d9b2535fa9bce7aa7fc2-ycDCiL.use\",\"id\":\"f5f4c70b21419313a0da2d0950fec69e1c6f5f68e387bbe0ce414f92b1c8256e\"},\"created_time\":\"2021-04-09T03:52:17.729151\"},{\"tid\":\"ef2c5900-9873-11eb-bb07-a11125b44791\",\"schedule\":\"100%\",\"args\":\"{\\\"video\\\": \\\"463c65f34f70718eff89897c99b28676a6ee8eb6797ed5f52a9c4ad39a7417ef\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-08T22:08:46.629866\"},{\"tid\":\"fe980596-989c-11eb-bb07-a11125b44791\",\"schedule\":\"0%\",\"args\":\"{\\\"video\\\": \\\"65bc77eefd7b98595ca9f6c93bbc8c4850452f9954756ab7148b36660c39d03b\\\", \\\"cover\\\": \\\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\\\", \\\"is_background\\\": true, \\\"img_id\\\": \\\"542532347c04593feb42ccd5d9ec34ccd6b77e4a5f4f9c8b614a8a1b18267474\\\", \\\"mode\\\": true, \\\"is_filter\\\": true, \\\"filter_id\\\": \\\"3\\\"}\",\"cover\":{\"url\":\"https://file.yhf2000.cn/img/07/64/07646e1f72a09b8bc45904ab2115cc5ce66ac29b9ebd72e007f7e8741585d800-vYPGdS.use\",\"id\":\"6d1ab5cad131087d6bc2fefd1f9115221f053e0158e901236bb4d185a9b91ddc\"},\"created_time\":\"2021-04-09T03:02:41.868806\"}]}";
                JSONObject res_json = new JSONObject(res);
                res_json_array1 = res_json.getJSONArray("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}