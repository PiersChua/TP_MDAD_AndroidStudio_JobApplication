package com.example.jobapplicationmdad.fragments.agent.job;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.AgentJobCardAdapter;
import com.example.jobapplicationmdad.fragments.jobseeker.profile.CreateAgencyApplicationFragment;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AgentJobsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgentJobsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userId";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private static final String get_jobs_url = MainActivity.root_url + "/api/agent/get-jobs.php";
    private String userId;
    RecyclerView recyclerView;
    List<Job> jobList;
    View dialogView;
    AlertDialog loadingDialog;
    AgentJobCardAdapter agentJobCardAdapter;
    SwipeRefreshLayout srlAgentJob;
    MaterialToolbar topAppBar;
    SharedPreferences sp;

    public AgentJobsFragment() {
        // Required empty public constructor
    }

    /**
     *
     *
     * @param userId The userId of the agent managing the job. This field is nullable
     *               if the agent is the one direct managing the fragment
     * @return A new instance of fragment AgentJobsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AgentJobsFragment newInstance(String userId) {
        AgentJobsFragment fragment = new AgentJobsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialogView = inflater.inflate(R.layout.dialog_loader, container, false);
        return inflater.inflate(R.layout.fragment_agent_jobs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        topAppBar = view.findViewById(R.id.topAppBarAgentJob);
        if (userId != null) {
            topAppBar.setNavigationIcon(R.drawable.ic_arrow_back);
            topAppBar.setNavigationIconTint(getResources().getColor(R.color.background));
            topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getParentFragmentManager().popBackStack();
                }
            });
        }
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        jobList = new ArrayList<>();
        getJobs();
        srlAgentJob = view.findViewById(R.id.srlAgentJob);
        recyclerView = view.findViewById(R.id.rvAgentJobCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Set the adapter
        agentJobCardAdapter = new AgentJobCardAdapter(jobList, new AgentJobCardAdapter.OnJobClickListener() {

            @Override
            public void onViewJobApplications(String jobId) {
                // check for double click
                FragmentManager fragmentManager = getChildFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
                    fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flAgentJob, AgentManageJobApplicationsFragment.newInstance(jobId,userId)).addToBackStack(null).commit();
            }

            @Override
            public void onEditJob(String jobId) {
                // check for double click
                FragmentManager fragmentManager = getChildFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
                    fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flAgentJob, EditAgentJobFragment.newInstance(jobId,userId)).addToBackStack(null).commit();
            }
        });
        recyclerView.setAdapter(agentJobCardAdapter);


        srlAgentJob.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshJobs();
                srlAgentJob.setRefreshing(false);
            }
        });
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.agent_listings_item_1) {
                    getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flAgentJob, CreateAgentJobFragment.newInstance(userId)).addToBackStack(null).commit();
                    return true;
                }
                return false;
            }
        });
        getChildFragmentManager().setFragmentResultListener("jobResult", this, (requestKey, result) -> {
            boolean isUpdated = result.getBoolean("isUpdated");
            if (isUpdated) {
                refreshJobs();
            }
        });
    }

    public void onResume() {
        super.onResume();
        if (userId != null) {
            ((MainActivity) requireActivity()).hideBottomNav();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (userId != null) {
            ((MainActivity) requireActivity()).showBottomNav();
        }

    }

    private void getJobs() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("agentUserId", userId != null ? userId : sp.getString("userId", ""));
        String url = UrlUtil.constructUrl(get_jobs_url, params);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jobsArray = response.getJSONArray("data");
                    for (int i = 0; i < jobsArray.length(); i++) {
                        JSONObject jobObject = jobsArray.getJSONObject(i);
                        Job job = new Job(
                                jobObject.getString("jobId"),
                                jobObject.getString("position"),
                                jobObject.getString("organisation"),
                                jobObject.getString("location"),
                                jobObject.optDouble("partTimeSalary", 0.0),
                                jobObject.optDouble("fullTimeSalary", 0.0),
                                jobObject.getInt("favourite_job_count"),
                                jobObject.getInt("job_application_count")
                        );
                        jobList.add(job);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                // toggle the visibility of loader
                loadingDialog.dismiss();
                recyclerView.setVisibility(View.VISIBLE);
            }
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(requireContext(), requireActivity().findViewById(android.R.id.content)).onErrorResponse(error);
        });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void refreshJobs() {
        jobList.clear();
        recyclerView.setVisibility(View.GONE);
        getJobs();
        agentJobCardAdapter.notifyDataSetChanged();
    }
}