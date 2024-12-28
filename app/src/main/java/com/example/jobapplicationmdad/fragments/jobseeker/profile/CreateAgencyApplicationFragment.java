//package com.example.jobapplicationmdad.fragments.jobseeker.profile;
//
//import android.content.Context;
//import android.os.Bundle;
//
//import androidx.activity.OnBackPressedCallback;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.example.jobapplicationmdad.R;
//import com.example.jobapplicationmdad.activities.MainActivity;
//import com.example.jobapplicationmdad.model.AgencyApplication;
//import com.example.jobapplicationmdad.model.User;
//import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
//import com.example.jobapplicationmdad.network.VolleyErrorHandler;
//import com.example.jobapplicationmdad.network.VolleySingleton;
//import com.example.jobapplicationmdad.util.AuthValidation;
//import com.google.android.material.appbar.MaterialToolbar;
//import com.google.android.material.snackbar.Snackbar;
//import com.google.android.material.textfield.TextInputLayout;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link CreateAgencyApplicationFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class CreateAgencyApplicationFragment extends Fragment {
//
//    private static final String ARG_PARAM1 = "userId";
//    private static final String ARG_PARAM2 = "token";
//    private static final String create_agency_application_url = MainActivity.root_url + "/api/job-seeker/create-agency-application.php";
//
//    private String userId;
//    private String token;
//    MaterialToolbar topAppBar;
//    EditText etNameAgencyApplication, etEmailAgencyApplication, etPhoneNumberAgencyApplication, etAddressAgencyApplication;
//    TextInputLayout etNameAgencyApplicationLayout, etEmailAgencyApplicationLayout, etPhoneNumberAgencyApplicationLayout, etAddressAgencyApplicationLayout;
//    Button btnCreateAgencyApplication,btnUploadImage;
//
//    public CreateAgencyApplicationFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 The userId of the user that will be submitting the application
//     * @param param2 The JSON Web Token (JWT) that is stored in shared preferences
//     * @return A new instance of fragment CreateAgencyApplicationFragment
//     */
//    // TODO: Rename and change types and number of parameters
//    public static CreateAgencyApplicationFragment newInstance(String param1, String param2) {
//        CreateAgencyApplicationFragment fragment = new CreateAgencyApplicationFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            userId = getArguments().getString(ARG_PARAM1);
//            token = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_create_agency_application, container, false);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        ((MainActivity) requireActivity()).hideBottomNav();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        ((MainActivity) requireActivity()).showBottomNav();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        topAppBar = view.findViewById(R.id.topAppBarAgencyApplication);
//        btnCreateAgencyApplication = view.findViewById(R.id.btnCreateAgencyApplication);
//        btnUploadImage = view.findViewById(R.id.btnUploadImage);
//
//        // Form
//        etNameAgencyApplication = view.findViewById(R.id.etNameAgencyApplication);
//        etEmailAgencyApplication = view.findViewById(R.id.etEmailAgencyApplication);
//        etPhoneNumberAgencyApplication = view.findViewById(R.id.etPhoneNumberAgencyApplication);
//        etAddressAgencyApplication = view.findViewById(R.id.etAddressAgencyApplication);
//
//        // Form Layouts
//        etNameAgencyApplicationLayout = view.findViewById(R.id.etNameAgencyApplicationLayout);
//        etEmailAgencyApplicationLayout = view.findViewById(R.id.etEmailAgencyApplicationLayout);
//        etPhoneNumberAgencyApplicationLayout = view.findViewById(R.id.etPhoneNumberAgencyApplicationLayout);
//        etAddressAgencyApplicationLayout = view.findViewById(R.id.etAddressAgencyApplicationLayout);
//        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                // Get the currently focused view
//                View currentFocus = requireActivity().getCurrentFocus();
//                // Hide the keyboard if a view is focused
//                if (currentFocus != null) {
//                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
//                }
//                getParentFragmentManager().popBackStack();
//            }
//        });
//        btnCreateAgencyApplication.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AgencyApplication application = getAgencyApplicationFromForm();
//                if (validateAgencyApplication(application)) {
//                    createAgencyApplication(application);
//                }
//            }
//        });
//        btnUploadImage.setOnClickListener(new View.OnClickListener() {
//        });
//
//    }
//
//    private AgencyApplication getAgencyApplicationFromForm() {
//        String name = etNameAgencyApplication.getText().toString().trim();
//        String email = etEmailAgencyApplication.getText().toString();
//        String phoneNumber = etPhoneNumberAgencyApplication.getText().toString();
//        String address = etAddressAgencyApplication.getText().toString().trim();
//        return new AgencyApplication(name, email, phoneNumber, address, userId);
//    }
//
//    private boolean validateAgencyApplication(AgencyApplication application) {
//        boolean isValidName = AuthValidation.validateName(etNameAgencyApplicationLayout, application.getName());
//        boolean isValidEmail = AuthValidation.validateEmail(etEmailAgencyApplicationLayout, application.getEmail());
//        boolean isValidPhoneNumber = AuthValidation.validatePhoneNumber(etPhoneNumberAgencyApplicationLayout, application.getPhoneNumber());
//        return isValidName && isValidEmail && isValidPhoneNumber;
//    }
//
//    private void createAgencyApplication(AgencyApplication application) {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("name", application.getName());
//        params.put("email", application.getEmail());
//        params.put("phoneNumber", application.getPhoneNumber());
//        params.put("address", application.getAddress());
//        params.put("userId", userId);
//
//        Map<String, String> headers = new HashMap<String, String>();
//        headers.put("Authorization", "Bearer " + token);
//        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, create_agency_application_url, params, headers, response -> {
//            try {
//                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                // Get the currently focused view
//                View currentFocus = requireActivity().getCurrentFocus();
//                // Hide the keyboard if a view is focused
//                if (currentFocus != null) {
//                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
//                }
//                Snackbar.make(requireActivity().findViewById(android.R.id.content), response.getString("message"), Snackbar.LENGTH_SHORT).setAnchorView(requireActivity().findViewById(R.id.bottom_navigation)).show();
//                getParentFragmentManager().popBackStack();
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//        }, VolleyErrorHandler.newErrorListener(requireContext(), requireActivity().findViewById(android.R.id.content)));
//        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
//    }
//}
package com.example.jobapplicationmdad.fragments.jobseeker.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.model.AgencyApplication;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.AuthValidation;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateAgencyApplicationFragment extends Fragment {

    private static final String ARG_PARAM1 = "userId";
    private static final String ARG_PARAM2 = "token";
    private static final String create_agency_application_url = MainActivity.root_url + "/api/job-seeker/create-agency-application.php";
    private static final int IMAGE_PICK_CODE = 1000;

    private String userId;
    private String token;
    private Bitmap selectedImageBitmap;

    MaterialToolbar topAppBar;
    EditText etNameAgencyApplication, etEmailAgencyApplication, etPhoneNumberAgencyApplication, etAddressAgencyApplication;
    TextInputLayout etNameAgencyApplicationLayout, etEmailAgencyApplicationLayout, etPhoneNumberAgencyApplicationLayout, etAddressAgencyApplicationLayout;
    Button btnCreateAgencyApplication, btnUploadImage;
    ImageView ivSelectedImage;

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
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        ivSelectedImage = view.findViewById(R.id.ivSelectedImage);

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

        btnUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE_PICK_CODE);
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
        return isValidName && isValidEmail && isValidPhoneNumber;
    }

    private void createAgencyApplication(AgencyApplication application) {
        Map<String, String> params = new HashMap<>();
        params.put("name", application.getName());
        params.put("email", application.getEmail());
        params.put("phoneNumber", application.getPhoneNumber());
        params.put("address", application.getAddress());
        params.put("userId", userId);

        if (selectedImageBitmap != null) {
            String encodedImage = encodeImageToBase64(selectedImageBitmap);
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
                Snackbar.make(requireActivity().findViewById(android.R.id.content), response.getString("message"), Snackbar.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, VolleyErrorHandler.newErrorListener(requireContext(), requireActivity().findViewById(android.R.id.content)));

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == requireActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                ivSelectedImage.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
