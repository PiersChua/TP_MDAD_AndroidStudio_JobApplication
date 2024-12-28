package com.example.jobapplicationmdad.fragments.agent.job;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.JobValidation;
import com.example.jobapplicationmdad.util.StringUtil;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditAgentJobFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditAgentJobFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "jobId";
    private static final String ARG_PARAM2 = "userId";
    private static final String get_job_url = MainActivity.root_url + "/api/agent/get-job.php";
    private static final String update_job_url = MainActivity.root_url + "/api/agent/update-job-details.php";
    private static final String delete_job_url = MainActivity.root_url + "/api/agent/delete-job.php";

    // TODO: Rename and change types of parameters
    private String jobId;
    private String userId;
    MaterialToolbar topAppBar;
    View dialogView;
    AlertDialog loadingDialog;
    CheckBox checkboxPartTimeJob, checkboxFullTimeJob;
    TextInputLayout etPositionJobLayout, etOrganisationJobLayout, etLocationJobLayout, etScheduleJobLayout, etPartTimeSalaryJobLayout, etFullTimeSalaryJobLayout, etResponsibilitiesJobLayout, etDescriptionJobLayout;
    EditText etPositionJob, etOrganisationJob, etLocationJob, etScheduleJob, etPartTimeSalaryJob, etFullTimeSalaryJob, etResponsibilitiesJob, etDescriptionJob;
    Button btnEditAgentJob;
    SharedPreferences sp;

    public EditAgentJobFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param jobId The associated jobId to edit the job
     * @return A new instance of fragment EditAgentJobFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditAgentJobFragment newInstance(String jobId, String userId) {
        EditAgentJobFragment fragment = new EditAgentJobFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, jobId);
        args.putString(ARG_PARAM2, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jobId = getArguments().getString(ARG_PARAM1);
            userId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialogView = inflater.inflate(R.layout.dialog_loader, container, false);
        return inflater.inflate(R.layout.fragment_edit_agent_job, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        topAppBar = view.findViewById(R.id.topAppBarAgentEditJob);
        btnEditAgentJob = view.findViewById(R.id.btnEditAgentJob);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        getJobDetails();

        // Form Layout
        etPositionJobLayout = view.findViewById(R.id.etPositionJobLayout);
        etOrganisationJobLayout = view.findViewById(R.id.etOrganisationJobLayout);
        etLocationJobLayout = view.findViewById(R.id.etLocationJobLayout);
        etScheduleJobLayout = view.findViewById(R.id.etScheduleJobLayout);
        etPartTimeSalaryJobLayout = view.findViewById(R.id.etPartTimeSalaryJobLayout);
        etFullTimeSalaryJobLayout = view.findViewById(R.id.etFullTimeSalaryJobLayout);
        etResponsibilitiesJobLayout = view.findViewById(R.id.etResponsibilitiesJobLayout);
        etDescriptionJobLayout = view.findViewById(R.id.etDescriptionJobLayout);

        // Form
        etPositionJob = view.findViewById(R.id.etPositionJob);
        etOrganisationJob = view.findViewById(R.id.etOrganisationJob);
        etLocationJob = view.findViewById(R.id.etLocationJob);
        etScheduleJob = view.findViewById(R.id.etScheduleJob);
        etPartTimeSalaryJob = view.findViewById(R.id.etPartTimeSalaryJob);
        etFullTimeSalaryJob = view.findViewById(R.id.etFullTimeSalaryJob);
        etResponsibilitiesJob = view.findViewById(R.id.etResponsibilitiesJob);
        etDescriptionJob = view.findViewById(R.id.etDescriptionJob);

        // Checkbox
        checkboxPartTimeJob = view.findViewById(R.id.checkboxPartTimeJob);
        checkboxFullTimeJob = view.findViewById(R.id.checkboxFullTimeJob);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                // Get the currently focused view
                View currentFocus = requireActivity().getCurrentFocus();
                // Hide the keyboard if a view is focused
                if (currentFocus != null) {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
                getParentFragmentManager().popBackStack();
            }
        });
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.agent_edit_listings_item_1) {
                    new MaterialAlertDialogBuilder(requireContext()).setTitle("Delete Job").setMessage("You are about to delete this listing.\nDo you wish to proceed?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            deleteJob();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    }).show();
                    return true;
                }
                return false;
            }
        });
        btnEditAgentJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Job job = getJobFromForm();
                if (validateJob(job)) {
                    updateJob(job);
                }
            }
        });
        checkboxPartTimeJob.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                etPartTimeSalaryJobLayout.setVisibility(b ? View.VISIBLE : View.GONE);
            }
        });
        checkboxPartTimeJob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    checkboxPartTimeJob.setChecked(!checkboxPartTimeJob.isChecked());
                }
            }
        });
        checkboxFullTimeJob.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                etFullTimeSalaryJobLayout.setVisibility(b ? View.VISIBLE : View.GONE);
            }
        });
        checkboxFullTimeJob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    checkboxFullTimeJob.setChecked(!checkboxFullTimeJob.isChecked());
                }
            }
        });

        etPartTimeSalaryJob.setFilters(new InputFilter[]{new StringUtil.DecimalDigitsInputFilter(4, 2)});
        etFullTimeSalaryJob.setFilters(new InputFilter[]{new StringUtil.DecimalDigitsInputFilter(6, 2)});
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
        if (userId == null) {
            ((MainActivity) requireActivity()).hideBottomNav();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (userId == null) {
            ((MainActivity) requireActivity()).showBottomNav();
        }

    }

    private void getJobDetails() {
        loadingDialog.show();
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

                    populateJobItems(job);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                loadingDialog.dismiss();
            }
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(requireContext()).onErrorResponse(error);

        });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private Job getJobFromForm() {
        double dPartTimeSalary = 0.0, dFullTimeSalary = 0.0;
        String position = etPositionJob.getText().toString().trim();
        String organisation = etOrganisationJob.getText().toString().trim();
        String location = etLocationJob.getText().toString().trim();
        String schedule = etScheduleJob.getText().toString().trim();
        String partTimeSalary = etPartTimeSalaryJob.getText().toString().trim();
        String fullTimeSalary = etFullTimeSalaryJob.getText().toString().trim();
        if (checkboxPartTimeJob.isChecked()) {
            dPartTimeSalary = Double.parseDouble(!partTimeSalary.isEmpty() ? partTimeSalary : "0.0");
        }
        if (checkboxFullTimeJob.isChecked()) {
            dFullTimeSalary = Double.parseDouble(!fullTimeSalary.isEmpty() ? etFullTimeSalaryJob.getText().toString().trim() : "0.0");
        }
        String responsibilities = etResponsibilitiesJob.getText().toString().trim();
        String description = etDescriptionJob.getText().toString().trim();

        // Create and return a new Job object using the constructor
        return new Job(position, organisation, location, schedule, dPartTimeSalary, dFullTimeSalary, responsibilities, description);
    }

    private void populateJobItems(Job job) {
        etPositionJob.setText(job.getPosition());
        etOrganisationJob.setText(job.getOrganisation());
        etLocationJob.setText(job.getLocation());
        if (job.getPartTimeSalary() != 0.0) {
            checkboxPartTimeJob.setChecked(true);
            etPartTimeSalaryJob.setText(String.valueOf(job.getPartTimeSalary()));
        }
        if (job.getFullTimeSalary() != 0.0) {
            checkboxFullTimeJob.setChecked(true);
            etFullTimeSalaryJob.setText(String.valueOf(job.getFullTimeSalary()));
        }
        etScheduleJob.setText(job.getSchedule());
        etDescriptionJob.setText(job.getDescription());
        etResponsibilitiesJob.setText(job.getResponsibilities());
    }

    private void updateJob(Job job) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("agentUserId", userId != null ? userId : sp.getString("userId", ""));
        params.put("jobId", jobId);
        params.put("position", job.getPosition());
        params.put("organisation", job.getOrganisation());
        params.put("location", job.getLocation());
        params.put("schedule", job.getSchedule());
        params.put("responsibilities", job.getResponsibilities());
        params.put("description", job.getDescription());
        if (checkboxPartTimeJob.isChecked() && job.getPartTimeSalary() != 0.0) {
            params.put("partTimeSalary", String.valueOf(job.getPartTimeSalary()));
        }
        if (checkboxFullTimeJob.isChecked() && job.getFullTimeSalary() != 0.0) {
            params.put("fullTimeSalary", String.valueOf(job.getFullTimeSalary()));
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, update_job_url, params, headers, response -> {
            try {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                // Get the currently focused view
                View currentFocus = requireActivity().getCurrentFocus();
                // Hide the keyboard if a view is focused
                if (currentFocus != null) {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
                Snackbar.make(requireActivity().findViewById(android.R.id.content), response.getString("message"), Snackbar.LENGTH_SHORT).setAnchorView(requireActivity().findViewById(R.id.bottom_navigation)).show();
                // allow profile to refresh when return to previous page
                Bundle result = new Bundle();
                result.putBoolean("isUpdated", true);
                getParentFragmentManager().setFragmentResult("jobResult", result);
                getParentFragmentManager().popBackStack();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, VolleyErrorHandler.newErrorListener(requireContext()));
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private boolean validateJob(Job job) {
        boolean isValidPosition = JobValidation.validatePosition(etPositionJobLayout, job.getPosition());
        boolean isValidOrganisation = JobValidation.validateOrganisation(etOrganisationJobLayout, job.getOrganisation());
        boolean isValidLocation = JobValidation.validateLocation(etLocationJobLayout, job.getLocation());
        boolean isValidSchedule = JobValidation.validateSchedule(etScheduleJobLayout, job.getSchedule());
        boolean isValidDescription = JobValidation.validateDescription(etDescriptionJobLayout, job.getDescription());
        boolean isValidResponsibilities = JobValidation.validateResponsibilities(etResponsibilitiesJobLayout, job.getResponsibilities());
        boolean isValidSalary = JobValidation.validateSalary(checkboxPartTimeJob, checkboxFullTimeJob, job.getPartTimeSalary(), job.getFullTimeSalary());
        return isValidPosition && isValidOrganisation && isValidLocation && isValidSchedule && isValidDescription && isValidResponsibilities && isValidSalary;
    }

    private void deleteJob() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("agentUserId", userId != null ? userId : sp.getString("userId", ""));
        params.put("jobId", jobId);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, delete_job_url, params, headers, response -> {
            try {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), response.getString("message"), Snackbar.LENGTH_SHORT).setAnchorView(requireActivity().findViewById(R.id.bottom_navigation)).show();
                // allow profile to refresh when return to previous page
                Bundle result = new Bundle();
                result.putBoolean("isUpdated", true);
                getParentFragmentManager().setFragmentResult("jobResult", result);
                getParentFragmentManager().popBackStack();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, VolleyErrorHandler.newErrorListener(requireContext()));
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

}