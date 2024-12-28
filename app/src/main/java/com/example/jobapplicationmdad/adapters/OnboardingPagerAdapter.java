package com.example.jobapplicationmdad.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.jobapplicationmdad.fragments.onboarding.FirstOnboardingFragment;
import com.example.jobapplicationmdad.fragments.onboarding.SecondOnboardingFragment;
import com.example.jobapplicationmdad.fragments.onboarding.ThirdOnboardingFragment;

import java.util.List;

public class OnboardingPagerAdapter extends FragmentStateAdapter {
    public static final int NUM_PAGES = 3;

    public OnboardingPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @Override
// to return the selected fragment depending on which tab is selected
    public Fragment createFragment(int position) {
        //return fList.get(position);
        switch (position) {
            case 0: {
                return FirstOnboardingFragment.newInstance("fragment 1", null);
            }
            case 1: {
                return SecondOnboardingFragment.newInstance("fragment 2", null);
            }
            case 2: {
                return ThirdOnboardingFragment.newInstance("fragment 3", null);
            }
            default:
                return new Fragment();
        }
    }

    @Override
// return count of tab items
    public int getItemCount() {
        return NUM_PAGES;
    }
}

