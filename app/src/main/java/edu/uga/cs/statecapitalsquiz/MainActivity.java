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
import java.util.List;
import java.util.ArrayList;

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


        //read from CSV file
        //db operations in StatesData class
        StatesData statesData = new StatesData(this);
        //try catch for error handling
        //deals with database initialization and transferring data from CSV to CAPITAL_DATA table
        try {
            //open db
            statesData.open();
            //gets list of states from CAPITAL_DATA table
            List<States> statesList = statesData.retrieveAllStates();
            //if list of states is empty, import it from CSV
            if (statesList.isEmpty()) {
                //method defined below
                importStatesFromCSV(statesData);
                //gets states from db
                statesList = statesData.retrieveAllStates();
            }
            //displays the states, method defined below
            //displayStates(statesList);//testing, dont actually need this
            deleteQuiz();//deletes the quiz before making new one. Need to remove later and put elsewhere
            createQuizQuestions(statesList);//testing listing the full quiz
        } catch (Exception e) {
            Log.e(TAG, "Error loading states", e);
        } finally {
            statesData.close();
        }

    }

    /**
     * Imports data from state_capitals.csv and puts it into the CAPITAL_DATA table in the db
     * Only called if the CAPITAL_DATA table is empty
     * @param statesData A reference to the StatesData object that performs db operations
     */
    private void importStatesFromCSV(StatesData statesData) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open("state_capitals.csv")))) {String[] nextRow;
            while ((nextRow = reader.readNext()) != null) {
                //doesn't include the header row
                if (nextRow.length > 0 && nextRow[0].trim().equalsIgnoreCase("state")) {
                    continue;
                }
                //gets each field needed for the row
                String state = nextRow[0].trim();
                String capital = nextRow[1].trim();
                String city2 = nextRow[2].trim();
                String city3 = nextRow[3].trim();
                int statehood = Integer.parseInt(nextRow[4].trim());
                int capitalSince = Integer.parseInt(nextRow[5].trim());
                int rank = Integer.parseInt(nextRow[6].trim());
                //makes the State object for the row
                States stateObject = new States(
                        state, capital, city2, city3, statehood, capitalSince, rank
                );
                //save row to db
                statesData.storeState(stateObject);
                Log.d(TAG, "Imported state: " + state);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error importing CSV data", e);
        }
    }

    /**
     * Prints the whole CAPITAL_DATA table in the main activity
     * Mostly for testing, this functionality is not required
     * @param statesList The list of states returned by the db
     */
    /*private void displayStates(List<States> statesList) {
        //the layout for the list
        TableLayout tableLayout = findViewById(R.id.table_main);
        //deletes any existing rows
        tableLayout.removeAllViews();
        // Table column headings
        String[] headers = {
                "ID", "State", "Capital", "City 2", "City 3",
                "Statehood", "Capital Since", "Rank"
        };
        addTableRow(tableLayout, headers);

        //display each state from db
        for (int i = 0; i < statesList.size(); i++) {
            //gets state and puts into string array
            States state = statesList.get(i);
            String[] values = {
                    String.valueOf(state.getId()),
                    state.getState(),
                    state.getCapital(),
                    state.getCity2(),
                    state.getCity3(),
                    String.valueOf(state.getStatehood()),
                    String.valueOf(state.getCapitalSince()),
                    String.valueOf(state.getRank())
            };
            //puts the row in the table layout
            addTableRow(tableLayout, values);
        }
    }*/

    /**
     * Puts a row into a table layout
     * @param tableLayout The reference to the view
     * @param values The row to add
     */
    private void addTableRow(TableLayout tableLayout, String[] values) {
        TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams layoutParams =
                new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            TextView textView = new TextView(this);
            textView.setGravity(Gravity.RIGHT);
            textView.setText(value);
            tableRow.addView(textView, layoutParams);
        }
        tableLayout.addView(tableRow);
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


    /**
     *Creates a quiz with 6 questions
     * @param questionList
     */
    private void createQuizQuestions(List<States> questionList) {
        QuizData quizData = new QuizData(this);
        try {
            quizData.open();
            //adds 6 questions
            for (int i = 0; i < 6; i++) {
                States state = questionList.get(i);
                //creates a list of the answers
                ArrayList<String> cities = new ArrayList<>();
                cities.add(state.getCapital());
                cities.add(state.getCity2());
                cities.add(state.getCity3());
                //randomize the order of questions
                int randomNumber = (int) (Math.random() * 3);
                int randomNumber2 = (int) (Math.random() * 3);
                int randomNumber3;
                while (randomNumber == randomNumber2) {
                    randomNumber2 = (int) (Math.random() * 3);
                }
                if (randomNumber != 0 && randomNumber2 != 0) {
                    randomNumber3 = 0;
                }
                else if (randomNumber != 1 && randomNumber2 != 1) {
                    randomNumber3 = 1;
                }
                else {
                    randomNumber3 = 2;
                }
                //create the quiz object
                Quiz quiz = new Quiz(
                        state.getId(),
                        cities.get(randomNumber),
                        cities.get(randomNumber2),
                        cities.get(randomNumber3),
                        null
                );
                //save the question in the quiz table
                quizData.storeQuiz(quiz);
            }
            //retrieve questions from the database
            List<ExtendedQuiz> quizList = quizData.retrieveExtendedQuizs();
            //display the quiz questions using the existing table
            //displayQuiz(quizList);//for testing
            displayExtendedQuiz(quizList);
        } catch (Exception e) {
            Log.e(TAG, "Error creating quiz questions", e);
        } finally {
            quizData.close();
        }
    }

    /**
     * Prints all records from the quiz table
     * Mostly for testing
     * @param quizList
     */
    /*private void displayQuiz(List<Quiz> quizList) {
        //the layout for the list
        TableLayout tableLayout = findViewById(R.id.table_main);
        //deletes any existing rows
        tableLayout.removeAllViews();
        // Add column headings.
        String[] headers = {
                "Quiz ID", "State ID", "City 1",
                "City 2", "City 3", "Correct Answer"
        };

        addTableRow(tableLayout, headers);

        //display each question from the quiz
        for (int i = 0; i < quizList.size(); i++) {
            Quiz quiz = quizList.get(i);
            //gets question and puts into string array
            String[] values = {
                    String.valueOf(quiz.getId()),
                    String.valueOf(quiz.getStateId()),
                    quiz.getCity1(),
                    quiz.getCity2(),
                    quiz.getCity3(),
                    quiz.getNumCorrectAnswer() == null
                            ? "null"
                            : String.valueOf(quiz.getNumCorrectAnswer())
            };
            //puts the row in the table layout
            addTableRow(tableLayout, values);
        }
    }*/

    /**
     * truncates the quiz table
     * Once this is truncated, a new quiz can be created
     */
    public void deleteQuiz() {
        QuizData quizData = new QuizData(this);
        try {
            quizData.open();
            quizData.truncateTable();
        } catch (Exception e) {
            Log.e(TAG, "Deleting quiz failed", e);
        } finally {
            quizData.close();
        }
    }
    private void displayExtendedQuiz(List<ExtendedQuiz> quizList) {
        TableLayout tableLayout = findViewById(R.id.table_main);
        tableLayout.removeAllViews();

        String[] headers = {
                "City 1", "City 2", "City 3",
                "Correct Answer", "State", "Capital",
                "Statehood", "Capital Since", "Rank"
        };
        addTableRow(tableLayout, headers);
        for (int i = 0; i < quizList.size(); i++) {
            ExtendedQuiz quiz = quizList.get(i);
            String[] values = {
                    quiz.getCity1(),
                    quiz.getCity2(),
                    quiz.getCity3(),
                    quiz.getNumCorrectAnswer() == null
                            ? "null"
                            : String.valueOf(quiz.getNumCorrectAnswer()),
                    quiz.getState(),
                    quiz.getCapital(),
                    String.valueOf(quiz.getStatehood()),
                    String.valueOf(quiz.getCapitalSince()),
                    String.valueOf(quiz.getRank())
            };
            addTableRow(tableLayout, values);
        }
    }
}