package com.example.demovideoview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class PlayVideoActivity extends VideoActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d("TestLog", "We are creating the PlayVideoActivity");
		mStop.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (mVideoView != null) {
					mVideoView.stopPlayback();

					mDoesNextActivityStarted = true;
					// Intent homeIntent = new
					// Intent(PlayVideoActivity.this,HomeScreenActivity .class);
					// startActivity(homeIntent);
					PlayVideoActivity.this.finish();

				}
			}
		});

		mVideoView.setOnErrorListener(new OnErrorListener() {

			public boolean onError(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2) {
				// TODO Auto-generated method stub
				if (isNetworkAvailable(PlayVideoActivity.this)) {
					return false;
				} else {
					raiseDialog("", "ERROR_NETWORK_NOT_FOUND");
					return true;
				}

			}
		});

		mVideoView.setOnCompletionListener(new OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				Log.d("In OnComplete listerner  ", "");

				mCurrent = null;
				mVideoView.stopPlayback();
				mSeekBar.setProgress(0);
				mSeekBar.setSecondaryProgress(0);

				if (!mDoesNextActivityStarted) {
					Log.d(" In setOnCompletionListener ", "Calling Home Screen " + (!mDoesNextActivityStarted));
					mDoesNextActivityStarted = true;
					// Intent RegistrationIntent = new
					// Intent(PlayVideoActivity.this,HomeScreenActivity .class);
					// startActivity(RegistrationIntent);
					PlayVideoActivity.this.finish();
				}
			}
		});
		String url = getIntent().getStringExtra("PLAYVIDEO");
		Log.d("url to play", url);
		//startPlaying(url); //hami

	}

	private void raiseDialog(String title, String message) {
		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new AlertDialog.Builder(PlayVideoActivity.this);
		builder.setMessage(message);
		builder.setTitle(title);
		builder.setPositiveButton("TEXT_OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				try {
					mCurrent = null;
					mVideoView.stopPlayback();
					mSeekBar.setProgress(0);
					mSeekBar.setSecondaryProgress(0);
				} catch (Exception e) {
					// TODO: handle exception
				}

				mDoesNextActivityStarted = true;
				// Intent RegistrationIntent = new
				// Intent(PlayVideoActivity.this,HomeScreenActivity .class);
				// startActivity(RegistrationIntent);
				PlayVideoActivity.this.finish();

			}

		});
		builder.setCancelable(true);
		builder.show();

	}

	private boolean isNetworkAvailable(Context context) {
		boolean isNetworkAvailable = false;
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						isNetworkAvailable = true;
						return true;
					}
				}
			}
		}
		return false;
	}// isNetworkAvailable()

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

}
