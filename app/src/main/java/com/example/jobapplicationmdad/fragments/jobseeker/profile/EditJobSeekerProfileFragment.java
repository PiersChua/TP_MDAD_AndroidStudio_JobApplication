package com.example.jobapplicationmdad.fragments.jobseeker.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.LoginActivity;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.activities.RegisterActivity;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.AuthValidation;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    EditText etFullNameProfile, etEmailProfile, etPhoneNumberProfile;
    TextInputLayout etFullNameProfileLayout, etEmailProfileLayout, etPhoneNumberProfileLayout;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topAppBar = view.findViewById(R.id.topAppBarEditJobSeekerProfile);
        btnEditProfile= view.findViewById(R.id.btnEditJobSeekerProfile);
        // Form
        etFullNameProfile = view.findViewById(R.id.etFullNameProfile);
        etEmailProfile = view.findViewById(R.id.etEmailProfile);
        etPhoneNumberProfile = view.findViewById(R.id.etPhoneNumberProfile);

        // Form Layouts
        etFullNameProfileLayout = view.findViewById(R.id.etFullNameProfileLayout);
        etEmailProfileLayout = view.findViewById(R.id.etEmailProfileLayout);
        etPhoneNumberProfileLayout = view.findViewById(R.id.etPhoneNumberProfileLayout);

        // Populate fields
        etFullNameProfile.setText(user.getFullName());
        etEmailProfile.setText(user.getEmail());
        etPhoneNumberProfile.setText(user.getPhoneNumber());


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
                User user = getUserFromForm();
                if (validateUser(user)) {
                    updateUser(user);
                } else {
                    return;
                }
            }
        });


    }
    private User getUserFromForm() {
        String fullName = etFullNameProfile.getText().toString().trim();
        String email = etEmailProfile.getText().toString();
        String phoneNumber = etPhoneNumberProfile.getText().toString();
        return new User(fullName, email, phoneNumber);
    }
    private boolean validateUser(User user) {
        boolean isValidName = AuthValidation.validateName(etFullNameProfileLayout, user.getFullName());
        boolean isValidEmail = AuthValidation.validateEmail(etEmailProfileLayout, user.getEmail());
        boolean isValidPhoneNumber = AuthValidation.validatePhoneNumber(etPhoneNumberProfileLayout, user.getPhoneNumber());
        return isValidName && isValidEmail && isValidPhoneNumber;
    }
    private void updateUser(User user){
        SharedPreferences sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId",sp.getString("userId",""));
        params.put("fullName", user.getFullName());
        params.put("email", user.getEmail());
        params.put("phoneNumber", user.getPhoneNumber());
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));

        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, update_user_url, params, headers, response -> {
            try {
                if (response.getString("type").equals("Success")) {
                    Toast.makeText(requireContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                    getParentFragmentManager().popBackStack();
                } else if (response.getString("type").equals("Error")) {
                    Toast.makeText(requireContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, VolleyErrorHandler.newErrorListener(requireContext()));
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }
}