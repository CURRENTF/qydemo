package com.example.qydemo0;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;
import com.example.qydemo0.QYpack.Img;

public class PopupWindowRight extends PopupWindow implements View.OnClickListener{
    private static final String TAG = "PopupWindowRight";
    private View view;
    private int bg_img=0, lj_id = 0, style_img = 0;
    private ImageView img0,img1,img2,img3,img4,img5,img6,img7,img8,img9,img10,img11,img12,img_local,
            lj0,lj1,lj2,lj3,lj4,lj5,lj6,lj7,lj8,lj9,lj10,
            sty0,sty1;
    private Button btn_change_bg,btn_lj,btn_sty;

    public PopupWindowRight(Context context) {
        //设置view
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.activity_popup_window_right, null);
        setContentView(view);

        init_btn(context);
        init_img(context);
        init_lj(context);
        init_sty(context);
        //其他设置
        setWidth(((Activity) context).findViewById(android.R.id.content).getWidth());//必须设置宽度
        setHeight(dp2px(150f));//必须设置高度
        setFocusable(false);//是否获取焦点
        setOutsideTouchable(false);//是否可以通过点击屏幕外来关闭
        setTouchable(true);
    }

    private Img img_process = new Img();

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        //加入动画
        ObjectAnimator.ofFloat(getContentView(), "translationX", getWidth(), 0).setDuration(500).start();
    }



    /**
     * 获取屏幕宽高
     *
     * @param context
     * @return
     */
    private static int[] getScreenSize(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    /**
     * Value of dp to value of px.
     *
     * @param dpValue The value of dp.
     * @return value of px
     */
    public static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void init_btn(Context context) {
        btn_change_bg = view.findViewById(R.id.change_background);
        btn_lj = view.findViewById(R.id.lj);
        btn_sty = view.findViewById(R.id.change_style);

        HorizontalScrollView bg = (HorizontalScrollView) view.findViewById(R.id.bg_scrollview),
        lj = (HorizontalScrollView) view.findViewById(R.id.lj_scrollview), sty = (HorizontalScrollView) view.findViewById(R.id.sty_scrollview);
        lj.setVisibility(View.GONE);
        sty.setVisibility(View.GONE);

        btn_change_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_change_bg.setBackgroundResource(R.drawable.btn_render_choice_on);
                btn_change_bg.setTextColor(context.getResources().getColor(R.color.qy_pink));
                bg.setVisibility(View.VISIBLE);
                btn_lj.setBackgroundResource(R.drawable.btn_render_choice);
                btn_lj.setTextColor(Color.WHITE);
                lj.setVisibility(View.GONE);
                btn_sty.setBackgroundResource(R.drawable.btn_render_choice);
                btn_sty.setTextColor(Color.WHITE);
                sty.setVisibility(View.GONE);
            }
        });

        btn_lj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_lj.setBackgroundResource(R.drawable.btn_render_choice_on);
                btn_lj.setTextColor(context.getResources().getColor(R.color.qy_pink));
                lj.setVisibility(View.VISIBLE);
                btn_change_bg.setBackgroundResource(R.drawable.btn_render_choice);
                btn_change_bg.setTextColor(Color.WHITE);
                bg.setVisibility(View.GONE);
                btn_sty.setBackgroundResource(R.drawable.btn_render_choice);
                btn_sty.setTextColor(Color.WHITE);
                sty.setVisibility(View.GONE);
            }
        });

        btn_sty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_sty.setBackgroundResource(R.drawable.btn_render_choice_on);
                btn_sty.setTextColor(context.getResources().getColor(R.color.qy_pink));
                sty.setVisibility(View.VISIBLE);
                btn_change_bg.setBackgroundResource(R.drawable.btn_render_choice);
                btn_change_bg.setTextColor(Color.WHITE);
                bg.setVisibility(View.GONE);
                btn_lj.setBackgroundResource(R.drawable.btn_render_choice);
                btn_lj.setTextColor(Color.WHITE);
                lj.setVisibility(View.GONE);
            }
        });
    }

    private void init_img(Context context) {

        img0 = view.findViewById(R.id.img0);

        img0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bg_img = 0;
                down_process_img();
                img0.setBackgroundResource(R.drawable.render_on);
            }
        });

        img1 = view.findViewById(R.id.img1);

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bg_img = 1;down_process_img();img1.setBackgroundResource(R.drawable.render_on);
            }
        });

        img2 = view.findViewById(R.id.img2);
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bg_img = 2;
                down_process_img();
                img2.setBackgroundResource(R.drawable.render_on);
            }
        });

        img3 = view.findViewById(R.id.img3);
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bg_img = 3;down_process_img();img3.setBackgroundResource(R.drawable.render_on);
            }
        });

        img4 = view.findViewById(R.id.img4);
        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bg_img = 4;down_process_img();img4.setBackgroundResource(R.drawable.render_on);
            }
        });

        img5 = view.findViewById(R.id.img5);
        img5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bg_img = 5;down_process_img();img5.setBackgroundResource(R.drawable.render_on);
            }
        });

        img6 = view.findViewById(R.id.img6);
        img6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bg_img = 6;down_process_img();img6.setBackgroundResource(R.drawable.render_on);
            }
        });

        img7 = view.findViewById(R.id.img7);
        img7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bg_img = 7;down_process_img();img7.setBackgroundResource(R.drawable.render_on);
            }
        });

        img8 = view.findViewById(R.id.img8);
        img8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bg_img = 8;down_process_img();img8.setBackgroundResource(R.drawable.render_on);
            }
        });

        img9 = view.findViewById(R.id.img9);
        img9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bg_img = 9;down_process_img();img9.setBackgroundResource(R.drawable.render_on);
            }
        });

        img10 = view.findViewById(R.id.img10);
        img10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bg_img = 10;down_process_img();img10.setBackgroundResource(R.drawable.render_on);
            }
        });

        img11 = view.findViewById(R.id.img11);
        img11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bg_img = 11;down_process_img();img11.setBackgroundResource(R.drawable.render_on);
            }
        });

        img12 = view.findViewById(R.id.img12);
        img12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bg_img = 12;
                down_process_img();
                img12.setBackgroundResource(R.drawable.render_on);
            }
        });

        img_local = view.findViewById(R.id.img_local);
        img_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bg_img = -2;
                down_process_img();
                img_local.setBackgroundResource(R.drawable.render_on);
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                ((Activity)context).startActivityForResult(intent, 2);
            }
        });
        down_process_img();

        img0.setBackgroundResource(R.drawable.render_on);

    }

    private void init_lj(Context context) {

        lj0 = view.findViewById(R.id.lj0);

        lj0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lj_id = 0;
                down_process_lj();
                lj0.setBackgroundResource(R.drawable.render_on);
            }
        });

        lj1 = view.findViewById(R.id.lj1);

        lj1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lj_id = 1;down_process_lj();lj1.setBackgroundResource(R.drawable.render_on);
            }
        });

        lj2 = view.findViewById(R.id.lj2);
        lj2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lj_id = 2;
                down_process_lj();
                lj2.setBackgroundResource(R.drawable.render_on);
            }
        });

        lj3 = view.findViewById(R.id.lj3);
        lj3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lj_id = 3;down_process_lj();lj3.setBackgroundResource(R.drawable.render_on);
            }
        });

        lj4 = view.findViewById(R.id.lj4);
        lj4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lj_id = 4;down_process_lj();lj4.setBackgroundResource(R.drawable.render_on);
            }
        });

        lj5 = view.findViewById(R.id.lj5);
        lj5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lj_id = 5;down_process_lj();lj5.setBackgroundResource(R.drawable.render_on);
            }
        });

        lj6 = view.findViewById(R.id.lj6);
        lj6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lj_id = 6;down_process_lj();lj6.setBackgroundResource(R.drawable.render_on);
            }
        });

        lj7 = view.findViewById(R.id.lj7);
        lj7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lj_id = 7;down_process_lj();lj7.setBackgroundResource(R.drawable.render_on);
            }
        });

        lj8 = view.findViewById(R.id.lj8);
        lj8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lj_id = 8;down_process_lj();lj8.setBackgroundResource(R.drawable.render_on);
            }
        });

        lj9 = view.findViewById(R.id.lj9);
        lj9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lj_id = 9;down_process_lj();lj9.setBackgroundResource(R.drawable.render_on);
            }
        });

        lj10 = view.findViewById(R.id.lj10);
        lj10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lj_id = 10;down_process_lj();lj10.setBackgroundResource(R.drawable.render_on);
            }
        });
        
        down_process_lj();

        lj0.setBackgroundResource(R.drawable.render_on);

    }
    private void init_sty(Context context) {

        sty0 = view.findViewById(R.id.sty0);

        sty0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                style_img = 0;
                down_process_sty();
                sty0.setBackgroundResource(R.drawable.render_on);
            }
        });

        sty1 = view.findViewById(R.id.sty1);

        sty1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                style_img = 1;
                down_process_sty();
                sty1.setBackgroundResource(R.drawable.render_on);
            }
        });

        down_process_sty();

        sty0.setBackgroundResource(R.drawable.render_on);

    }

    private void down_process_img(){
        img0.setBackgroundResource(R.drawable.render_out);
        img1.setBackgroundResource(R.drawable.render_out);
        img2.setBackgroundResource(R.drawable.render_out);
        img3.setBackgroundResource(R.drawable.render_out);
        img4.setBackgroundResource(R.drawable.render_out);
        img5.setBackgroundResource(R.drawable.render_out);
        img6.setBackgroundResource(R.drawable.render_out);
        img7.setBackgroundResource(R.drawable.render_out);
        img8.setBackgroundResource(R.drawable.render_out);
        img9.setBackgroundResource(R.drawable.render_out);
        img10.setBackgroundResource(R.drawable.render_out);
        img11.setBackgroundResource(R.drawable.render_out);
        img12.setBackgroundResource(R.drawable.render_out);
        img_local.setBackgroundResource(R.drawable.render_out);
    }
    private void down_process_lj(){
        lj0.setBackgroundResource(R.drawable.render_out);
        lj1.setBackgroundResource(R.drawable.render_out);
        lj2.setBackgroundResource(R.drawable.render_out);
        lj3.setBackgroundResource(R.drawable.render_out);
        lj4.setBackgroundResource(R.drawable.render_out);
        lj5.setBackgroundResource(R.drawable.render_out);
        lj6.setBackgroundResource(R.drawable.render_out);
        lj7.setBackgroundResource(R.drawable.render_out);
        lj8.setBackgroundResource(R.drawable.render_out);
        lj9.setBackgroundResource(R.drawable.render_out);
        lj10.setBackgroundResource(R.drawable.render_out);
    }

    private void down_process_sty(){
        sty0.setBackgroundResource(R.drawable.render_out);
        sty1.setBackgroundResource(R.drawable.render_out);
    }

    public int[] getRenderParams(){
        return new int[]{bg_img, lj_id, style_img};
    }

//    protected  void getImageFromAlbum(Context context){
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//    }

    @Override
    public void onClick(View v) {

    }

}


