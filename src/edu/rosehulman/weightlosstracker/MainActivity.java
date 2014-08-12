package edu.rosehulman.weightlosstracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private Button mStartStopButton;
	private Button mDailyButton;
	private Button mChallengeButton;
	private Button mInspireButton;
	private TextView mWelcomeText;
	private Date endDate;
	private int goalLoss;
	protected ArrayList<SleepTime> mSleepTimes;
	private Button mSleepTrackerButton;
	private ImageView mCameraImageView;

	private static final int REQUEST_CODE_SLEEP_TRACKER = 1;
	public static final int RESULT_CODE_SLEEP_IN_PROGRESS = 101;
	public static final int RESULT_CODE_SLEEP_FINISHED = 102;

	private static final int INITIALIZE_DIALOG_ID = 0;
	private static final int DAILY_DIALOG_ID = 1;
	private static final int CHALLENGE_DIALOG_ID = 2;
	private static final int INSPIRE_DIALOG_ID = 3;

	// Log filter values
	private static final String MAIN = "MAIN";

	/*
	 * Keys for intents
	 */
	// Sleep Activity
	public static final String KEY_SLEEP_DATE = "KEY_SLEEP_DATE";
	public static final String KEY_SLEEP_HOURS = "KEY_SLEEP_HOURS";
	public static final String KEY_SLEEP_MINUTES = "KEY_SLEEP_MINUTES";
	public static final String KEY_SLEEP_SECONDS = "KEY_SLEEP_SECONDS";
	public static final String KEY_IS_RUNNING = "KEY_IS_RUNNING";
	private static final String KEY_SLEEP_TIME = "KEY_SLEEP_TIME";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Resources res = getResources();

		this.mWelcomeText = (TextView) findViewById(R.id.welcome_label);
		setWelcomeText("");
		this.mStartStopButton = (Button) findViewById(R.id.main_start_end_button);
		this.mStartStopButton.setOnClickListener(this);
		this.mDailyButton = (Button) findViewById(R.id.main_daily_button);
		this.mDailyButton.setOnClickListener(this);
		this.mChallengeButton = (Button) findViewById(R.id.main_challenge_button);
		this.mChallengeButton.setOnClickListener(this);
		this.mSleepTrackerButton = (Button) findViewById(R.id.main_track_sleep_button);
		this.mSleepTrackerButton.setOnClickListener(this);
		this.mInspireButton = (Button) findViewById(R.id.main_inspire_button);
		this.mInspireButton.setOnClickListener(this);
		this.mCameraImageView = (ImageView) findViewById(R.id.camera_image_view);

		this.mSleepTimes = new ArrayList<SleepTime>();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_start_end_button:
			Log.d(MAIN, "Initialize button clicked");
			// Intent initialize = new Intent(this, Initialize.class);
			// startActivity(initialize);
			showDialog(INITIALIZE_DIALOG_ID);
			break;
		case R.id.camera_image_view:
			Camera camera = Camera.open();
			Parameters cameraParams = camera.getParameters();
			// TODO: Implement camera
			camera.startPreview();
			break;
		case R.id.main_daily_button:
			Log.d(MAIN, "Daily Tasks button clicked");
			// Intent daily = new Intent(this, DailyUpdate.class);
			// startActivity(daily);
			showDialog(DAILY_DIALOG_ID);
			break;
		case R.id.main_challenge_button:
			Log.d(MAIN, "Challenge Activity button clicked");
			// Intent challenge = new Intent(this, RandomChallenge.class);
			// startActivity(challenge);
			showDialog(CHALLENGE_DIALOG_ID);
			break;
		case R.id.main_inspire_button:
			Log.d(MAIN, "Be Inspired button clicked");
			Intent inspireIntent = new Intent(this, RandomInspiration.class);
			startActivity(inspireIntent);
			break;
		case R.id.main_track_sleep_button:
			Log.d(MAIN, "Track Sleep button clicked");
			Intent sleepIntent = new Intent(this, SleepActivity.class);
			Date date = new Date();
			sleepIntent.putExtra(KEY_SLEEP_DATE, date);

			int hours = 0,
			minutes = 0,
			seconds = 0;
			if (!mSleepTimes.isEmpty()) {
				int lastIndex = mSleepTimes.size() - 1;
				hours = mSleepTimes.get(lastIndex).getHours();
				minutes = mSleepTimes.get(lastIndex).getMinutes();
				seconds = mSleepTimes.get(lastIndex).getSeconds();
			}
			sleepIntent.putExtra(KEY_SLEEP_HOURS, hours);
			sleepIntent.putExtra(KEY_SLEEP_MINUTES, minutes);
			sleepIntent.putExtra(KEY_SLEEP_SECONDS, seconds);
			Log.d(MAIN, "Starting Sleep Activity");
			startActivityForResult(sleepIntent, REQUEST_CODE_SLEEP_TRACKER);
			break;
		default:
			Log.e(MAIN, "No ID matched what was clicked.");
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);
		final Dialog dialog = new Dialog(this);
		switch (id) {
		case INITIALIZE_DIALOG_ID:
			dialog.setContentView(R.layout.activity_initialize);
			dialog.setTitle(R.string.initial_title);
			final EditText nameText = (EditText) dialog
					.findViewById(R.id.dialogAddName);
			final EditText goalText = (EditText) dialog
					.findViewById(R.id.dialogAddGoalWeight);
			final DatePicker endDate = (DatePicker) dialog
					.findViewById(R.id.dialogEndDatePicker);
			final Button okButton = (Button) dialog
					.findViewById(R.id.initialize_this_button);
			final Button cancelButton = (Button) dialog
					.findViewById(R.id.cancel_initialization_button);

			okButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					int month = endDate.getMonth();
					int day = endDate.getDayOfMonth();
					int year = endDate.getYear();
					if (nameText.getText().length() > 0
							&& goalText.getText().length() > 0) {
						setWelcomeText(nameText.getText().toString() + "'s");
						setEndDate(new Date(year, month, day));
						setGoalLost(Integer.parseInt(goalText.getText()
								.toString()));
					}
					dialog.dismiss();
				}
			});

			cancelButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			break;
		case DAILY_DIALOG_ID:
			dialog.setContentView(R.layout.activity_daily);
			dialog.setTitle(R.string.daily_title);
			break;
		case CHALLENGE_DIALOG_ID:
			dialog.setContentView(R.layout.activity_challenge);
			dialog.setTitle(R.string.challenge_title);
			final TextView challengeView = (TextView) findViewById(R.id.challenge_textview);
			break;
		case INSPIRE_DIALOG_ID:
			dialog.setContentView(R.layout.activity_inspire);
			dialog.setTitle(R.string.inspire_title);
			final String[] quoteList = {
					"If it's to be, it's up to me!",
					"Take care of your body. It's the only place you have to live!",
					"The best revenge on a gain is an even biffer loss. Vengence can feel good!",
					"Persevere and you will win the prize.",
					"It comes down to a simple question: what do you want out of life, and what are you willing to do to get it?",
					"Rather than aiming for being perfect, just aim to be little bit better today than you were yesterday." };

			final TextView quoteView = (TextView) findViewById(R.id.inspire_textview);
			final Button newButton = (Button) findViewById(R.id.inspire_new_button);
			final Button closeButton = (Button) findViewById(R.id.inspire_close_button);

			newButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Resources res = getResources();
					Random r = new Random();
					String randomQuote = res.getString(R.string.inspire_quote,
							quoteList[r.nextInt(quoteList.length - 1)]);
					quoteView.setText(randomQuote);

				}
			});

			closeButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			break;
		default:
			break;
		}
		return dialog;
	}

	private void setWelcomeText(String name) {
		Resources res = getResources();
		this.mWelcomeText.setText(res.getString(R.string.welcome_text, name));
	}

	private void setEndDate(Date date) {
		Resources res = getResources();
		this.endDate = date;
	}

	private void setGoalLost(int goal) {
		this.goalLoss = goal;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		Log.d(MAIN, "Got an activity result (onActivityResult() )");

		if (requestCode == REQUEST_CODE_SLEEP_TRACKER) {
			if (resultCode == RESULT_CODE_SLEEP_FINISHED) {
				Log.d(MAIN, "Resulted from SleepActivity with sleep FINISHED");
				Date date = (Date) data.getExtras().get(KEY_SLEEP_DATE);
				int hours = data.getIntExtra(KEY_SLEEP_HOURS, 0);
				int minutes = data.getIntExtra(KEY_SLEEP_MINUTES, 0);
				int seconds = data.getIntExtra(KEY_SLEEP_SECONDS, 0);
				SleepTime sleepTime = new SleepTime(date, hours, minutes,
						seconds);
				this.mSleepTimes.add(sleepTime); // TODO: Implement comparable
			} else if (resultCode == RESULT_CODE_SLEEP_IN_PROGRESS) {
				Log.d(MAIN,
						"Resulted from SleepActivity with sleep IN PROGRESS");
			}
		}

	}
}
