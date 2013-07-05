package com.adnan.videoapp.video;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.adnan.videoapp.R;

public class VideoPlayerActivity extends Activity implements OnPreparedListener, OnCompletionListener{
	
	VideoView mVideoPlayer;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_player_layout);
		System.out.println("Data:  " + getIntent().getExtras().getString("VIDEO_URI"));
		Uri videoUri = Uri.parse(getIntent().getExtras().getString("VIDEO_URI"));
		mVideoPlayer = (VideoView) findViewById(R.id.video_videoView);
		MediaController mc = new MediaController(getApplicationContext());
		mVideoPlayer.setMediaController(mc); /* To get controls for a MediaPlayer */
		mc.show();
		mVideoPlayer.setKeepScreenOn(true); /* Set screen on */
		mVideoPlayer.setVideoURI(videoUri);
		mVideoPlayer.setOnPreparedListener(this);
		mVideoPlayer.setOnCompletionListener(this);
		
		//mVideoPlayer.start();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		 if(mVideoPlayer.canSeekForward()) mVideoPlayer.seekTo(mVideoPlayer.getDuration()/5);
	        mVideoPlayer.start();		
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		this.finish();
	}

	

}
