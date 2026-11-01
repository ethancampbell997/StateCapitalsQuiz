package edu.uga.cs.statecapitalsquiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Displays a single quiz question including statehood year, 3 capital choices,
 * and the CSCI 6060 Yes/No largest city question.
 */
public class QuizQuestionFragment extends Fragment {

    private static final String PREFS_NAME = "QuizTempPrefs";
    private long quizId;
    private int position;
    private String stateName;
    private String capital;
    private String city1;
    private String city2;
    private String city3;
    private int statehood;
    private int capitalSince;
    private int rank;

    private RadioGroup rgCapitalChoices;
    private RadioGroup rgLargestCity;

    public static QuizQuestionFragment newInstance(ExtendedQuiz q, int position) {
        QuizQuestionFragment fragment = new QuizQuestionFragment();
        Bundle args = new Bundle();
        args.putLong("quizId", q.getQuizId());
        args.putInt("position", position);
        args.putString("state", q.getState());
        args.putString("capital", q.getCapital());
        args.putString("city1", q.getCity1());
        args.putString("city2", q.getCity2());
        args.putString("city3", q.getCity3());
        args.putInt("statehood", q.getStatehood());
        args.putInt("capitalSince", q.getCapitalSince());
        args.putInt("rank", q.getRank());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            quizId = getArguments().getLong("quizId");
            position = getArguments().getInt("position");
            stateName = getArguments().getString("state");
            capital = getArguments().getString("capital");
            city1 = getArguments().getString("city1");
            city2 = getArguments().getString("city2");
            city3 = getArguments().getString("city3");
            statehood = getArguments().getInt("statehood");
            capitalSince = getArguments().getInt("capitalSince");
            rank = getArguments().getInt("rank");
        }

        TextView tvQuestionNumber = view.findViewById(R.id.tvQuestionNumber);
        TextView tvStatehoodInfo = view.findViewById(R.id.tvStatehoodInfo);
        TextView tvCapitalPrompt = view.findViewById(R.id.tvCapitalPrompt);
        TextView tvLargestPrompt = view.findViewById(R.id.tvLargestPrompt);

        rgCapitalChoices = view.findViewById(R.id.rgCapitalChoices);
        rgLargestCity = view.findViewById(R.id.rgLargestCity);

        RadioButton rbCity1 = view.findViewById(R.id.rbCity1);
        RadioButton rbCity2 = view.findViewById(R.id.rbCity2);
        RadioButton rbCity3 = view.findViewById(R.id.rbCity3);

        tvQuestionNumber.setText("Question " + (position + 1) + " of 6");
        tvStatehoodInfo.setText(stateName + " joined the Union in " + statehood
                + " (Capital since " + capitalSince + ")");
        tvCapitalPrompt.setText("1. What is the capital of " + stateName + "?");
        tvLargestPrompt.setText("2. Is the capital city the largest city in " + stateName + "?");

        rbCity1.setText("A. " + city1);
        rbCity2.setText("B. " + city2);
        rbCity3.setText("C. " + city3);

        // Ensure no default selection initially (Requirement 12)
        rgCapitalChoices.clearCheck();
        rgLargestCity.clearCheck();

        // Restore in-progress selections if user rotated screen or paused before swiping
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int savedCapChoice = prefs.getInt("cap_choice_" + quizId, -1);
        int savedLargestChoice = prefs.getInt("largest_choice_" + quizId, -1);

        if (savedCapChoice == 1) rbCity1.setChecked(true);
        else if (savedCapChoice == 2) rbCity2.setChecked(true);
        else if (savedCapChoice == 3) rbCity3.setChecked(true);

        if (savedLargestChoice == 1) ((RadioButton) view.findViewById(R.id.rbLargestYes)).setChecked(true);
        else if (savedLargestChoice == 0) ((RadioButton) view.findViewById(R.id.rbLargestNo)).setChecked(true);

        rgCapitalChoices.setOnCheckedChangeListener((group, checkedId) -> {
            int choice = -1;
            if (checkedId == R.id.rbCity1) choice = 1;
            else if (checkedId == R.id.rbCity2) choice = 2;
            else if (checkedId == R.id.rbCity3) choice = 3;
            prefs.edit().putInt("cap_choice_" + quizId, choice).apply();
        });

        rgLargestCity.setOnCheckedChangeListener((group, checkedId) -> {
            int choice = -1;
            if (checkedId == R.id.rbLargestYes) choice = 1;
            else if (checkedId == R.id.rbLargestNo) choice = 0;
            prefs.edit().putInt("largest_choice_" + quizId, choice).apply();
        });
    }

    /**
     * Evaluates the user's selections for this state (0, 1, or 2 points).
     */
    public static int evaluateQuestion(Context context, ExtendedQuiz q) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int capChoice = prefs.getInt("cap_choice_" + q.getQuizId(), -1);
        int largestChoice = prefs.getInt("largest_choice_" + q.getQuizId(), -1);

        int points = 0;
        String selectedCity = null;
        if (capChoice == 1) selectedCity = q.getCity1();
        else if (capChoice == 2) selectedCity = q.getCity2();
        else if (capChoice == 3) selectedCity = q.getCity3();

        if (selectedCity != null && selectedCity.equalsIgnoreCase(q.getCapital())) {
            points += 1;
        }

        boolean isLargest = (q.getRank() == 1);
        if ((largestChoice == 1 && isLargest) || (largestChoice == 0 && !isLargest)) {
            points += 1;
        }

        return points;
    }

    public static void clearSavedSelections(Context context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }
}