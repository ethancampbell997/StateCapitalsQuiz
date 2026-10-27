package edu.uga.cs.statecapitalsquiz;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.InputStream;

/**
 * This is the splash page to welcome users to the app
 */
public class SplashActivity extends AppCompatActivity {
    private Button button;

    /**
     * When activity is created/app is started
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        button = findViewById( R.id.button );
        button.setOnClickListener( new ButtonClickListener() );
    }
    private class ButtonClickListener implements
            View.OnClickListener
    {
        /**
         * When the button is clicked, send the user to the main page
         * @param view The view that was clicked.
         */
        @Override
        public void onClick( View view ) {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}