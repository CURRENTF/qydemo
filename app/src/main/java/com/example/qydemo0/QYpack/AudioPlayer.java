package com.example.qydemo0.QYpack;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.qydemo0.R;

import java.io.IOException;
import java.util.function.Consumer;

public class AudioPlayer {

    private int duration;
    private boolean isPlaying = false;

    private MediaPlayer mMediaPlayer;
    private Consumer<AudioPlayer> mOnCompletionListener;

    public AudioPlayer(Context context, int audio_source) throws IOException {
        //mMediaPlayer.setDataSource(targetFile);
        mMediaPlayer = (MediaPlayer) MediaPlayer.create(context, audio_source);
    }

    public void setOnCompletionListener(Consumer<AudioPlayer> consumer) {
        this.mOnCompletionListener = consumer;
    }

    public boolean start() {

        try {
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(mp -> mMediaPlayer.start());

            this.duration = mMediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();

            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        boolean result = (mMediaPlayer != null);
        this.isPlaying = result;

        return result;
    }

    public void stop() {
        this.isPlaying = false;
        this.duration = 0;

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void resume() {
        if (mMediaPlayer != null) {
            this.isPlaying = true;

            mMediaPlayer.start();
        }
    }

    public void pause() {
        this.isPlaying = false;

        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public boolean isRunning() {
        return (mMediaPlayer != null);
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public int getDuration() {
        if (mMediaPlayer == null) {
            return this.duration;
        }

        return mMediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        if (mMediaPlayer == null) {
            return 0;
        }

        return mMediaPlayer.getCurrentPosition();
    }

    public MediaPlayer getMediaPlayer() {
        return this.mMediaPlayer;
    }

}

