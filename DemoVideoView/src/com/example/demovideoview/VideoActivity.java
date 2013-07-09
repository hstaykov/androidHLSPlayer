package com.example.demovideoview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import com.example.IptvPlayer.R;

public class VideoActivity extends Activity implements SeekBar.OnSeekBarChangeListener {


	private static final String TAG = "VideoViewDemo";

	protected int mDuration;
	protected VideoView mVideoView;
	protected FrameLayout mFrameLayout;
	protected ImageButton mPlay;
	protected ImageButton mFullScreen;
	protected ImageButton mStop;
	protected String mCurrent;
	private LinearLayout mControlsLinearLayout;
	protected ProgressBar mProgressBar;
	protected boolean mIsIntroVideo;
	private Timer mTimer;
	private TimerTask mTimerTask;
	protected SeekBar mSeekBar;
	protected boolean mDoesNextActivityStarted;

	private TimerTask mSeekbarTimerTask;
	private Timer mSeekBarTimer;
	private boolean mIsPrefetched;

	private TextView mCurrentDuration;
	private TextView mChannelName;
	private long mLastPOosition = -1;
	private long mCurrentPosition = -1;
	private int mLagCount = 0;
	
	private int mCurrentState = STATE_IDLE;
	
    // all possible internal states
    private static final int STATE_ERROR              = -1;
    private static final int STATE_IDLE               = 0;
    private static final int STATE_PREPARING          = 1;
    private static final int STATE_PREPARED           = 2;
    private static final int STATE_PLAYING            = 3;
    private static final int STATE_PAUSED             = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private TVChannel currentChannel;
    private int user_channel ;
    
    private boolean isFullscreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        
	    isFullscreen = false;
	    
		setContentView(R.layout.video);
		DBAdapter db = new DBAdapter(this);
		int x = db.getChannels().size();
		if( x == 0){
			fillWithTestData();
		}
		Log.d("Filled", "All In");
		showChannelsNames();
		updateFromPrefs();
		
		
		mVideoView = (VideoView) findViewById(R.id.video_view);
		mDuration = -1;

		mProgressBar = (ProgressBar) findViewById(R.id.progressbar_video);

		mControlsLinearLayout = (LinearLayout) findViewById(R.id.linearLayout1);
		mFrameLayout = (FrameLayout) findViewById(R.id.videoframelayout);
		mCurrentDuration = (TextView) findViewById(R.id.tvCurrentDuration);
		mChannelName = (TextView) findViewById(R.id.tvChannelName);

		
		mCurrentDuration.setText("00:00");
		mChannelName.setText(currentChannel.getName());
		mChannelName.setTextColor(Color.RED);

