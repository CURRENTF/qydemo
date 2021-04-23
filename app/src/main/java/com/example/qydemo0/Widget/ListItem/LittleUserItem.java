package com.example.qydemo0.Widget.ListItem;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
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
import com.example.qydemo0.entry.Image;

import org.json.JSONException;
import org.json.JSONObject;

public class LittleUserItem extends LinearLayout implements View.OnClickListener{


    private Context mContext = null;
    private View mView = null;
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
            sign.setText(json.getString("intro"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        CancelFollow cancelFollow = new CancelFollow();
        cancelFollow.execute();
    }

    public void hideBtn(){
        btn.setVisibility(GONE);
    }


    class CancelFollow extends AsyncTask<String, Integer, Boolean>{
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
                btn.setText("关注");
            }
            else {
                Toast.makeText((Activity)mContext, "取消失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
