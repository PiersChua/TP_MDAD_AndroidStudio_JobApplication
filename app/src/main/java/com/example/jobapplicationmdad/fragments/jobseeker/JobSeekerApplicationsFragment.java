package com.example.jobapplicationmdad.fragments.jobseeker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.JobApplicationCardAdapter;
import com.example.jobapplicationmdad.fragments.jobseeker.job.JobSeekerJobDetailsFragment;
import com.example.jobapplicationmdad.model.Agency;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.model.JobApplication;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.progressindicator.CircularProgressIndicator;

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
    CircularProgressIndicator progressIndicator;
    RecyclerView recyclerView;
    List<JobApplication> jobApplicationList;
    JobApplicationCardAdapter jobApplicationCardAdapter;

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
        return inflater.inflate(R.layout.fragment_job_seeker_applications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        getJobApplications();
        progressIndicator = view.findViewById(R.id.piJobSeekerApplication);
        recyclerView = view.findViewById(R.id.rvJobSeekerJobApplicationCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        jobApplicationList = new ArrayList<>();
        // Set the adapter
        jobApplicationCardAdapter = new JobApplicationCardAdapter(jobApplicationList, new JobApplicationCardAdapter.OnJobClickListener() {

            @Override
            public void onViewJobDetails(String jobId) {
                getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flJobSeekerJob, JobSeekerJobDetailsFragment.newInstance(jobId)).addToBackStack(null).commit();
            }
        });
        recyclerView.setAdapter(jobApplicationCardAdapter);
    }

    private void getJobApplications() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        String url = UrlUtil.constructUrl(get_job_applications_url, params);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("type").equals("Success")) {
                        JSONArray jobsArray = response.getJSONArray("data");
                        for (int i = 0; i < jobsArray.length(); i++) {
                            JSONObject jobObject = jobsArray.getJSONObject(i);
                            Agency agency = new Agency();
                            agency.setName(jobObject.getString("agency_name"));
                            User user = new User();
                            user.setAgency(agency);
                            Job job = new Job(jobObject.getString("jobId"), jobObject.getString("position"), jobObject.getString("responsibilities"), jobObject.getString("location"), jobObject.optDouble("partTimeSalary", 0.0), jobObject.optDouble("fullTimeSalary", 0.0), jobObject.getString("updatedAt"), user);
                            JobApplication jobApplication = new JobApplication(jobObject.getString("status"), jobObject.getString("job_application_created_at"), jobObject.getString("job_application_updated_at"), job);
                            jobApplicationList.add(jobApplication);
                        }
                        // toggle the visibility of loader
                        progressIndicator.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else if (response.getString("type").equals("Error")) {
                        Toast.makeText(requireContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

        }, VolleyErrorHandler.newErrorListener(requireContext()));
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }
}