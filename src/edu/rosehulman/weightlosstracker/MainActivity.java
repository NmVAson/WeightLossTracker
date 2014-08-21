package edu.rosehulman.weightlosstracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MainActivity extends Activity implements OnClickListener {

	private SQLHelper dbHelper;
	private SQLiteDatabase db;
	
	private String usrName;
	private int usrID;
	private ToggleButton mStartStopButton;
	private Button mDailyButton;
	private Button mChallengeButton;
	private Button mInspireButton;
	private Button mRecipeButton;
	private TextView mWelcomeText;
	protected ArrayList<SleepTime> mSleepTimes;
	private Button mSleepTrackerButton;
	private ImageView mCameraImageView;
	private int goalWeight;

	private static final int REQUEST_CODE_SLEEP_TRACKER = 1;
	public static final int RESULT_CODE_SLEEP_IN_PROGRESS = 101;
	public static final int RESULT_CODE_SLEEP_FINISHED = 102;

	private static final int INITIALIZE_DIALOG_ID = 0;
	private static final int DAILY_DIALOG_ID = 1;
	private static final int CHALLENGE_DIALOG_ID = 2;
	private static final int INSPIRE_DIALOG_ID = 3;
	private static final int RECIPE_DIALOG_ID = 4;
	
	private static final int TRUE = 1;
	private static final int FALSE = 0;

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
		
		dbHelper = new SQLHelper(this.getBaseContext());
		db = dbHelper.getWritableDatabase();

		this.mWelcomeText = (TextView) findViewById(R.id.welcome_label);
		this.mStartStopButton = (ToggleButton) findViewById(R.id.main_start_end_button);
		this.mStartStopButton.setOnClickListener(this);
		Cursor cursor = db.query("person", new String[] {"id","name","isdone"}, null, null, null, null, null, null);
		if(cursor.moveToLast()){
			usrName = cursor.getString(cursor.getColumnIndex("name"));
			usrID = cursor.getInt(cursor.getColumnIndex("id"));
			if(cursor.getInt(cursor.getColumnIndex("isdone")) == FALSE){
				mStartStopButton.setChecked(true);
			} else {
				mStartStopButton.setChecked(false);
			}
		} else {
			usrName = "";
			cursor.close();
			Log.d("dbug","ERR: Can't return person isDone info.");
		}
		setWelcomeText(usrName);
		this.mDailyButton = (Button) findViewById(R.id.main_daily_button);
		this.mDailyButton.setOnClickListener(this);
		this.mChallengeButton = (Button) findViewById(R.id.main_challenge_button);
		this.mChallengeButton.setOnClickListener(this);
		this.mSleepTrackerButton = (Button) findViewById(R.id.main_track_sleep_button);
		this.mSleepTrackerButton.setOnClickListener(this);
		this.mInspireButton = (Button) findViewById(R.id.main_inspire_button);
		this.mInspireButton.setOnClickListener(this);
		this.mRecipeButton = (Button) findViewById(R.id.main_recipe_button);
		this.mRecipeButton.setOnClickListener(this);
		this.mCameraImageView = (ImageView) findViewById(R.id.camera_image_view);

		this.mSleepTimes = new ArrayList<SleepTime>();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_start_end_button:
			ContentValues values = new ContentValues();
			int value;
			if(mStartStopButton.isChecked()){
				showDialog(INITIALIZE_DIALOG_ID);
				value = FALSE;
			} else {
				value = TRUE;
			}
			values.put("isdone", value);
			db.update("person", values, "id=?", new String[]{usrID+""});
			Log.d(MAIN, "Initialize button clicked");
			// Intent initialize = new Intent(this, Initialize.class);
			// startActivity(initialize);
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
			showDialog(INSPIRE_DIALOG_ID);
			break;
		case R.id.main_recipe_button:
			Log.d(MAIN, "Random Recipe button clicked");
			showDialog(RECIPE_DIALOG_ID);
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
			break;
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
			final ToggleButton gender = (ToggleButton) dialog.findViewById(R.id.dialogAddGender);
			final EditText nameText = (EditText) dialog.findViewById(R.id.dialogAddName);
			final EditText ageText = (EditText) dialog.findViewById(R.id.dialogAddAge);
			final EditText heightText = (EditText) dialog.findViewById(R.id.dialogAddHeight);
			final EditText weightText = (EditText) dialog.findViewById(R.id.dialogAddGoalWeight);
			final DatePicker endDate = (DatePicker) dialog.findViewById(R.id.dialogEndDatePicker);
			final Button okButton = (Button) dialog.findViewById(R.id.initialize_this_button);
			final Button cancelButton = (Button) dialog.findViewById(R.id.cancel_initialization_button);

			okButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String[] weights = weightText.getText().toString().split(",");
					String[] heights = heightText.getText().toString().split(",");
					int heightInInches = (Integer.parseInt(heights[0])*12)+Integer.parseInt(heights[1]);
					int weightStart = Integer.parseInt(weights[0]);
					int weightEnd = Integer.parseInt(weights[1]);
					int age = Integer.parseInt(ageText.getText().toString());
					
					int month = endDate.getMonth();
					int day = endDate.getDayOfMonth();
					int year = endDate.getYear();
					
					Calendar end = Calendar.getInstance();
					end.set(year, month, day);
					long calsPerDay;
					if (nameText.getText().length() > 0
							&& weightText.getText().length() > 0) {
						
						ContentValues values = new ContentValues();
						values.put("name",nameText.getText().toString());
						values.put("age", age);
						values.put("height", heightInInches);
						values.put("weightstart", weightStart);
						values.put("weightend", weightEnd);
						values.put("endD", day);
						values.put("endM", month);
						values.put("endY", year);
						if(gender.isChecked()){
							values.put("ismale", TRUE);
						} else {
							values.put("ismale", FALSE);
						}
						values.put("isdone", FALSE);
						long success = db.insert("person", null, values);
						if(success == -1){
							Log.d("dbug","ERR: Could not insert initial values into 'person' table");
						} else {
							Log.d("dbug","SUCCESS: New person initialized.");
						}
						
						setWelcomeText(nameText.getText().toString() + "'s");
						
						calsPerDay = calculateCalories(gender.isChecked(),heightInInches,age,weightStart,weightEnd, getDurration(end));
					}
					
					Resources res = getResources();
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					// Add the buttons
					builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface alertDialog, int id) {
					        	   alertDialog.dismiss();
					           }
					       });
					
					builder.setMessage(res.getString(R.string.calculation,calsPerDay));
					builder.setTitle(R.string.cals_title);
					// Create the AlertDialog
					AlertDialog alertDialog = builder.create();
					
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
			
			final EditText weightET = (EditText) dialog.findViewById(R.id.weight);
			final Button setButton = (Button) dialog.findViewById(R.id.weight_log_button);
			final Button foodButton = (Button) dialog.findViewById(R.id.food_log_button);
			final Button activityButton = (Button) dialog.findViewById(R.id.activity_log_button);
			final Button closeButton = (Button) dialog.findViewById(R.id.close_daily);
			final TextView calsTV = (TextView) dialog.findViewById(R.id.cals_left);
			final TextView daysTV = (TextView) dialog.findViewById(R.id.days_left);
			final ProgressBar progress = (ProgressBar) dialog.findViewById(R.id.progressBar);
			
			Calendar end = Calendar.getInstance();
			
			Cursor cursor = db.query("person", new String[] {"weight","endD","endM","endY"}, null, null, null, null, null, null);
			if(cursor.moveToLast()){
				goalWeight = cursor.getInt(cursor.getColumnIndex("weight"));
				int day = cursor.getInt(cursor.getColumnIndex("endD"));
				int month = cursor.getInt(cursor.getColumnIndex("endM"));
				int year = cursor.getInt(cursor.getColumnIndex("endY"));
				end.set(year, month, day);
			} else {
				cursor.close();
				Log.d("dbug","ERR: Can't return person info.");
			}
			Resources res = getResources();
			//calsTV.setText(res.getString(R.string.calories_left, name));
			daysTV.setText(res.getString(R.string.day_countdown, getDurration(end)));
			
			setButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Resources res = getResources();
					int weight = Integer.parseInt(weightET.getText().toString());
					int percent = ((weight*100)/goalWeight);
					progress.setProgress(percent);
					String date = dateToString(Calendar.getInstance());
					ContentValues values = new ContentValues();
					long success;
					if(setButton.getText() == res.getString(R.string.weight_log_submit)){
						values.put("date", date);
						values.put("weight", weight);
						success = db.insert("weight", null, values);
						setButton.setText(res.getString(R.string.weight_log_update));
					} else {
						values.put("weight", weight);
						success = db.update("weight", values, "date=?", new String[] {date});
					}
					
					if(success == -1){
						Log.d("dbug","ERR: Could not insert/update weight into 'weight' table");
					} else {
						Log.d("dbug","SUCCESS: Weight inserted/updated.");
					}
				}	
			});
			foodButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Resources res = getResources();
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					// Add the buttons
					 LayoutInflater inflater = getActivity().getLayoutInflater();

					    // Inflate and set the layout for the dialog
					    // Pass null as the parent view because its going in the dialog layout
					builder.setView(inflater.inflate(R.layout.dialog_food, null));
					
					EditText foodName = (EditText) builder.findViewById(R.id.food_name);
					Spinner spinner = (Spinner) builder.findViewById(R.id.food_type_spinner);
					EditText foodCals = (EditText) builder.findViewById(R.id.food_calories);
					
					builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface alertDialog, int id) {
					        	   String name = foodName.getText().toString();
					        	   String type = spinner.getSelectedItem().toString();
					        	   int cals = Integer.parseInt(foodCals.getText().toString());
					        	   String date = dateToString(Calendar.getInstance());
					        	   
					        	   CurrentValues values = new CurrentValues();
					        	   values.put("date",date);
					        	   values.put("name",name);
					        	   values.put("type",type);
					        	   values.put("cals",cals);
					        	   long success = db.insert("food", null, values);
									if(success == -1){
										Log.d("dbug","ERR: Could not insert into 'food' table");
									} else {
										Log.d("dbug","SUCCESS: New food logged.");
									}
					        	   alertDialog.dismiss();
					           }
					       });
					builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface alertDialog, int id) {
					        	   alertDialog.cancel();
					           }
							});
				           
					
					builder.setMessage(res.getString(R.string.calculation,calsPerDay));
					builder.setTitle(R.string.cals_title);
					
					
					// Create an ArrayAdapter using the string array and a default spinner layout
					ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
					        R.array.food_types, android.R.layout.simple_spinner_item);
					// Specify the layout to use when the list of choices appears
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					// Apply the adapter to the spinner
					spinner.setAdapter(adapter);
