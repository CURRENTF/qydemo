package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYAdapter.CommentExpandAdapter;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.Widget.PostItem;
import com.example.qydemo0.Widget.QYScrollView;
import com.example.qydemo0.bean.Belong;
import com.example.qydemo0.bean.CommentBean;
import com.example.qydemo0.bean.CommentDetailBean;
import com.example.qydemo0.bean.ReplyDetailBean;
import com.example.qydemo0.bean.WorkBean;
import com.example.qydemo0.view.CommentExpandableListView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Bundle bundle = getIntent().getExtras();
        String s = bundle.getString("json");
        main = findViewById(R.id.main);
        try {
            JSONObject json = new JSONObject(s);
            postItem = new PostItem(this);
            postItem.init(json, true, false, true);
            if(json.getBoolean("like")) {
                postItem.like_img.setImageResource(R.drawable.like_gray);
                like_op = 1;
            }
            else {
                postItem.like_img.setImageResource(R.drawable.ic_like);
                like_op = -1;
            }
            main.addView(postItem);
            Log.e("whc_post", String.valueOf(json));
            post_id = json.getInt("pid");
            new GetCommentJson().execute(post_id,0,20);
        } catch (JSONException e) {
            Log.e("hjt.json.post.detail.wrong", "onCreate");
            e.printStackTrace();
        }
        postItem.like_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like_op *= -1;
                if(status == 0){
                    OPPost opPost = new OPPost();
                    opPost.execute(like_op);
                }
            }
        });
    }

    private void initView() {
        expandableListView = (CommentExpandableListView) findViewById(R.id.detail_page_lv_comment);
        bt_comment = (TextView) findViewById(R.id.detail_page_do_comment);
        bt_comment.setOnClickListener(this);
        initExpandableListView(commentsList);
    }


    class OPPost extends AsyncTask<Integer, Integer, Boolean>{

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
                 if(like_op == 1) postItem.like_img.setImageResource(R.drawable.like_gray);
                 else postItem.like_img.setImageResource(R.drawable.ic_like);
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
                //toast("展开第"+groupPosition+"个分组");

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
                    new doComment().execute(commentContent);

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
                    new doReply().execute(""+position, ""+second_position, replyContent);
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

    public class GetCommentJson extends AsyncTask<Integer, Void, String> {

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

    public class doComment extends AsyncTask<String, Void, String[]>{
        @Override
        protected void onPostExecute(String... contentt) {
            super.onPostExecute(contentt);
            try {
                success_commment(contentt[0], Integer.valueOf(contentt[1]));
            } catch (JSONException e) {
                e.printStackTrace();
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
                    String[] res_reply_all = {strings[0], String.valueOf(res_jsonobj.getJSONObject("data").getInt("cid"))};
                    return res_reply_all;
                }
                else
                    return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class doReply extends AsyncTask<String, Void, String[]>{
        @Override
        protected void onPostExecute(String[] contentt) {
            super.onPostExecute(contentt);
            try {
                success_reply(contentt[2],Integer.valueOf(contentt[1]),Integer.valueOf(contentt[0]),Integer.valueOf(contentt[3]));
            } catch (JSONException e) {
                e.printStackTrace();
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
                    String[] res_to_reply = {strings[0], strings[1], strings[2], String.valueOf((new JSONObject(res)).getJSONObject("data").getInt("cid"))};
                    return res_to_reply;
                }
                else
                    return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}