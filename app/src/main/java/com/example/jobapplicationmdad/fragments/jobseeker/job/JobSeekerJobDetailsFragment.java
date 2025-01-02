package com.example.jobapplicationmdad.fragments.jobseeker.job;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.fragments.jobseeker.JobSeekerApplicationsFragment;
import com.example.jobapplicationmdad.model.Agency;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private static final String ARG_PARAM2 = "isFromFavouriteFragment";

    private String jobId;
    private boolean isFromFavouriteFragment = false;
    private boolean isFavourite;
    private boolean isApplied;
    SharedPreferences sp;
    MaterialToolbar topAppBar;
    NestedScrollView nsvJobSeekerJobDetails;
    LinearProgressIndicator progressIndicator;
    Button btnApplyJob;
    MaterialButton btnFavouriteJob;
    TextView tvPosition, tvAgencyName, tvLocation, tvSalary, tvEmploymentType, tvOrganisation, tvSchedule, tvDescription, tvResponsibilities,
            tvAgentDetailsAgentName, tvAgentDetailsAgentEmail,tvAgentDetailsAgentPhoneNumber,
            tvAgencyDetailsAgencyName, tvAgencyDetailsAgencyEmail,tvAgencyDetailsAgencyPhoneNumber,tvAgencyDetailsAgencyAddress;
    private static final String get_job_url = MainActivity.root_url + "/api/job-seeker/get-job.php";
    private static final String create_job_application_url = MainActivity.root_url + "/api/job-seeker/create-job-application.php";
    private static final String favourite_job_url = MainActivity.root_url + "/api/job-seeker/favourite-job.php";

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
    public static JobSeekerJobDetailsFragment newInstance(String jobId, boolean isFromFavouriteFragment) {
        JobSeekerJobDetailsFragment fragment = new JobSeekerJobDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, jobId);
        args.putBoolean(ARG_PARAM2, isFromFavouriteFragment);
        fragment.setArguments(args);
        return fragment;
    }

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
            isFromFavouriteFragment = getArguments().getBoolean(ARG_PARAM2);
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
        btnFavouriteJob = view.findViewById(R.id.btnFavouriteJobJobDetails);

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
        tvAgentDetailsAgentName = view.findViewById(R.id.tvJobSeekerAgentDetailsAgentName);
        tvAgentDetailsAgentEmail = view.findViewById(R.id.tvJobSeekerAgentDetailsAgentEmail);
        tvAgentDetailsAgentPhoneNumber = view.findViewById(R.id.tvJobSeekerAgentDetailsAgentPhoneNumber);
        tvAgencyDetailsAgencyName = view.findViewById(R.id.tvJobSeekerAgencyDetailsAgencyName);
        tvAgencyDetailsAgencyEmail = view.findViewById(R.id.tvJobSeekerAgencyDetailsAgencyEmail);
        tvAgencyDetailsAgencyPhoneNumber = view.findViewById(R.id.tvJobSeekerAgencyDetailsAgencyPhoneNumber);
        tvAgencyDetailsAgencyAddress = view.findViewById(R.id.tvJobSeekerAgencyDetailsAgencyAddress);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyFavouriteFragmentChange();
                getParentFragmentManager().popBackStack();
            }
        });

        btnApplyJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(requireContext()).setTitle("Apply for Job").setMessage("This will submit your application for the position.\nDo you wish to proceed?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        createJobApplication();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).show();

            }
        });

        btnFavouriteJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favouriteJob();
            }
        });

        // configure the back button to return to previous fragment
        // use getViewLifecycleOwner to remove callback when fragment's view is destroyed
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isAdded()) {
                    notifyFavouriteFragmentChange();
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
        ((MainActivity) requireActivity()).showBottomNav();
    }

    private void getJobDetails() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("jobId", jobId);
        String url = UrlUtil.constructUrl(get_job_url, params);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jobObject = response.getJSONObject("data");
                    // init job attributes
                    Job job = new Job();
                    job.setJobId(jobObject.getString("jobId"));
                    job.setPosition(jobObject.getString("position"));
                    job.setResponsibilities(jobObject.getString("responsibilities"));
                    job.setDescription(jobObject.getString("description"));
                    job.setLocation(jobObject.getString("location"));
                    job.setSchedule(jobObject.getString("schedule"));
                    job.setOrganisation(jobObject.getString("organisation"));
                    job.setPartTimeSalary(jobObject.optDouble("partTimeSalary", 0.0));
                    job.setFullTimeSalary(jobObject.optDouble("fullTimeSalary", 0.0));
                    job.setCreatedAt(jobObject.getString("createdAt"));
                    job.setUpdatedAt(jobObject.getString("updatedAt"));

                    // init agent attributes
                    User user = new User();
                    user.setFullName(jobObject.getString("user_fullName"));
                    user.setEmail(jobObject.getString("user_email"));
                    user.setPhoneNumber(jobObject.getString("user_phoneNumber"));

                    // init agency attributes
                    Agency agency = new Agency();
                    agency.setName(jobObject.getString("agency_name"));
                    agency.setEmail(jobObject.getString("agency_email"));
                    agency.setPhoneNumber(jobObject.getString("agency_phoneNumber"));
                    agency.setAddress(jobObject.getString("agency_address"));

                    // link the entities
                    user.setAgency(agency);
                    job.setUser(user);
                    isFavourite = jobObject.getBoolean("isFavourite");
                    isApplied = jobObject.getBoolean("isApplied");
                    toggleFavouriteIcon();
                    toggleApplyButtonState();
                    populateJobItems(job);
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
            salary.append("$").append(String.format("%.2f", job.getPartTimeSalary())).append(" per hr");
            employmentType.append("Part-Time");
        }
        if (job.getFullTimeSalary() != 0.0) {
            if (salary.length() > 0) {
                salary.append(" / ");
                employmentType.append(" / ");
            }
            salary.append("$").append(String.format("%.2f", job.getFullTimeSalary())).append(" per mth");
            employmentType.append("Full-Time");
        }
        if (salary.length() == 0) {
            tvSalary.setVisibility(View.GONE);
        } else {
            tvSalary.setText(salary);
        }

        // init jobs
        tvPosition.setText(job.getPosition());
        tvAgencyName.setText(job.getUser().getAgency().getName());
        tvLocation.setText(job.getLocation());
        tvEmploymentType.setText(employmentType);
        tvOrganisation.setText(job.getOrganisation());
        tvSchedule.setText(job.getSchedule());
        tvDescription.setText(job.getDescription());
        tvResponsibilities.setText(job.getResponsibilities());
        // init agent
        tvAgentDetailsAgentName.setText(job.getUser().getFullName());
        tvAgentDetailsAgentEmail.setText(job.getUser().getEmail());
        tvAgentDetailsAgentPhoneNumber.setText(job.getUser().getPhoneNumber());

        // init agency
        tvAgencyDetailsAgencyName.setText(job.getUser().getAgency().getName());
        tvAgencyDetailsAgencyEmail.setText(job.getUser().getAgency().getEmail());
        tvAgencyDetailsAgencyPhoneNumber.setText(job.getUser().getAgency().getPhoneNumber());
        tvAgencyDetailsAgencyAddress.setText(job.getUser().getAgency().getAddress());
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
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), response.getString("message"), Snackbar.LENGTH_SHORT).setAnchorView(requireActivity().findViewById(R.id.bottom_navigation)).show();
                    getParentFragmentManager().popBackStack();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, VolleyErrorHandler.newErrorListener(requireContext()));
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void favouriteJob() {
        Map<String, String> params = new HashMap<>();
        params.put("userId", sp.getString("userId", ""));
        params.put("jobId", jobId);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, favourite_job_url, params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), response.getString("message"), Snackbar.LENGTH_SHORT).setAnchorView(requireView().findViewById(R.id.bottomAppBar)).show();
                    isFavourite = !isFavourite;
                    toggleFavouriteIcon();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, VolleyErrorHandler.newErrorListener(requireContext()));
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void toggleFavouriteIcon() {
        if (isFavourite) {
            btnFavouriteJob.setIconResource(R.drawable.ic_favourite);
            btnFavouriteJob.setIconTintResource(R.color.primary);
        } else {
            btnFavouriteJob.setIconResource(R.drawable.ic_favourite_outline);
            btnFavouriteJob.setIconTintResource(R.color.placeholder_foreground);
        }
    }

    private void toggleApplyButtonState() {
        btnApplyJob.setEnabled(!isApplied);
        btnApplyJob.setClickable(!isApplied);
        if (isApplied) {
            btnApplyJob.setText("Application received");
            btnApplyJob.setBackgroundResource(R.drawable.button_disabled);
        }
    }

    /**
     * Set fragment result if the current fragment is accessed from the favourites fragment and the favourite is removed
     */
    private void notifyFavouriteFragmentChange() {
        if (isFromFavouriteFragment && !isFavourite) {
            Bundle result = new Bundle();
            result.putBoolean("isFavouriteRemoved", true);
            getParentFragmentManager().setFragmentResult("favouriteJobResult", result);
        }
    }

}
