package com.example.qydemo0.RegisterWays;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GenerateJson;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.R;

import org.json.JSONException;
import org.json.JSONObject;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link RegisterUsernameFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class RegisterUsernameFragment extends Fragment {

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public RegisterUsernameFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment RegisterUsernameFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static RegisterUsernameFragment newInstance(String param1, String param2) {
//        RegisterUsernameFragment fragment = new RegisterUsernameFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_username, container, false);
    }

    Constant C = new Constant();

    public void getRegisterTokenAndUid(JSONObject json){
        try {
            GlobalVariable.mInstance.registerToken = json.getString("token");
            GlobalVariable.mInstance.uid = json.getString("uid");
            GlobalVariable.mInstance.isRegisterTokenExisted = true;
            Log.d("hjt", GlobalVariable.mInstance.registerToken);
        } catch (JSONException e){
            Log.d("hjt", e.getMessage());
        }
    }

    public class Register implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            EditText et_username = getActivity().findViewById(R.id.edit_text_register_username);
            EditText et_password = getActivity().findViewById(R.id.edit_text_register_password);
            EditText et_phone = getActivity().findViewById(R.id.edit_text_register_phone);
            CharSequence username = et_username.getText();
            CharSequence password = et_password.getText();
            CharSequence phone = et_phone.getText();
            Log.d("hjt", username.toString());
            Log.d("hjt", password.toString());
            GenerateJson g = new GenerateJson();
            QYrequest htp = new QYrequest();
            String msg = htp.post(g.registerPostJson(username.toString(), password.toString(), phone.toString()), C.register_url);
            Log.d("hjt", msg);
            JSONObject json = MsgProcess.msgProcess(msg);
            if(json == null){
                Log.e("hjt", "registerNULL");
                return;
            }
            getRegisterTokenAndUid(json);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Button btn = getActivity().findViewById(R.id.button_register_username);
        btn.setOnClickListener(new Register());
    }
}