		mControlsLinearLayout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha));

		mPlay = (ImageButton) findViewById(R.id.play);
		mFullScreen = (ImageButton) findViewById(R.id.reset);
		mStop = (ImageButton) findViewById(R.id.stop);

		final Drawable playDrawable = getResources().getDrawable(R.drawable.play);
		final Drawable pauseDrawable = getResources().getDrawable(R.drawable.pause);

		mPlay.setImageDrawable(playDrawable);
		mVideoView.setOnPreparedListener(new OnPreparedListener() {

			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				Log.d("onPrepared", "mp.getDuration() : " + mp.getDuration());
				mIsPrefetched = true;
				// loadingScreenImageView.setVisibility(View.GONE);
			}
		});
		
		mVideoView.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mCurrentState = STATE_PLAYBACK_COMPLETED;
			}
		});

		mPlay.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {

				if (mVideoView != null && mVideoView.isPlaying()) {
					mPlay.setImageDrawable(playDrawable);
					mVideoView.pause();
					mCurrentState = STATE_PAUSED;
				} else {
					mPlay.setImageDrawable(pauseDrawable);
					playVideo();
				}

			}
		});
		
		

		mFullScreen.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				 makeFullscreen();
			}

			private void makeFullscreen() {
	             mSeekBar.setVisibility(View.GONE);
	             mPlay.setVisibility(View.GONE);
	             mStop.setVisibility(View.GONE);
	             isFullscreen = true;
	             mFullScreen.setVisibility(View.GONE);
	             mCurrentDuration.setVisibility(View.GONE);
	             mChannelName.setVisibility(View.GONE);
			}
		});

		mStop.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (mVideoView != null) {
					mCurrent = null;
					mVideoView.stopPlayback();
					mPlay.setImageDrawable(playDrawable);
					mCurrentState = STATE_IDLE;
					onSeekForward(mSeekBar, 10000);
				}
			}
		});
		mSeekBar = (SeekBar) findViewById(R.id.seekBar1);
		mSeekBar.setOnSeekBarChangeListener(this);

		mTimer = new Timer();
		mTimerTask = new TimerTask() {

			public void run() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					public void run() {
						// TODO Auto-generated method stub
						if (mVideoView != null) {
							mDuration = mVideoView.getDuration();

							int percent = mVideoView.getBufferPercentage();

//							Log.d("hami2000", "getCurrentPosition(): " + mVideoView.getCurrentPosition());

							// if(Constants.LOG)Log.d(" mVideoView.getBufferPercentage() : ",
							// ""+mVideoView.getBufferPercentage());

							if (mSeekBar.getSecondaryProgress() != 100 && percent != 100 && mDuration != -1) {
								mSeekBar.setSecondaryProgress(percent + 10);
								// mSeekBar.setSecondaryProgress(mTestSecondaryProgress++);
							}
						}
						// if(Constants.LOG)Log.d(" Intimer task before ifisTouchedOnceAgain : ",
						// ""+isTouchedOnceAgain);
						if (!mIsTouchedOnceAgain) {

							// setVisibilityOfControls(View.GONE);

						} else {

							mIsTouchedOnceAgain = false;
						}
					}
				});
			}
		};
		mTimer.scheduleAtFixedRate(mTimerTask, 2000, 3000);

		mSeekBarTimer = new Timer();
		mSeekbarTimerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (mVideoView != null) {
							// if(Constants.LOG)Log.d(" mVideoView.getDuration() : "+
							// mVideoView.getDuration(),
							// "  mVideoView.getBufferPercentage() : "+mVideoView.getBufferPercentage()+" mVideoView.getCurrentPosition() : "+mVideoView.getCurrentPosition());
							mDuration = mVideoView.getDuration();
							int percent = mVideoView.getBufferPercentage();

//							Log.d("hami", "getCurrentPosition(): " + mVideoView.getCurrentPosition());

							/* hami */
							// show loading when stay at current position
							/*
							 * mCurrentPosition =
							 * mVideoView.getCurrentPosition();
							 * mCurrentDuration.
							 * setText(TimeFormat.milisecondToHMS
							 * (mCurrentPosition));
							 * 
							 * if(mLastPOosition == mCurrentPosition){ if
							 * (mProgressBar != null &&
							 * mProgressBar.getVisibility() == View.GONE){
							 * mProgressBar.setVisibility(View.VISIBLE); } }
							 * else { mLastPOosition = mCurrentPosition; if
							 * (mProgressBar != null &&
							 * mProgressBar.getVisibility() == View.VISIBLE){
							 * mProgressBar.setVisibility(View.GONE); } }
							 */
							/* #hami */

							if (mVideoView.getCurrentPosition() != -1 && mDuration != -1) {

								mSeekBar.setSecondaryProgress(percent);
								// mSeekBar.setSecondaryProgress(mTestSecondaryProgress++);
								mSeekBar.setProgress((int) (((float) mVideoView.getCurrentPosition() / mDuration) * 100));
							}
						}
					}
				});
			}
		};
		mSeekBarTimer.scheduleAtFixedRate(mSeekbarTimerTask, 1000, 1000);

		Timer timerForLoading = new Timer();
		TimerTask timerTaskForLoading = new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						
						if(!(mCurrentState == STATE_PLAYBACK_COMPLETED || mCurrentState == STATE_PAUSED || mCurrentState == STATE_IDLE)) {
							// show loading when stay at current position
							mCurrentPosition = mVideoView.getCurrentPosition();
							mCurrentDuration.setText(TimeFormat.milisecondToHMS(mCurrentPosition));
	
							if (mLastPOosition == mCurrentPosition) {
								if (mProgressBar != null && mProgressBar.getVisibility() == View.GONE) {
									mProgressBar.setVisibility(View.VISIBLE);
									mChannelName.setText(currentChannel.getName());
									Log.e("lag", "Lag at :" + TimeFormat.milisecondToHMS(mCurrentPosition) + " " + mLagCount++ + " times");
								}
								mCurrentDuration.setTextColor(Color.RED);
								
							} else {
								mLastPOosition = mCurrentPosition;
								if (mProgressBar != null && mProgressBar.getVisibility() == View.VISIBLE) {
									mProgressBar.setVisibility(View.GONE);
								}
								mCurrentDuration.setTextColor(Color.GREEN);
								
							}
						}
					}
				});

			}
		};
		
		timerForLoading.scheduleAtFixedRate(timerTaskForLoading, 500, 1000);

	
	}
	catch(Exception e){
		Toast.makeText(VideoActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
	}
	}

	private void playVideo() {
		try {
			final String path = currentChannel.getURL();
			Log.d(TAG, "path: " + path);
			if (path == null || path.length() == 0) {
				Toast.makeText(VideoActivity.this, "Empty URL", Toast.LENGTH_LONG).show();

			} else {
				// loading
				mProgressBar.setVisibility(View.VISIBLE);

				// If the path has not changed, just start the media player
				if (path.equals(mCurrent) && mVideoView != null) {
					mVideoView.start();
					mVideoView.requestFocus();

					mCurrentState = STATE_PLAYING;
					// hide loading
					mProgressBar.setVisibility(View.GONE);

					return;
				}
				mCurrent = path;
				mVideoView.setVideoURI(Uri.parse(path));

				// mVideoView.setVideoPath(getDataSource(path));
				final Drawable playDrawable = getResources().getDrawable(R.drawable.play);
				mPlay.setImageDrawable(playDrawable);
				mVideoView.start();
				mVideoView.requestFocus();
				mCurrentState = STATE_PLAYING;
				mDuration = mVideoView.getDuration();

				Log.d("duration", "" + mDuration);
				// hide loading
				mProgressBar.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			Log.d(TAG, "error: " + e.getMessage(), e);
			if (mVideoView != null) {
				mVideoView.stopPlayback();
				mCurrentState = STATE_IDLE;
			}

		}
	}

	private String getDataSource(String path) {
		try {
			if (!URLUtil.isNetworkUrl(path)) {
				return path;
			} else {
				URL url = new URL(path);
				URLConnection cn = url.openConnection();
				cn.connect();
				InputStream stream = cn.getInputStream();
				if (stream == null)
					throw new RuntimeException("stream is null");
				File temp = File.createTempFile("mediaplayertmp", "dat");
				temp.deleteOnExit();
				String tempPath = temp.getAbsolutePath();
				FileOutputStream out = new FileOutputStream(temp);
				byte buf[] = new byte[128];
				do {
					int numread = stream.read(buf);
					if (numread <= 0)
						break;
					out.write(buf, 0, numread);
				} while (true);
				try {
					stream.close();
				} catch (IOException ex) {
					Log.e(TAG, "error: " + ex.getMessage(), ex);
				}
				return tempPath;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	boolean mIsTouchedOnceAgain = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		Log.d(TAG, "We toucheed the screen..");
		
		
		if(isFullscreen){
			 Log.d(TAG, "Showing buttons");
			 mSeekBar.setVisibility(View.VISIBLE);
             mPlay.setVisibility(View.VISIBLE);
             mStop.setVisibility(View.VISIBLE);
             mFullScreen.setVisibility(View.VISIBLE);
             mCurrentDuration.setVisibility(View.VISIBLE);
             mChannelName.setVisibility(View.VISIBLE);
             isFullscreen = false;
		}
		
		
		if (mControlsLinearLayout.getVisibility() == View.GONE) {

			setVisibilityOfControls(View.VISIBLE);

		}
		mIsTouchedOnceAgain = true;

		if (mIsPrefetched) {

			return super.onTouchEvent(event);
		} else {

			return false;
		}
	}

	private void setVisibilityOfControls(int visibility) {
		// TODO Auto-generated method stub
		mSeekBar.setVisibility(visibility);
		mControlsLinearLayout.setVisibility(visibility);
		mPlay.setVisibility(visibility);
		mFullScreen.setVisibility(visibility);
		mStop.setVisibility(visibility);

	}

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		/*
		 * if(mVideoView.getCurrentPosition()!=-1){ if(Constants.LOG)Log.d(
		 * "onProgressChanged : seekbar position changing to ",
		 * ""+((float)mVideoView.getCurrentPosition()/duration)*100);
		 * seekBar.setProgress
		 * ((int)(((float)mVideoView.getCurrentPosition()/duration)*100)); }
		 */
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		int progress = seekBar.getProgress();
		if (mDuration != 0 && mDuration != -1) {
			if (mProgressBar != null && mProgressBar.getVisibility() == View.GONE) {
				mProgressBar.setVisibility(View.VISIBLE);
			}
			
			Log.d("onStopTrackingTouch : Seeking to :", "" + (int) (mDuration * ((float) progress) / 100));
			mVideoView.seekTo((int) (mDuration * ((float) progress) / 100));
		}

	}
	
	public void onSeekForward(SeekBar seekBar, int addTime){
		if(mVideoView.canSeekForward()) {
			if (mDuration != 0 && mDuration != -1) {
				seekBar.setProgress(seekBar.getProgress() + 5); // 5 = tmp percent
				int progress = seekBar.getProgress();
				
				Log.d("onSeekForward : Seeking to :", "" + mDuration);
				mVideoView.seekTo((int) (mDuration * ((float) progress) / 100));
			}
		}
	}
	
	public void onSeekBackward(){
		if(mVideoView.canSeekBackward()) {
			if (mDuration != 0 && mDuration != -1) {
				Log.d("onSeekBackward : Seeking to :", "" + mDuration  + 10000);
				mVideoView.seekTo(mDuration  - 10000);
			}
		}
	}

	private void updateFromPrefs(){
    	SharedPreferences prefs = getSharedPreferences(PreferencesActivity.USER_PREFS, Activity.MODE_PRIVATE);
        user_channel = prefs.getInt(PreferencesActivity.USER_CHANNEL, 1);
        DBAdapter db = new DBAdapter(this);
        currentChannel = db.getChannelById(user_channel);        
        Log.d("Getting the channel", currentChannel.getName() + " ; " + currentChannel.getURL());
        playVideo();
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		
		updateFromPrefs();
		
	}
	
	   @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.ac_preferences, menu);
	        menu.add(0, 0, Menu.NONE, R.string.change_cannel);
	        return true;
	    }
	   
	   @Override
	   public boolean onOptionsItemSelected(MenuItem item){
		   super.onOptionsItemSelected(item);
		   switch(item.getItemId()){
			   case(0):{
				   Intent i = new Intent(this, PreferencesActivity.class);
				   startActivityForResult(i, 1);
				   return true;
			   }
		   }
		   return false;
	   }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	private void fillWithTestData(){
		DBAdapter db = new DBAdapter(this);
		db.addChannel(new TVChannel(0, "NASA", "http://nasahd-i.akamaihd.net/hls/live/203739/NASATV1_iOS_HD/Edge.m3u8" ));
//		db.addChannel(new TVChannel(0, "A Sports channel", "http://212.226.124.236/mtv3/smil:DR40.smil/chunklist.m3u8" ));
//		db.addChannel(new TVChannel(0, "Other Sports channel", "http://5.79.65.209:1935/base/jsc_3.stream/playlisy.m3u8" ));
		db.addChannel(new TVChannel(0, "Fight Channel", "http://spiinternational-i.akamaihd.net/hls/live/204308/FIGHTBOXHD_MT_HLS/once1200.m3u8" ));
		db.addChannel(new TVChannel(0, "RTL", "http://webtv-aarh-8.stofa.dk:80/187_01.m3u8" ));
		db.addChannel(new TVChannel(0, "Big Bunny file", "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4" ));
//		db.addChannel(new TVChannel(0, "Euro Sport", "rtmp://109.163.226.87/pull?testkey=3ece383457277526800eb94beffc859e/gba3emj" ));
		db.addChannel(new TVChannel(0, "Megalan NG", "http://iptv.megalan.bg/?plgen&type=m3u" ));
		db.addChannel(new TVChannel(0, "Video from pc", "http://192.168.0.104:8080" ));
		db.close();
	}
	
	private void showChannelsNames(){
		  DBAdapter db = new DBAdapter(this);
		    List<TVChannel> chanls = db.getChannels();
		    for(TVChannel s : chanls){
		    	Log.d("Prefs Check", s.getNumber() + " ; " + s.getName() + " ; " + s.getURL());
		    }
	}

}
