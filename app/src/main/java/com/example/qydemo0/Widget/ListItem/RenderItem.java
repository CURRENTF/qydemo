package com.example.qydemo0.Widget.ListItem;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qydemo0.QYpack.Constant;
import com.example.qydemo0.QYpack.GlobalVariable;
import com.example.qydemo0.QYpack.Img;
import com.example.qydemo0.QYpack.MsgProcess;
import com.example.qydemo0.QYpack.QYrequest;
import com.example.qydemo0.QYpack.TimeTool;
import com.example.qydemo0.R;
import com.example.qydemo0.entry.Image;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;

public class RenderItem extends RelativeLayoutItem {

    Context context;
    View mView;
    String tid;
    int prog;


    public RenderItem(Context context) {
        super(context);
        this.context = context;
        initINFLATE();
    }

    public RenderItem(ViewGroup parent, Activity activity){
        super(activity);
        this.context = activity;
        mView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.render_item, this, true);
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
    TextView name, progress;
    ProgressBar progressBar, download_progress;

    public void init(JSONObject json){
        FileDownloader.setup(context);
        cover = mView.findViewById(R.id.cover);
        name = mView.findViewById(R.id.render_name);
        progress = mView.findViewById(R.id.progress);
        progressBar = mView.findViewById(R.id.render_progress_bar);
        download = mView.findViewById(R.id.download_btn);

        try {
            String cover_url = json.getJSONObject("cover").getString("url");
            Img.url2imgViewRoundRectangle(cover_url, cover, context, 20);
            name.setText(TimeTool.stringTime(json.getString("created_time")));
            progress.setText(json.getString("schedule"));
            String p = json.getString("schedule");
            progressBar.setProgress(Integer.parseInt(p.substring(0, p.length() - 1)));
            prog = Integer.parseInt(p.substring(0, p.length() - 1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Boolean is_finished(){
        return prog == 100;
    }

    private void setDownload(String http_url, String file_path){
        FileDownloader.getImpl().create(http_url)
                .setPath(file_path)
                .setListener(new FileDownloadListener() {

                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        download_progress.setProgress((int)((double)soFarBytes / totalBytes + 0.5));
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        Toast.makeText(getActivity(), "已下载至" + file_path, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                }).start();
    }

    void updateSelf(String htp, String loc){
        download.setVisibility(VISIBLE);
        download_progress.setVisibility(VISIBLE);
        download.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                setDownload(htp, loc);
            }
        });
    }

    public void refresh(){
        GetProgress getProgress = new GetProgress();
        getProgress.execute();
    }


    class GetProgress extends AsyncTask<String, Integer, JSONObject>{

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
                        for(int i = 0; i < Constant.mInstance.video_quality.length; i++){
                            if(video.has(Constant.mInstance.video_quality[i])){
                                updateSelf(video.getString(Constant.mInstance.video_quality[i]),
                                        Constant.mInstance.default_download_path);
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