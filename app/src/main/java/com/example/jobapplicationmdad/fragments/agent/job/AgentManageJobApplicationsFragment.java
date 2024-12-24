package com.example.jobapplicationmdad.fragments.agent.job;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AgentManageJobApplicationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgentManageJobApplicationsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "jobId";

    // TODO: Rename and change types of parameters
    private String jobId;
    MaterialToolbar topAppBar;

    public AgentManageJobApplicationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param jobId The associated jobId to manage the job applications
     * @return A new instance of fragment AgentManageJobApplicationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AgentManageJobApplicationsFragment newInstance(String jobId) {
        AgentManageJobApplicationsFragment fragment = new AgentManageJobApplicationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, jobId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jobId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_agent_manage_job_applications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topAppBar = view.findViewById(R.id.topAppBarAgentManageJobApplication);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).hideBottomNav();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) requireActivity()).showBottomNav();
    }
}