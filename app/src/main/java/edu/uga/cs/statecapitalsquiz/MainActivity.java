package edu.uga.cs.statecapitalsquiz;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.InputStream;
import java.io.InputStreamReader;
import com.opencsv.CSVReader;

public class MainActivity extends AppCompatActivity {
    final String TAG = "CSVReading";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        //reads from CSV file
        try {
            // Open the CSV data file in the assets folder
            InputStream in_s = getAssets().open( "state_capitals.csv" );

            // get the TableLayout view
            TableLayout tableLayout = findViewById(R.id.table_main);

            // set up margins for each TextView in the table layout
            android.widget.TableRow.LayoutParams layoutParams =
                    new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT );
            layoutParams.setMargins(20, 0, 20, 0);

            // read the CSV data
            CSVReader reader = new CSVReader( new InputStreamReader( in_s ) );
            String[] nextRow;
            while( ( nextRow = reader.readNext() ) != null ) {

                // nextRow[] is an array of values from the line

                // create the next table row for the layout
                TableRow tableRow = new TableRow( getBaseContext() );
                for( int i = 0; i < nextRow.length; i++ ) {

                    // create a new TextView and set its text
                    TextView textView = new TextView( getBaseContext() );
                    // for all columns exept the SCHOOL, align right
                    if( i != 1 )
                        textView.setGravity( Gravity.RIGHT );
                    textView.setText( nextRow[i] );

                    // add the new TextView to the table row in the table supplying the
                    // layout parameters
                    tableRow.addView( textView, layoutParams );
                }

                // add the next row to the table layout
                tableLayout.addView( tableRow );
            }
        } catch (Exception e) {
            Log.e( TAG, e.toString() );
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Implement Back button listener method.
        // This method may be used for other actions from the ActionBar menu, if provided in the layout.
        int id = item.getItemId();

        // android.R.id.home is the built-in designation of the back button widget.
        if( id == android.R.id.home ) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }
}