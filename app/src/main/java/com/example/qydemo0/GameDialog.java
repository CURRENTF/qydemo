package com.example.qydemo0;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.qydemo0.Widget.QYDIalog;

import java.util.ArrayList;
import java.util.List;

public class GameDialog {
    QYDIalog qydIalog;
    ImageView content;
    Button retry, next, out;
    int[] ui = {R.id.content, R.id.retry, R.id.next, R.id.out};
    int[] star_img = {R.drawable.star0, R.drawable.star1, R.drawable.star2, R.drawable.star3};
    public GameDialog(Context context, int[] p, int star_num, QYDIalog.OnCenterItemClickListener lsr){
        int ind = 0;
        List<Integer> gu = new ArrayList<>();
        for(int i = 0; i < p.length; i++){
            while(ind!=p[i]){
                gu.add(ind);
                ind++;
            }
        }
        qydIalog = new QYDIalog(context, R.layout.game_dialog, ui);
        qydIalog.show();
        content = qydIalog.findViewById(R.id.content);
        set_content(star_num);
        for(int i=0;i<gu.size();i++){
            (qydIalog.findViewById(ui[gu.get(i)])).setVisibility(View.GONE);
        }
        qydIalog.setOnCenterItemClickListener(lsr);
    }
    public void set_content(int star_num){
        content.setImageResource(star_img[star_num]);
    }
}
