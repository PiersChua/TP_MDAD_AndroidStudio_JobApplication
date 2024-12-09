package com.example.jobapplicationmdad.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.fragments.agent.AgentHomeFragment;
import com.example.jobapplicationmdad.fragments.jobseeker.JobSeekerApplicationsFragment;
import com.example.jobapplicationmdad.fragments.jobseeker.JobSeekerFavouriteFragment;
import com.example.jobapplicationmdad.fragments.jobseeker.JobSeekerHomeFragment;
import com.example.jobapplicationmdad.fragments.jobseeker.JobSeekerJobsFragment;
import com.example.jobapplicationmdad.fragments.jobseeker.JobSeekerProfileFragment;
import com.google.android.material.navigation.NavigationBarView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static String root_url = "http://192.168.0.5/job-application";
    NavigationBarView bottom_navigation;
    private Map<Integer, Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setPadding(0, 0, 0, 0);
        bottom_navigation.setOnApplyWindowInsetsListener(null);

        SharedPreferences sp = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String name = sp.getString("name", "user"); // default value is 'user'
        String role = sp.getString("role", "");
        initBottomNavMenu(role);
        initFragments(role);

        bottom_navigation.setOnItemSelectedListener(menuItem -> {
            if (fragments != null && fragments.containsKey(menuItem.getItemId())) {
                Fragment selectedFragment = fragments.get(menuItem.getItemId());
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.flMain, selectedFragment).commit();
                    return true;
                }
            }
            return false;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.llMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Initialise the appropriate navigation based on the user's role
     * @param role The role of the user when logged in
     */
    private void initBottomNavMenu(String role) {
        switch (role) {
            case "Job Seeker":
                bottom_navigation.inflateMenu(R.menu.menu_bottom_nav_job_seeker);
                break;
            case "Agent":
                bottom_navigation.inflateMenu(R.menu.menu_bottom_nav_agent);
                break;
            case "Agency Admin":
                bottom_navigation.inflateMenu(R.menu.menu_bottom_nav_agency_admin);
                break;
            case "Admin":
                bottom_navigation.inflateMenu(R.menu.menu_bottom_nav_admin);
                break;
            default:
                throw new Error("No role found");
        }
    }

    /**
     * Initialise the fragments that will be displayed based on the user's role
     *
     * @param role The role of the user when logged in
     */
    private void initFragments(String role) {
        fragments = new HashMap<>();
        switch (role) {
            case "Job Seeker":
                fragments.put(R.id.bottom_nav_job_seeker_item_1, new JobSeekerHomeFragment());
                fragments.put(R.id.bottom_nav_job_seeker_item_2,new JobSeekerJobsFragment());
                fragments.put(R.id.bottom_nav_job_seeker_item_3,new JobSeekerApplicationsFragment());
                fragments.put(R.id.bottom_nav_job_seeker_item_4,new JobSeekerFavouriteFragment());
                fragments.put(R.id.bottom_nav_job_seeker_item_5,new JobSeekerProfileFragment());

            case "Agent":
                fragments.put(R.id.bottom_nav_agent_item_1, new AgentHomeFragment());
        }
    }
}