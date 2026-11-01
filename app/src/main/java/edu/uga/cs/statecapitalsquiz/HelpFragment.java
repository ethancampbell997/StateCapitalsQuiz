package edu.uga.cs.statecapitalsquiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Displays quiz instructions and rules inside the Navigation Drawer.
 */
public class HelpFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvInfoContent = view.findViewById(R.id.tvInfoContent);
        tvInfoContent.setText("US State Capitals Quiz Rules\n\n"
                + "1. Each quiz selects 6 unique US states at random.\n\n"
                + "2. For each state, select the capital city from the 3 choices (1 point) "
                + "and answer whether the capital is the largest city in the state (1 point).\n\n"
                + "3. Maximum score per quiz is 12 points.\n\n"
                + "4. Swipe left to submit your answer and move to the next question.\n\n"
                + "5. If your quiz is interrupted, your progress is saved automatically and will resume where you left off.");
    }
}