package com.example.qydemo0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(getBaseContext()).build();
        PlayerView p = findViewById(R.id.player);
        p.setPlayer(simpleExoPlayer);

        // Build the media item.
        Log.e("hjt", Environment.getExternalStorageState());
        MediaItem mediaItem = MediaItem.fromUri("/sdcard/video/0.mp4");
        // Set the media item to be played.
        simpleExoPlayer.setMediaItem(mediaItem);
        // Prepare the player.
        simpleExoPlayer.prepare();
        // Start the playback.
        simpleExoPlayer.play();

    }


}