package com.example.qydemo0.QYAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GridViewAdapter extends ArrayAdapter<Map<String, Object>> {

    Context mContext;
    int layoutResId;
    List<Map<String, Object>> dataList = new ArrayList<>();


    public GridViewAdapter(@NonNull Context context, int resource, @NonNull List<Map<String, Object>> objects) {
        super(context, resource, objects);
        mContext = context;
        layoutResId = resource;
        dataList = objects;
    }

    public void addData(Map<String, Object> m){
        dataList.add(m);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResId, parent, false);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.img = (ImageView) convertView.findViewById(R.id.image);
            holder.clas = (LinearLayout) convertView.findViewById(R.id.class_t);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        String text = (String) dataList.get(position).get("text");
        String url = (String) dataList.get(position).get("image");
        Img.url2imgViewRoundRectangle(url, holder.img, mContext, 20);
        holder.text.setText(text);
        holder.clas.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView text;
        ImageView img;
        LinearLayout clas;
    }
}
