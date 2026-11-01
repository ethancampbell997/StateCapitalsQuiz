package edu.uga.cs.statecapitalsquiz;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.opencsv.CSVReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Hosts the ViewPager2 for the 6 quiz questions and final result page.
 * Handles asynchronous DB loading, swipe scoring, and onPause/onResume state preservation.
 */
public class QuizPagerFragment extends Fragment {

    private static final String TAG = "QuizPagerFragment";
    private ViewPager2 viewPager;
    private List<ExtendedQuiz> currentQuizList = new ArrayList<>();
    private QuizSummaryFragment summaryFragment;
    private boolean forceNewQuiz = false;
    private int lastPageIndex = 0;

    public static QuizPagerFragment newInstance(boolean forceNewQuiz) {
        QuizPagerFragment fragment = new QuizPagerFragment();
        Bundle args = new Bundle();
        args.putBoolean("FORCE_NEW_QUIZ", forceNewQuiz);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            forceNewQuiz = getArguments().getBoolean("FORCE_NEW_QUIZ", false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.quizViewPager);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position > lastPageIndex && lastPageIndex < currentQuizList.size()) {
                    gradeAndSaveQuestion(lastPageIndex);
                }
                lastPageIndex = position;

                if (position == 6) {
                    finalizeQuizResult();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null) {
            new LoadOrCreateQuizTask(getContext().getApplicationContext()).execute(forceNewQuiz);
            forceNewQuiz = false;
            if (getArguments() != null) {
                getArguments().putBoolean("FORCE_NEW_QUIZ", false);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Quiz progress preserved at page " + lastPageIndex);
    }

    private void gradeAndSaveQuestion(int questionIndex) {
        if (getContext() == null || questionIndex < 0 || questionIndex >= currentQuizList.size()) {
            return;
        }
        ExtendedQuiz q = currentQuizList.get(questionIndex);
        int points = QuizQuestionFragment.evaluateQuestion(getContext(), q);
        q.setNumCorrectAnswer(points);
        new UpdateAnswerTask(getContext().getApplicationContext()).execute(q.getQuizId(), (long) points);
    }

    private void finalizeQuizResult() {
        int totalScore = 0;
        for (int i = 0; i < currentQuizList.size(); i++) {
            ExtendedQuiz q = currentQuizList.get(i);
            if (q.getNumCorrectAnswer() == null && getContext() != null) {
                int pts = QuizQuestionFragment.evaluateQuestion(getContext(), q);
                q.setNumCorrectAnswer(pts);
            }
            if (q.getNumCorrectAnswer() != null) {
                totalScore += q.getNumCorrectAnswer();
            }
        }
        if (summaryFragment != null) {
            summaryFragment.updateDisplayedScore(totalScore);
        }
        if (getContext() != null) {
            new CompleteQuizTask(getContext().getApplicationContext()).execute(totalScore);
        }
    }

    private class LoadOrCreateQuizTask extends AsyncTask<Boolean, List<ExtendedQuiz>> {
        private final Context appContext;

        LoadOrCreateQuizTask(Context appContext) {
            this.appContext = appContext;
        }

        @Override
        protected List<ExtendedQuiz> doInBackground(Boolean... params) {
            boolean startFresh = params[0];
            QuizData quizData = new QuizData(appContext);
            StatesData statesData = new StatesData(appContext);
            List<ExtendedQuiz> result = new ArrayList<>();

            try {
                // Open both against the shared DBHelper singleton (do NOT close statesData early!)
                statesData.open();
                quizData.open();

                List<States> allStates = statesData.retrieveAllStates();
                if (allStates.isEmpty()) {
                    importCSVFallback(statesData);
                    allStates = statesData.retrieveAllStates();
                }

                List<ExtendedQuiz> existing = quizData.retrieveExtendedQuizs();

                boolean hasUnanswered = false;
                for (ExtendedQuiz eq : existing) {
                    if (eq.getNumCorrectAnswer() == null) {
                        hasUnanswered = true;
                        break;
                    }
                }

                if (startFresh || existing.size() < 6 || !hasUnanswered) {
                    quizData.truncateTable();
                    QuizQuestionFragment.clearSavedSelections(appContext);

                    if (allStates.size() >= 6) {
                        Random random = new Random();
                        List<States> selectedStates = new ArrayList<>();
                        List<States> pool = new ArrayList<>(allStates);
                        for (int i = 0; i < 6; i++) {
                            int randIndex = random.nextInt(pool.size());
                            selectedStates.add(pool.remove(randIndex));
                        }

                        for (States state : selectedStates) {
                            List<String> cities = new ArrayList<>();
                            cities.add(state.getCapital());
                            cities.add(state.getCity2());
                            cities.add(state.getCity3());
                            Collections.shuffle(cities, random);

                            Quiz quizRow = new Quiz(
                                    state.getId(),
                                    cities.get(0),
                                    cities.get(1),
                                    cities.get(2),
                                    null
                            );
                            quizData.storeQuiz(quizRow);
                        }
                    }
                    result = quizData.retrieveExtendedQuizs();
                } else {
                    result = existing;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading/creating quiz", e);
            } finally {
                // Closing quizData closes the shared DBHelper singleton once all queries are done
                quizData.close();
            }
            return result;
        }

        private void importCSVFallback(StatesData statesData) {
            try (CSVReader reader = new CSVReader(new InputStreamReader(appContext.getAssets().open("state_capitals.csv")))) {
                String[] nextRow;
                while ((nextRow = reader.readNext()) != null) {
                    if (nextRow.length < 7) continue;
                    if (nextRow[0].toLowerCase().contains("state") || nextRow[4].toLowerCase().contains("statehood")) {
                        continue;
                    }
                    String state = nextRow[0].trim();
                    String capital = nextRow[1].trim();
                    String city2 = nextRow[2].trim();
                    String city3 = nextRow[3].trim();
                    int statehood = Integer.parseInt(nextRow[4].trim());
                    int capitalSince = Integer.parseInt(nextRow[5].trim());
                    int rank = Integer.parseInt(nextRow[6].trim());
                    statesData.storeState(new States(state, capital, city2, city3, statehood, capitalSince, rank));
                }
            } catch (Exception e) {
                Log.e(TAG, "Fallback CSV import error", e);
            }
        }

        @Override
        protected void onPostExecute(List<ExtendedQuiz> quizList) {
            if (!isAdded() || quizList == null || quizList.isEmpty()) {
                Log.e(TAG, "onPostExecute: quizList is empty or fragment not added");
                return;
            }
            currentQuizList = quizList;
            summaryFragment = new QuizSummaryFragment();

            viewPager.setAdapter(new FragmentStateAdapter(QuizPagerFragment.this) {
                @NonNull
                @Override
                public Fragment createFragment(int position) {
                    if (position < 6) {
                        return QuizQuestionFragment.newInstance(currentQuizList.get(position), position);
                    } else {
                        return summaryFragment;
                    }
                }

                @Override
                public int getItemCount() {
                    return 7;
                }
            });

            int firstUnanswered = 0;
            for (int i = 0; i < currentQuizList.size(); i++) {
                if (currentQuizList.get(i).getNumCorrectAnswer() == null) {
                    firstUnanswered = i;
                    break;
                }
            }
            lastPageIndex = firstUnanswered;
            viewPager.setCurrentItem(firstUnanswered, false);
        }
    }

    private static class UpdateAnswerTask extends AsyncTask<Long, Void> {
        private final Context appContext;

        UpdateAnswerTask(Context appContext) {
            this.appContext = appContext;
        }

        @Override
        protected Void doInBackground(Long... params) {
            long quizId = params[0];
            int points = params[1].intValue();
            QuizData quizData = new QuizData(appContext);
            try {
                quizData.open();
                quizData.updateNumCorrectAnswer(quizId, points);
            } finally {
                quizData.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {}
    }

    private static class CompleteQuizTask extends AsyncTask<Integer, Void> {
        private final Context appContext;

        CompleteQuizTask(Context appContext) {
            this.appContext = appContext;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            int finalScore = params[0];
            QuizData quizData = new QuizData(appContext);
            try {
                quizData.open();
                quizData.truncateTable();
                QuizQuestionFragment.clearSavedSelections(appContext);
                Log.d(TAG, "Quiz finished with score: " + finalScore + "/12.");
            } finally {
                quizData.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {}
    }
}