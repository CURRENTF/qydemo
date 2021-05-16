package com.example.qydemo0.QYAdapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.Widget.ListItem.LinearLayoutItem;
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
        RelativeLayoutItem control_item;
        public View item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView;
        }
        void fill(JSONObject json){
            control_item.fill(json);
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
        View view_item = null;
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
            view_item = ((SmartItem)item).mView;
        }
        else {
            item = new RenderItem(parent, ac);
            view_item = ((RenderItem)item).mView;
        }
        ViewHolder holder = new ViewHolder(view_item);
        holder.control_item = (RelativeLayoutItem) item;
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
    }

    public void clearData(){
        dataList.clear();
        notifyDataSetChanged();
    }
}
