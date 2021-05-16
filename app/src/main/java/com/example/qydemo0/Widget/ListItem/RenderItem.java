package com.example.qydemo0.Widget.ListItem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.download.library.DownloadImpl;
import com.download.library.DownloadListenerAdapter;
import com.download.library.Extra;
import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.TimeTool;
import com.example.qydemo0.R;
import com.example.qydemo0.RenderQueueActivity;
import com.example.qydemo0.Widget.MyAppCompatActivity;
import com.example.qydemo0.Widget.MyAsyncTask;
import com.example.qydemo0.entry.Image;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.Time;

public class RenderItem extends RelativeLayoutItem {

    Context context;
    public View mView;
    String tid;
    int prog;
    Activity ac;
    boolean render_finished = false;
//    boolean filled = false;


    public RenderItem(Context context) {
        super(context);
        this.context = context;
        ac = (Activity) context;
        initINFLATE();
    }

    public RenderItem(ViewGroup parent, Activity activity){
        super(activity);
        ac = activity;
        this.context = activity;
        mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.render_item, parent, false);
    }

    @Override
    public void fill(JSONObject json) {
        init(json);
    }

    private Activity getActivity(){
        return (Activity) context;
    }

    private void initINFLATE(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.render_item, this, true);
    }

    ImageView cover, download;
    TextView name, progress, time;
    ProgressBar progressBar, download_progress;

    public void init(JSONObject json){
        FileDownloader.setup(ac);
        cover = mView.findViewById(R.id.cover);
        name = mView.findViewById(R.id.render_name);
        time = mView.findViewById(R.id.render_time);
        progress = mView.findViewById(R.id.progress);
        progressBar = mView.findViewById(R.id.render_progress_bar);
        download = mView.findViewById(R.id.download_btn);
        download_progress = mView.findViewById(R.id.download_progress);
        try {
            String cover_url = json.getJSONObject("cover").getString("url");
            Img.url2imgViewRoundRectangle(cover_url, cover, context, 20);
            name.setText("进程："+json.getString("step"));
            time.setText("时间："+TimeTool.stringTime(json.getString("created_time")));
            progress.setText(json.getString("prog") + "%");
            String p = json.getString("prog");
            progressBar.setProgress(Integer.parseInt(p));
            prog = Integer.parseInt(p);
            boolean dashboard = json.getBoolean("dashboard");
            if(dashboard){
                onDashboard();
            }
            if(json.getInt("is_finish") == 1 && !dashboard){
                String video = json.getString("video");
                name.setVisibility(GONE);
                time.setTextSize(20);
                render_finished = true;
                updateSelf(video,
                        Constant.mInstance.default_download_path);
//                for(int i = 0; i < Constant.mInstance.video_quality.length; i++){
//                    if(video.has(Constant.mInstance.video_quality[i])){
//                        break;
//                    }
//                }
            }
//            refresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onDashboard(){
        mView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ac, RenderQueueActivity.class);
                ac.startActivity(intent);
            }
        });
    }

    public Boolean is_finished(){
        return prog == 100;
    }

    private void setDownload(String http_url, String file_path){
//        FileDownloader.getImpl().create(http_url)
//                .setPath(file_path)
//                .setListener(new FileDownloadListener() {
//
//                    @Override
//                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                    }
//
//                    @Override
//                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
//                        Log.d("hjt.download", "connected");
//                    }
//
//                    @Override
//                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                        Log.d("hjt.download.progress", String.valueOf((int)((double)soFarBytes / totalBytes + 0.5)));
//                        download_progress.setProgress((int)((double)soFarBytes / totalBytes + 0.5));
//                    }
//
//                    @Override
//                    protected void blockComplete(BaseDownloadTask task) {
//                    }
//
//                    @Override
//                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
//                    }
//
//                    @Override
//                    protected void completed(BaseDownloadTask task) {
//                        Toast.makeText(getActivity(), "已下载至" + file_path, Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                    }
//
//                    @Override
//                    protected void error(BaseDownloadTask task, Throwable e) {
//                    }
//
//                    @Override
//                    protected void warn(BaseDownloadTask task) {
//                    }
//                }).start();
        DownloadImpl.getInstance(ac)
                .with(ac.getApplicationContext())
                .target(new File(file_path, System.currentTimeMillis() + ".mp4"))
                .setUniquePath(false)
                .setForceDownload(true)
                .url(http_url)
                .enqueue(new DownloadListenerAdapter() {
                    @Override
                    public void onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra) {
                        super.onStart(url, userAgent, contentDisposition, mimetype, contentLength, extra);
                    }

                    @Override
                    public void onProgress(String url, long downloaded, long length, long usedTime) {
                        super.onProgress(url, downloaded, length, usedTime);
                        Log.i("hjt.down", " progress:" + downloaded + " url:" + url);
                        download_progress.setProgress((int)((double)downloaded / length * 100 + 0.5));
                    }

                    @Override
                    public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {
                        Log.i("hjt.down", " path:" + path + " url:" + url + " length:" + new File(path.getPath()).length());
                        Toast.makeText(ac, "下载完成", Toast.LENGTH_SHORT).show();
                        return super.onResult(throwable, path, url, extra);
                    }
                });

    }

    void updateSelf(String htp, String loc){
        download.setVisibility(VISIBLE);
        download_progress.setVisibility(VISIBLE);
        download_progress.setProgress(0);
        download.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                setDownload(htp, loc);
            }
        });
    }

    private void refresh(){
        Handler handler = new Handler(Looper.myLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                GetProgress getProgress = new GetProgress((MyAppCompatActivity) ac);
                getProgress.execute();
                if(!render_finished) handler.postDelayed(this, 10000);
            }
        };
        handler.post(runnable);
    }

    class GetProgress extends MyAsyncTask<String, Integer, JSONObject> {

        protected GetProgress(MyAppCompatActivity activity) {
            super(activity);
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            QYrequest htp = new QYrequest();
            return MsgProcess.msgProcess(htp.advanceGet(Constant.mInstance.render_progress_url + tid + "/",
                    "Authorization", GlobalVariable.mInstance.token), false, null);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if(json == null){
                Log.e("hjt.get.progress", tid+".");
            }
            else {
                try {
                    int up = prog;
                    prog = json.getInt("schedule");
                    String t = json.getString("schedule");
                    if(prog == 0) prog = Integer.parseInt(t.substring(0, t.length() - 1));
                    progressBar.setProgress(prog);
                    progress.setText(prog+"%");
                    tid = json.getString("tid");
                    if(prog == 100 && up < prog){
                        JSONObject video = json.getJSONObject("video");
                        video = video.getJSONObject("url");
                        render_finished = true;
                        for(int i = 0; i < Constant.mInstance.video_quality.length; i++){
                            if(video.has(Constant.mInstance.video_quality[i])){
                                updateSelf(video.getString(Constant.mInstance.video_quality[i]),
                                        Constant.mInstance.default_download_path);
                                break;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}