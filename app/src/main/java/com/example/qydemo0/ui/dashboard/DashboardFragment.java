package com.example.qydemo0.ui.dashboard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.R;

import org.json.JSONException;
import org.json.JSONObject;

public class DashboardFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson == null){
            GetUserInfo g = new GetUserInfo();
            g.execute();
        }
        else reWriteInfo(GlobalVariable.mInstance.fragmentDataForMain.userInfoJson);
    }

    void reWriteInfo(JSONObject json){
        GlobalVariable.mInstance.fragmentDataForMain.userInfoJson = json;
        // 设置头像
        ImageView userAvatar = getActivity().findViewById(R.id.user_avatar);
        try {
            json.put("img_url", "https://file.yhf2000.cn/img/defult4.jpeg");
            Glide.with(getActivity())
                    .load(json.getString("img_url"))
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(userAvatar);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class GetUserInfo extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return htp.advanceGet(Constant.mInstance.userInfo_url, "Authorization", GlobalVariable.mInstance.token);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("hjtGetUserInfo", s);
            JSONObject json = MsgProcess.msgProcess(s);
            if(json != null){
                reWriteInfo(json);
            }
        }
    }

    

}