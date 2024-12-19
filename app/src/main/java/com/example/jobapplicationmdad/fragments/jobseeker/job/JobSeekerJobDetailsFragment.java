package com.example.jobapplicationmdad.fragments.jobseeker.job;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JobSeekerJobDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobSeekerJobDetailsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "jobId";

    private String jobId;
    SharedPreferences sp;
    MaterialToolbar topAppBar;
    NestedScrollView nsvJobSeekerJobDetails;
    LinearProgressIndicator progressIndicator;
    Button btnApplyJob;
    TextView tvPosition, tvAgencyName, tvLocation, tvSalary, tvEmploymentType, tvOrganisation, tvSchedule, tvDescription, tvResponsibilities;
    private static final String get_job_url = MainActivity.root_url + "/api/job-seeker/get-job.php";
    private static final String create_job_application_url = MainActivity.root_url + "/api/job-seeker/create-job-application.php";

    public JobSeekerJobDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param jobId The id associated with the job
     * @return A new instance of fragment JobSeekerJobDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobSeekerJobDetailsFragment newInstance(String jobId) {
        JobSeekerJobDetailsFragment fragment = new JobSeekerJobDetailsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_seeker_job_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        getJobDetails();
        topAppBar = view.findViewById(R.id.topAppBarJobSeekerJobDetails);
        progressIndicator = view.findViewById(R.id.piJobSeekerJobDetails);
        nsvJobSeekerJobDetails = view.findViewById(R.id.nsvJobSeekerJobDetails);
        btnApplyJob = view.findViewById(R.id.btnApplyJob);

        // TextViews
        tvPosition = view.findViewById(R.id.tvJobSeekerJobDetailsPosition);
        tvAgencyName = view.findViewById(R.id.tvJobSeekerJobDetailsAgencyName);
        tvLocation = view.findViewById(R.id.tvJobSeekerJobDetailsLocation);
        tvSalary = view.findViewById(R.id.tvJobSeekerJobDetailsSalary);
        tvEmploymentType = view.findViewById(R.id.tvJobSeekerJobDetailsEmploymentType);
        tvOrganisation = view.findViewById(R.id.tvJobSeekerJobDetailsOrganisation);
        tvSchedule = view.findViewById(R.id.tvJobSeekerJobDetailsSchedule);
        tvDescription = view.findViewById(R.id.tvJobSeekerJobDetailsDescription);
        tvResponsibilities = view.findViewById(R.id.tvJobSeekerJobDetailsResponsibilities);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        btnApplyJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createJobApplication();
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

    private void getJobDetails() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("jobId", jobId);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, get_job_url, params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("type").equals("Success")) {
                        JSONObject jobJson = response.getJSONObject("data");
                        Job job = new Job();
                        job.setJobId(jobJson.getString("jobId"));
                        job.setPosition(jobJson.getString("position"));
                        job.setResponsibilities(jobJson.getString("responsibilities"));
                        job.setDescription(jobJson.getString("description"));
                        job.setLocation(jobJson.getString("location"));
                        job.setSchedule(jobJson.getString("schedule"));
                        job.setOrganisation(jobJson.getString("organisation"));
                        job.setPartTimeSalary(jobJson.optDouble("partTimeSalary", 0.0));
                        job.setFullTimeSalary(jobJson.optDouble("fullTimeSalary", 0.0));
                        job.setUserId(jobJson.getString("userId"));
                        job.setCreatedAt(jobJson.getString("createdAt"));
                        job.setUpdatedAt(jobJson.getString("updatedAt"));

                        populateJobItems(job);
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

    private void populateJobItems(Job job) {
        StringBuilder salary = new StringBuilder();
        StringBuilder employmentType = new StringBuilder();
        if (job.getPartTimeSalary() != 0.0) {
            salary.append("$").append(String.format("%.2f", job.getPartTimeSalary())).append(" per hour");
            employmentType.append("Part-Time");
        }
        if (job.getFullTimeSalary() != 0.0) {
            if (salary.length() > 0) {
                salary.append("/");
                employmentType.append(" / ");
            }
            salary.append("$").append(String.format("%.2f", job.getFullTimeSalary())).append(" per month");
            employmentType.append("Full-Time");
        }
        if (salary.length() == 0) {
            tvSalary.setVisibility(View.GONE);
        } else {
            tvSalary.setText(salary);
        }

        tvPosition.setText(job.getPosition());
        // TODO: fetch nested query
        //tvAgencyName.setText(job.);
        tvLocation.setText(job.getLocation());
        tvEmploymentType.setText(employmentType);
        tvOrganisation.setText(job.getOrganisation());
        tvSchedule.setText(job.getSchedule());
        tvDescription.setText(job.getDescription());
        tvResponsibilities.setText(job.getResponsibilities());
        // Toggle the loader
        nsvJobSeekerJobDetails.setVisibility(View.VISIBLE);
        progressIndicator.setVisibility(View.GONE);
    }

    private void createJobApplication() {
        Map<String, String> params = new HashMap<>();
        params.put("userId", sp.getString("userId", ""));
        params.put("jobId", jobId);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, create_job_application_url, params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("type").equals("Success")) {
                        Toast.makeText(requireContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                        getParentFragmentManager().popBackStack();
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
