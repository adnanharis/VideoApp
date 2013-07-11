/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adnan.videoapp.video;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.adnan.videoapp.R;
import com.adnan.videoapp.paint.ColorPickerDialog;
import com.adnan.videoapp.paint.PaintView;

public class VideoPlayerActivity extends Activity
implements OnPreparedListener, OnCompletionListener{    

	VideoView mVideoPlayer;
	PaintView myView;
	ProgressBar videoProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.video_player_layout);
		Uri videoUri = Uri.parse(getIntent().getExtras().getString("VIDEO_URI"));
		mVideoPlayer = (VideoView) findViewById(R.id.videoplayer_videoView);
		mVideoPlayer.setKeepScreenOn(true); /* Set screen on */
		mVideoPlayer.setVideoURI(videoUri);
		mVideoPlayer.setOnPreparedListener(this);
		mVideoPlayer.setOnCompletionListener(this);

		myView = new PaintView(this);
		mPaint = myView.mPaint;
		addContentView(myView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		View mediaControllerView =  View.inflate(this, R.layout.media_controller, null);
		RelativeLayout.LayoutParams rParam =  new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
		addContentView(mediaControllerView.findViewById(R.id.mediacontroller_mediacontrollerLayout), rParam);

		videoProgressBar = (ProgressBar) mediaControllerView.findViewById(R.id.mediacontroller_progressBar);
		videoStatusThread = new Thread(new Runnable() {
			@Override
			public void run() {
				float pos;
				while(true){
					if(! mVideoPlayer.isPlaying())
						synchronized(videoStatusThread){
							try {
								videoStatusThread.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}						
						}

					pos = mVideoPlayer.getCurrentPosition() * 100 / (float)mVideoPlayer.getDuration();
					videoProgressBar.setProgress((int) (pos));
					// System.out.println("position " + mVideoPlayer.getCurrentPosition() + " " + mVideoPlayer.getDuration() + " " + pos);

					synchronized(this){
						try {
							this.wait(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}						
					}					
				}
			}
		});
		videoStatusThread.start();

		mediaControllerView.findViewById(R.id.mediacontroller_playpauseButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(! mVideoPlayer.isPlaying()){
					mVideoPlayer.start();
					synchronized(videoStatusThread){
						videoStatusThread.notifyAll();						
					}	
				}else{
					mVideoPlayer.pause();
				}
			}
		});

	}

	Thread videoStatusThread;

	@Override
	public void onPrepared(MediaPlayer mp) {
		mVideoPlayer.start();
		synchronized(videoStatusThread){
			videoStatusThread.notifyAll();						
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		videoProgressBar.setProgress(100);
	}

	private Paint 		mPaint;

	private static final int COLOR_MENU_ID = Menu.FIRST;
	private static final int ERASE_MENU_ID = Menu.FIRST + 1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
		menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mPaint.setXfermode(null);
		mPaint.setAlpha(0xFF);

		switch (item.getItemId()) {
		case COLOR_MENU_ID:
			new ColorPickerDialog(this, myView, mPaint.getColor()).show();
			return true;
		case ERASE_MENU_ID:
			mPaint.setXfermode(new PorterDuffXfermode(
					PorterDuff.Mode.CLEAR));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
