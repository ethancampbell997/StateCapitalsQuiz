package edu.uga.cs.statecapitalsquiz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DEBUG_TAG = "DBHelper";

    private static final String DB_NAME = "CAPITAL_DATA.db";
    private static final int DB_VERSION = 2;

    // Define all names (strings) for table and column names.
    // This will be useful if we want to change these names later.
    //CAPITAL_DATA table info
    public static final String TABLE_CAPITAL_DATA = "CAPITAL_DATA";
    public static final String CAPITAL_DATA_COLUMN_ID = "_id";
    public static final String CAPITAL_DATA_COLUMN_STATE = "state";
    public static final String CAPITAL_DATA_COLUMN_CAPITAL = "capital";
    public static final String CAPITAL_DATA_COLUMN_CITY2 = "city2";
    public static final String CAPITAL_DATA_COLUMN_CITY3 = "city3";
    public static final String CAPITAL_DATA_COLUMN_STATEHOOD = "statehood";
    public static final String CAPITAL_DATA_COLUMN_CAPITAL_SINCE = "capital_since";
    public static final String CAPITAL_DATA_COLUMN_RANK = "rank";
    //QUIZ table info
    public static final String TABLE_QUIZ = "QUIZ";
    public static final String QUIZ_COLUMN_ID = "_id";
    public static final String QUIZ_COLUMN_STATE_ID = "StateID";
    public static final String QUIZ_COLUMN_CITY1 = "City1";
    public static final String QUIZ_COLUMN_CITY2 = "City2";
    public static final String QUIZ_COLUMN_CITY3 = "City3";
    public static final String QUIZ_COLUMN_NUM_CORRECT_ANSWER = "NumCorrectAnswer";

    // This is a reference to the only instance for the helper.
    private static DBHelper helperInstance;

    // A Create table SQL statement to create a table for job leads.
    // Note that _id is an auto increment primary key, i.e. the database will
    // automatically generate unique id values as keys.
    private static final String CREATE_CAPITAL_DATA =
            "create table " + TABLE_CAPITAL_DATA + " ("
                    + CAPITAL_DATA_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + CAPITAL_DATA_COLUMN_STATE + " TEXT, "
                    + CAPITAL_DATA_COLUMN_CAPITAL + " TEXT, "
                    + CAPITAL_DATA_COLUMN_CITY2 + " TEXT, "
                    + CAPITAL_DATA_COLUMN_CITY3 + " TEXT, "
                    + CAPITAL_DATA_COLUMN_STATEHOOD + " INTEGER, "
                    + CAPITAL_DATA_COLUMN_CAPITAL_SINCE + " INTEGER, "
                    + CAPITAL_DATA_COLUMN_RANK + " INTEGER"
                    + ")";
    private static final String CREATE_QUIZ =
            "CREATE TABLE " + TABLE_QUIZ + " ("
                    + QUIZ_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + QUIZ_COLUMN_STATE_ID + " INTEGER, "
                    + QUIZ_COLUMN_CITY1 + " TEXT, "
                    + QUIZ_COLUMN_CITY2 + " TEXT, "
                    + QUIZ_COLUMN_CITY3 + " TEXT, "
                    + QUIZ_COLUMN_NUM_CORRECT_ANSWER + " INTEGER, "
                    + "FOREIGN KEY (" + QUIZ_COLUMN_STATE_ID + ") "
                    + "REFERENCES " + TABLE_CAPITAL_DATA
                    + "(" + CAPITAL_DATA_COLUMN_ID + ")"
                    + ")";

    // Note that the constructor is private!
    // So, it can be called only from
    // this class, in the getInstance method.
    private DBHelper( Context context ) {
        super( context, DB_NAME, null, DB_VERSION );
    }

    // Access method to the single instance of the class.
    // It is synchronized, so that only one thread can executes this method, at a time.
    public synchronized static DBHelper getInstance( Context context ) {
        // check if the instance already exists and if not, create the instance
        if( helperInstance == null ) {
            helperInstance = new DBHelper( context.getApplicationContext() );
        }
        return helperInstance;
    }

    // We must override onCreate method, which will be used to create the database if
    // it does not exist yet.
    @Override
    public void onCreate( SQLiteDatabase db ) {
        db.execSQL( CREATE_CAPITAL_DATA );
        db.execSQL(CREATE_QUIZ);
        Log.d( DEBUG_TAG, "Table " + TABLE_CAPITAL_DATA + " created" );
    }

    // We should override onUpgrade method, which will be used to upgrade the database if
    // its version (DB_VERSION) has changed.  This will be done automatically by Android
    // if the version will be bumped up, as we modify the database schema.
    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ);
        db.execSQL( "drop table if exists " + TABLE_CAPITAL_DATA );
        onCreate( db );
        Log.d( DEBUG_TAG, "Table " + TABLE_CAPITAL_DATA + " upgraded" );
    }
}
