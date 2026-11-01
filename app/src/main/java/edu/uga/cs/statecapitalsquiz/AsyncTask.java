package edu.uga.cs.statecapitalsquiz;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Custom AsyncTask replacement using ExecutorService and Handler,
 * following the JobsTrackerSQLite implementation.
 *
 * @param <Param>  Input parameter type.
 * @param <Result> Output result type.
 */
public abstract class AsyncTask<Param, Result> {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler handler = new Handler(Looper.getMainLooper());

    protected abstract Result doInBackground(Param... params);

    protected abstract void onPostExecute(Result result);

    @SafeVarargs
    public final void execute( final Param... params ) {
        executor.execute(() -> {
            Result result = doInBackground(params);
            handler.post(() -> onPostExecute(result));
        });
    }
}