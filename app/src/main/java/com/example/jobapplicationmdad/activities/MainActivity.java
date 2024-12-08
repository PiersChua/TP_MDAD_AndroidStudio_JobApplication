package com.example.jobapplicationmdad.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jobapplicationmdad.R;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    public static String root_url = "http://192.168.0.5/job-application";
    NavigationBarView bottom_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setPadding(0, 0, 0, 0);
        bottom_navigation.setOnApplyWindowInsetsListener(null);

        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.bottom_nav_job_seeker_item_1) {
                    return true;
                } else if (id == R.id.bottom_nav_job_seeker_item_2) {
                    return true;
                } else if (id == R.id.bottom_nav_job_seeker_item_3) {
                    return true;
                }else if (id == R.id.bottom_nav_job_seeker_item_4) {
                    return true;
                }
                else if (id == R.id.bottom_nav_job_seeker_item_5) {
                    return true;
                }

                return false;
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.llMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}