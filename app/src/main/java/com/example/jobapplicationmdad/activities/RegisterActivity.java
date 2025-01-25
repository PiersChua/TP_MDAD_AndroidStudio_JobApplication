package com.example.jobapplicationmdad.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.AuthValidation;
import com.example.jobapplicationmdad.util.DateConverter;
import com.example.jobapplicationmdad.util.EmailSender;
import com.example.jobapplicationmdad.util.StringUtil;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class RegisterActivity extends AppCompatActivity {
    private static final String register_url = MainActivity.root_url + "/api/auth/signup.php";
    TextView tvRedirectToLogin;
    Button btnRegister;
    View dialogView;
    AlertDialog loadingDialog;
    EditText etFullNameRegister, etEmailRegister, etPhoneNumberRegister, etPasswordRegister, etConfirmPasswordRegister, etDateOfBirthRegister;

    TextInputLayout etFullNameRegisterLayout, etEmailRegisterLayout, etPhoneNumberRegisterLayout, etRoleRegisterLayout, etPasswordRegisterLayout, etConfirmPasswordRegisterLayout, etDateOfBirthRegisterLayout, etGenderRegisterLayout, etNationalityRegisterLayout, etRaceRegisterLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        tvRedirectToLogin = findViewById(R.id.tvRedirectToLogin);
        btnRegister = findViewById(R.id.btnRegister);

        dialogView = getLayoutInflater().inflate(R.layout.dialog_loader, findViewById(android.R.id.content), false);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();

        // Form
        etFullNameRegister = findViewById(R.id.etFullNameRegister);
        etEmailRegister = findViewById(R.id.etEmailRegister);
        etPhoneNumberRegister = findViewById(R.id.etPhoneNumberRegister);
        etPasswordRegister = findViewById(R.id.etPasswordRegister);
        etConfirmPasswordRegister = findViewById(R.id.etConfirmPasswordRegister);
        etDateOfBirthRegister = findViewById(R.id.etDateOfBirthRegister);


        // Form Layouts
        etFullNameRegisterLayout = findViewById(R.id.etFullNameRegisterLayout);
        etEmailRegisterLayout = findViewById(R.id.etEmailRegisterLayout);
        etPhoneNumberRegisterLayout = findViewById(R.id.etPhoneNumberRegisterLayout);
        etRoleRegisterLayout = findViewById(R.id.etRoleRegisterLayout);
        etPasswordRegisterLayout = findViewById(R.id.etPasswordRegisterLayout);
        etConfirmPasswordRegisterLayout = findViewById(R.id.etConfirmPasswordRegisterLayout);
        etDateOfBirthRegisterLayout = findViewById(R.id.etDateOfBirthRegisterLayout);
        etGenderRegisterLayout = findViewById(R.id.etGenderRegisterLayout);
        etRaceRegisterLayout = findViewById(R.id.etRaceRegisterLayout);
        etNationalityRegisterLayout = findViewById(R.id.etNationalityRegisterLayout);

        etDateOfBirthRegister.setOnClickListener(new View.OnClickListener() {
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
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select Date of Birth").setCalendarConstraints(constraintsBuilder.build()).build();
                datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

                datePicker.addOnPositiveButtonClickListener(selection -> {
                    etDateOfBirthRegister.setText(DateConverter.formatDateFromMilliseconds(selection));
                });
            }
        });
        tvRedirectToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = getUserFromForm();
                if (validateUser(user)) {
                    loadingDialog.show();
                    registerUser(user);
                }
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.llRegister), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private User getUserFromForm() {
        String fullName = etFullNameRegister.getText().toString().trim();
        String email = etEmailRegister.getText().toString().trim();
        String phoneNumber = etPhoneNumberRegister.getText().toString();
        String role = Objects.requireNonNull(etRoleRegisterLayout.getEditText()).getText().toString();
        String password = etPasswordRegister.getText().toString();
        String confirmPassword = etConfirmPasswordRegister.getText().toString();
        String dateOfBirth = etDateOfBirthRegister.getText().toString();
        String gender = Objects.requireNonNull(etGenderRegisterLayout.getEditText()).getText().toString();
        String race = Objects.requireNonNull(etRaceRegisterLayout.getEditText()).getText().toString();
        String nationality = Objects.requireNonNull(etNationalityRegisterLayout.getEditText()).getText().toString();
        return new User(fullName, email, phoneNumber, role, password, confirmPassword, dateOfBirth, gender, race, nationality);
    }

    private boolean validateUser(User user) {
        boolean isValidName = AuthValidation.validateName(etFullNameRegisterLayout, user.getFullName());
        boolean isValidEmail = AuthValidation.validateEmail(etEmailRegisterLayout, user.getEmail());
        boolean isValidPhoneNumber = AuthValidation.validatePhoneNumber(etPhoneNumberRegisterLayout, user.getPhoneNumber());
        boolean isValidRole = AuthValidation.validateEnum(etRoleRegisterLayout, "Role", user.getRole(), getResources().getStringArray(R.array.role_items));
        boolean isValidPassword = AuthValidation.validatePassword(etPasswordRegisterLayout, user.getPassword(), false);
        boolean isValidConfirmPassword = AuthValidation.validateConfirmPassword(etConfirmPasswordRegisterLayout, user.getPassword(), user.getConfirmPassword());
        boolean isValidDateOfBirth = AuthValidation.validateNull(etDateOfBirthRegisterLayout, "Date of Birth", user.getDateOfBirth());
        boolean isValidGender = AuthValidation.validateEnum(etGenderRegisterLayout, "Gender", user.getGender(), getResources().getStringArray(R.array.gender_items));
        boolean isValidRace = AuthValidation.validateEnum(etRaceRegisterLayout, "Race", user.getRace(), getResources().getStringArray(R.array.race_items));
        boolean isValidNationality = AuthValidation.validateEnum(etNationalityRegisterLayout, "Nationality", user.getNationality(), getResources().getStringArray(R.array.nationality_items));
        ;

        return isValidName && isValidEmail && isValidPhoneNumber && isValidRole && isValidPassword && isValidConfirmPassword && isValidDateOfBirth && isValidGender && isValidRace && isValidNationality;
    }

    private void registerUser(User user) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("fullName", user.getFullName());
        params.put("email", user.getEmail());
        params.put("phoneNumber", user.getPhoneNumber());
        params.put("role", user.getRole());
        params.put("password", user.getPassword());
        params.put("dateOfBirth", DateConverter.formatDateForSql(user.getDateOfBirth()));
        params.put("gender", user.getGender());
        params.put("race", user.getRace());
        params.put("nationality", user.getNationality());
        String otp = StringUtil.generateOTP();
        params.put("otp", otp);
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, register_url, params, response -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            // Get the currently focused view
            View currentFocus = getCurrentFocus();
            // Hide the keyboard if a view is focused
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);

            }

            String recipientEmail = user.getEmail();
            String subject = "Account verification - One time Password";
            String messageBody = String.format("Dear valued user of SGJobMarket, \n\nYour one time password is %s", otp);

            EmailSender emailSender = new EmailSender(recipientEmail, subject, messageBody);
            emailSender.execute();
            Intent i = new Intent(RegisterActivity.this, OTPVerificationActivity.class);
            i.putExtra("email", user.getEmail());
            startActivity(i);
            loadingDialog.dismiss();
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(RegisterActivity.this).onErrorResponse(error);
        });
        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }

}