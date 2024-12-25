package com.example.jobapplicationmdad.fragments.agent.job;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.android.volley.Request;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.model.Job;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.JobValidation;
import com.example.jobapplicationmdad.util.StringUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateAgentJobFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateAgentJobFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String create_job_url = MainActivity.root_url + "/api/agent/create-job.php";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MaterialToolbar topAppBar;
    CheckBox checkboxPartTimeJob, checkboxFullTimeJob;
    TextInputLayout etPositionJobLayout, etOrganisationJobLayout, etLocationJobLayout, etScheduleJobLayout, etPartTimeSalaryJobLayout, etFullTimeSalaryJobLayout, etResponsibilitiesJobLayout, etDescriptionJobLayout;
    EditText etPositionJob, etOrganisationJob, etLocationJob, etScheduleJob, etPartTimeSalaryJob, etFullTimeSalaryJob, etResponsibilitiesJob, etDescriptionJob;
    Button btnCreateAgentJob;
    SharedPreferences sp;

    public CreateAgentJobFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateAgentJobFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateAgentJobFragment newInstance(String param1, String param2) {
        CreateAgentJobFragment fragment = new CreateAgentJobFragment();
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
        return inflater.inflate(R.layout.fragment_create_agent_job, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topAppBar = view.findViewById(R.id.topAppBarAgentCreateJob);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        btnCreateAgentJob = view.findViewById(R.id.btnCreateAgentJob);

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
                getParentFragmentManager().popBackStack();
            }
        });

        btnCreateAgentJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Job job = getJobFromForm();
                if (validateJob(job)) {
                    createJob(job);
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

    private void createJob(Job job) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
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
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, create_job_url, params, headers, response -> {
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
                getParentFragmentManager().setFragmentResult("editJobResult", result);
                getParentFragmentManager().popBackStack();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, VolleyErrorHandler.newErrorListener(requireContext(), requireActivity().findViewById(android.R.id.content)));
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
}