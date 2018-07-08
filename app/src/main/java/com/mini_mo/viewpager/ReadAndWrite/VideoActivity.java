package com.mini_mo.viewpager.ReadAndWrite;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import com.mini_mo.viewpager.R;

public class VideoActivity extends AppCompatActivity {

    VideoView vv;
    MediaController mc;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);

        mc = new MediaController(this);
        vv = (VideoView)findViewById(R.id.videoView);

        vv.setMediaController(mc);

        Intent intent = getIntent();
        String uri = intent.getStringExtra("video");

        vv.setVideoPath(uri);

    }

}
