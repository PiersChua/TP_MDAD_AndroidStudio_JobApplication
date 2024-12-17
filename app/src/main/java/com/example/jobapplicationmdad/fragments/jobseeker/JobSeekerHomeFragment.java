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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.JobCardAdapter;
import com.example.jobapplicationmdad.adapters.ProfileAdapter;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JobSeekerHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobSeekerHomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String get_jobs_url = MainActivity.root_url + "/api/job-seeker/get-jobs.php";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SharedPreferences sp;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    List<Job> jobList;
    JobCardAdapter jobCardAdapter;


    public JobSeekerHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JobSeekerHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobSeekerHomeFragment newInstance(String param1, String param2) {
        JobSeekerHomeFragment fragment = new JobSeekerHomeFragment();
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
        return inflater.inflate(R.layout.fragment_job_seeker_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        progressBar = view.findViewById(R.id.pbJobSeekerHome);
        recyclerView = view.findViewById(R.id.rvJobSeekerJobCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        jobList = new ArrayList<>();
        getJobs();

    }

    private void getJobs() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, get_jobs_url, params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("type").equals("Success")) {
                        JSONArray jobsArray = response.getJSONArray("data");
                        for (int i = 0; i < jobsArray.length(); i++) {
                            JSONObject jobObject = jobsArray.getJSONObject(i);
                            Job job = new Job(jobObject.getString("jobId"), jobObject.getString("position"), jobObject.getString("responsibilities"), jobObject.getString("location"), jobObject.optDouble("partTimeSalary", 0.0), jobObject.optDouble("fullTimeSalary", 0.0));
                            jobList.add(job);
                        }
                        // Set the adapter
                        jobCardAdapter = new JobCardAdapter(jobList, new JobCardAdapter.OnJobClickListener() {
                            @Override
                            public void onViewJobDetails(String jobId) {
                                getParentFragmentManager().beginTransaction().replace(R.id.flMain, JobSeekerJobDetailsFragment.newInstance(jobId)).addToBackStack(null).commit();
                            }
                        });
                        recyclerView.setAdapter(jobCardAdapter);

                        // toggle the visibility of loader
                        progressBar.setVisibility(View.GONE);
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