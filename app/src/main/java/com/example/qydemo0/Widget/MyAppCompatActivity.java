package com.example.qydemo0.Widget;

import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MyAppCompatActivity extends AppCompatActivity {

    protected List<MyAsyncTask> asyncTasks = new ArrayList<>();

    public void addObject(MyAsyncTask myAsyncTask){
        asyncTasks.add(myAsyncTask);
    }

    public void cancelAllTasks(){
        for(int i = 0; i < asyncTasks.size(); i++){
            MyAsyncTask t = asyncTasks.get(i);
            if(t != null && t.getStatus() == AsyncTask.Status.RUNNING){
                t.cancel(true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        cancelAllTasks();
        super.onDestroy();
    }
}
