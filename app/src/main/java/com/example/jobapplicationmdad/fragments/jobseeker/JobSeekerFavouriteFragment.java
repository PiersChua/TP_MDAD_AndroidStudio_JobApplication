package com.example.jobapplicationmdad.fragments.jobseeker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.FavouriteJobCardAdapter;
import com.example.jobapplicationmdad.adapters.JobCardAdapter;
import com.example.jobapplicationmdad.fragments.jobseeker.job.JobSeekerJobDetailsFragment;
import com.example.jobapplicationmdad.model.Job;
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
 * Use the {@link JobSeekerFavouriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobSeekerFavouriteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String get_favourite_jobs_url = MainActivity.root_url + "/api/job-seeker/get-favourite-jobs.php";
    private static final String favourite_job_url = MainActivity.root_url + "/api/job-seeker/favourite-job.php";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SharedPreferences sp;
    CircularProgressIndicator progressIndicator;
    RecyclerView recyclerView;
    List<Job> favouriteJoblist;
    FavouriteJobCardAdapter favouriteJobCardAdapter;

    public JobSeekerFavouriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JobSeekerFavouriteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobSeekerFavouriteFragment newInstance(String param1, String param2) {
        JobSeekerFavouriteFragment fragment = new JobSeekerFavouriteFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_seeker_favourite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        getFavouriteJobs();
        progressIndicator = view.findViewById(R.id.piJobSeekerFavourite);
        recyclerView = view.findViewById(R.id.rvJobSeekerFavouriteJobCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        favouriteJoblist = new ArrayList<>();
        // Set the adapter
        favouriteJobCardAdapter = new FavouriteJobCardAdapter(favouriteJoblist, new FavouriteJobCardAdapter.OnJobClickListener() {
            @Override
            public void onViewJobDetails(String jobId) {
                getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flMain, JobSeekerJobDetailsFragment.newInstance(jobId)).addToBackStack(null).commit();
            }

            @Override
            public void onRemoveFavourite(String jobId,int position) {
                removeFavouriteJob(jobId,position);
            }
        });
        recyclerView.setAdapter(favouriteJobCardAdapter);
    }

    private void getFavouriteJobs() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        String url = UrlUtil.constructUrl(get_favourite_jobs_url,params);
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
                            Job job = new Job(jobObject.getString("jobId"), jobObject.getString("position"), jobObject.getString("responsibilities"), jobObject.getString("location"), jobObject.optDouble("partTimeSalary", 0.0), jobObject.optDouble("fullTimeSalary", 0.0), jobObject.getString("updatedAt"));
                            favouriteJoblist.add(job);
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

    private void removeFavouriteJob(String jobId,int position){
        Map<String, String> params = new HashMap<>();
        params.put("userId", sp.getString("userId", ""));
        params.put("jobId", jobId);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, favourite_job_url, params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("type").equals("Success")) {
                        Toast.makeText(requireContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                        favouriteJoblist.remove(position);
                        favouriteJobCardAdapter.notifyItemRemoved(position);
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