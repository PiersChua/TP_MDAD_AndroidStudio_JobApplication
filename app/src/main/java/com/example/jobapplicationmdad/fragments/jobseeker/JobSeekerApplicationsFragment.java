package com.example.jobapplicationmdad.fragments.jobseeker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.JobSeekerJobApplicationCardAdapter;
import com.example.jobapplicationmdad.fragments.jobseeker.job.JobSeekerJobDetailsFragment;
import com.example.jobapplicationmdad.model.Agency;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.model.JobApplication;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.UrlUtil;
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
 * Use the {@link JobSeekerApplicationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobSeekerApplicationsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String get_job_applications_url = MainActivity.root_url + "/api/job-seeker/get-job-applications.php";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SharedPreferences sp;
    RecyclerView recyclerView;
    View dialogView;
    AlertDialog loadingDialog;
    List<JobApplication> jobApplicationList;
    JobSeekerJobApplicationCardAdapter jobApplicationCardAdapter;
    SwipeRefreshLayout srlJobSeekerApplication;

    public JobSeekerApplicationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JobSeekerApplicationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobSeekerApplicationsFragment newInstance(String param1, String param2) {
        JobSeekerApplicationsFragment fragment = new JobSeekerApplicationsFragment();
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
        dialogView = inflater.inflate(R.layout.dialog_loader, container, false);
        return inflater.inflate(R.layout.fragment_job_seeker_applications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        jobApplicationList = new ArrayList<>();
        getJobApplications();
        srlJobSeekerApplication = view.findViewById(R.id.srlJobSeekerApplication);

        recyclerView = view.findViewById(R.id.rvJobSeekerJobApplicationCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Set the adapter
        jobApplicationCardAdapter = new JobSeekerJobApplicationCardAdapter(jobApplicationList, new JobSeekerJobApplicationCardAdapter.OnJobClickListener() {

            @Override
            public void onViewJobDetails(String jobId) {
                // check for double click
                FragmentManager fragmentManager = getParentFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
                    fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flJobSeekerJob, JobSeekerJobDetailsFragment.newInstance(jobId)).addToBackStack(null).commit();
            }
        });
        recyclerView.setAdapter(jobApplicationCardAdapter);

        srlJobSeekerApplication.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshJobApplications();
                srlJobSeekerApplication.setRefreshing(false);
            }
        });
    }

    private void getJobApplications() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        String url = UrlUtil.constructUrl(get_job_applications_url, params);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jobApplicationsArray = response.getJSONArray("data");
                    for (int i = 0; i < jobApplicationsArray.length(); i++) {
                        JSONObject jobApplicationObject = jobApplicationsArray.getJSONObject(i);
                        Agency agency = new Agency();
                        agency.setName(jobApplicationObject.getString("agency_name"));
                        User user = new User();
                        user.setAgency(agency);
                        Job job = new Job(jobApplicationObject.getString("jobId"), jobApplicationObject.getString("position"), jobApplicationObject.getString("responsibilities"), jobApplicationObject.getString("location"), jobApplicationObject.optDouble("partTimeSalary", 0.0), jobApplicationObject.optDouble("fullTimeSalary", 0.0), jobApplicationObject.getString("updatedAt"), user);
                        JobApplication jobApplication = new JobApplication(JobApplication.Status.valueOf(jobApplicationObject.getString("status")), jobApplicationObject.getString("job_application_created_at"), jobApplicationObject.getString("job_application_updated_at"), job);
                        jobApplicationList.add(jobApplication);

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

    private void refreshJobApplications() {
        jobApplicationList.clear();
        recyclerView.setVisibility(View.GONE);
        getJobApplications();
        jobApplicationCardAdapter.notifyDataSetChanged();
    }
}