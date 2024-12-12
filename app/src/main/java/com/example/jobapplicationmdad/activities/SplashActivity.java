package com.example.jobapplicationmdad.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jobapplicationmdad.R;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        sp = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String token = sp.getString("token", "");

        new Handler().postDelayed(() -> {
            if (!token.isEmpty()) {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
            else{
                Intent i = new Intent(SplashActivity.this, OnboardingActivity.class);
                startActivity(i);
                finish();
            }
            Intent i = new Intent(SplashActivity.this, OnboardingActivity.class);
            startActivity(i);
            finish();
        }, 2000);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.llMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}