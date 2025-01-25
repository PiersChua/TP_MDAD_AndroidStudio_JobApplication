package com.example.jobapplicationmdad.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.AuthValidation;
import com.example.jobapplicationmdad.util.DateConverter;
import com.example.jobapplicationmdad.util.EmailSender;
import com.example.jobapplicationmdad.util.StringUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class OTPVerificationActivity extends AppCompatActivity {
    public static final String verify_otp_url = MainActivity.root_url + "/api/auth/verify-otp.php";
    public static final String create_new_otp_url = MainActivity.root_url + "/api/auth/create-new-otp.php";
    TextView tvResendOtp;
    EditText etOtpFirst, etOtpSecond, etOtpThird, etOtpFourth, etOtpFifth, etOtpSixth;
    Button btnVerifyAccount;
    MaterialToolbar topAppBarAccountVerification;
    String email;
    View dialogView;
    AlertDialog loadingDialog;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otpverification);
        dialogView = getLayoutInflater().inflate(R.layout.dialog_loader, findViewById(android.R.id.content), false);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();

        email = getIntent().getStringExtra("email");
        topAppBarAccountVerification = findViewById(R.id.topAppBarAccountVerification);
        etOtpFirst = findViewById(R.id.etOtpFirst);
        etOtpSecond = findViewById(R.id.etOtpSecond);
        etOtpThird = findViewById(R.id.etOtpThird);
        etOtpFourth = findViewById(R.id.etOtpFourth);
        etOtpFifth = findViewById(R.id.etOtpFifth);
        etOtpSixth = findViewById(R.id.etOtpSixth);
        btnVerifyAccount = findViewById(R.id.btnVerifyAccount);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        sp = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        setupOtpFieldNavigation(etOtpFirst, etOtpSecond);
        setupOtpFieldNavigation(etOtpSecond, etOtpThird);
        setupOtpFieldNavigation(etOtpThird, etOtpFourth);
        setupOtpFieldNavigation(etOtpFourth, etOtpFifth);
        setupOtpFieldNavigation(etOtpFifth, etOtpSixth);
        setupOtpFieldNavigation(etOtpSixth, null);

        btnVerifyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateOtp()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OTPVerificationActivity.this);
                    builder.setMessage("Please fill in all fields")
                            .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
                    builder.create().show();
                    return;
                }
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
                finish();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupOtpFieldNavigation(EditText currentField, EditText nextField) {
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
                if (s.length() == 0) {
                    View previousField = currentField.focusSearch(View.FOCUS_LEFT);
                    if (previousField != null) {
                        previousField.requestFocus();
                    }
                }
            }
        });
        currentField.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == android.view.KeyEvent.KEYCODE_DEL
                    && event.getAction() == android.view.KeyEvent.ACTION_DOWN
                    && currentField.getText().toString().isEmpty()) {
                View previousField = currentField.focusSearch(View.FOCUS_LEFT);
                if (previousField != null) {
                    previousField.requestFocus();
                }
                return true;
            }
            return false;
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
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, verify_otp_url, params, response -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            // Get the currently focused view
            View currentFocus = getCurrentFocus();
            // Hide the keyboard if a view is focused
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
            try {
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
            loadingDialog.dismiss();
            Intent i = new Intent(OTPVerificationActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(this).onErrorResponse(error);
        });
        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }

    private void resendOtp() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        String otp = StringUtil.generateOTP();
        params.put("otp", otp);
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, create_new_otp_url, params, response -> {
            try {
                String subject = "Account verification - One time Password";
                String messageBody = String.format("Dear valued user of SGJobMarket, \n\nYour one time password is %s", otp);
                EmailSender emailSender = new EmailSender(email, subject, messageBody);
                emailSender.execute();
                String message = response.getString("message");
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            loadingDialog.dismiss();
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(this).onErrorResponse(error);
        });
        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }
}

