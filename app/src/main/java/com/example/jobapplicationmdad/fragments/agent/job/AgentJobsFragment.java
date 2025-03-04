package com.example.jobapplicationmdad.fragments.agent.job;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.AgentJobCardAdapter;
import com.example.jobapplicationmdad.fragments.jobseeker.profile.CreateAgencyApplicationFragment;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private static final String ARG_PARAM2 = "showAppBarIfAgentJobsFragmentClosed";
    private static final String ARG_PARAM3 = "showAppBarIfAgentManageJobApplicationsFragmentClosed";

    // TODO: Rename and change types of parameters
    private static final String get_jobs_url = MainActivity.root_url + "/api/agent/get-jobs.php";
    private String userId;
    private boolean showAppBarIfAgentJobsFragmentClosed;
    private boolean showAppBarIfAgentManageJobApplicationsFragmentClosed;
    private long mLastClickTime;
    RecyclerView recyclerView;
    List<Job> jobList;
    View dialogView;
    AlertDialog loadingDialog;
    AgentJobCardAdapter agentJobCardAdapter;
    SwipeRefreshLayout srlAgentJob;
    MaterialToolbar topAppBar;
    FloatingActionButton fabCreateJob;
    SearchView searchView;

    FrameLayout flContent, flEmptyState;
    SharedPreferences sp;


    public AgentJobsFragment() {
        // Required empty public constructor
    }

    /**
     * @param userId The userId of the agent managing the job. This field is nullable
     *               if the agent is the one direct managing the fragment
     * @return A new instance of fragment AgentJobsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AgentJobsFragment newInstance(String userId, boolean showAppBarIfAgentJobsFragmentClosed, boolean showAppBarIfAgentManageJobApplicationsFragmentClosed) {
        AgentJobsFragment fragment = new AgentJobsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, userId);
        args.putBoolean(ARG_PARAM2, showAppBarIfAgentJobsFragmentClosed);
        args.putBoolean(ARG_PARAM3, showAppBarIfAgentManageJobApplicationsFragmentClosed);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_PARAM1);
            showAppBarIfAgentJobsFragmentClosed = getArguments().getBoolean(ARG_PARAM2, true);
            showAppBarIfAgentManageJobApplicationsFragmentClosed = getArguments().getBoolean(ARG_PARAM3, true);
        } else {
            showAppBarIfAgentJobsFragmentClosed = true;
            showAppBarIfAgentManageJobApplicationsFragmentClosed = true;
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
            topAppBar.setNavigationIconTint(ContextCompat.getColor(requireContext(), R.color.background));
            topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getParentFragmentManager().popBackStack();
                }
            });
        }
        fabCreateJob = view.findViewById(R.id.fabAgentCreateJob);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        jobList = new ArrayList<>();
        getJobs();

        MenuItem searchMenuItem = topAppBar.getMenu().findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint("Search for listings...");
        searchView.setQuery("",false);
        EditText etSearch = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        etSearch.setTextColor(requireContext().getColor(R.color.background));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterJobs(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                filterJobs(query);
                return true;
            }
        });

        flContent = view.findViewById(R.id.flContent);
        flEmptyState = view.findViewById(R.id.flEmptyState);
        TextView emptyStateText = flEmptyState.findViewById(R.id.emptyStateText);
        emptyStateText.setText("Oops\nNo listings found");
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
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flAgentJob, AgentManageJobApplicationsFragment.newInstance(jobId, userId, showAppBarIfAgentManageJobApplicationsFragmentClosed)).addToBackStack(null).commit();
            }

            @Override
            public void onEditJob(String jobId) {
                // check for double click
                FragmentManager fragmentManager = getChildFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
                    fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flAgentJob, EditAgentJobFragment.newInstance(jobId, userId)).addToBackStack(null).commit();
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
        fabCreateJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (System.currentTimeMillis() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = System.currentTimeMillis();
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flAgentJob, CreateAgentJobFragment.newInstance(userId)).addToBackStack(null).commit();
            }
        });
        getChildFragmentManager().setFragmentResultListener("jobResult", this, (requestKey, result) -> {
            boolean isUpdated = result.getBoolean("isUpdated");
            if (isUpdated) {
                refreshJobs();
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isAdded()) {
                    getParentFragmentManager().popBackStack();
                }

            }
        });
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        searchView.setIconified(true);
        searchView.setIconified(true);
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
        if (userId != null && showAppBarIfAgentJobsFragmentClosed) {
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
                if (!jobList.isEmpty()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) flContent.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    flContent.setLayoutParams(params);
                } else {
                    flEmptyState.setVisibility(View.VISIBLE);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) flContent.getLayoutParams();
                    params.gravity = Gravity.CENTER;
                    flContent.setLayoutParams(params);
                }
                loadingDialog.dismiss();
            }
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(requireContext()).onErrorResponse(error);
        });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void refreshJobs() {
        jobList.clear();
        recyclerView.setVisibility(View.GONE);
        getJobs();
        agentJobCardAdapter.notifyDataSetChanged();
        searchView.setQuery("", false);
        searchView.setIconified(true);
    }

    private void filterJobs(String query) {
        if (query.isEmpty()) {
            agentJobCardAdapter.filterList(jobList);
            flEmptyState.setVisibility(View.GONE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) flContent.getLayoutParams();
            params.gravity = Gravity.TOP;
            flContent.setLayoutParams(params);
            return;
        }
        List<Job> filteredList = new ArrayList<>();
        for (Job job : jobList) {
            if (job.getPosition().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(job);
            }
        }
        if (!filteredList.isEmpty()) {
            flEmptyState.setVisibility(View.GONE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) flContent.getLayoutParams();
            params.gravity = Gravity.TOP;
            flContent.setLayoutParams(params);
        } else {
            flEmptyState.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) flContent.getLayoutParams();
            params.gravity = Gravity.CENTER;
            flContent.setLayoutParams(params);
        }
        agentJobCardAdapter.filterList(filteredList);
    }
}