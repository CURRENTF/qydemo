package com.example.qydemo0.QYAdapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.DeviceInfo;
import com.example.qydemo0.QYpack.Video.Work;
import com.example.qydemo0.R;
import com.example.qydemo0.Widget.ListItem.LinearLayoutItem;
import com.example.qydemo0.Widget.ListItem.LittleLearnItem;
import com.example.qydemo0.Widget.ListItem.LittleUserItem;
import com.example.qydemo0.Widget.ListItem.LittleWorkItem;
import com.example.qydemo0.Widget.ListItem.PostItem;
import com.example.qydemo0.Widget.ListItem.RenderItem;
import com.example.qydemo0.Widget.ListItem.SmartItem;
import com.example.qydemo0.Widget.ListItem.WorkItem;
import com.example.qydemo0.Widget.Post;

import org.json.JSONObject;

import java.util.List;


public class LinearLayoutAdapter extends RecyclerView.Adapter<LinearLayoutAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder{
        public View item;
        public LinearLayoutItem control_item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
        }
        void fill(JSONObject json){
            control_item.fill(json);
        }
    }

    public LinearLayoutAdapter(List<JSONObject> list, int item_type, Activity ac){
        dataList = list;
        this.item_type = item_type;
        this.ac = ac;
    }

    List<JSONObject> dataList;
    int item_type;
    Activity ac;

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item;
        View view_item = null;
        if(item_type == Constant.mInstance.WORK){
            item = new WorkItem(parent, ac);
            view_item = ((WorkItem)item).mView;
        }
        else if(item_type == Constant.mInstance.POST){
            item = new PostItem(parent, ac);
            view_item = ((PostItem)item).mView;
        }
        else if(item_type == Constant.mInstance.LITTLE_LEARN){
            item = new LittleLearnItem(parent, ac);
            view_item = ((LittleLearnItem)item).mView;
        }
        else if(item_type == Constant.mInstance.LITTLE_USER){
            item = new LittleUserItem(parent, ac);
            view_item = ((LittleUserItem)item).mView;
        }
        else if(item_type == Constant.mInstance.LITTLE_WORK){
            item = new LittleWorkItem(parent, ac);
            view_item = ((LittleWorkItem)item).mView;
        }
        else if(item_type == Constant.mInstance.SMART){
            item = new SmartItem(parent, ac);
        }
        else {
            item = new RenderItem(parent, ac);
        }
        ViewHolder holder = new ViewHolder(view_item);
        holder.control_item = (LinearLayoutItem) item;
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.fill(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size(); // 提供上拉加载item
    }

    public void addData(JSONObject item){
        dataList.add(item);
        notifyDataSetChanged();
        Log.d("hjt.add.data", "ok");
    }
}