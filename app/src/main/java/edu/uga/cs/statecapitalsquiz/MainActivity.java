package edu.uga.cs.statecapitalsquiz;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.opencsv.CSVReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Single-Activity host managing the Navigation Drawer, asynchronous CSV DB initialization,
 * and fragment transactions.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                android.R.string.ok, android.R.string.cancel);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_new_quiz) {
                loadQuizFragment(true);
            } else if (id == R.id.nav_resume_quiz) {
                loadQuizFragment(false);
            } else if (id == R.id.nav_past_results) {
                loadPastResultsFragment();
            } else if (id == R.id.nav_help) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HelpFragment())
                        .commit();
            } else if (id == R.id.nav_quit) {
                finishAffinity();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Initialize database from CSV asynchronously before loading target screen
        new InitDatabaseTask().execute(savedInstanceState == null);
    }

    public void loadQuizFragment(boolean forceNewQuiz) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, QuizPagerFragment.newInstance(forceNewQuiz))
                .commit();
    }

    public void loadPastResultsFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PastResultsFragment())
                .commit();
    }

    /**
     * Reads state_capitals.csv in a background thread on first launch so the UI thread never blocks.
     */
    private class InitDatabaseTask extends AsyncTask<Boolean, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... params) {
            boolean isFirstCreate = params[0];
            StatesData statesData = new StatesData(MainActivity.this);
            try {
                statesData.open();
                List<States> statesList = statesData.retrieveAllStates();
                if (statesList.isEmpty()) {
                    importStatesFromCSV(statesData);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error initializing database", e);
            } finally {
                statesData.close();
            }
            return isFirstCreate;
        }

        @Override
        protected void onPostExecute(Boolean isFirstCreate) {
            if (isFirstCreate) {
                String target = getIntent().getStringExtra("TARGET_SCREEN");
                if ("RESULTS".equals(target)) {
                    loadPastResultsFragment();
                } else {
                    loadQuizFragment(false);
                }
            }
        }
    }

    private void importStatesFromCSV(StatesData statesData) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open("state_capitals.csv")))) {
            String[] nextRow;
            while ((nextRow = reader.readNext()) != null) {
                try {
                    // 1. Skip blank or incomplete rows at the bottom of the file
                    if (nextRow.length < 7) {
                        continue;
                    }

                    // 2. Flexible header check (bypasses invisible UTF-8 BOM characters)
                    if (nextRow[0].toLowerCase().contains("state") || nextRow[4].toLowerCase().contains("statehood")) {
                        continue;
                    }

                    // 3. Parse the data
                    String state = nextRow[0].trim();
                    String capital = nextRow[1].trim();
                    String city2 = nextRow[2].trim();
                    String city3 = nextRow[3].trim();
                    int statehood = Integer.parseInt(nextRow[4].trim());
                    int capitalSince = Integer.parseInt(nextRow[5].trim());
                    int rank = Integer.parseInt(nextRow[6].trim());

                    // 4. Save to Database
                    States stateObject = new States(state, capital, city2, city3, statehood, capitalSince, rank);
                    statesData.storeState(stateObject);

                } catch (Exception rowEx) {
                    // If a single row is corrupted, log it and move to the next row instead of aborting the whole file
                    Log.e(TAG, "Skipped a malformed row in CSV: " + rowEx.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening CSV file. Check if it is in app/src/main/assets/", e);
        }
    }
}