//					spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//						public void onItemSelected(AdapterView<?> parent, View view, 
//					            int pos, long id) {
//					        // An item was selected. You can retrieve the selected item using
//					        // parent.getItemAtPosition(pos)
//							
//					    }
//
//					    public void onNothingSelected(AdapterView<?> parent) {
//					        // Another interface callback
//					    }
//					});
					// Create the AlertDialog
					AlertDialog alertDialog = builder.create();
					
				}	
			});
			activityButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Resources res = getResources();
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					// Add the buttons
					 LayoutInflater inflater = getActivity().getLayoutInflater();

					    // Inflate and set the layout for the dialog
					    // Pass null as the parent view because its going in the dialog layout
					builder.setView(inflater.inflate(R.layout.dialog_activity, null));
					
					EditText actName = (EditText) builder.findViewById(R.id.activity_name);
					EditText actCals = (EditText) builder.findViewById(R.id.burned_calories);
					
					builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface alertDialog, int id) {
					        	   String name = actName.getText().toString();
					        	   int cals = Integer.parseInt(actCals.getText().toString());
					        	   String date = dateToString(Calendar.getInstance());
					        	   
					        	   CurrentValues values = new CurrentValues();
					        	   values.put("date",date);
					        	   values.put("name",name);
					        	   values.put("cals",cals);
					        	   long success = db.insert("activity", null, values);
									if(success == -1){
										Log.d("dbug","ERR: Could not insert into 'activity' table");
									} else {
										Log.d("dbug","SUCCESS: New activity logged.");
									}
					        	   alertDialog.dismiss();
					           }
					       });
					builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface alertDialog, int id) {
					        	   alertDialog.cancel();
					           }
							});
				           
					
					builder.setMessage(res.getString(R.string.calculation,calsPerDay));
					builder.setTitle(R.string.cals_title);
					
				
					// Create the AlertDialog
					AlertDialog alertDialog = builder.create();
					
				}	
			});
			closeButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}	
			});
		
			
			break;
		case CHALLENGE_DIALOG_ID:
			dialog.setContentView(R.layout.activity_challenge);
			dialog.setTitle(R.string.challenge_title);
			final TextView challengeView = (TextView) dialog.findViewById(R.id.challenge_textview);
			final Button newActButton = (Button) dialog.findViewById(R.id.challenge_new_button);
			final Button closeActButton = (Button) dialog.findViewById(R.id.challenge_close_button);

			newActButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Random r = new Random();
					int id = r.nextInt(dbHelper.getNumActivities()) + dbHelper.getNumQuotes();
					String activity = null;
					Cursor cursor = db.query("randoms", new String[] {"text"}, "type='activity' AND id=?", new String[] {id+""}, null, null, null, null);
					if(cursor.moveToFirst()){
						do{
							activity = cursor.getString(cursor.getColumnIndex("text"));
						} while  (cursor.moveToNext());
					} else {
						cursor.close();
						Log.d("dbug","ERR: Can't return activity quote.");
					}
					challengeView.setText(activity);
				}
			});

			closeActButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			break;
		case INSPIRE_DIALOG_ID:
			dialog.setContentView(R.layout.activity_inspire);
			dialog.setTitle(R.string.inspire_title);
	
			final TextView quoteView = (TextView) dialog.findViewById(R.id.inspire_textview);
			final Button newInspireButton = (Button) dialog.findViewById(R.id.inspire_new_button);
			final Button closeInspireButton = (Button) dialog.findViewById(R.id.inspire_close_button);

			newInspireButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Random r = new Random();
					int id = r.nextInt(dbHelper.getNumQuotes());
					String quote = null;
					Cursor cursor = db.query("randoms", new String[] {"text"}, "type='quote' AND id=?", new String[] {id+""}, null, null, null, null);
					if(cursor.moveToFirst()){
						do{
							quote = cursor.getString(cursor.getColumnIndex("text"));
						} while  (cursor.moveToNext());
					} else {
						cursor.close();
						Log.d("dbug","ERR: Can't return random quote.");
					}
					quoteView.setText(quote);
				}
			});

			closeInspireButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			break;
		case RECIPE_DIALOG_ID:
			dialog.setContentView(R.layout.activity_recipe);
			dialog.setTitle(R.string.recipe_title);
			final WebView page = (WebView) dialog.findViewById(R.id.wb_webview);
			final Button newRecipeButton = (Button) dialog.findViewById(R.id.recipe_new_button);
			final Button closeRecipeButton = (Button) dialog.findViewById(R.id.recipe_close_button);

			newRecipeButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Random r = new Random();
					int id = r.nextInt(dbHelper.getNumActivities()) + dbHelper.getNumQuotes() + dbHelper.getNumActivities();
					String recipe = null;
					Cursor cursor = db.query("randoms", new String[] {"text"}, "type='recipe' AND id=?", new String[] {id+""}, null, null, null, null);
					if(cursor.moveToFirst()){
						do{
							recipe = cursor.getString(cursor.getColumnIndex("text"));
						} while  (cursor.moveToNext());
					} else {
						cursor.close();
						Log.d("dbug","ERR: Can't return activity quote.");
					}
					page.loadUrl(recipe);
				}
			});

			closeRecipeButton.setOnClickListener(new OnClickListener() {
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

	private int getDurration(Calendar endDate) {
		Calendar startDate = Calendar.getInstance();
		Calendar date = (Calendar) startDate.clone();  
		  int daysBetween = 0;  
		  while (date.before(endDate)) {  
		    date.add(Calendar.DAY_OF_MONTH, 1);  
		    daysBetween++;  
		  }
		return daysBetween;
	}
	
	private String dateToString(Calendar date){
		int day = date.getTime().getDate();
		int month = date.getTime().getMonth();
		int year = date.getTime().getYear();
		String stringDate = month + "/" + day + "/" + year;
		return stringDate;
	}
	
	private long calculateCalories(boolean isMale,int height,int age,int actualWeight,int goalWeight, int days){
		int natural;
		if(isMale){
			natural = Math.round(66 + (6.23*actualWeight) + (12.7*height) - (6.8*age));
		}else {
			natural = Math.round(655 + (4.35*actualWeight) + (4.7*height) - (4.7*age));
		}
		int calsBurnPerDay = Math.round((actualWeight - goalWeight)*(3500.0/days));
		return natural-calsBurnPerDay;
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
