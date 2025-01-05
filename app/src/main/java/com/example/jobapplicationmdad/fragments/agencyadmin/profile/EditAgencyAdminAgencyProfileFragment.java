package com.example.jobapplicationmdad.fragments.agencyadmin.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.model.Agency;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.ApplicationValidation;
import com.example.jobapplicationmdad.util.AuthValidation;
import com.example.jobapplicationmdad.util.DateConverter;
import com.example.jobapplicationmdad.util.ImageUtil;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;

import java.io.IOException;
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
    private static final String ARG_PARAM1 = "userId";

    // TODO: Rename and change types of parameters
    private String userId;
    private Agency agency;
    private static final String update_agency_url = MainActivity.root_url + "/api/auth/update-agency-details.php";
    private static final String get_agency_url = MainActivity.root_url + "/api/auth/get-agency-details.php";
    // User that is passed from profile fragment
    MaterialToolbar topAppBar;
    Button btnEditProfile;

    EditText etNameProfile, etEmailProfile, etPhoneNumberProfile, etAddressProfile;

    TextInputLayout etNameProfileLayout, etEmailProfileLayout, etPhoneNumberProfileLayout, etAddressProfileLayout;
    MaterialCardView mcvImageProfile;
    View dialogView;
    AlertDialog loadingDialog;
    ImageView ivAgencyImageProfile;
    private Bitmap imageBitmap;
    SharedPreferences sp;
    private static final int IMAGE_PICK_CODE = 103;


    public EditAgencyAdminAgencyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId
     * @return A new instance of fragment EditAgencyAdminAgencyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditAgencyAdminAgencyProfileFragment newInstance(String userId) {
        EditAgencyAdminAgencyProfileFragment fragment = new EditAgencyAdminAgencyProfileFragment();
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
        return inflater.inflate(R.layout.fragment_edit_agency_admin_agency_profile, container, false);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        topAppBar = view.findViewById(R.id.topAppBarEditAgencyAdminAgencyProfile);
        btnEditProfile = view.findViewById(R.id.btnEditAgencyAdminAgencyProfile);
        ivAgencyImageProfile = view.findViewById(R.id.ivAgencyImageProfile);
        mcvImageProfile = view.findViewById(R.id.mcvImageProfile);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        getAgencyDetails();

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
        mcvImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICK_CODE);
            }
        });
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == requireActivity().RESULT_OK) {
            if (requestCode == IMAGE_PICK_CODE && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri == null) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), "URI cannot be found", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                ImageUtil.startCrop(selectedImageUri, requireContext(), this);
            } else if (requestCode == UCrop.REQUEST_CROP) {
                Uri croppedImageUri = UCrop.getOutput(data);
                if (croppedImageUri != null) {
                    try {
                        // reset the imageview
                        ivAgencyImageProfile.setImageDrawable(null);
                        ivAgencyImageProfile.setImageURI(croppedImageUri);
                        // update the bitmap
                        imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), croppedImageUri);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
                if (cropError != null) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), "Crop error: " + cropError.getMessage(), Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), "Unknown crop error occurred.", Snackbar.LENGTH_SHORT).show();
                }
            }
        }
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
        boolean isValidAddress = ApplicationValidation.validateAddress(etAddressProfileLayout, agency.getAddress());
        return isValidName && isValidEmail && isValidPhoneNumber && isValidAddress;
    }

    private void updateAgency(Agency agency) {
        SharedPreferences sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("agencyAdminUserId", userId != null ? userId : sp.getString("userId", ""));
        if (imageBitmap != null) {
            String encodedImage = ImageUtil.encodeBase64(imageBitmap);
            params.put("image", encodedImage);
        }
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

        }, VolleyErrorHandler.newErrorListener(requireContext()));
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void getAgencyDetails() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("agencyAdminUserId", userId != null ? userId : sp.getString("userId", ""));
        String url = UrlUtil.constructUrl(get_agency_url, params);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, response -> {
            try {
                // retrieve user details
                agency = new Agency(response.getString("name"), response.getString("email"), response.getString("phoneNumber"), response.getString("address"));
                agency.setImage(ImageUtil.decodeBase64(response.getString("image")));
                populateAgencyItems();

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

    private void populateAgencyItems() {
        // Populate fields
        etNameProfile.setText(agency.getName());
        etEmailProfile.setText(agency.getEmail());
        etPhoneNumberProfile.setText(agency.getPhoneNumber());
        etAddressProfile.setText(agency.getAddress());
        if (agency.getImage() != null) {
            ivAgencyImageProfile.setImageBitmap(agency.getImage());
            ivAgencyImageProfile.setPadding(0, 0, 0, 0);
        }
    }
}