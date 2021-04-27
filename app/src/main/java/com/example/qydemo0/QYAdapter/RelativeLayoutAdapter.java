package com.example.qydemo0.QYAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qydemo0.Widget.ListItem.RelativeLayoutItem;

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

    public RelativeLayoutAdapter(List<JSONObject> list, int resId){
        dataList = list;
        this.resId = resId;
    }

    List<JSONObject> dataList;
    int resId;

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resId, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.fill(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size(); // 提供上拉加载item
    }
}
