package com.example.qydemo0;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.File;
import java.io.FilenameFilter;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{

    protected SimpleExoPlayer player = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
    }

    @Override
    protected void onStart() {
        Button btn = findViewById(R.id.button_browse_file);
        btn.setOnClickListener(this);
        player = new SimpleExoPlayer.Builder(getBaseContext()).build();
        PlayerView p = findViewById(R.id.player_for_upload_video);
        p.setPlayer(player);
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_browse_file:
                launcher.launch(true);
                break;
        }

    }

    ActivityResultLauncher launcher = registerForActivityResult(new ResultContract(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if(result == null) return;
            ((TextView)findViewById(R.id.edit_text_file_url)).setText(getResources().getString(R.string.get_file_now));
            MediaItem mediaItem = MediaItem.fromUri(result);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }
    });

    class ResultContract extends ActivityResultContract<Boolean, Uri> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Boolean input) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
            return intent;
        }

        @Override
        public Uri parseResult(int resultCode, @Nullable Intent intent) {
            if (intent != null) {
                return intent.getData();
            }
            else {
                Toast.makeText(UploadActivity.this, "未选择视频", Toast.LENGTH_SHORT).show();
                Log.e("hjtGetVideoNull", "oh");
                return null;
            }
        }
    }
}