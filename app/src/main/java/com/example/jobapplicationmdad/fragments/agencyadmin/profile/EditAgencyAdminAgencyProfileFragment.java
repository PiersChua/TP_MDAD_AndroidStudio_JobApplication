package com.example.jobapplicationmdad.fragments.agencyadmin.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.model.Agency;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.AuthValidation;
import com.example.jobapplicationmdad.util.DateConverter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditAgencyAdminAgencyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditAgencyAdminAgencyProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "agency";

    // TODO: Rename and change types of parameters
    private Agency agency;
    private static final String update_agency_url = MainActivity.root_url + "/api/auth/update-agency-details.php";
    // User that is passed from profile fragment
    MaterialToolbar topAppBar;
    Button btnEditProfile;
    EditText etNameProfile, etEmailProfile, etPhoneNumberProfile, etAddressProfile;

    TextInputLayout etNameProfileLayout, etEmailProfileLayout, etPhoneNumberProfileLayout, etAddressProfileLayout;

    public EditAgencyAdminAgencyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param agency The agency to edit.
     * @return A new instance of fragment EditAgencyAdminAgencyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditAgencyAdminAgencyProfileFragment newInstance(Agency agency) {
        EditAgencyAdminAgencyProfileFragment fragment = new EditAgencyAdminAgencyProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, agency);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            agency = (Agency) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_agency_admin_agency_profile, container, false);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topAppBar = view.findViewById(R.id.topAppBarEditAgencyAdminAgencyProfile);
        btnEditProfile = view.findViewById(R.id.btnEditAgencyAdminAgencyProfile);

        // Form
        etNameProfile = view.findViewById(R.id.etNameProfile);
        etEmailProfile = view.findViewById(R.id.etEmailProfile);
        etPhoneNumberProfile = view.findViewById(R.id.etPhoneNumberProfile);
        etAddressProfile = view.findViewById(R.id.etAddressProfile);

        // Form Layouts
        etNameProfileLayout = view.findViewById(R.id.etNameProfileLayout);
        etEmailProfileLayout = view.findViewById(R.id.etEmailProfileLayout);
        etPhoneNumberProfileLayout = view.findViewById(R.id.etPhoneNumberProfileLayout);
        etAddressProfileLayout = view.findViewById(R.id.etAddressProfileLayout);


        // Populate fields
        etNameProfile.setText(agency.getName());
        etEmailProfile.setText(agency.getEmail());
        etPhoneNumberProfile.setText(agency.getPhoneNumber());
        etAddressProfile.setText(agency.getAddress());


        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // removes the backstack that was added when navigating to the current page
                // essentially simulates clicking the back button
                getParentFragmentManager().popBackStack();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Agency agency = getAgencyFromForm();
                if (validateAgency(agency)) {
                    updateAgency(agency);
                }
            }
        });
    }

    private Agency getAgencyFromForm() {
        String name = etNameProfile.getText().toString().trim();
        String email = etEmailProfile.getText().toString();
        String phoneNumber = etPhoneNumberProfile.getText().toString();
        String address = etAddressProfile.getText().toString().trim();
        return new Agency(name, email, phoneNumber, address);
    }

    private boolean validateAgency(Agency agency) {
        boolean isValidName = AuthValidation.validateName(etNameProfileLayout, agency.getName());
        boolean isValidEmail = AuthValidation.validateEmail(etEmailProfileLayout, agency.getEmail());
        boolean isValidPhoneNumber = AuthValidation.validatePhoneNumber(etPhoneNumberProfileLayout, agency.getPhoneNumber());
        return isValidName && isValidEmail && isValidPhoneNumber;
    }

    private void updateAgency(Agency agency) {
        SharedPreferences sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("name", agency.getName());
        params.put("email", agency.getEmail());
        params.put("phoneNumber", agency.getPhoneNumber());
        params.put("address", agency.getAddress());
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));

        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, update_agency_url, params, headers, response -> {
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
                getParentFragmentManager().setFragmentResult("editAgencyResult", result);
                getParentFragmentManager().popBackStack();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, VolleyErrorHandler.newErrorListener(requireContext(), requireActivity().findViewById(android.R.id.content)));
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

}