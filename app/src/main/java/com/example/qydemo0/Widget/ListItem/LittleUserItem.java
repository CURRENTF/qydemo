package com.example.qydemo0.Widget.ListItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.R;
import com.example.qydemo0.UserDetailActivity;
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;
import com.example.qydemo0.entry.Image;

import org.json.JSONException;
import org.json.JSONObject;

public class LittleUserItem extends LinearLayoutItem implements View.OnClickListener{


    private Context mContext = null;
    public View mView = null;
    ImageView avatar;
    TextView name, sign;
    Button btn;
    int uid;
    boolean in_cancel = false;

    public LittleUserItem(Context context) {
        super(context);
        mContext = context;
        initDf();
    }
    public LittleUserItem(ViewGroup p, Activity a){
        super(a);
        mContext = a;
        mView = LayoutInflater.from(p.getContext())
                .inflate(R.layout.little_user_item, p, false);
    }

    @Override
    public void fill(JSONObject json) {
        init(json);
    }

    private void initDf(){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.little_user_item, this, true);
    }

    public void init(JSONObject json){
        avatar = mView.findViewById(R.id.avatar);
        name = mView.findViewById(R.id.username);
        sign = mView.findViewById(R.id.sign);
        btn = mView.findViewById(R.id.cancel_follow);
        btn.setOnClickListener(this);

        try {
            uid = json.getInt("uid");
            name.setText(json.getString("username"));
            if(json.getString("img_url").equals("null")) json.put("img_url", Constant.mInstance.default_avatar);
            Img.roundImgUrl((Activity) mContext, avatar, json.getString("img_url"));
            sign.setText(json.getString("sign"));
            mView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass((Activity) mContext, UserDetailActivity.class);
                    try {
                        intent.putExtra("uid", json.getInt("uid"));
                        intent.putExtra("username", json.getString("username"));
                        intent.putExtra("sign", json.getString("sign"));
                        ((Activity)mContext).startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        CancelFollow cancelFollow = new CancelFollow((MyAppCompatActivity)mContext);
        cancelFollow.execute();
    }

    public void hideBtn(){
        btn.setVisibility(GONE);
    }


    class CancelFollow extends MyAsyncTask<String, Integer, Boolean> {
        protected CancelFollow(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            String[] data = {"target", "int", String.valueOf(uid)};
            return MsgProcess.checkMsg(htp.advanceMethod("DELETE",
                    GenerateJson.universeJson2(data), Constant.mInstance.follow_url,
                    "Authorization", GlobalVariable.mInstance.token), false, null);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
//                btn.setText("??????");
                mView.setVisibility(GONE);
            }
            else {
                Toast.makeText((Activity)mContext, "????????????", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
