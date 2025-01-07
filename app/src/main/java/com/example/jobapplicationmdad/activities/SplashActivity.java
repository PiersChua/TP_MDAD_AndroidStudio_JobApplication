package com.example.jobapplicationmdad.activities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.DateConverter;
import com.example.jobapplicationmdad.util.ImageUtil;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences sp;
    boolean isValidCredentials;
    LinearProgressIndicator progressIndicator;
    private static final int SPLASH_DURATION = 2000; // Total duration: 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        sp = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String token = sp.getString("token", "");

        progressIndicator = findViewById(R.id.piSplash);
        progressIndicator.setMax(100);

        // Animate the progress indicator
        animateProgressIndicator();

        new Handler().postDelayed(() -> {
            if (!token.isEmpty()) {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            } else {
                Intent i = new Intent(SplashActivity.this, OnboardingActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_DURATION);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.llSplashMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void animateProgressIndicator() {
        ObjectAnimator firstPhase = ObjectAnimator.ofInt(progressIndicator, "progress", 0, 90);
        firstPhase.setDuration(1500);
        firstPhase.start();

        // delays the second phase by 1.5 seconds before executing
        new Handler().postDelayed(() -> {
            ObjectAnimator secondPhase = ObjectAnimator.ofInt(progressIndicator, "progress", 90, 100);
            secondPhase.setDuration(500);
            secondPhase.start();
        }, 1500);
    }
}
