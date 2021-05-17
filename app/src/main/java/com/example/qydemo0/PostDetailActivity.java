package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYUser;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYAdapter.CommentExpandAdapter;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.Widget.ListItem.PostItem;
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;
import com.example.qydemo0.Widget.Post;
import com.example.qydemo0.bean.Belong;
import com.example.qydemo0.bean.CommentBean;
import com.example.qydemo0.bean.CommentDetailBean;
import com.example.qydemo0.bean.ReplyDetailBean;
import com.example.qydemo0.view.CommentExpandableListView;
import com.google.android.exoplayer2.C;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PostDetailActivity extends MyAppCompatActivity implements View.OnClickListener {

    LinearLayout main;
    PostItem postItem;
    int like_op = -1, status = 0;
    private TextView bt_comment;
    private CommentExpandableListView expandableListView;
    private CommentExpandAdapter adapter;
    private CommentBean commentBean;
    private List<CommentDetailBean> commentsList;
    private BottomSheetDialog dialog;
    QYrequest work_request = new QYrequest();
    private int post_id;
    String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Bundle bundle = getIntent().getExtras();
        String s = bundle.getString("json");
        main = findViewById(R.id.main);
        try {
            JSONObject json = new JSONObject(s);
            pid = json.getString("pid");
            postItem = new PostItem(this);
            postItem.init(json, true, false, true);
            GetIFLike getIFLike = new GetIFLike(this);
            getIFLike.execute();
            main.addView(postItem);
            Log.e("whc_post", String.valueOf(json));
            post_id = json.getInt("pid");
            new GetCommentJson(PostDetailActivity.this).execute(post_id,0,20);
        } catch (JSONException e) {
            Log.e("hjt.json.post.detail.wrong", "onCreate");
            e.printStackTrace();
        }

    }

    class GetIFLike extends MyAsyncTask<String, Integer, JSONObject>{

        protected GetIFLike(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return MsgProcess.msgProcess(htp.advanceGet(Constant.mInstance.post_url + "info/" + pid + "/", "Authorization", GlobalVariable.mInstance.token), false, null);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                Log.d("hjt.json", json.toString());
                if(json.getBoolean("like")) {
                    postItem.like_img.setImageResource(R.drawable.like_gray);
                    like_op = 1;
                    postItem.like_num.setText(json.getString("like_num"));
                }
                else {
                    postItem.like_img.setImageResource(R.drawable.ic_like);
                    like_op = -1;
                }
                postItem.like_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        like_op *= -1;
                        if(status == 0){
                            OPPost opPost = new OPPost(PostDetailActivity.this);
                            opPost.execute(like_op);
                        }
                    }
                });
                if(json.getString("follow").equals("true")){
                    postItem.btn_follow.setText("已关注");
                    postItem.btn_follow.setVisibility(View.VISIBLE);
                }
                else if(json.getString("follow").equals("false")) {
                    postItem.btn_follow.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            Follow follow = new Follow(PostDetailActivity.this);
                            try {
                                follow.execute(json.getJSONObject("belong").getInt("uid"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    postItem.btn_follow.setVisibility(View.VISIBLE);
                }
                else {
                    postItem.btn_follow.setText("");
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        expandableListView = (CommentExpandableListView) findViewById(R.id.detail_page_lv_comment);
        bt_comment = (TextView) findViewById(R.id.detail_page_do_comment);
        bt_comment.setOnClickListener(this);
        initExpandableListView(commentsList);
    }

    public class Follow extends MyAsyncTask<Integer, Integer, Boolean> {

        protected Follow(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            return QYUser.follow(integers[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                postItem.btn_follow.setText("已关注");
            }
            else {
                Toast.makeText(PostDetailActivity.this, "关注失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


    class OPPost extends MyAsyncTask<Integer, Integer, Boolean> {

        protected OPPost(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected Boolean doInBackground(Integer... strings) {
            status = 1;
            QYrequest htp = new QYrequest();
            try {
                Log.d("hjt.post.pid", postItem.json.getString("pid"));
                return MsgProcess.checkMsg(htp.advancePut("{}", Constant.mInstance.post_url + "func/" + postItem.json.getString("pid") + "/" + strings[0] + "/",  "Authorization", GlobalVariable.mInstance.token), false, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                 if(like_op == 1) {
                     postItem.like_img.setImageResource(R.drawable.like_gray);
                     postItem.incLikes();
                 }
                 else {
                     postItem.like_img.setImageResource(R.drawable.ic_like);
                     postItem.decLikes();
                 }
            }
            else {
                like_op *= -1;
                Toast.makeText(PostDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
            }
            status = 0;
        }
    }

    /**
     * 初始化评论和回复列表
     */
    private void initExpandableListView(final List<CommentDetailBean> commentList){
        expandableListView.setGroupIndicator(null);
        //默认展开所有回复
        adapter = new CommentExpandAdapter(this, commentList);
        expandableListView.setAdapter(adapter);
        for(int i = 0; i<commentList.size(); i++){
            expandableListView.expandGroup(i);
        }
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                showReplyDialog(groupPosition, -1);
                return true;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                Toast.makeText(PostDetailActivity.this,"点击了回复",Toast.LENGTH_SHORT).show();
                showReplyDialog(groupPosition,childPosition);
                return true;
            }
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

    }

    /**
     * by moos on 2018/04/20
     * func:生成测试数据
     * @return 评论数据
     */
    private List<CommentDetailBean> generateTestData(String commentJson){
        Gson gson = new Gson();
        commentBean = gson.fromJson(commentJson, CommentBean.class);
        List<CommentDetailBean> commentList = commentBean.getData();
        return commentList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.detail_page_do_comment){

            showCommentDialog();
        }
    }

    /**
     * by moos on 2018/04/20
     * func:弹出评论框
     */
    private void showCommentDialog(){
        dialog = new BottomSheetDialog(this);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        dialog.setContentView(commentView);
        /**
         * 解决bsd显示不全的情况
         */
        View parent = (View) commentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        commentView.measure(0,0);
        behavior.setPeekHeight(commentView.getMeasuredHeight());

        bt_comment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String commentContent = commentText.getText().toString().trim();
                if(!TextUtils.isEmpty(commentContent)){

                    //commentOnWork(commentContent);
                    dialog.dismiss();
                    new doComment(PostDetailActivity.this).execute(commentContent);

                }else {
                    Toast.makeText(PostDetailActivity.this,"评论内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && charSequence.length()>2){
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                }else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }

    private void success_commment(String commentContent, int cid) throws JSONException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        Log.i("raw_date",String.valueOf(date));
        CommentDetailBean detailBean = new CommentDetailBean(cid,commentContent,0,simpleDateFormat.format(date),
                true,false,false,
                new Belong(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getInt("uid"),
                        GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("username"), GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("img_url")),null);
        adapter.addTheCommentData(detailBean);
        Toast.makeText(PostDetailActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
    }

    private void showReplyDialog(final int position, final int second_position){
        dialog = new BottomSheetDialog(this);
        View commentView = LayoutInflater.from(this).inflate(R.layout.comment_dialog_layout,null);
        final EditText commentText = (EditText) commentView.findViewById(R.id.dialog_comment_et);
        final Button bt_comment = (Button) commentView.findViewById(R.id.dialog_comment_bt);
        if(second_position!=-1) {
            commentText.setHint("回复 " + commentsList.get(position).getReplies().get(second_position).getBelong().getUsername() + " 的评论:");
        }
        else{
            commentText.setHint("回复 " + commentsList.get(position).getBelong().getUsername() + " 的评论:");
        }
        dialog.setContentView(commentView);
        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String replyContent = commentText.getText().toString().trim();
                if(!TextUtils.isEmpty(replyContent)){
                    dialog.dismiss();
                    new doReply(PostDetailActivity.this).execute(""+position, ""+second_position, replyContent);
                }else {
                    Toast.makeText(PostDetailActivity.this,"回复内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence) && charSequence.length()>2){
                    bt_comment.setBackgroundColor(Color.parseColor("#FFB568"));
                }else {
                    bt_comment.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();
    }

    private void success_reply(String replyContent, int second_position, int position, int cid) throws JSONException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        ReplyDetailBean detailBean;
        if(second_position!=-1) {
            detailBean = new ReplyDetailBean(-1, replyContent, 0, simpleDateFormat.format(date), true, false, false, new Belong(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getInt("uid"),
                    GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("username"), GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("img_url")),
                    new Belong(commentsList.get(position).getReplies().get(second_position).getBelong().getUid(),
                            commentsList.get(position).getReplies().get(second_position).getBelong().getUsername(),
                            commentsList.get(position).getReplies().get(second_position).getBelong().getImg_url()));
        } else{
            detailBean = new ReplyDetailBean(cid, replyContent, 0, simpleDateFormat.format(date), true, false, false, new Belong(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getInt("uid"),
                    GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("username"), GlobalVariable.mInstance.fragmentDataForMain.userInfoJson.getString("username")),
                    null);
        }
        adapter.addTheReplyData(detailBean, position);
        expandableListView.expandGroup(position);
        Toast.makeText(PostDetailActivity.this,"回复成功",Toast.LENGTH_SHORT).show();
    }

    public class GetCommentJson extends MyAsyncTask<Integer, Void, String> {

        protected GetCommentJson(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected String doInBackground(Integer... ints) {
            String[] ss = new String[0];
            String res = work_request.advanceMethod("GET", GenerateJson.universeJson2(ss), Constant.mInstance.comment+"1/"+ints[0]+"/?start="+ints[1]+"&lens="+ints[2], "Authorization", GlobalVariable.mInstance.token);
            Log.i("commentJson",res);
            //Log.i("token",""+ GlobalVariable.mInstance.token);
            return res;
        }

        @Override
        protected void onPostExecute(String cur_work_json) {
            super.onPreExecute();
            commentsList = generateTestData(cur_work_json);
            initView();
        }
    }

    public class doComment extends MyAsyncTask<String, Void, String[]>{
        protected doComment(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected void onPostExecute(String... contentt) {
            super.onPostExecute(contentt);
            if(contentt==null){
                Toast.makeText(PostDetailActivity.this, "出错啦！", Toast.LENGTH_SHORT).show();
            } else {
                if(contentt[0].equals("success")) {
                    try {
                        success_commment(contentt[1], Integer.valueOf(contentt[2]));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(PostDetailActivity.this, contentt[1], Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected String[] doInBackground(String... strings) {
            String[] callToJson = {"text", "string",strings[0]};
            String res = work_request.advancePost(GenerateJson.universeJson2(callToJson),
                    Constant.mInstance.comment+"1/"+post_id+"/", "Authorization", GlobalVariable.mInstance.token);
            try {
                JSONObject res_jsonobj = new JSONObject(res);
                Log.i("comment_callback",res);
                if(res_jsonobj.getString("msg").equals("Success")) {
                    String[] res_reply_all = {"success", strings[0], String.valueOf(res_jsonobj.getJSONObject("data").getInt("cid"))};
                    return res_reply_all;
                } else {
                    return new String[]{"wrong", MsgProcess.getWrongMsg(res)};
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class doReply extends MyAsyncTask<String, Void, String[]>{
        protected doReply(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected void onPostExecute(String[] contentt) {
            super.onPostExecute(contentt);
            if(contentt==null){
                Toast.makeText(PostDetailActivity.this, "出错啦！", Toast.LENGTH_SHORT).show();
            } else {
                if(contentt[0].equals("success")){
                try {
                    success_reply(contentt[3], Integer.valueOf(contentt[2]), Integer.valueOf(contentt[1]), Integer.valueOf(contentt[4]));
                } catch (JSONException e) {
                    e.printStackTrace();
                } } else {
                    Toast.makeText(PostDetailActivity.this, contentt[1], Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected String[] doInBackground(String... strings) {
            String res = "";
            if (Integer.valueOf(strings[1]) != -1) {
                String[] call_to_json = {"top", "int", "" + commentsList.get(Integer.valueOf(strings[0])).getCid(), "reply", "int", "" + commentsList.get(Integer.valueOf(strings[0])).getReplies().get(Integer.valueOf(strings[1])).getCid(), "text", "string", strings[2]};
                res = work_request.advancePost(GenerateJson.universeJson2(call_to_json),
                        Constant.mInstance.comment + "1/" + post_id + "/", "Authorization", GlobalVariable.mInstance.token);
                Log.e("herher",String.valueOf(GenerateJson.universeJson2(call_to_json)));
            } else {
                String[] call_to_json = {"top", "int", "" + commentsList.get(Integer.valueOf(strings[0])).getCid(), "reply", "int", "" + commentsList.get(Integer.valueOf(strings[0])).getCid(), "text", "string", strings[2]};
                res = work_request.advancePost(GenerateJson.universeJson2(call_to_json),
                        Constant.mInstance.comment + "1/" + post_id + "/", "Authorization", GlobalVariable.mInstance.token);
                Log.e("herher",String.valueOf(GenerateJson.universeJson2(call_to_json)));
            }
            Log.e("whc_res",""+post_id);

            try {

                if ((new JSONObject(res)).getString("msg").equals("Success")) {
                    String[] res_to_reply = {"success", strings[0], strings[1], strings[2], String.valueOf((new JSONObject(res)).getJSONObject("data").getInt("cid"))};
                    return res_to_reply;
                } else {
                    return new String[]{"wrong", MsgProcess.getWrongMsg(res)};
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}