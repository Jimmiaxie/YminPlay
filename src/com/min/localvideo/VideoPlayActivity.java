package com.min.localvideo;


import com.min.yminplay.R;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayActivity extends Activity implements OnPreparedListener {

	private VideoView videoview;
	private Uri uri;
	private static VideoPlayActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_videoplay);
		videoview=(VideoView) findViewById(R.id.videoview);
		instance = this;
		getActionBar().hide(); //隐藏Actionbar
		String path=getIntent().getStringExtra("videopath");
		uri=Uri.parse(path);
		videoview.setVideoURI(uri);
		videoview.setOnPreparedListener(this);
		videoview.setMediaController(new MediaController(this));		
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		videoview.start();
	}
}
