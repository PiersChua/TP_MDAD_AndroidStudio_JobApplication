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


import com.android.volley.Request;
import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.FavouriteJobCardAdapter;
import com.example.jobapplicationmdad.fragments.jobseeker.job.JobSeekerJobDetailsFragment;
import com.example.jobapplicationmdad.model.Agency;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.UrlUtil;
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
    RecyclerView recyclerView;
    List<Job> favouriteJoblist;
    View dialogView;
    AlertDialog loadingDialog;
    FavouriteJobCardAdapter favouriteJobCardAdapter;
    SwipeRefreshLayout srlJobSeekerFavourite;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialogView = inflater.inflate(R.layout.dialog_loader, container, false);

        return inflater.inflate(R.layout.fragment_job_seeker_favourite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        favouriteJoblist = new ArrayList<>();
        getFavouriteJobs();
        srlJobSeekerFavourite = view.findViewById(R.id.srlJobSeekerFavourite);
        recyclerView = view.findViewById(R.id.rvJobSeekerFavouriteJobCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        // Set the adapter
        favouriteJobCardAdapter = new FavouriteJobCardAdapter(favouriteJoblist, new FavouriteJobCardAdapter.OnJobClickListener() {
            @Override
            public void onViewJobDetails(String jobId) {
                // check for double click
                FragmentManager fragmentManager = getParentFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
                    fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flJobSeekerJob, JobSeekerJobDetailsFragment.newInstance(jobId, true)).addToBackStack(null).commit();
            }

            @Override
            public void onRemoveFavourite(String jobId, int position) {
                removeFavouriteJob(jobId, position);
            }
        });
        recyclerView.setAdapter(favouriteJobCardAdapter);

        srlJobSeekerFavourite.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            // re-fetch the data and turn off the refreshing
            @Override
            public void onRefresh() {
                refreshFavouriteJobs();
                srlJobSeekerFavourite.setRefreshing(false);
            }
        });
        getParentFragmentManager().setFragmentResultListener("favouriteJobResult", this, (requestKey, result) -> {
            boolean isFavouriteChanged = result.getBoolean("isFavouriteRemoved");
            if (isFavouriteChanged) {
                refreshFavouriteJobs();
                srlJobSeekerFavourite.setRefreshing(false);
            }
        });
    }

    private void getFavouriteJobs() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        String url = UrlUtil.constructUrl(get_favourite_jobs_url, params);
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
                        Job job = new Job(jobObject.getString("jobId"), jobObject.getString("position"), jobObject.getString("responsibilities"), jobObject.getString("location"), jobObject.optDouble("partTimeSalary", 0.0), jobObject.optDouble("fullTimeSalary", 0.0), jobObject.getString("updatedAt"), user);
                        favouriteJoblist.add(job);


                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                // toggle the visibility of loader
                loadingDialog.dismiss();
                recyclerView.setVisibility(favouriteJoblist.isEmpty() ? View.GONE : View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }

        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(requireContext(), requireActivity().findViewById(android.R.id.content)).onErrorResponse(error);
        });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void removeFavouriteJob(String jobId, int position) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", sp.getString("userId", ""));
        params.put("jobId", jobId);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, favourite_job_url, params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Snackbar.make(requireContext(),requireActivity().findViewById(android.R.id.content), response.getString("message"), Snackbar.LENGTH_SHORT).setAnchorView(requireActivity().findViewById(R.id.bottom_navigation)).show();
                    favouriteJoblist.remove(position);
                    favouriteJobCardAdapter.notifyItemRemoved(position);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, VolleyErrorHandler.newErrorListener(requireContext(),requireActivity().findViewById(android.R.id.content)));
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void refreshFavouriteJobs() {
        favouriteJoblist.clear();
        getFavouriteJobs();
        recyclerView.setVisibility(View.GONE);
        favouriteJobCardAdapter.notifyDataSetChanged();

    }
}