package edu.uga.cs.statecapitalsquiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Displays the final quiz score right after the final swipe left following Question 6.
 */
public class QuizSummaryFragment extends Fragment {

    private TextView tvFinalScore;
    private int currentScore = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvFinalScore = view.findViewById(R.id.tvFinalScore);
        tvFinalScore.setText("Your Score: " + currentScore + " / 12");

        Button btnPlayAgain = view.findViewById(R.id.btnPlayAgain);
        Button btnViewHistory = view.findViewById(R.id.btnViewHistory);
        Button btnQuitSummary = view.findViewById(R.id.btnQuitSummary);

        btnPlayAgain.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadQuizFragment(true);
            }
        });

        btnViewHistory.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadPastResultsFragment();
            }
        });

        btnQuitSummary.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finishAffinity();
            }
        });
    }

    public void updateDisplayedScore(int score) {
        this.currentScore = score;
        if (tvFinalScore != null) {
            tvFinalScore.setText("Your Score: " + score + " / 12");
        }
    }
}