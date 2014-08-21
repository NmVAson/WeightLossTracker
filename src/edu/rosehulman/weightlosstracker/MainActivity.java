package edu.rosehulman.weightlosstracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
	private Button mSummariesButton;
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
	private static final int SUMMARY_DIALOG_ID = 5;
	
	private static final int DATA_WEIGHT_POS = 0;
	private static final int DATA_ACTIVITY_POS = 1;
	private static final int DATA_SLEEP_POS = 2;
	private static final int DATA_FOOD_POS = 3;
	
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
		this.mSummariesButton = (Button) findViewById(R.id.main_summary_button);
		this.mSummariesButton.setOnClickListener(this);
		this.mCameraImageView = (ImageView) findViewById(R.id.camera_image_view);

		this.mSleepTimes = new ArrayList<SleepTime>();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_start_end_button:
			final ContentValues values = new ContentValues();
			if(mStartStopButton.isChecked()){
				showDialog(INITIALIZE_DIALOG_ID);
				values.put("isdone", FALSE);
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setMessage(R.string.are_you_sure)
		               .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   values.put("isdone", TRUE);
		                       dialog.dismiss();
		                   }
		               })
		               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   values.put("isdone", FALSE);
		                       dialog.cancel();
		                   }
		               });
		        // Create the AlertDialog object and return it
		        builder.create();
				
			}
			
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
		case R.id.main_summary_button:
			Log.d(MAIN, "Data Summaries button clicked");
			showDialog(SUMMARY_DIALOG_ID);
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
					long calsPerDay=0;
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
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					// Add the buttons
					builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface alertDialog, int id) {
					        	   alertDialog.dismiss();
					           }
					       });
					
					builder.setMessage(res.getString(R.string.calculation,calsPerDay));
					builder.setTitle(R.string.calc_title);
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
			
			int age = 0,height = 0,isMale = 0,actualWeight = 0;
			Cursor cursor = db.query("person", new String[] {"age","height","weightend","endD","endM","endY","ismale"}, null, null, null, null, null, null);
			if(cursor.moveToLast()){
				
				age = cursor.getInt(cursor.getColumnIndex("age"));
				height = cursor.getInt(cursor.getColumnIndex("height"));
				goalWeight = cursor.getInt(cursor.getColumnIndex("weightend"));
				int day = cursor.getInt(cursor.getColumnIndex("endD"));
				int month = cursor.getInt(cursor.getColumnIndex("endM"));
				int year = cursor.getInt(cursor.getColumnIndex("endY"));
				isMale = cursor.getInt(cursor.getColumnIndex("ismale"));
				
				end.set(year, month, day);
				
			} else {
				
				Log.d("dbug","ERR: Can't return person info.");
			}
			cursor.close();
			
			//GET CURRENT WEIGHT
			Cursor cursorWeight = db.query("weight", new String[] {"weight"}, null, null, null, null, null, null);
			if(cursorWeight.moveToLast()){
				actualWeight = cursorWeight.getInt(cursorWeight.getColumnIndex("weight"));
			} else {
				Log.d("dbug","ERR: Can't return weight info.");
			}
			cursorWeight.close();
			
			//GET TODAYS CALS CONSUMED
			Cursor cursorCals = db.rawQuery("SELECT SUM(cals) FROM food WHERE date="+dateToString(Calendar.getInstance()),null);
			int calsConsumed;
			if(cursorCals.moveToFirst()){
				calsConsumed = cursorCals.getInt(0);
			} else {
				calsConsumed = 0;
				Log.d("dbug","ERR: Can't return cals consumed.");
			}
			cursorCals.close();
			
			//GET TODAYS CALS BURNED
			Cursor cursorBurned = db.rawQuery("SELECT SUM(calsburned) FROM activity WHERE date="+dateToString(Calendar.getInstance()),null);
			int calsBurned;
			if(cursorBurned.moveToFirst()){
				calsBurned = cursorBurned.getInt(0);
			} else {
				calsBurned = 0;
				Log.d("dbug","ERR: Can't return cals burned.");
			}
			cursorBurned.close();
			
			int calsAllowed = (int) calculateCalories(isMale == TRUE, height, age, actualWeight, goalWeight, getDurration(end));
			int calsLeft = calsAllowed-calsConsumed+calsBurned;
			Resources res = getResources();
			calsTV.setText(res.getString(R.string.calories_left, calsLeft));
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
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					// Add the buttons
					 LayoutInflater inflater = MainActivity.this.getLayoutInflater();

					    // Inflate and set the layout for the dialog
					    // Pass null as the parent view because its going in the dialog layout
					View thisView = inflater.inflate(R.layout.dialog_food, null);
					builder.setView(thisView);
					
					final EditText foodName = (EditText) thisView.findViewById(R.id.food_name);
					final Spinner spinner = (Spinner) thisView.findViewById(R.id.food_type_spinner);
					final EditText foodCals = (EditText) thisView.findViewById(R.id.food_calories);
					
					builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface alertDialog, int id) {
					        	   String name = foodName.getText().toString();
					        	   String type = spinner.getSelectedItem().toString();
					        	   int cals = Integer.parseInt(foodCals.getText().toString());
					        	   String date = dateToString(Calendar.getInstance());
					        	   
					        	   ContentValues values = new ContentValues();
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
				           
					builder.setTitle(R.string.food_title);
					
					//SET UP THE SPINNER
					ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this,
					        R.array.food_types, android.R.layout.simple_spinner_item);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner.setAdapter(adapter);
					AlertDialog alertDialog = builder.create();
					
				}	
			});
			activityButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					// Add the buttons
					 LayoutInflater inflater = MainActivity.this.getLayoutInflater();

					    // Inflate and set the layout for the dialog
					    // Pass null as the parent view because its going in the dialog layout
					 View thisView = inflater.inflate(R.layout.dialog_activity, null);
					builder.setView(thisView);
					
					final EditText actName = (EditText) thisView.findViewById(R.id.activity_name);
					final EditText actCals = (EditText) thisView.findViewById(R.id.burned_calories);
					
					builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface alertDialog, int id) {
					        	   String name = actName.getText().toString();
					        	   int cals = Integer.parseInt(actCals.getText().toString());
					        	   String date = dateToString(Calendar.getInstance());
					        	   
					        	   ContentValues values = new ContentValues();
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
				           
					builder.setTitle(R.string.activity_title);
					
				
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
		case SUMMARY_DIALOG_ID:
			dialog.setContentView(R.layout.data_summary_layout);
			dialog.setTitle(R.string.summary_title);
			
			final Spinner spinner = (Spinner) thisView.findViewById(R.id.summary_type_spinner);
			
			//SET UP THE SPINNER
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this,
			        R.array.summaries, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
	                
					GraphViewData[] data;
					String message;
					switch(getLastVisiblePosition()){
					case DATA_WEIGHT_POS:
						Cursor cursor = db.query("weight", new String[] {"weight"}, null, null, null, null, null, null);
						int count = 0;
						if(cursor.moveToFirst()){
							do{
								int weight = cursor.getString(cursor.getColumnIndex("weight"));
								data.add(new GraphViewData(count,weight));
								count ++;
							} while  (cursor.moveToNext());
						} else {
							cursor.close();
							Log.d("dbug","ERR: Can't return weight data.");
						}
						break;
					case DATA_ACTIVITY_POS:
						Cursor cursor = db.rawQuery("SELECT date,SUM(calsburned) AS burned FROM activity GROUP BY date",null);
						int count = 0;
						if(cursor.moveToFirst()){
							do{
								int cals = cursor.getString(cursor.getColumnIndex("burned"));
								data.add(new GraphViewData(count,cals));
								count ++;
							} while  (cursor.moveToNext());
						} else {
							cursor.close();
							Log.d("dbug","ERR: Can't return activity data.");
						}
						break;
					case  DATA_SLEEP_POS:
						Cursor cursor = db.query("sleep", new String[] {"hr","min"}, null, null, null, null, null, null);
						int count = 0;
						if(cursor.moveToFirst()){
							do{
								int hour = cursor.getString(cursor.getColumnIndex("hr"));
								int min = cursor.getString(cursor.getColumnIndex("min"));
								double time = hour + (min/60);
								data.add(new GraphViewData(count,time));
								count ++;
							} while  (cursor.moveToNext());
						} else {
							cursor.close();
							Log.d("dbug","ERR: Can't return sleep data.");
						}
						break;
					case DATA_FOOD_POS:
						//select type, count(type) from table group by type;
						Cursor cursor = db.rawQuery("SELECT type,COUNT(type) AS count FROM food GROUP BY type",null);
						message = "Your diet breaks down as follows:\n";
						if(cursor.moveToFirst()){
							do{
								String type = cursor.getString(cursor.getColumnIndex("type"));
								int count = cursor.getString(cursor.getColumnIndex("count"));
								message.append(type+"   "+count+"\n");
							} while  (cursor.moveToNext());
						} else {
							cursor.close();
							Log.d("dbug","ERR: Can't return activity data.");
						}
						break;
					default:
						break;
					}
					
					LinearLayout layout = (LinearLayout) findViewById(R.id.data_summary_layout); 
					if(getLastVisiblePosition() != DATA_FOOD_POS){
						GraphViewSeries thisSeries = new GraphViewSeries(data);
						GraphView graphView = new LineGraphView(this, "Job Status Graph");  
						graphView.addSeries(thisSeries); 
						layout.addView(graphView);  
					} else {
						TextView summary = new TextView();
						summary.setText(message);
						layout.addView(summary);
					}
	            }

	            public void onNothingSelected(AdapterView<?> arg0) {
	                // do nothing
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
		long natural;
		if(isMale){
			natural = Math.round(66 + (6.23*actualWeight) + (12.7*height) - (6.8*age));
		}else {
			natural = Math.round(65 + (4.35*actualWeight) + (4.7*height) - (4.7*age));
		}
		long calsBurnPerDay = Math.round((actualWeight - goalWeight)*(3500.0/days));
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
				
				//SUBMIT TO DATABASE
				Calendar cal = Calendar.getInstance();
				cal.set(date.getYear(),date.getMonth(),date.getDate());
				ContentValues values = new ContentValues();
				values.put("date",dateToString(cal));
				values.put("hr",hours);
				values.put("min",minutes);
				values.put("sec",seconds);
				long success = db.insert("sleep", null, values);
				if(success == -1){
					Log.d("dbug","ERR: Could not insert into 'sleep' table");
				} else {
					Log.d("dbug","SUCCESS: New sleep logged.");
				}
				
			} else if (resultCode == RESULT_CODE_SLEEP_IN_PROGRESS) {
				Log.d(MAIN,
						"Resulted from SleepActivity with sleep IN PROGRESS");
			}
		}

	}
}
