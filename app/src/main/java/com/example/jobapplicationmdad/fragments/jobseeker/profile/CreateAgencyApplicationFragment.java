
package com.example.jobapplicationmdad.fragments.jobseeker.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.fragments.profile.EditProfileFragment;
import com.example.jobapplicationmdad.model.AgencyApplication;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.ApplicationValidation;
import com.example.jobapplicationmdad.util.AuthValidation;
import com.example.jobapplicationmdad.util.ImageUtil;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateAgencyApplicationFragment extends Fragment {

    private static final String ARG_PARAM1 = "userId";
    private static final String ARG_PARAM2 = "token";
    private static final String create_agency_application_url = MainActivity.root_url + "/api/job-seeker/create-agency-application.php";
    private static final String get_agency_application_count_url = MainActivity.root_url + "/api/job-seeker/get-agency-application-count.php";
    private static final int IMAGE_PICK_CODE = 101;
    private static final int IMAGE_CAPTURE_CODE = 102;

    private String userId;
    private String token;
    private Bitmap imageBitmap;
    private String photoPath;

    MaterialToolbar topAppBar;
    EditText etNameAgencyApplication, etEmailAgencyApplication, etPhoneNumberAgencyApplication, etAddressAgencyApplication;
    TextInputLayout etNameAgencyApplicationLayout, etEmailAgencyApplicationLayout, etPhoneNumberAgencyApplicationLayout, etAddressAgencyApplicationLayout;
    Button btnCreateAgencyApplication;
    ImageView ivAgencyApplicationAgencyImageProfile;
    MaterialCardView mcvAgencyApplicationImage;
    FrameLayout flContent, flAgencyApplicationSubmitted;
    LinearLayout llCreateAgencyApplication;

    public CreateAgencyApplicationFragment() {
        // Required empty public constructor
    }

    public static CreateAgencyApplicationFragment newInstance(String param1, String param2) {
        CreateAgencyApplicationFragment fragment = new CreateAgencyApplicationFragment();
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
            userId = getArguments().getString(ARG_PARAM1);
            token = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_agency_application, container, false);
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
        topAppBar = view.findViewById(R.id.topAppBarAgencyApplication);
        btnCreateAgencyApplication = view.findViewById(R.id.btnCreateAgencyApplication);
        ivAgencyApplicationAgencyImageProfile = view.findViewById(R.id.ivAgencyApplicationAgencyImageProfile);
        mcvAgencyApplicationImage = view.findViewById(R.id.mcvImageProfile);
        flContent = view.findViewById(R.id.flContent);
        flAgencyApplicationSubmitted = view.findViewById(R.id.flAgencyApplicationSubmitted);
        llCreateAgencyApplication = view.findViewById(R.id.llCreateAgencyApplication);
        getAgencyApplicationCount();

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

        topAppBar.setNavigationOnClickListener(view1 -> {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            View currentFocus = requireActivity().getCurrentFocus();
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
            getParentFragmentManager().popBackStack();
        });

        mcvAgencyApplicationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                List<String> options = new ArrayList<>();
                options.add("Import from library");
                options.add("Take photo");
                if (imageBitmap != null) {
                    options.add("Remove image");
                }
                builder.setItems(options.toArray(new String[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0: {
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, IMAGE_PICK_CODE);
                                break;
                            }
                            case 1: {
                                photoPath = ImageUtil.dispatchTakePictureIntent(CreateAgencyApplicationFragment.this, requireActivity(), requireContext(), IMAGE_CAPTURE_CODE);
                                break;
                            }

                            case 2: {
                                ivAgencyApplicationAgencyImageProfile.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_building));
                                ivAgencyApplicationAgencyImageProfile.setPadding(6, 6, 6, 6);
                                break;
                            }
                        }

                    }
                });
                builder.create().show();

            }
        });

        btnCreateAgencyApplication.setOnClickListener(v -> {
            AgencyApplication application = getAgencyApplicationFromForm();
            if (validateAgencyApplication(application)) {
                createAgencyApplication(application);
            }
        });
    }

    private AgencyApplication getAgencyApplicationFromForm() {
        String name = etNameAgencyApplication.getText().toString().trim();
        String email = etEmailAgencyApplication.getText().toString();
        String phoneNumber = etPhoneNumberAgencyApplication.getText().toString();
        String address = etAddressAgencyApplication.getText().toString().trim();
        return new AgencyApplication(name, email, phoneNumber, address, userId);
    }

    private boolean validateAgencyApplication(AgencyApplication application) {
        boolean isValidName = AuthValidation.validateName(etNameAgencyApplicationLayout, application.getName());
        boolean isValidEmail = AuthValidation.validateEmail(etEmailAgencyApplicationLayout, application.getEmail());
        boolean isValidPhoneNumber = AuthValidation.validatePhoneNumber(etPhoneNumberAgencyApplicationLayout, application.getPhoneNumber());
        boolean isValidAddress = ApplicationValidation.validateAddress(etAddressAgencyApplicationLayout, application.getAddress());
        return isValidName && isValidEmail && isValidPhoneNumber && isValidAddress;
    }

    private void getAgencyApplicationCount() {
        Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        String url = UrlUtil.constructUrl(get_agency_application_count_url, params);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, response -> {
            try {
                int agencyApplicationCount = response.getInt("data");
                if(agencyApplicationCount>0){
                    flAgencyApplicationSubmitted.setVisibility(View.VISIBLE);
                    llCreateAgencyApplication.setVisibility(View.GONE);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) flContent.getLayoutParams();
                    layoutParams.gravity = Gravity.CENTER;
                    flContent.setLayoutParams(layoutParams);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, VolleyErrorHandler.newErrorListener(requireContext()));

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void createAgencyApplication(AgencyApplication application) {
        Map<String, String> params = new HashMap<>();
        params.put("name", application.getName());
        params.put("email", application.getEmail());
        params.put("phoneNumber", application.getPhoneNumber());
        params.put("address", application.getAddress());
        params.put("userId", userId);

        if (imageBitmap != null) {
            String encodedImage = ImageUtil.encodeBase64(imageBitmap);
            params.put("image", encodedImage);
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, create_agency_application_url, params, headers, response -> {
            try {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                View currentFocus = requireActivity().getCurrentFocus();
                if (currentFocus != null) {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
                Snackbar.make(requireActivity().findViewById(android.R.id.content), response.getString("message"), Snackbar.LENGTH_SHORT).setAnchorView(R.id.bottom_navigation).show();
                getParentFragmentManager().popBackStack();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, VolleyErrorHandler.newErrorListener(requireContext()));

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
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
            } else if (requestCode == IMAGE_CAPTURE_CODE && photoPath != null) {
                File imgFile = new File(photoPath);
                Uri capturedImageUri = null;
                if (imgFile.exists()) {
                    capturedImageUri = Uri.fromFile(imgFile);
                }
                if (capturedImageUri == null) {
                    return;
                }
                ImageUtil.startCrop(capturedImageUri, requireContext(), this);
            } else if (requestCode == UCrop.REQUEST_CROP) {
                Uri croppedImageUri = UCrop.getOutput(data);
                if (croppedImageUri != null) {
                    try {
                        // reset the imageview
                        ivAgencyApplicationAgencyImageProfile.setImageDrawable(null);
                        ivAgencyApplicationAgencyImageProfile.setImageURI(croppedImageUri);
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


}
