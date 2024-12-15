package com.example.jobapplicationmdad.fragments.jobseeker.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.model.AgencyApplication;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.util.AuthValidation;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateAgencyApplicationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateAgencyApplicationFragment extends Fragment {

    private static final String ARG_PARAM1 = "userId";

    private String userId;
    MaterialToolbar topAppBar;
    EditText etNameAgencyApplication, etEmailAgencyApplication, etPhoneNumberAgencyApplication, etAddressAgencyApplication;
    TextInputLayout etNameAgencyApplicationLayout, etEmailAgencyApplicationLayout, etPhoneNumberAgencyApplicationLayout, etAddressAgencyApplicationLayout;
    Button btnCreateAgencyApplication;

    public CreateAgencyApplicationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 The userId of the user that will be submitting the application
     * @return A new instance of fragment CreateAgencyApplicationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateAgencyApplicationFragment newInstance(String param1) {
        CreateAgencyApplicationFragment fragment = new CreateAgencyApplicationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
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
        return inflater.inflate(R.layout.fragment_create_agency_application, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topAppBar = view.findViewById(R.id.topAppBarAgencyApplication);
        btnCreateAgencyApplication = view.findViewById(R.id.btnCreateAgencyApplication);

        // Form
        etNameAgencyApplication = view.findViewById(R.id.etNameAgencyApplication);
        etEmailAgencyApplication = view.findViewById(R.id.etEmailAgencyApplication);
        etPhoneNumberAgencyApplication = view.findViewById(R.id.etPhoneNumberAgencyApplication);
        etAddressAgencyApplication = view.findViewById(R.id.etAddressAgencyApplication);

        // Form Layouts
        etNameAgencyApplicationLayout = view.findViewById(R.id.etNameAgencyApplicationLayout);
        etEmailAgencyApplicationLayout = view.findViewById(R.id.etEmailAgencyApplicationLayout);
        etPhoneNumberAgencyApplicationLayout = view.findViewById(R.id.etPhoneNumberAgencyApplicationLayout);
        etAddressAgencyApplicationLayout = view.findViewById(R.id.etAddressAgencyApplicationLayout);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
        btnCreateAgencyApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgencyApplication application = getAgencyApplicationFromForm();
                if (validateAgencyApplication(application)) {
                    createAgencyApplication(application);
                }
            }
        });
    }

    private AgencyApplication getAgencyApplicationFromForm() {
        String fullName = etNameAgencyApplication.getText().toString().trim();
        String email = etEmailAgencyApplication.getText().toString();
        String phoneNumber = etPhoneNumberAgencyApplication.getText().toString();
        String address = etAddressAgencyApplication.getText().toString();
        return new AgencyApplication(fullName, email, phoneNumber, address, userId);
    }

    private boolean validateAgencyApplication(AgencyApplication application) {
        boolean isValidName = AuthValidation.validateName(etNameAgencyApplicationLayout, application.getName());
        boolean isValidEmail = AuthValidation.validateEmail(etEmailAgencyApplicationLayout, application.getEmail());
        boolean isValidPhoneNumber = AuthValidation.validatePhoneNumber(etPhoneNumberAgencyApplicationLayout, application.getPhoneNumber());
        return isValidName && isValidEmail && isValidPhoneNumber;
    }

    private void createAgencyApplication(AgencyApplication application) {

    }
}