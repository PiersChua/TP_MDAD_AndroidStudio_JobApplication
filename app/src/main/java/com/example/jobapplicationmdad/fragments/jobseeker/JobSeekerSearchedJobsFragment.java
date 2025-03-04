package com.example.jobapplicationmdad.fragments.jobseeker;

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

import android.text.InputFilter;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.JobSeekerJobCardAdapter;
import com.example.jobapplicationmdad.fragments.jobseeker.job.JobSeekerJobDetailsFragment;
import com.example.jobapplicationmdad.model.Agency;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.StringUtil;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JobSeekerSearchedJobsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobSeekerSearchedJobsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "query";
    private static final String ARG_PARAM2 = "param2";
    private static final String get_jobs_url = MainActivity.root_url + "/api/job-seeker/get-jobs.php";

    // TODO: Rename and change types of parameters
    private String query;

    RecyclerView recyclerView;
    List<Job> jobList;
    View dialogView, filterDialogView;
    AlertDialog loadingDialog, filterDialog;
    JobSeekerJobCardAdapter jobCardAdapter;
    SwipeRefreshLayout srlJobSeekerSearchedJobs;
    FrameLayout flContent, flEmptyState;
    MaterialButton btnBack, btnFilter;
    SharedPreferences sp;
    SearchView searchView;
    SearchBar searchBar;
    RadioGroup rgJobType;
    TextInputLayout etMinSalaryFilterLayout;
    EditText etMinSalaryFilter;
    ChipGroup chipGroup;


    public JobSeekerSearchedJobsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param query The query passed from the searchview.
     * @return A new instance of fragment JobSeekerSearchedJobsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobSeekerSearchedJobsFragment newInstance(String query) {
        JobSeekerSearchedJobsFragment fragment = new JobSeekerSearchedJobsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            query = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialogView = inflater.inflate(R.layout.dialog_loader, container, false);
        filterDialogView = inflater.inflate(R.layout.dialog_job_filter, container, false);
        return inflater.inflate(R.layout.fragment_job_seeker_searched_jobs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        MaterialAlertDialogBuilder filterBuilder = new MaterialAlertDialogBuilder(requireContext());
        filterBuilder.setView(filterDialogView);
        rgJobType = filterDialogView.findViewById(R.id.rgJobType);
        etMinSalaryFilterLayout = filterDialogView.findViewById(R.id.etMinSalaryFilterLayout);
        etMinSalaryFilter = filterDialogView.findViewById(R.id.etMinSalaryFilter);
        etMinSalaryFilter.setFilters(new InputFilter[]{new StringUtil.DecimalDigitsInputFilter(4, 2)});
        chipGroup = view.findViewById(R.id.cgJobFilter);
        filterBuilder.setPositiveButton("Confirm", null).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                rgJobType.clearCheck();
                chipGroup.removeAllViews();
                refreshSearchedJobs();
            }
        });
        filterDialog = filterBuilder.create();
        filterDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = filterDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (rgJobType.getCheckedRadioButtonId() == -1) {
                            etMinSalaryFilterLayout.setError("Select either part time or full time");
                            return; // Prevent dialog from closing
                        }
                        chipGroup.removeAllViews();
                        Chip chip = new Chip(requireContext());
                        if(rgJobType.getCheckedRadioButtonId()==R.id.rbPartTime){
                            chip.setText("Part Time");
                        }
                        else{
                            chip.setText("Full Time");
                        }
                        chip.setCloseIconVisible(true);
                        chip.setOnCloseIconClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                chipGroup.removeView(chip);
                                rgJobType.clearCheck();
                                refreshSearchedJobs();
                            }
                        });
                        chipGroup.addView(chip);
                        refreshSearchedJobs();
                        filterDialog.dismiss();
                    }
                });
            }
        });
        rgJobType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rbPartTime) {
                    etMinSalaryFilter.setText("0.00");
                    etMinSalaryFilterLayout.setHelperText("Max: 9999.99");
                    etMinSalaryFilter.setFilters(new InputFilter[]{new StringUtil.DecimalDigitsInputFilter(4, 2)});
                } else if (i == R.id.rbFullTime) {
                    etMinSalaryFilter.setText("0.00");
                    etMinSalaryFilterLayout.setHelperText("Max: 999999.99");
                    etMinSalaryFilter.setFilters(new InputFilter[]{new StringUtil.DecimalDigitsInputFilter(6, 2)});
                }
            }
        });

        jobList = new ArrayList<>();
        getSearchedJobs();
        btnBack = view.findViewById(R.id.btnBack);
        btnFilter = view.findViewById(R.id.btnFilter);
        flContent = view.findViewById(R.id.flContent);
        flEmptyState = view.findViewById(R.id.flEmptyState);
        TextView emptyStateText = flEmptyState.findViewById(R.id.emptyStateText);
        emptyStateText.setText("Oops\nNo jobs found");
        searchView = view.findViewById(R.id.svJobs);
        searchBar = view.findViewById(R.id.search_bar);
        srlJobSeekerSearchedJobs = view.findViewById(R.id.srlJobSeekerSearchedJobs);
        recyclerView = view.findViewById(R.id.rvJobSeekerJobCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialog.show();

            }
        });

        jobCardAdapter = new JobSeekerJobCardAdapter(jobList, new JobSeekerJobCardAdapter.OnJobClickListener() {
            @Override
            public void onViewJobDetails(String jobId) {
                // check for double click
                FragmentManager fragmentManager = getChildFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
                    fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flJobSeekerSearchedJobs, JobSeekerJobDetailsFragment.newInstance(jobId, false, false)).addToBackStack(null).commit();
            }
        });
        recyclerView.setAdapter(jobCardAdapter);

        srlJobSeekerSearchedJobs.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshSearchedJobs();
                srlJobSeekerSearchedJobs.setRefreshing(false);
            }
        });
        searchBar.setText(query);
        searchView.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchView.hide();
                searchBar.setText(textView.getText());
                query = textView.getText().toString();
                refreshSearchedJobs();
                return false;
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isAdded() && !getParentFragmentManager().isStateSaved()) {
                    if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                        getParentFragmentManager().popBackStack();
                    }
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
        ((MainActivity) requireActivity()).showBottomNav();
    }

    private void getSearchedJobs() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("query", query);
        int rbId = rgJobType.getCheckedRadioButtonId();
        if (rbId == R.id.rbPartTime) {
            params.put("jobType", "part-time");
            params.put("minSalary", etMinSalaryFilter.getEditableText().toString());
        } else if (rbId == R.id.rbFullTime) {
            params.put("jobType", "full-time");
            params.put("minSalary", etMinSalaryFilter.getEditableText().toString());
        }
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


    private void refreshSearchedJobs() {
        jobList.clear();
        recyclerView.setVisibility(View.GONE);
        getSearchedJobs();
        jobCardAdapter.notifyDataSetChanged();
    }
}