package com.example.qydemo0.QYAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.renderscript.Sampler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.UiThread;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.R;
import com.example.qydemo0.bean.CallBackBean;
import com.example.qydemo0.bean.CommentDetailBean;
import com.example.qydemo0.bean.ReplyDetailBean;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Author: Moos
 * E-mail: moosphon@gmail.com
 * Date:  18/4/20.
 * Desc: 评论与回复列表的适配器
 */

public class CommentExpandAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "CommentExpandAdapter";
    private List<CommentDetailBean> commentBeanList;
    private List<ReplyDetailBean> replyBeanList;
    private Context context;
    private int pageIndex = 1;
    private QYrequest comment_request = new QYrequest();

    public CommentExpandAdapter(Context context, List<CommentDetailBean> commentBeanList) {
        this.context = context;
        this.commentBeanList = commentBeanList;
    }

    @Override
    public int getGroupCount() {
        return commentBeanList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if(commentBeanList.get(i).getReplies() == null){
            return 0;
        }else {
            return commentBeanList.get(i).getReplies().size()>0 ? commentBeanList.get(i).getReplies().size():0;
        }

    }

    @Override
    public Object getGroup(int i) {
        return commentBeanList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return commentBeanList.get(i).getReplies().get(i1);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getCombinedChildId(groupPosition, childPosition);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpand, View convertView, ViewGroup viewGroup) {
        final GroupHolder groupHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item_layout, viewGroup, false);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        }else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        Glide.with(context)
                .load(commentBeanList.get(groupPosition).getBelong().getImg_url())
                .transform(/*new CenterInside(), */new RoundedCorners(50))
                .into(groupHolder.logo);
        int cur_like_num = commentBeanList.get(groupPosition).getLike_num();
        if(cur_like_num !=0) {
            groupHolder.like_num.setText(String.valueOf(cur_like_num));
        }
        else{
            groupHolder.like_num.setText("");
        }
        groupHolder.tv_name.setText(commentBeanList.get(groupPosition).getBelong().getUsername());
        groupHolder.tv_time.setText(commentBeanList.get(groupPosition).getCreated_time());
        if(!commentBeanList.get(groupPosition).getIs_delete()) {
            groupHolder.tv_content.setText(commentBeanList.get(groupPosition).getText());
        } else {
            groupHolder.tv_content.setText("");
        }
        if(commentBeanList.get(groupPosition).getLike()){
            groupHolder.iv_like.setColorFilter(Color.parseColor("#FF5C5C"));
        }
        else{
            groupHolder.iv_like.setColorFilter(Color.parseColor("#aaaaaa"));
        }
        groupHolder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(commentBeanList.get(groupPosition).getLike()){
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            String[] j = new String[0];
                            String res = comment_request.advancePut(GenerateJson.universeJson2(j),
                                    Constant.mInstance.comment+"func/"+commentBeanList.get(groupPosition).getCid()+"/-1/",
                                    "Authorization", GlobalVariable.mInstance.token);
                            Log.i("reply_call_back",res);
                            try {
                                JSONObject res_json = new JSONObject(res);
                                if(res_json.getString("msg").equals("Success")) {
                                    commentBeanList.get(groupPosition).setLike(false);
                                    groupHolder.iv_like.setColorFilter(Color.parseColor("#aaaaaa"));
                                    commentBeanList.get(groupPosition).setLike_num(commentBeanList.get(groupPosition).getLike_num() - 1);
                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyDataSetChanged();
                                        }
                                    });
                                }
                                else{
                                    Log.i("comment","WRONG!!!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String[] j = new String[0];
                            String res = comment_request.advancePut(GenerateJson.universeJson2(j),
                                    Constant.mInstance.comment+"func/"+commentBeanList.get(groupPosition).getCid()+"/1/",
                                    "Authorization", GlobalVariable.mInstance.token);
                            Log.i("reply_call_back",res);
                            Gson gson = new Gson();
                            CallBackBean call_back_bean = gson.fromJson(res, CallBackBean.class);
                            if(call_back_bean.getMsg().equals("Success")) {
                                commentBeanList.get(groupPosition).setLike(true);
                                groupHolder.iv_like.setColorFilter(Color.parseColor("#FF5C5C"));
                                commentBeanList.get(groupPosition).setLike_num(commentBeanList.get(groupPosition).getLike_num() + 1);
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataSetChanged();
                                    }
                                });
                            }
                            else{
                                Log.i("reply","WRONG!!!");
                            }
                        }
                    }).start();
                }
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        final ChildHolder childHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_reply_item_layout,viewGroup, false);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        }
        else {
            childHolder = (ChildHolder) convertView.getTag();
        }
        int cur_like_num = commentBeanList.get(groupPosition).getReplies().get(childPosition).getLike_num();
        if(cur_like_num !=0) {
            childHolder.like_num.setText(String.valueOf(cur_like_num));
        }
        else{
            childHolder.like_num.setText("");
        }
        if(commentBeanList.get(groupPosition).getReplies().get(childPosition).getLike()){
            commentBeanList.get(groupPosition).getReplies().get(childPosition).setLike(true);
            childHolder.iv_like.setColorFilter(Color.parseColor("#FF5C5C"));
        }
        String replyUser = commentBeanList.get(groupPosition).getReplies().get(childPosition).getBelong().getUsername();
        if(!TextUtils.isEmpty(replyUser)){
            if(commentBeanList.get(groupPosition).getReplies().get(childPosition).getReply_to()==null) {
                childHolder.tv_name.setText(replyUser + ":");
            } else {
                childHolder.tv_name.setText(replyUser + " 回复 " + commentBeanList.get(groupPosition).getReplies().get(childPosition).getReply_to().getUsername());
            }
        }else {
            childHolder.tv_name.setText("无名"+":");
        }
        childHolder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(commentBeanList.get(groupPosition).getReplies().get(childPosition).getLike()){
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            String[] j = new String[0];
                            String res = comment_request.advancePut(GenerateJson.universeJson2(j),
                                    Constant.mInstance.comment+"func/"+commentBeanList.get(groupPosition).getReplies().get(childPosition).getCid()+"/-1/",
                                    "Authorization", GlobalVariable.mInstance.token);
                            Log.i("reply_call_back",res);
                            try {
                                JSONObject res_json = new JSONObject(res);
                                if(res_json.getString("msg").equals("Success")) {
                                    commentBeanList.get(groupPosition).getReplies().get(childPosition).setLike(false);
                                    childHolder.iv_like.setColorFilter(Color.parseColor("#aaaaaa"));
                                    commentBeanList.get(groupPosition).getReplies().get(childPosition).setLike_num(commentBeanList.get(groupPosition).getReplies().get(childPosition).getLike_num() - 1);
                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyDataSetChanged();
                                        }
                                    });
                                }
                                else{
                                    Log.i("reply","WRONG!!!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String[] j = new String[0];
                            String res = comment_request.advancePut(GenerateJson.universeJson2(j),
                                    Constant.mInstance.comment+"func/"+commentBeanList.get(groupPosition).getReplies().get(childPosition).getCid()+"/1/",
                                    "Authorization", GlobalVariable.mInstance.token);
                            Log.i("reply_call_back",res);
                            Gson gson = new Gson();
                            CallBackBean call_back_bean = gson.fromJson(res, CallBackBean.class);
                            if(call_back_bean.getMsg().equals("Success")) {
                                commentBeanList.get(groupPosition).getReplies().get(childPosition).setLike(true);
                                childHolder.iv_like.setColorFilter(Color.parseColor("#FF5C5C"));
                                commentBeanList.get(groupPosition).getReplies().get(childPosition).setLike_num(commentBeanList.get(groupPosition).getReplies().get(childPosition).getLike_num() + 1);
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataSetChanged();
                                    }
                                });
                            }
                            else{
                                Log.i("reply","WRONG!!!");
                            }
                        }
                    }).start();
                }
            }
        });
        if(!commentBeanList.get(groupPosition).getReplies().get(childPosition).getIs_delete()) {
            childHolder.tv_content.setText(commentBeanList.get(groupPosition).getReplies().get(childPosition).getText());
        } else {
            childHolder.tv_content.setText("");
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private class GroupHolder{
        private CircleImageView logo;
        private TextView tv_name, tv_content, tv_time, like_num;
        private ImageView iv_like;
        public GroupHolder(View view) {
            logo = (CircleImageView) view.findViewById(R.id.comment_item_logo);
            tv_content = (TextView) view.findViewById(R.id.comment_item_content);
            tv_name = (TextView) view.findViewById(R.id.comment_item_userName);
            tv_time = (TextView) view.findViewById(R.id.comment_item_time);
            like_num = (TextView) view.findViewById(R.id.comment_like_num);
            iv_like = (ImageView) view.findViewById(R.id.comment_item_like);
        }
    }

    private class ChildHolder{
        private TextView tv_name, tv_content, like_num;
        private ImageView iv_like;
        public ChildHolder(View view) {
            tv_name = (TextView) view.findViewById(R.id.reply_item_user);
            tv_content = (TextView) view.findViewById(R.id.reply_item_content);
            like_num = (TextView) view.findViewById(R.id.reply_like_num);
            iv_like = (ImageView) view.findViewById(R.id.comment_item_like);
        }
    }


    /**
     * by moos on 2018/04/20
     * func:评论成功后插入一条数据
     * @param commentDetailBean 新的评论数据
     */
    public void addTheCommentData(CommentDetailBean commentDetailBean){
        if(commentDetailBean!=null){

            commentBeanList.add(commentDetailBean);
            notifyDataSetChanged();
        }else {
            throw new IllegalArgumentException("评论数据为空!");
        }

    }

    /**
     * by moos on 2018/04/20
     * func:回复成功后插入一条数据
     * @param replyDetailBean 新的回复数据
     */
    public void addTheReplyData(ReplyDetailBean replyDetailBean, int groupPosition){
        if(replyDetailBean!=null){
            Log.e(TAG, "addTheReplyData: >>>>该刷新回复列表了:"+replyDetailBean.toString() );
            if(commentBeanList.get(groupPosition).getReplies() != null ){
                commentBeanList.get(groupPosition).getReplies().add(replyDetailBean);
            }else {
                List<ReplyDetailBean> replyList = new ArrayList<>();
                replyList.add(replyDetailBean);
                commentBeanList.get(groupPosition).setReplies(replyList);
            }
            notifyDataSetChanged();
        }else {
            throw new IllegalArgumentException("回复数据为空!");
        }

    }

    /**
     * by moos on 2018/04/20
     * func:添加和展示所有回复
     * @param replyBeanList 所有回复数据
     * @param groupPosition 当前的评论
     */
    private void addReplyList(List<ReplyDetailBean> replyBeanList, int groupPosition){
        if(commentBeanList.get(groupPosition).getReplies() != null ){
            commentBeanList.get(groupPosition).getReplies().clear();
            commentBeanList.get(groupPosition).getReplies().addAll(replyBeanList);
        }else {

            commentBeanList.get(groupPosition).setReplies(replyBeanList);
        }

        notifyDataSetChanged();
    }

    public class CommentChange extends AsyncTask<Integer, Void, Integer>{
        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            if(aVoid!=-1){

            }
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            String[] j = new String[0];
            String res = comment_request.advancePut(GenerateJson.universeJson2(j), Constant.mInstance.comment+"func/"+integers[0]+"/"+integers[1]+"/","Authorization", GlobalVariable.mInstance.token);
            Log.i("json",res);
            Gson gson = new Gson();
            CallBackBean call_back_bean = gson.fromJson(res, CallBackBean.class);
            if(call_back_bean.getMsg().equals("Success"))
                return integers[0];
            else
                return -1;
        }
    }

}
