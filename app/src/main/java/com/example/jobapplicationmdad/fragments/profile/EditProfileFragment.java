package com.example.jobapplicationmdad.fragments.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.AuthValidation;
import com.example.jobapplicationmdad.util.DateConverter;
import com.example.jobapplicationmdad.util.StringUtil;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private static final String ARG_PARAM1 = "userId";
    private static final String update_user_url = MainActivity.root_url + "/api/auth/update-user-details.php";
    private static final String get_user_url = MainActivity.root_url + "/api/auth/get-user-details.php";
    // User that is passed from profile fragment
    private String userId;
    private User user;
    MaterialToolbar topAppBar;
    Button btnEditProfile;
    View dialogView;
    AlertDialog loadingDialog;
    SharedPreferences sp;
    EditText etFullNameProfile, etEmailProfile, etPhoneNumberProfile, etDateOfBirthProfile;

    TextInputLayout etFullNameProfileLayout, etEmailProfileLayout, etPhoneNumberProfileLayout, etDateOfBirthProfileLayout, etGenderProfileLayout, etNationalityProfileLayout, etRaceProfileLayout;
    AutoCompleteTextView actvGenderProfile, actvRaceProfile, actvNationalityprofile;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId The userId of the user to edit the profile
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String userId) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialogView = inflater.inflate(R.layout.dialog_loader, container, false);
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
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
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        topAppBar = view.findViewById(R.id.topAppBarEditProfile);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        getUserDetails();

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


        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // removes the backstack that was added when navigating to the current page
                // essentially simulates clicking the back button
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

    private void updateUser(User updatedUser) {
        SharedPreferences sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("userIdToBeUpdated", userId);
        params.put("fullName", updatedUser.getFullName());
        params.put("email", updatedUser.getEmail());
        params.put("phoneNumber", updatedUser.getPhoneNumber());
        params.put("dateOfBirth", DateConverter.formatDateForSql(updatedUser.getDateOfBirth()));
        params.put("gender", updatedUser.getGender());
        params.put("race", updatedUser.getRace());
        params.put("nationality", updatedUser.getNationality());
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

        }, VolleyErrorHandler.newErrorListener(requireContext()));
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void getUserDetails() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("userIdToGet", userId);
        String url = UrlUtil.constructUrl(get_user_url, params);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, response -> {
            try {
                // retrieve user details
                user = new User();
                user.setFullName(response.getString("fullName"));
                user.setEmail(response.getString("email"));
                user.setDateOfBirth(DateConverter.formatDateFromSql(response.getString("dateOfBirth")));
                user.setPhoneNumber(response.getString("phoneNumber"));
                user.setRace(response.getString("race"));
                user.setNationality(response.getString("nationality"));
                user.setGender(response.getString("gender"));
                populateUserItems();

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            // toggle the visibility of loader
            loadingDialog.dismiss();
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(requireContext()).onErrorResponse(error);
        });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);

    }

    private void populateUserItems() {
        // Populate fields
        etFullNameProfile.setText(user.getFullName());
        etEmailProfile.setText(user.getEmail());
        etPhoneNumberProfile.setText(user.getPhoneNumber());
        etDateOfBirthProfile.setText(user.getDateOfBirth());
        actvGenderProfile.setText(user.getGender(), false);
        actvRaceProfile.setText(user.getRace(), false);
        actvNationalityprofile.setText(user.getNationality(), false);
    }
}