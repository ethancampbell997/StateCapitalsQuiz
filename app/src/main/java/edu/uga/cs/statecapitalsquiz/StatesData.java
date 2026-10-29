package edu.uga.cs.statecapitalsquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class StatesData {

    public static final String DEBUG_TAG = "StatesData";

    // this is a reference to our database; it is used later to run SQL commands
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private static final String[] allColumns = {
            DBHelper.CAPITAL_DATA_COLUMN_ID,
            DBHelper.CAPITAL_DATA_COLUMN_STATE,
            DBHelper.CAPITAL_DATA_COLUMN_CAPITAL,
            DBHelper.CAPITAL_DATA_COLUMN_CITY2,
            DBHelper.CAPITAL_DATA_COLUMN_CITY3,
            DBHelper.CAPITAL_DATA_COLUMN_STATEHOOD,
            DBHelper.CAPITAL_DATA_COLUMN_CAPITAL_SINCE,
            DBHelper.CAPITAL_DATA_COLUMN_RANK
    };

    public StatesData( Context context ) {
        //DBHelper = DBHelper.getInstance( context );
        dbHelper = DBHelper.getInstance( context );
    }

    // Open the database
    public void open() {
        db = dbHelper.getWritableDatabase();
        Log.d( DEBUG_TAG, "StatesData: db open" );
    }

    // Close the database
    public void close() {
        if( dbHelper != null ) {
            dbHelper.close();
            Log.d(DEBUG_TAG, "StatesData: db closed");
        }
    }

    public boolean isDBOpen()
    {
        return db.isOpen();
    }

    // Retrieve all job leads and return them as a List.
    // This is how we restore persistent objects stored as rows in the job leads table in the database.
    // For each retrieved row, we create a new States (Java POJO object) instance and add it to the list.
    public List<States> retrieveAllStates() {
        ArrayList<States> State = new ArrayList<>();
        Cursor cursor = null;
        int columnIndex;

        try {
            // Execute the select query and get the Cursor to iterate over the retrieved rows
            cursor = db.query( DBHelper.TABLE_CAPITAL_DATA, allColumns,
                    null, null, null, null, null );

            // collect all job leads into a List
            if( cursor != null && cursor.getCount() > 0 ) {

                while( cursor.moveToNext() ) {

                    if( cursor.getColumnCount() >= 5) {

                        // get all attribute values of this job lead
                        columnIndex = cursor.getColumnIndex( DBHelper.CAPITAL_DATA_COLUMN_ID );
                        long id = cursor.getLong( columnIndex );
                        columnIndex = cursor.getColumnIndex( DBHelper.CAPITAL_DATA_COLUMN_STATE );
                        String state = cursor.getString( columnIndex );
                        columnIndex = cursor.getColumnIndex( DBHelper.CAPITAL_DATA_COLUMN_CAPITAL );
                        String capital = cursor.getString( columnIndex );
                        columnIndex = cursor.getColumnIndex( DBHelper.CAPITAL_DATA_COLUMN_CITY2 );
                        String city2 = cursor.getString( columnIndex );
                        columnIndex = cursor.getColumnIndex( DBHelper.CAPITAL_DATA_COLUMN_CITY3 );
                        String city3 = cursor.getString( columnIndex );
                        columnIndex = cursor.getColumnIndex( DBHelper.CAPITAL_DATA_COLUMN_STATEHOOD );
                        int statehood = cursor.getInt( columnIndex );
                        columnIndex = cursor.getColumnIndex( DBHelper.CAPITAL_DATA_COLUMN_CAPITAL_SINCE );
                        int capitalSince = cursor.getInt( columnIndex );
                        columnIndex = cursor.getColumnIndex( DBHelper.CAPITAL_DATA_COLUMN_RANK );
                        int rank = cursor.getInt( columnIndex );

                        // create a new States object and set its state to the retrieved values
                        States States = new States( state, capital, city2, city3, statehood, capitalSince, rank );
                        States.setId(id); // set the id (the primary key) of this object
                        // add it to the list
                        State.add( States );
                        Log.d(DEBUG_TAG, "Retrieved States: " + States);
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
    public States storeState( States States ) {

        // Prepare the values for all of the necessary columns in the table
        // and set their values to the variables of the States argument.
        // This is how we are providing persistence to a States (Java object) instance
        // by storing it as a new row in the database table representing job leads.
        ContentValues values = new ContentValues();
        values.put( DBHelper.CAPITAL_DATA_COLUMN_STATE, States.getState());
        values.put( DBHelper.CAPITAL_DATA_COLUMN_CAPITAL, States.getCapital() );
        values.put( DBHelper.CAPITAL_DATA_COLUMN_CITY2, States.getCity2() );
        values.put( DBHelper.CAPITAL_DATA_COLUMN_CITY3, States.getCity3() );
        values.put( DBHelper.CAPITAL_DATA_COLUMN_STATEHOOD, States.getStatehood() );
        values.put( DBHelper.CAPITAL_DATA_COLUMN_CAPITAL_SINCE, States.getCapitalSince() );
        values.put( DBHelper.CAPITAL_DATA_COLUMN_RANK, States.getRank() );


        // Insert the new row into the database table;
        // The id (primary key) is automatically generated by the database system
        // and returned as from the insert method call.
        long id = db.insert( DBHelper.TABLE_CAPITAL_DATA, null, values );

        // store the id (the primary key) in the States instance, as it is now persistent
        States.setId( id );

        Log.d( DEBUG_TAG, "Stored new job lead with id: " + String.valueOf( States.getId() ) );

        return States;
    }
}
