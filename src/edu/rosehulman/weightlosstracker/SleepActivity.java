package edu.rosehulman.weightlosstracker;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class SleepActivity extends Activity implements OnClickListener{
	
	private static final String SLEEP = "SLEEP";
	private Button mDoneButton;
	private Chronometer mChronometer;
	private Switch mSwitch;
	private TextView mSleepTextView;
	private long mTotalTime;
	private SleepTime mSleepTime;
	private boolean isChronometerRunning = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleep);
		
		mSleepTime = new SleepTime();
		mSleepTime.setDate((Date) getIntent().getExtras().get(MainActivity.KEY_SLEEP_DATE));
		mSleepTextView = (TextView)findViewById(R.id.total_hours_textview);
		setTimeText(getIntent().getIntExtra(MainActivity.KEY_SLEEP_HOURS, 0),
					getIntent().getIntExtra(MainActivity.KEY_SLEEP_MINUTES, 0), 
					getIntent().getIntExtra(MainActivity.KEY_SLEEP_SECONDS, 0));
		mDoneButton = (Button)findViewById(R.id.sleep_done_button);
		mDoneButton.setOnClickListener(this);
		mChronometer = (Chronometer)findViewById(R.id.chronometer);
		mSwitch = (Switch)findViewById(R.id.sleep_switch);
		mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton button, boolean checked) {
				if(checked) {
					mTotalTime = 0;
					mChronometer.setBase(SystemClock.elapsedRealtime());
					mChronometer.start();
					isChronometerRunning = true;
				}else {
					mChronometer.stop();
					mTotalTime = SystemClock.elapsedRealtime() - mChronometer.getBase();
					isChronometerRunning = false;
					setSleepTime();
					setTimeText(mSleepTime.getHours(), mSleepTime.getMinutes(), mSleepTime.getSeconds());
				}
				
			}


		});
		
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.sleep_done_button) {
			Intent returnIntent = new Intent();
			if(isChronometerRunning) {
				Log.d(SLEEP, "Exiting SleepActivity with chronometer counting");
				//this.moveTaskToBack(true);
				setResult(MainActivity.RESULT_CODE_SLEEP_IN_PROGRESS);
				//finish();
				

			}
			else {
				Log.d(SLEEP, "Exiting SleepActivity with chronometer stopped");

				returnIntent.putExtra(MainActivity.KEY_SLEEP_HOURS, mSleepTime.getHours());
				returnIntent.putExtra(MainActivity.KEY_SLEEP_MINUTES, mSleepTime.getMinutes());
				returnIntent.putExtra(MainActivity.KEY_SLEEP_SECONDS, mSleepTime.getSeconds());
				setResult(MainActivity.RESULT_CODE_SLEEP_FINISHED, returnIntent);
				finish();
			}
			
			Log.d(SLEEP, "About to return from SleepActivity");
						
		}
		
		Log.d(SLEEP, "Returning from SleepActivity's OnClick method");
	}
	
	@Override
	public void onBackPressed() {
		this.moveTaskToBack(true);
	}
	
	private void setTimeText(int hours, int minutes, int seconds) {
		if( (hours == 0) && (minutes == 0) && (seconds == 0)) {
			mSleepTextView.setText(R.string.total_hours_text_empty);
		} else {
			mSleepTextView.setText(String.format(getResources().getString(R.string.total_hours_text_format), 
					hours, minutes, seconds));
		}
	}
	
	private void setSleepTime() {
		long remainingTime = mTotalTime;
		int hours = (int) Math.floor((double) TimeUnit.MILLISECONDS.toHours(mTotalTime));
		remainingTime -= hours * 60 * 60 * 1000;
		int minutes = (int) Math.floor((double) TimeUnit.MILLISECONDS.toMinutes(remainingTime));
		remainingTime -= minutes * 60 * 1000;
		int seconds = (int) (double) TimeUnit.MILLISECONDS.toSeconds(remainingTime);
		
		mSleepTime.setHours(hours);
		mSleepTime.setMinutes(minutes);
		mSleepTime.setSeconds(seconds);
	}
}
