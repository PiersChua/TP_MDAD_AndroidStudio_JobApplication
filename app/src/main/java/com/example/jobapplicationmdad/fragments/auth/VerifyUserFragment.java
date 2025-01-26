package com.example.jobapplicationmdad.fragments.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.EmailSender;
import com.example.jobapplicationmdad.util.StringUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VerifyUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifyUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";
    private static final String ARG_PARAM2 = "isPasswordReset";

    // TODO: Rename and change types of parameters
    private String email;
    private boolean isPasswordReset;
    public static final String verify_verification_otp_url = MainActivity.root_url + "/api/auth/verify-verification-otp.php";
    public static final String verify_password_reset_otp_url = MainActivity.root_url + "/api/auth/verify-password-reset-otp.php";
    public static final String create_new_verification_otp_url = MainActivity.root_url + "/api/auth/create-new-verification-otp.php";
    public static final String create_new_password_reset_otp_url = MainActivity.root_url + "/api/auth/create-new-password-reset-otp.php";
    TextView tvResendOtp;
    EditText etOtpFirst, etOtpSecond, etOtpThird, etOtpFourth, etOtpFifth, etOtpSixth;
    Button btnVerifyAccount;
    MaterialToolbar topAppBarAccountVerification;
    View dialogView;
    AlertDialog loadingDialog;
    SharedPreferences sp;

    public VerifyUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment VerifyUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VerifyUserFragment newInstance(String email, boolean isPasswordReset) {
        VerifyUserFragment fragment = new VerifyUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, email);
        args.putBoolean(ARG_PARAM2, isPasswordReset);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(ARG_PARAM1);
            isPasswordReset = getArguments().getBoolean(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialogView = getLayoutInflater().inflate(R.layout.dialog_loader, container, false);
        return inflater.inflate(R.layout.fragment_verify_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();

        topAppBarAccountVerification = view.findViewById(R.id.topAppBarAccountVerification);
        etOtpFirst = view.findViewById(R.id.etOtpFirst);
        etOtpSecond = view.findViewById(R.id.etOtpSecond);
        etOtpThird = view.findViewById(R.id.etOtpThird);
        etOtpFourth = view.findViewById(R.id.etOtpFourth);
        etOtpFifth = view.findViewById(R.id.etOtpFifth);
        etOtpSixth = view.findViewById(R.id.etOtpSixth);
        btnVerifyAccount = view.findViewById(R.id.btnVerifyOtp);
        tvResendOtp = view.findViewById(R.id.tvResendOtp);


        setupOtpFieldNavigation(null, etOtpFirst, etOtpSecond);
        setupOtpFieldNavigation(etOtpFirst, etOtpSecond, etOtpThird);
        setupOtpFieldNavigation(etOtpSecond, etOtpThird, etOtpFourth);
        setupOtpFieldNavigation(etOtpThird, etOtpFourth, etOtpFifth);
        setupOtpFieldNavigation(etOtpFourth, etOtpFifth, etOtpSixth);
        setupOtpFieldNavigation(etOtpFifth, etOtpSixth, null);

        btnVerifyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateOtp()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage("Please fill in all fields")
                            .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
                    builder.create().show();
                    return;
                }
                loadingDialog.show();
                String otp = etOtpFirst.getText().toString().trim() +
                        etOtpSecond.getText().toString().trim() +
                        etOtpThird.getText().toString().trim() +
                        etOtpFourth.getText().toString().trim() +
                        etOtpFifth.getText().toString().trim() +
                        etOtpSixth.getText().toString().trim();
                verifyOtp(otp);
            }
        });
        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendOtp();
            }
        });
        topAppBarAccountVerification.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void setupOtpFieldNavigation(EditText previousField, EditText currentField, EditText nextField) {
        currentField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && nextField != null) {
                    nextField.requestFocus();
                }
            }

            // used when a text has been changed (eg. deleted or inserted)
            @Override
            public void afterTextChanged(Editable s) {
                // checks if the action is a delete
                if (s.length() == 0 && previousField != null) {
                    previousField.requestFocus();
                }
            }
        });
    }

    private boolean validateOtp() {
        return !etOtpFirst.getText().toString().trim().isEmpty() &&
                !etOtpSecond.getText().toString().trim().isEmpty() &&
                !etOtpThird.getText().toString().trim().isEmpty() &&
                !etOtpFourth.getText().toString().trim().isEmpty() &&
                !etOtpFifth.getText().toString().trim().isEmpty() &&
                !etOtpSixth.getText().toString().trim().isEmpty();
    }

    private void verifyOtp(String otp) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("otp", otp);
        String url = isPasswordReset ? verify_password_reset_otp_url : verify_verification_otp_url;
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, url, params, response -> {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            // Get the currently focused view
            View currentFocus = requireActivity().getCurrentFocus();
            // Hide the keyboard if a view is focused
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
            loadingDialog.dismiss();
            try {
                if (isPasswordReset) {
                    getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flLogin, NewPasswordFragment.newInstance(email)).addToBackStack(null).commit();
                    return;
                }
                String name = response.getString("fullName");
                String userId = response.getString("userId");
                String role = response.getString("role");
                String token = response.getString("token");
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("name", name);
                editor.putString("userId", userId);
                editor.putString("role", role);
                editor.putString("token", token);
                editor.apply(); // use apply to write update asynchronously, alternatively can use commit()
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            Intent i = new Intent(requireContext(), MainActivity.class);
            startActivity(i);
            requireActivity().finish();
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(requireContext()).onErrorResponse(error);
        });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void resendOtp() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        String otp = StringUtil.generateOTP();
        params.put("otp", otp);
        String url = isPasswordReset ? create_new_password_reset_otp_url : create_new_verification_otp_url;
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, url, params, response -> {
            try {
                String subject = String.format("%s - One time Password", isPasswordReset ? "Reset password" : "Account Verification");
                String messageBody = String.format("Dear valued user of SGJobMarket, \n\nYour one time password is %s", otp);
                EmailSender emailSender = new EmailSender(email, subject, messageBody);
                emailSender.execute();
                String message = response.getString("message");
                Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            loadingDialog.dismiss();
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(requireContext()).onErrorResponse(error);
        });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }
}