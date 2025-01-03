package com.example.jobapplicationmdad.fragments.agent.job;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.AgentJobApplicationCardAdapter;
import com.example.jobapplicationmdad.adapters.AgentJobCardAdapter;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.model.JobApplication;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AgentManageJobApplicationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgentManageJobApplicationsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "jobId";
    private static final String ARG_PARAM2 = "userId";
    private static final String ARG_PARAM3 = "showAppBarIfAgentManageJobApplicationsFragmentClosed";

    // TODO: Rename and change types of parameters
    private String jobId;
    private String userId;
    private boolean showAppBarIfAgentManageJobApplicationsFragmentClosed;
    RecyclerView recyclerView;
    List<JobApplication> jobApplicationList;
    View dialogView;
    AlertDialog loadingDialog;
    AgentJobApplicationCardAdapter agentJobApplicationCardAdapter;
    SwipeRefreshLayout srlAgentJobApplication;
    MaterialToolbar topAppBar;
    SharedPreferences sp;
    FrameLayout flContent, flEmptyState;
    private static final String get_job_applications_url = MainActivity.root_url + "/api/agent/get-job-applications.php";
    private static final String update_job_application_url = MainActivity.root_url + "/api/agent/update-job-application.php";

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
    public static AgentManageJobApplicationsFragment newInstance(String jobId, String userId, boolean showAppBarIfAgentManageJobApplicationsFragmentClosed) {
        AgentManageJobApplicationsFragment fragment = new AgentManageJobApplicationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, jobId);
        args.putString(ARG_PARAM2, userId);
        args.putBoolean(ARG_PARAM3, showAppBarIfAgentManageJobApplicationsFragmentClosed);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jobId = getArguments().getString(ARG_PARAM1);
            userId = getArguments().getString(ARG_PARAM2);
            showAppBarIfAgentManageJobApplicationsFragmentClosed = getArguments().getBoolean(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialogView = inflater.inflate(R.layout.dialog_loader, container, false);
        return inflater.inflate(R.layout.fragment_agent_manage_job_applications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        topAppBar = view.findViewById(R.id.topAppBarAgentManageJobApplication);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        jobApplicationList = new ArrayList<>();
        getJobApplications();
        flContent = view.findViewById(R.id.flContent);
        flEmptyState = view.findViewById(R.id.flEmptyState);
        TextView emptyStateText = flEmptyState.findViewById(R.id.emptyStateText);
        emptyStateText.setText("Oops\nNo applications found");
        srlAgentJobApplication = view.findViewById(R.id.srlAgentJobApplication);
        recyclerView = view.findViewById(R.id.rvAgentJobApplicationCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        agentJobApplicationCardAdapter = new AgentJobApplicationCardAdapter(jobApplicationList, new AgentJobApplicationCardAdapter.OnJobClickListener() {
            @Override
            public void onViewUser(String userId) {
                // check for double click
                FragmentManager fragmentManager = getChildFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
                    fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flAgentManageJobApplication, AgentApplicantDetailsFragment.newInstance(userId)).addToBackStack(null).commit();
            }

            @Override
            public void onAcceptJobApplication(String userId) {
                new MaterialAlertDialogBuilder(requireContext()).setTitle("Accept Job Application").setMessage("You are about to accept this application.\nDo you wish to proceed?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        updateJobApplication(userId, JobApplication.Status.ACCEPTED);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).show();

            }

            @Override
            public void onRejectJobApplication(String userId) {
                new MaterialAlertDialogBuilder(requireContext()).setTitle("Reject Job Application").setMessage("You are about to reject this application.\nDo you wish to proceed?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        updateJobApplication(userId, JobApplication.Status.REJECTED);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).show();

            }
        });
        recyclerView.setAdapter(agentJobApplicationCardAdapter);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
        srlAgentJobApplication.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshApplications();
                srlAgentJobApplication.setRefreshing(false);
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
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).hideBottomNav();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (showAppBarIfAgentManageJobApplicationsFragmentClosed) {
            ((MainActivity) requireActivity()).showBottomNav();
        }

    }

    private void getJobApplications() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("jobId", jobId);
        params.put("agentUserId", userId != null ? userId : sp.getString("userId", ""));
        String url = UrlUtil.constructUrl(get_job_applications_url, params);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jobApplicationArray = response.getJSONArray("data");
                    for (int i = 0; i < jobApplicationArray.length(); i++) {
                        JSONObject jobApplicationObject = jobApplicationArray.getJSONObject(i);
                        JobApplication jobApplication = new JobApplication();
                        User user = new User();
                        user.setFullName(jobApplicationObject.getString("user_full_name"));
                        user.setEmail(jobApplicationObject.getString("user_email"));
                        user.setPhoneNumber(jobApplicationObject.getString("user_phone_number"));
                        jobApplication.setUser(user);
                        jobApplication.setUserId(jobApplicationObject.getString("userId"));
                        jobApplication.setStatus(JobApplication.Status.valueOf(jobApplicationObject.getString("status")));
                        jobApplication.setUpdatedAt(jobApplicationObject.getString("updatedAt"));
                        jobApplicationList.add(jobApplication);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                if (!jobApplicationList.isEmpty()) {
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

    private void updateJobApplication(String userId, JobApplication.Status status) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("jobApplicationUserId", userId);
        params.put("jobId", jobId);
        params.put("status", status.toString());
        String url = UrlUtil.constructUrl(update_job_application_url, params);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, url, params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), response.getString("message"), Snackbar.LENGTH_SHORT).show();
                    refreshApplications();
                    Bundle result = new Bundle();
                    result.putBoolean("isUpdated", true);
                    getParentFragmentManager().setFragmentResult("jobResult", result);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, VolleyErrorHandler.newErrorListener(requireContext())
        );
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void refreshApplications() {
        jobApplicationList.clear();
        recyclerView.setVisibility(View.GONE);
        getJobApplications();
        agentJobApplicationCardAdapter.notifyDataSetChanged();
    }
}