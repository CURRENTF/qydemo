package com.example.qydemo0.ui.home;

import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Vector;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ImageView> img;
    private Vector<MutableLiveData<ImageView>> mImg;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        img = new MutableLiveData<>();
        mImg = new Vector<MutableLiveData<ImageView>>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<ImageView> getImg() {
        return img;
    }
}