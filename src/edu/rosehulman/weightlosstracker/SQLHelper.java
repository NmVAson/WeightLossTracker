package edu.rosehulman.weightlosstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLHelper extends SQLiteOpenHelper {

	  private static final String DATABASE_NAME = "wltracker.db";
	  private static final int DATABASE_VERSION = 3;
	  private static final String[] quoteList = {
			"If it's to be, it's up to me!",
			"Take care of your body. It's the only place you have to live!",
			"The best revenge on a gain is an even biffer loss. Vengence can feel good!",
			"Persevere and you will win the prize.",
			"It comes down to a simple question: what do you want out of life, and what are you willing to do to get it?",
			"Rather than aiming for being perfect, just aim to be little bit better today than you were yesterday." 
	  };
	  private static final String[] activityList = {
		  "Run outside",
		  "Go on a hike",
		  "Take a yoga class",
		  "Take a dance class",
		  "Bike around the park",
		  "Take a martial arts class"
	  };
	  private static final String[] recipeList = {
		  "http://www.foodnetwork.com/recipes/food-network-kitchens/poached-ginger-chicken-recipe.html",
		  "http://www.foodnetwork.com/recipes/ellie-krieger/sloppy-joes-recipe.html",
		  "http://www.foodnetwork.com/recipes/food-network-kitchens/soy-glazed-salmon-with-cucumber-avocado-salad-recipe.html",
		  "http://www.foodnetwork.com/recipes/food-network-kitchens/20-minute-shrimp-and-couscous-with-yogurt-hummus-sauce-recipe.html",
		  "http://www.foodnetwork.com/recipes/food-network-kitchens/spicy-pasta-with-tilapia-recipe.html",
		  "http://www.foodnetwork.com/recipes/ellie-krieger/shrimp-and-snow-pea-salad-recipe.html"
	  };

	  
	public SQLHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS person (id       	INTEGER PRIMARY KEY AUTOINCREMENT,"
													  +"name		TEXT,"
													  +"age			INTEGER,"
													  +"height		INTEGER,"
													  +"weightstart	INTEGER,"
													  +"weightend	INTEGER,"
													  +"endD		INTEGER,"
													  +"endM		INTEGER,"
													  +"endY		INTEGER,"
													  +"ismale		INTEGER,"
													  +"isdone		INTEGER"
													  +");");
		
		Log.d("dbug","Created 'person'");
		db.execSQL("CREATE TABLE IF NOT EXISTS weight (id       INTEGER PRIMARY KEY AUTOINCREMENT,"
													  +"date	TEXT,"
													  +"weight	INTEGER"
													  +");");
		Log.d("dbug","Created 'weight'");
		db.execSQL("CREATE TABLE IF NOT EXISTS food (id         INTEGER PRIMARY KEY AUTOINCREMENT,"
													+"date		TEXT,"
													+"name		TEXT,"
													+"type		TEXT,"
													+"cals		INTEGER"
													+");");
		Log.d("dbug","Created 'food'");
		db.execSQL("CREATE TABLE IF NOT EXISTS activity (id         INTEGER PRIMARY KEY AUTOINCREMENT,"
														+"date		TEXT,"
														+"name		TEXT,"
														+"calsburned	INTEGER"
														+");");
		Log.d("dbug","Created 'activity'");
		db.execSQL("CREATE TABLE IF NOT EXISTS sleep (id        INTEGER PRIMARY KEY AUTOINCREMENT,"
													+"date		TEXT,"
												    +"sleephr	INTEGER,"
												    +"sleepmin	INTEGER,"
												    +"wakehr	INTEGER,"
												    +"wakemin	INTEGER,"
												    +"hrs		INTEGER"
												    +");");
		Log.d("dbug","Created 'sleep'");
		db.execSQL("CREATE TABLE IF NOT EXISTS randoms (id			INTEGER PRIMARY KEY AUTOINCREMENT,"
													   +"type		TEXT,"
													   +"text		TEXT"
													   +");");
		Log.d("dbug","Created 'randoms'");
		for(String quote:quoteList){
			ContentValues values = new ContentValues();
			values.put("type","quote");
			values.put("text", quote);
			if(db.insert("randoms", null, values) == -1){
				Log.d("DB ERR","Could not load quotes into DB");
			} else {
				Log.d("DB Success","Quotes loaded into DB");
			}
		}
		for(String activity:activityList){
			ContentValues values = new ContentValues();
			values.put("type","activity");
			values.put("text", activity);
			if(db.insert("randoms", null, values) == -1){
				Log.d("DB ERR","Could not load activities into DB");
			} else {
				Log.d("DB Success","Activities loaded into DB");
			}
		}
		for(String recipe:recipeList){
			ContentValues values = new ContentValues();
			values.put("type","recipe");
			values.put("text", recipe);
			if(db.insert("randoms", null, values) == -1){
				Log.d("DB ERR","Could not load recipes into DB");
			} else {
				Log.d("DB Success","Recipes loaded into DB");
			}
		}
		Log.d("DB Success","DB Initialized");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVers, int newVers) {
		Log.w(SQLHelper.class.getName(),
		        "Upgrading database from version " + oldVers + " to "
		            + newVers);
		    db.execSQL("DROP TABLE IF EXISTS person;");
		    db.execSQL("DROP TABLE IF EXISTS weight;");
		    db.execSQL("DROP TABLE IF EXISTS food;");
		    db.execSQL("DROP TABLE IF EXISTS activity;");
		    db.execSQL("DROP TABLE IF EXISTS sleep;");
		    db.execSQL("DROP TABLE IF EXISTS randoms;");
		    onCreate(db);
	}
	
	public int getNumQuotes(){
		return quoteList.length;
	}
	public int getNumActivities(){
		return activityList.length;
	}
	public int getNumRecipes(){
		return recipeList.length;
	}
}
