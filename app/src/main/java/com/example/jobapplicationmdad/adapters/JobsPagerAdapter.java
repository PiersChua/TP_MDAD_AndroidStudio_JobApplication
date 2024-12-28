package com.example.jobapplicationmdad.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.jobapplicationmdad.fragments.jobseeker.JobSeekerApplicationsFragment;
import com.example.jobapplicationmdad.fragments.jobseeker.JobSeekerFavouriteFragment;

public class JobsPagerAdapter extends FragmentStateAdapter {
    public static String [] pagerTitles = {"Favourite", "Applied"};

    // Constructor using fragment, since it will be hosted in the Job Fragment
    public JobsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: {
                return new JobSeekerFavouriteFragment();
            }
            case 1: {
                return new JobSeekerApplicationsFragment();
            }
            default:
                return new Fragment();
        }
    }
    @Override
    public int getItemCount() {
        return pagerTitles.length;
    }

}
