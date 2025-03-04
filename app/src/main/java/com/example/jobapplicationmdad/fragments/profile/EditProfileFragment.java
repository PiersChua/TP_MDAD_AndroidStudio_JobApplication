package com.example.jobapplicationmdad.fragments.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.example.jobapplicationmdad.fragments.admin.agencies.AdminAgenciesFragment;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
    private static final String ARG_PARAM2 = "showAppBarIfFragmentClosed";
    private static final String ARG_PARAM3 = "isDeleteAgencyAdmin";
    private static final String update_user_url = MainActivity.root_url + "/api/auth/update-user-details.php";
    private static final String get_user_url = MainActivity.root_url + "/api/auth/get-user-details.php";
    private static final String delete_user_url = MainActivity.root_url + "/api/auth/delete-user.php";
    private static final String remove_user_image_url = MainActivity.root_url + "/api/auth/remove-user-image.php";
    // User that is passed from profile fragment
    private String userId;
    private boolean showAppBarIfFragmentClosed;
    private boolean isDeleteAgencyAdmin;
    private User user;
    MaterialToolbar topAppBar;
    Button btnEditProfile;
    View dialogView;
    AlertDialog loadingDialog;
    SharedPreferences sp;
    EditText etFullNameProfile, etEmailProfile, etPhoneNumberProfile, etDateOfBirthProfile;

    TextInputLayout etFullNameProfileLayout, etEmailProfileLayout, etPhoneNumberProfileLayout, etDateOfBirthProfileLayout, etGenderProfileLayout, etNationalityProfileLayout, etRaceProfileLayout;
    AutoCompleteTextView actvGenderProfile, actvRaceProfile, actvNationalityprofile;
    ImageView ivUserImageProfile;
    private Bitmap imageBitmap;
    private String photoPath;
    private static final int IMAGE_PICK_CODE = 101;
    private static final int IMAGE_CAPTURE_CODE = 102;
    MaterialCardView mcvImageProfile;

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
    public static EditProfileFragment newInstance(String userId, boolean showAppBarIfFragmentClosed, boolean isDeleteAgencyAdmin) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, userId);
        args.putBoolean(ARG_PARAM2, showAppBarIfFragmentClosed);
        args.putBoolean(ARG_PARAM3, isDeleteAgencyAdmin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_PARAM1);
            showAppBarIfFragmentClosed = getArguments().getBoolean(ARG_PARAM2, true);
            isDeleteAgencyAdmin = getArguments().getBoolean(ARG_PARAM3, false);
        } else {
            showAppBarIfFragmentClosed = true;
            isDeleteAgencyAdmin = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        if (showAppBarIfFragmentClosed) {
            ((MainActivity) requireActivity()).showBottomNav();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        topAppBar = view.findViewById(R.id.topAppBarEditProfile);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        ivUserImageProfile = view.findViewById(R.id.ivUserImageProfile);
        mcvImageProfile = view.findViewById(R.id.mcvImageProfile);
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

        if (userId != null) {
            topAppBar.inflateMenu(R.menu.menu_edit_profile);
            topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.edit_profile_item_1) {
                        new MaterialAlertDialogBuilder(requireContext()).setTitle("Delete User").setMessage("You are about to delete this user. This action is NON REVERSIBLE. \nDo you wish to proceed?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                deleteUser();
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
        }
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
        mcvImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                List<String> options = new ArrayList<>();
                options.add("Import from library");
                options.add("Take photo");
                if (user.getImage() != null) {
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
                                photoPath = ImageUtil.dispatchTakePictureIntent(EditProfileFragment.this, requireActivity(), requireContext(), IMAGE_CAPTURE_CODE);
                                break;
                            }

                            case 2: {
                                removeUserImage();
                                break;
                            }
                        }

                    }
                });
                builder.create().show();

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
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setSelection(DateConverter.formatDateToMilliseconds(user.getDateOfBirth())).setTitleText("Select Date of Birth").setCalendarConstraints(constraintsBuilder.build()).build();
                datePicker.show(getParentFragmentManager(), "DATE_PICKER");

                datePicker.addOnPositiveButtonClickListener(selection -> {
                    etDateOfBirthProfile.setText(DateConverter.formatDateFromMilliseconds(selection));
                });
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
                        ivUserImageProfile.setImageDrawable(null);
                        ivUserImageProfile.setImageURI(croppedImageUri);
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
        params.put("userIdToBeUpdated", userId != null ? userId : sp.getString("userId", ""));
        params.put("fullName", updatedUser.getFullName());
        params.put("email", updatedUser.getEmail());
        params.put("phoneNumber", updatedUser.getPhoneNumber());
        params.put("dateOfBirth", DateConverter.formatDateForSql(updatedUser.getDateOfBirth()));
        params.put("gender", updatedUser.getGender());
        params.put("race", updatedUser.getRace());
        params.put("nationality", updatedUser.getNationality());
        if (imageBitmap != null) {
            String encodedImage = ImageUtil.encodeBase64(imageBitmap);
            params.put("image", encodedImage);
        }
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
        params.put("userIdToGet", userId != null ? userId : sp.getString("userId", ""));
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
                user.setImage(ImageUtil.decodeBase64(response.getString("image")));
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

    private void removeUserImage() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("userIdToBeUpdated", userId != null ? userId : sp.getString("userId", ""));
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, remove_user_image_url, params, headers, response -> {
            try {
                // reset the drawable back to default image
                ivUserImageProfile.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_person));
                ivUserImageProfile.setPadding(6, 6, 6, 6);
                user.setImage(null);
                Bundle result = new Bundle();
                result.putBoolean("isUpdated", true);
                getParentFragmentManager().setFragmentResult("editProfileResult", result);
                Snackbar.make(requireActivity().findViewById(android.R.id.content), response.getString("message"), Snackbar.LENGTH_SHORT).show();
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
        if (user.getImage() != null) {
            ivUserImageProfile.setImageBitmap(user.getImage());
            ivUserImageProfile.setPadding(0, 0, 0, 0);
        }
    }

    private void deleteUser() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("userIdToBeDeleted", userId);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, delete_user_url, params, headers, response -> {
            try {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), response.getString("message"), Snackbar.LENGTH_SHORT).setAnchorView(requireActivity().findViewById(R.id.bottom_navigation)).show();
                if (isDeleteAgencyAdmin) {
                    Bundle result = new Bundle();
                    result.putBoolean("isUpdated", true);
                    getParentFragmentManager().setFragmentResult("deleteAgencyResult", result);
                    requireActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    requireActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_left_to_right, R.anim.exit_left_to_right, R.anim.slide_right_to_left, R.anim.exit_right_to_left).replace(R.id.flMain, new AdminAgenciesFragment()).commit();
                } else {
                    Bundle result = new Bundle();
                    result.putBoolean("isUpdated", true);
                    getParentFragmentManager().setFragmentResult("editProfileResult", result);
                    getParentFragmentManager().popBackStack();
                }

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
}