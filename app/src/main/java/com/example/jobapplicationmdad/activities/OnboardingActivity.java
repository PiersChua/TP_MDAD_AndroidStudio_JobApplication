package com.example.jobapplicationmdad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.adapters.OnboardingPagerAdapter;
import com.example.jobapplicationmdad.fragments.onboarding.FirstOnboardingFragment;
import com.example.jobapplicationmdad.fragments.onboarding.SecondOnboardingFragment;
import com.example.jobapplicationmdad.fragments.onboarding.ThirdOnboardingFragment;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {
    private FragmentStateAdapter onboardingPagerAdapter;
    public static ViewPager2 viewPager;
    Button btnOnboardingNext;
    LinearLayout llOnboardingIndicator;
    ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.vpOnboarding);
        llOnboardingIndicator = findViewById(R.id.llOnboardingIndicator);

        onboardingPagerAdapter = new OnboardingPagerAdapter(this);
        viewPager.setAdapter(onboardingPagerAdapter);

        btnOnboardingNext = findViewById(R.id.btnOnboardingNext);

        // Initialize dots based on the number of fragments
        initializeOnboardingIndicator(OnboardingPagerAdapter.NUM_PAGES);

        btnOnboardingNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem < OnboardingPagerAdapter.NUM_PAGES-1) {
                    viewPager.setCurrentItem(currentItem + 1);
                } else {
                    // Go to the sign-up or login activity
                    Intent i = new Intent(OnboardingActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish(); // don't allow user to come back to this page
                }
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Update the dots and the button text when the page changes
                updateIndicator(position);
                if (position == OnboardingPagerAdapter.NUM_PAGES-1) {
                    btnOnboardingNext.setText(R.string.onboarding_fragment_get_started);
                } else {
                    btnOnboardingNext.setText(R.string.onboarding_fragment_next);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.llOnboarding), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeOnboardingIndicator(int totalPages) {
        dots = new ImageView[totalPages];
        llOnboardingIndicator.removeAllViews(); // Clear any existing dots

        for (int i = 0; i < totalPages; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.onboarding_indicator_inactive);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(12, 0, 12, 0);
            llOnboardingIndicator.addView(dots[i], params);
        }
    }

    // update the active dot based on the current page
    private void updateIndicator(int position) {
        for (int i = 0; i < dots.length; i++) {
            if (i == position) {
                dots[i].setImageResource(R.drawable.onboarding_indicator_active);
            } else {
                dots[i].setImageResource(R.drawable.onboarding_indicator_inactive);
            }
        }
    }
}
