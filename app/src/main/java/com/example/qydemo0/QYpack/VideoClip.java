package com.example.qydemo0.QYpack;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static android.telecom.DisconnectCause.LOCAL;
import static com.google.android.exoplayer2.scheduler.Requirements.NETWORK;

/**
 * Author: AlanWang4523.
 * Date: 17/3/21 11:40.
 * Mail: alanwang6584@gmail.com
 */

public class VideoClip {
    private static final String TAG = VideoClip.class.getSimpleName();

    /**
     * 裁剪视频
     * @param srcPath 需要裁剪的原视频路径
     * @param outPath 裁剪后的视频输出路径
     * @param startTimeMs 裁剪的起始时间
     * @param endTimeMs 裁剪的结束时间
     */
    public static void clip(String srcPath, String outPath, double startTimeMs, double endTimeMs) throws IOException, IllegalArgumentException {
        if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(outPath)) {
            throw new IllegalArgumentException("file path can't be null!!!!");
        }
        if (!(new File(srcPath).exists())) {
            throw new IllegalArgumentException("the source file is not exist!!!!");
        }
        if (startTimeMs >= endTimeMs) {
            throw new IllegalArgumentException("the startTimeMs is larger than endTimeMs!!!!");
        }
        Movie movie = MovieCreator.build(srcPath);
        List<Track> tracks = movie.getTracks();
        //移除旧的track
        movie.setTracks(new LinkedList<Track>());
        //处理的时间以秒为单位
        double startTime = startTimeMs/1000;
        double endTime = endTimeMs/1000;
        Log.d(TAG, "--->>>>startTimeMs = " + startTimeMs + "\n endTimeMs = " + endTimeMs + "\n tracks.size = " + tracks.size());
        //计算剪切时间，视频的采样间隔大，以视频为准
        for (Track track : tracks) {
            if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                startTime = correctTimeToSyncSample(track, startTime, false);
                endTime = correctTimeToSyncSample(track, endTime, true);
                if (track.getHandler().equals("vide")) {
                    break;
                }
            }
        }
        Log.d(TAG, "--->>>>startTime = " + startTime + "\n endTime = " + endTime);

        long currentSample;
        double currentTime;
        double lastTime;
        long startSample1;
        long endSample1;
        long delta;

        for (Track track : tracks) {
            currentSample = 0;
            currentTime = 0;
            lastTime = -1;
            startSample1 = -1;
            endSample1 = -1;

            //根据起始时间和截止时间获取起始sample和截止sample的位置
            for (int i = 0; i < track.getSampleDurations().length; i++) {
                delta = track.getSampleDurations()[i];
                if (currentTime > lastTime && currentTime <= startTime) {
                    startSample1 = currentSample;
                }
                if (currentTime > lastTime && currentTime <= endTime) {
                    endSample1 = currentSample;
                }
                lastTime = currentTime;
                currentTime += (double)delta / (double)track.getTrackMetaData().getTimescale();
                currentSample++;
            }
            Log.d(TAG, "track.getHandler() = " + track.getHandler() + "\n startSample1 = " + startSample1 + "\n endSample1 = " + endSample1);
            if (startSample1 <= 0 && endSample1 <= 0) {
                throw new RuntimeException("clip failed !!");
            }
            movie.addTrack(new CroppedTrack(track, startSample1, endSample1));// 添加截取的track
        }

        //合成视频mp4
        Container out = new DefaultMp4Builder().build(movie);
        FileOutputStream fos = new FileOutputStream(outPath);
        FileChannel fco = fos.getChannel();
        out.writeContainer(fco);
        fco.close();
        fos.close();
    }


    /**
     * 换算剪切时间
     * @param track
     * @param cutHere
     * @param next
     * @return
     */
    public static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];
            int index = Arrays.binarySearch(track.getSyncSamples(), currentSample + 1);
            if (index >= 0) {
                timeOfSyncSamples[index] = currentTime;
            }
            currentTime += ((double)delta / (double)track.getTrackMetaData().getTimescale());
            currentSample++;
        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }


    public Bitmap getCoverFromVideo(String url){

        long CoverTime = getDurationLong(url,LOCAL)/2;
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象
        File file=new File(url);//实例化File对象，文件路径为/storage/sdcard/Movies/music1.mp4
        if(file.exists()){
            mmr.setDataSource(file.getAbsolutePath());//设置数据源为该文件对象指定的绝对路径
            return mmr.getFrameAtTime(CoverTime * 1000);//获得视频第一帧的Bitmap对象
        }
        else{
            return null;
        }
    }

    public static List<Bitmap> getFromTime(String url){
        List<Bitmap> bitmaps = new ArrayList<>();
        MediaMetadataRetriever mmr=new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象
        File file=new File(url);//实例化File对象，文件路径为/storage/sdcard/Movies/music1.mp4
        if(file.exists()){
            long total_time = getDurationLong(url,LOCAL);
            mmr.setDataSource(file.getAbsolutePath());//设置数据源为该文件对象指定的绝对路径
            for(long i=0; i < total_time; i+=1000) {
                bitmaps.add(mmr.getFrameAtTime(i * 1000));//获得视频第一帧的Bitmap对象
            }
            return bitmaps;
        }
        else{
            return null;
        }
    }

    public static long getDurationLong(String url, int type){
        String duration = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //如果是网络路径
            if(type == NETWORK){
                retriever.setDataSource(url,new HashMap<String, String>());
            }else if(type == LOCAL){//如果是本地路径
                retriever.setDataSource(url);
            }
            duration = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception ex) {
            Log.d("nihao", "获取音频时长失败");
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                Log.d("nihao", "释放MediaMetadataRetriever资源失败");
            }
        }
        if(!TextUtils.isEmpty(duration)){
            return Long.parseLong(duration);
        }else{
            return 0;
        }
    }

}
