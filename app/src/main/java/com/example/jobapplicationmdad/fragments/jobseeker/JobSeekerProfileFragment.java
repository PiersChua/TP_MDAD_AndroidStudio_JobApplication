package com.example.jobapplicationmdad.fragments.jobseeker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.LoginActivity;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JobSeekerProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobSeekerProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MaterialToolbar topAppBar;

    public JobSeekerProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JobSeekerProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobSeekerProfileFragment newInstance(String param1, String param2) {
        JobSeekerProfileFragment fragment = new JobSeekerProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_seeker_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topAppBar = view.findViewById(R.id.topAppBarJobSeekerProfile);
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.job_seeker_profile_item_1) {
                    return true;
                } else if (id == R.id.job_seeker_profile_item_2) {
                    Toast.makeText(getContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.job_seeker_profile_item_3) {
                    // clear shared preferences
                    SharedPreferences sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    sp.edit().clear().apply();
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                    return true;
                }
                return false;

            }
        });
    }

}