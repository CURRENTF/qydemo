package com.example.qydemo0.QYAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qydemo0.DetailCategoryActivity;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.R;

import java.util.List;
import java.util.Map;

public class LittleGridViewAdapter extends GridViewAdapter {

    TextView outer;
    ViewHolder lastHolder;
    public LittleGridViewAdapter(@NonNull Context context, int resource, @NonNull List<Map<String, Object>> objects, TextView txt) {
        super(context, resource, objects);
        lastHolder = null;
        outer = txt;
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
                holder.clas.setBackground(mContext.getDrawable(R.drawable.gradient_color_orange_no_padding));
                outer.setText(holder.text.getText());
                if(lastHolder != null){
                    lastHolder.clas.setBackgroundResource(0);
                }
                lastHolder = holder;
            }
        });
        return convertView;
    }
}
