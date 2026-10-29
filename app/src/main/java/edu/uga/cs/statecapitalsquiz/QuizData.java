package edu.uga.cs.statecapitalsquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class QuizData {

    public static final String DEBUG_TAG = "QuizData";

    // this is a reference to our database; it is used later to run SQL commands
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private static final String[] allColumns = {
            DBHelper.QUIZ_COLUMN_ID,
            DBHelper.QUIZ_COLUMN_STATE_ID,
            DBHelper.QUIZ_COLUMN_CITY1,
            DBHelper.QUIZ_COLUMN_CITY2,
            DBHelper.QUIZ_COLUMN_CITY3,
            DBHelper.QUIZ_COLUMN_NUM_CORRECT_ANSWER,
    };

    public QuizData( Context context ) {
        //DBHelper = DBHelper.getInstance( context );
        dbHelper = DBHelper.getInstance( context );
    }

    // Open the database
    public void open() {
        db = dbHelper.getWritableDatabase();
        Log.d( DEBUG_TAG, "QuizData: db open" );
    }

    // Close the database
    public void close() {
        if( dbHelper != null ) {
            dbHelper.close();
            Log.d(DEBUG_TAG, "QuizData: db closed");
        }
    }

    public boolean isDBOpen()
    {
        return db.isOpen();
    }

    // Retrieve all job leads and return them as a List.
    // This is how we restore persistent objects stored as rows in the job leads table in the database.
    // For each retrieved row, we create a new Quiz (Java POJO object) instance and add it to the list.
    public List<Quiz> retrieveAllQuiz() {
        ArrayList<Quiz> State = new ArrayList<>();
        Cursor cursor = null;
        int columnIndex;

        try {
            // Execute the select query and get the Cursor to iterate over the retrieved rows
            cursor = db.query( DBHelper.TABLE_QUIZ, allColumns,
                    null, null, null, null, null );

            // collect all job leads into a List
            if( cursor != null && cursor.getCount() > 0 ) {

                while( cursor.moveToNext() ) {

                    if( cursor.getColumnCount() >= 6) {

                        // get all attribute values of this job lead
                        columnIndex = cursor.getColumnIndex( DBHelper.QUIZ_COLUMN_ID );
                        long id = cursor.getLong( columnIndex );
                        columnIndex = cursor.getColumnIndex( DBHelper.QUIZ_COLUMN_STATE_ID );
                        long stateId = cursor.getLong( columnIndex );
                        columnIndex = cursor.getColumnIndex( DBHelper.QUIZ_COLUMN_CITY1 );
                        String city1 = cursor.getString( columnIndex );
                        columnIndex = cursor.getColumnIndex( DBHelper.QUIZ_COLUMN_CITY2 );
                        String city2 = cursor.getString( columnIndex );
                        columnIndex = cursor.getColumnIndex( DBHelper.QUIZ_COLUMN_CITY3 );
                        String city3 = cursor.getString( columnIndex );
                        columnIndex = cursor.getColumnIndex( DBHelper.QUIZ_COLUMN_NUM_CORRECT_ANSWER );
                        //need the NULL distinction to tell if the user has answered the question or not
                        Integer numCorrectAnswer;
                        if (cursor.isNull(columnIndex)) {
                            numCorrectAnswer = null;
                        } else {
                            numCorrectAnswer = cursor.getInt(columnIndex);
                        }

                        // create a new Quiz object and set its state to the retrieved values
                        Quiz Quiz = new Quiz( stateId, city1, city2, city3, numCorrectAnswer );
                        Quiz.setId(id); // set the id (the primary key) of this object
                        // add it to the list
                        State.add( Quiz );
                        Log.d(DEBUG_TAG, "Retrieved Quiz: " + Quiz);
                    }
                }
            }
            if( cursor != null )
                Log.d( DEBUG_TAG, "Number of records from DB: " + cursor.getCount() );
            else
                Log.d( DEBUG_TAG, "Number of records from DB: 0" );
        }
        catch( Exception e ){
            Log.d( DEBUG_TAG, "Exception caught: " + e );
        }
        finally{
            // we should close the cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        // return a list of retrieved job leads
        return State;
    }

    // Store a new job lead in the database.
    public Quiz storeQuiz( Quiz Quiz ) {

        // Prepare the values for all of the necessary columns in the table
        // and set their values to the variables of the Quiz argument.
        // This is how we are providing persistence to a Quiz (Java object) instance
        // by storing it as a new row in the database table representing job leads.
        ContentValues values = new ContentValues();
        values.put( DBHelper.QUIZ_COLUMN_STATE_ID, Quiz.getStateId());
        values.put( DBHelper.QUIZ_COLUMN_CITY1, Quiz.getCity1() );
        values.put( DBHelper.QUIZ_COLUMN_CITY2, Quiz.getCity2() );
        values.put( DBHelper.QUIZ_COLUMN_CITY3, Quiz.getCity3() );
        values.put( DBHelper.QUIZ_COLUMN_NUM_CORRECT_ANSWER, Quiz.getNumCorrectAnswer() );


        // Insert the new row into the database table;
        // The id (primary key) is automatically generated by the database system
        // and returned as from the insert method call.
        long id = db.insert( DBHelper.TABLE_QUIZ, null, values );

        // store the id (the primary key) in the Quiz instance, as it is now persistent
        Quiz.setId( id );

        Log.d( DEBUG_TAG, "Stored new job lead with id: " + String.valueOf( Quiz.getId() ) );

        return Quiz;
    }
}
