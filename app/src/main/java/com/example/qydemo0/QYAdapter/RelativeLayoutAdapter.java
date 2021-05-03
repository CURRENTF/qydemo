package com.example.qydemo0.QYAdapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.Widget.ListItem.LittleLearnItem;
import com.example.qydemo0.Widget.ListItem.LittleUserItem;
import com.example.qydemo0.Widget.ListItem.LittleWorkItem;
import com.example.qydemo0.Widget.ListItem.PostItem;
import com.example.qydemo0.Widget.ListItem.RelativeLayoutItem;
import com.example.qydemo0.Widget.ListItem.RenderItem;
import com.example.qydemo0.Widget.ListItem.SmartItem;
import com.example.qydemo0.Widget.ListItem.WorkItem;

import org.json.JSONObject;

import java.util.List;

public class RelativeLayoutAdapter extends RecyclerView.Adapter<RelativeLayoutAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayoutItem item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = (RelativeLayoutItem) itemView;
        }
        void fill(JSONObject json){
            item.fill(json);
        }
    }

    public RelativeLayoutAdapter(List<JSONObject> list, int item_type, Activity ac){
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
        if(item_type == Constant.mInstance.WORK){
            item = new WorkItem(parent, ac);
        }
        else if(item_type == Constant.mInstance.POST){
            item = new PostItem(parent, ac);
        }
        else if(item_type == Constant.mInstance.LITTLE_LEARN){
            item = new LittleLearnItem(parent, ac);
        }
        else if(item_type == Constant.mInstance.LITTLE_USER){
            item = new LittleUserItem(parent, ac);
        }
        else if(item_type == Constant.mInstance.LITTLE_WORK){
            item = new LittleWorkItem(parent, ac);
        }
        else if(item_type == Constant.mInstance.SMART){
            item = new SmartItem(parent, ac);
        }
        else {
            item = new RenderItem(parent, ac);
        }
        return new ViewHolder(item);
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
    }
}
