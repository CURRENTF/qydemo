package com.example.qydemo0.Widget;

import android.os.AsyncTask;

public abstract class MyAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>{

    protected MyAsyncTask(MyAppCompatActivity activity){
        super();
        activity.addObject(this);
    }
}