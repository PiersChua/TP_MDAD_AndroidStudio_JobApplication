package com.example.jobapplicationmdad.fragments.jobseeker.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
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

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditJobSeekerProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditJobSeekerProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "user";
    private static final String update_user_url = MainActivity.root_url + "/api/auth/update-user-details.php";
    // User that is passed from profile fragment
    private User user;
    MaterialToolbar topAppBar;
    Button btnEditProfile;
    EditText etFullNameProfile, etEmailProfile, etPhoneNumberProfile, etDateOfBirthProfile;

    TextInputLayout etFullNameProfileLayout, etEmailProfileLayout, etPhoneNumberProfileLayout, etDateOfBirthProfileLayout, etGenderProfileLayout, etNationalityProfileLayout, etRaceProfileLayout;
    AutoCompleteTextView actvGenderProfile, actvRaceProfile, actvNationalityprofile;

    public EditJobSeekerProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user The user to edit the profile
     * @return A new instance of fragment EditJobSeekerProfileFragment.
     */
    public static EditJobSeekerProfileFragment newInstance(User user) {
        EditJobSeekerProfileFragment fragment = new EditJobSeekerProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_job_seeker_profile, container, false);

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
        topAppBar = view.findViewById(R.id.topAppBarEditJobSeekerProfile);
        btnEditProfile = view.findViewById(R.id.btnEditJobSeekerProfile);

        // Form
        etFullNameProfile = view.findViewById(R.id.etFullNameProfile);
        etEmailProfile = view.findViewById(R.id.etEmailProfile);
        etPhoneNumberProfile = view.findViewById(R.id.etPhoneNumberProfile);
        etDateOfBirthProfile = view.findViewById(R.id.etDateOfBirthProfile);

        // Form Layouts
        etFullNameProfileLayout = view.findViewById(R.id.etFullNameProfileLayout);
        etEmailProfileLayout = view.findViewById(R.id.etEmailProfileLayout);
        etPhoneNumberProfileLayout = view.findViewById(R.id.etPhoneNumberProfileLayout);
        etDateOfBirthProfileLayout = view.findViewById(R.id.etDateOfBirthProfileLayout);
        etGenderProfileLayout = view.findViewById(R.id.etGenderProfileLayout);
        etRaceProfileLayout = view.findViewById(R.id.etRaceProfileLayout);
        etNationalityProfileLayout = view.findViewById(R.id.etNationalityProfileLayout);

        // Autocomplete TextViews
        actvGenderProfile = view.findViewById(R.id.actvGenderProfile);
        actvRaceProfile = view.findViewById(R.id.actvRaceProfile);
        actvNationalityprofile = view.findViewById(R.id.actvNationalityProfile);

        // Populate fields
        etFullNameProfile.setText(user.getFullName());
        etEmailProfile.setText(user.getEmail());
        etPhoneNumberProfile.setText(user.getPhoneNumber());
        etDateOfBirthProfile.setText(user.getDateOfBirth());
        actvGenderProfile.setText(user.getGender(), false);
        actvRaceProfile.setText(user.getRace(), false);
        actvNationalityprofile.setText(user.getNationality(), false);


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
                // removes the backstack that was added when navigating to the current page
                // essentially simulates clicking the back button
                getParentFragmentManager().popBackStack();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = getUserFromForm();
                if (validateUser(user)) {
                    updateUser(user);
                }
            }
        });
        etDateOfBirthProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                long today = calendar.getTimeInMillis();

                // Minimum date is 16 years from today
                calendar.add(Calendar.YEAR, -16);
                long sixteenYearsAgo = calendar.getTimeInMillis();

                // Maximum date is 100 years from today
                calendar.setTimeInMillis(today);
                calendar.add(Calendar.YEAR, -100);
                long hundredYearsAgo = calendar.getTimeInMillis();

                // Set up the date picker with constraints
                CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
                constraintsBuilder.setStart(hundredYearsAgo);
                constraintsBuilder.setEnd(sixteenYearsAgo);
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setSelection(DateConverter.formatDateToMilliseconds(user.getDateOfBirth()))
                        .setTitleText("Select Date of Birth")
                        .setCalendarConstraints(constraintsBuilder.build())
                        .build();
                datePicker.show(getParentFragmentManager(), "DATE_PICKER");

                datePicker.addOnPositiveButtonClickListener(selection -> {
                    etDateOfBirthProfile.setText(DateConverter.formatDateFromMilliseconds(selection));
                });
            }
        });

    }

    private User getUserFromForm() {
        String fullName = etFullNameProfile.getText().toString().trim();
        String email = etEmailProfile.getText().toString();
        String phoneNumber = etPhoneNumberProfile.getText().toString();
        String dateOfBirth = etDateOfBirthProfile.getText().toString();
        String gender = Objects.requireNonNull(etGenderProfileLayout.getEditText()).getText().toString();
        String race = Objects.requireNonNull(etRaceProfileLayout.getEditText()).getText().toString();
        String nationality = Objects.requireNonNull(etNationalityProfileLayout.getEditText()).getText().toString();
        return new User(fullName, email, phoneNumber, dateOfBirth, gender, race, nationality);
    }

    private boolean validateUser(User user) {
        boolean isValidName = AuthValidation.validateName(etFullNameProfileLayout, user.getFullName());
        boolean isValidEmail = AuthValidation.validateEmail(etEmailProfileLayout, user.getEmail());
        boolean isValidPhoneNumber = AuthValidation.validatePhoneNumber(etPhoneNumberProfileLayout, user.getPhoneNumber());
        boolean isValidDateOfBirth = AuthValidation.validateNull(etDateOfBirthProfileLayout, "Date of Birth", user.getDateOfBirth());
        boolean isValidGender = AuthValidation.validateEnum(etGenderProfileLayout, "Gender", user.getGender(), getResources().getStringArray(R.array.gender_items));
        boolean isValidRace = AuthValidation.validateEnum(etRaceProfileLayout, "Race", user.getRace(), getResources().getStringArray(R.array.race_items));
        boolean isValidNationality = AuthValidation.validateEnum(etNationalityProfileLayout, "Nationality", user.getNationality(), getResources().getStringArray(R.array.nationality_items));
        ;

        return isValidName && isValidEmail && isValidPhoneNumber && isValidDateOfBirth && isValidGender && isValidRace && isValidNationality;
    }

    private void updateUser(User userToUpdate) {
        SharedPreferences sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("userIdToBeUpdated",user.getUserId());
        params.put("fullName", userToUpdate.getFullName());
        params.put("email", userToUpdate.getEmail());
        params.put("phoneNumber", userToUpdate.getPhoneNumber());
        params.put("dateOfBirth", DateConverter.formatDateForSql(userToUpdate.getDateOfBirth()));
        params.put("gender", userToUpdate.getGender());
        params.put("race", userToUpdate.getRace());
        params.put("nationality", userToUpdate.getNationality());
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));

        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, update_user_url, params, headers, response -> {
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
                getParentFragmentManager().setFragmentResult("editProfileResult", result);
                getParentFragmentManager().popBackStack();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, VolleyErrorHandler.newErrorListener(requireContext(), requireActivity().findViewById(android.R.id.content)));
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }
}