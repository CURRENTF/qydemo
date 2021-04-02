package com.example.qydemo0.ui.home;

import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.qydemo0.R;

import java.util.ArrayList;
import java.util.Vector;

public class HomeViewModel extends ViewModel {

    public MutableLiveData<String> mText;
    public Vector<String> imgURL;
    HomeViewModel(){
        imgURL = new Vector<String>();
    }

}