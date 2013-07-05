package com.adnan.videoapp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.adnan.videoapp.paint.FingerPaint;
import com.adnan.videoapp.video.VideoPlayerActivity;

public class StartActivity extends Activity {

    private static final int CAPTURENATIVEVIDEO = 100;
    private static final int IMPORT_VIDEO = 101;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_start, menu);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch(item.getItemId()){
    	case R.id.menu_camera:
    		Toast.makeText(StartActivity.this, "Camera Selected.", Toast.LENGTH_SHORT).show();
    		Intent nativeCameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    		nativeCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
    		Uri saveVideoAt = getVideoStoreUri();
    		nativeCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, saveVideoAt);
    		startActivityForResult(nativeCameraIntent, CAPTURENATIVEVIDEO);
    		break;
    	
    	case R.id.menu_import:
    		Intent pickVideoIntent = new Intent(Intent.ACTION_PICK);
    		pickVideoIntent.setType("video/*");
    		
//    		pickVideoIntent.setAction(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
    		/* Not the get content as  It says what kind of data desired not the Uri of selected data */
    		
    		//pickVideoIntent.addCategory(Intent.CATEGORY_OPENABLE);
    		startActivityForResult(pickVideoIntent, IMPORT_VIDEO);
    		break;
    	
    	}
    	return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode){
    	case CAPTURENATIVEVIDEO:
    		if(resultCode == RESULT_OK){
    			Toast.makeText(this, "Video saved at " + data.getData(), Toast.LENGTH_SHORT).show();
    		}
    		else if(resultCode == RESULT_CANCELED){
    			Toast.makeText(this, "Video cancelled.", Toast.LENGTH_SHORT).show();
    		}else{
    			Toast.makeText(this, "Failed to capture.", Toast.LENGTH_SHORT).show();
    		}
    		break;
    		
    	case IMPORT_VIDEO:
    		if(resultCode == RESULT_OK){
    			System.out.println("Data " + data.getData());
    			Intent videoPlayerIntent = new Intent(this, FingerPaint.class);
    			videoPlayerIntent.putExtra("VIDEO_URI", data.getData().toString());
    			startActivity(videoPlayerIntent);
    		}
    		else if(resultCode == RESULT_CANCELED){
    			Toast.makeText(this, "Import cancelled.", Toast.LENGTH_SHORT).show();
    		}
    		break;
    	}
    }
    
    private Uri getVideoStoreUri(){
    	return Uri.fromFile(getVideoStoreFile());
    }
    
    private File getVideoStoreFile(){
    	File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), this.getString(this
    			.getApplicationInfo().labelRes));
    	Toast.makeText(this, mediaStorageDir.getPath(), Toast.LENGTH_SHORT).show();
    	if(! mediaStorageDir.exists()){
    		if(! mediaStorageDir.mkdir()){
    			Log.d("VideoApp", "Cannot create the file.");
    		}
    	}
    	
    	String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    	File videoPath = new File(mediaStorageDir + File.separator + "VID_" + timeStamp + ".mp4");
    	Log.i("VideoApp", "Saving video at " + videoPath);
    	
    	return videoPath;
    }
}
