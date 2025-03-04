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

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.JobSeekerSmallJobCardAdapter;
import com.example.jobapplicationmdad.adapters.JobSeekerJobCardAdapter;
import com.example.jobapplicationmdad.fragments.jobseeker.job.JobSeekerJobDetailsFragment;
import com.example.jobapplicationmdad.model.Agency;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

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
    RecyclerView recyclerViewJobs;
    RecyclerView recyclerViewRecommendedJobs;
    List<Job> jobList;
    List<Job> recommendedJobList;
    View dialogView;
    AlertDialog loadingDialog;
    JobSeekerSmallJobCardAdapter smallJobCardAdapter;
    JobSeekerJobCardAdapter jobCardAdapter;
    SwipeRefreshLayout srlJobSeekerHome;
    SearchView searchView;
    SearchBar searchBar;


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
        dialogView = inflater.inflate(R.layout.dialog_loader, container, false);
        return inflater.inflate(R.layout.fragment_job_seeker_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        recommendedJobList = new ArrayList<>();
        jobList = new ArrayList<>();
        getJobs();
        searchView = view.findViewById(R.id.svJobs);
        searchBar = view.findViewById(R.id.search_bar);
        srlJobSeekerHome = view.findViewById(R.id.srlJobSeekerHome);
        recyclerViewRecommendedJobs = view.findViewById(R.id.rvJobSeekerSmallJobCard);
        recyclerViewRecommendedJobs.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));


        // Set the adapter
        smallJobCardAdapter = new JobSeekerSmallJobCardAdapter(recommendedJobList, new JobSeekerSmallJobCardAdapter.OnJobClickListener() {
            @Override
            public void onViewJobDetails(String jobId) {
                // check for double click
                FragmentManager fragmentManager = getParentFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
                    fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flJobSeekerHome, JobSeekerJobDetailsFragment.newInstance(jobId,false,true)).addToBackStack(null).commit();
            }
        });
        recyclerViewRecommendedJobs.setAdapter(smallJobCardAdapter);

        recyclerViewJobs = view.findViewById(R.id.rvJobSeekerJobCard);
        recyclerViewJobs.setLayoutManager(new LinearLayoutManager(requireContext()));
        jobCardAdapter = new JobSeekerJobCardAdapter(jobList, new JobSeekerJobCardAdapter.OnJobClickListener() {
            @Override
            public void onViewJobDetails(String jobId) {
                // check for double click
                FragmentManager fragmentManager = getParentFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
                    fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flJobSeekerHome, JobSeekerJobDetailsFragment.newInstance(jobId,false,true)).addToBackStack(null).commit();
            }
        });
        recyclerViewJobs.setAdapter(jobCardAdapter);

        srlJobSeekerHome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshJobs();
                srlJobSeekerHome.setRefreshing(false);
            }
        });
        searchView.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchView.hide();
                String query = textView.getText().toString();
                FragmentManager fragmentManager = getChildFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
                    fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flJobSeekerHome, JobSeekerSearchedJobsFragment.newInstance(query)).addToBackStack(null).commit();
                ((MainActivity) requireActivity()).hideBottomNav();
                return false;
            }
        });
    }


    private void getJobs() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
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
                        Agency agency = new Agency();
                        agency.setName(jobObject.getString("agency_name"));
                        User user = new User();
                        user.setAgency(agency);
                        Job job = new Job(jobObject.getString("jobId"), jobObject.getString("position"), jobObject.getString("location"), jobObject.optDouble("partTimeSalary", 0.0), jobObject.optDouble("fullTimeSalary", 0.0), jobObject.getString("updatedAt"), user);
                        // add the first 5 jobs to the recommended job list
                        if (i < 5) {
                            recommendedJobList.add(job);
                        } else {
                            jobList.add(job);
                        }
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                // toggle the visibility of loader
                recyclerViewRecommendedJobs.setVisibility(View.VISIBLE);
                recyclerViewJobs.setVisibility(View.VISIBLE);
                loadingDialog.dismiss();
            }

        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(requireContext()).onErrorResponse(error);
        });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);

    }

    private void refreshJobs() {
        recommendedJobList.clear();
        jobList.clear();
        recyclerViewRecommendedJobs.setVisibility(View.GONE);
        recyclerViewJobs.setVisibility(View.GONE);
        getJobs();
        smallJobCardAdapter.notifyDataSetChanged();
        jobCardAdapter.notifyDataSetChanged();
    }

}