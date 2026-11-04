package edu.uga.cs.statecapitalsquiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.TableLayout;
import android.widget.TableRow;
import java.util.List;

/**
 * Displays past quiz results ordered from newest to oldest.
 */
public class PastResultsFragment extends Fragment {

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
        //Make table layout to put past quizzes in
        TableLayout tableLayout = new TableLayout(requireContext());
        tableLayout.setStretchAllColumns(true);
        ViewGroup parent = (ViewGroup) tvInfoContent.getParent();
        ViewGroup.LayoutParams params = tvInfoContent.getLayoutParams();
        parent.removeView(tvInfoContent);
        parent.addView(tableLayout, params);
        //open db
        QuizResultData quizResultData = new QuizResultData(requireContext());
        quizResultData.open();
        //get all past quizzes. ordered by date time desc
        List<QuizResult> quizResults = quizResultData.retrieveAllQuizResults();
        //close db
        quizResultData.close();
        //table header
        TableRow headerRow = new TableRow(requireContext());
        headerRow.addView(createTextView("Date", true));
        headerRow.addView(createTextView("Result", true));
        tableLayout.addView(headerRow);
        //add quizzes to table
        for (QuizResult quizResult : quizResults) {
            TableRow row = new TableRow(requireContext());
            row.addView(createTextView(quizResult.getDate(), false));
            row.addView(createTextView(quizResult.getResult() + "/12", false));
            tableLayout.addView(row);
        }
        //Message if there are no past quiz results
        if (quizResults.isEmpty()) {
            tableLayout.addView(createTextView("No quizzes taken.", false));
        }
    }

    /**
     * Puts rows into table
     * @param text what to put into row
     * @param isHeader if it needs to be bolded
     * @return
     */
    private TextView createTextView(String text, boolean isHeader) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setPadding(12, 16, 12, 16);
        textView.setTextSize(16);
        //so "Date" and "result" are bolded
        if (isHeader) {
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
        }
        return textView;
    }
}