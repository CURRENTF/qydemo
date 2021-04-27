package com.example.qydemo0.QYAdapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qydemo0.QYpack.Video.Work;
import com.example.qydemo0.R;
import com.example.qydemo0.Widget.ListItem.LinearLayoutItem;
import com.example.qydemo0.Widget.ListItem.WorkItem;

import org.json.JSONObject;

import java.util.List;

public class LinearLayoutAdapter extends RecyclerView.Adapter<LinearLayoutAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayoutItem item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            if(itemView instanceof WorkItem){
                Log.d("hjt.workItem", "yes");
            }
            item = (LinearLayoutItem) itemView;
        }
        void fill(JSONObject json){
            item.fill(json);
        }
    }



    public LinearLayoutAdapter(List<JSONObject> list, int resId, Activity ac){
        dataList = list;
        this.resId = resId;
        this.ac = ac;
    }

    List<JSONObject> dataList;
    int resId;
    Activity ac;

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        WorkItem item = new WorkItem(parent, ac);
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
        Log.d("hjt.add.data", "ok");
    }
}