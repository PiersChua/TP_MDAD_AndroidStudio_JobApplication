package com.example.jobapplicationmdad.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private static final String register_url = MainActivity.root_url + "/api/auth/signup.php";
    TextView tvRedirectToLogin;
    Button btnRegister;
    View dialogView;
    AlertDialog loadingDialog;
    EditText etFullNameRegister, etEmailRegister, etPhoneNumberRegister, etPasswordRegister, etConfirmPasswordRegister;

    TextInputLayout etFullNameRegisterLayout, etEmailRegisterLayout, etPhoneNumberRegisterLayout, etRoleRegisterLayout, etPasswordRegisterLayout, etConfirmPasswordRegisterLayout;

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

        // Form Layouts
        etFullNameRegisterLayout = findViewById(R.id.etFullNameRegisterLayout);
        etEmailRegisterLayout = findViewById(R.id.etEmailRegisterLayout);
        etPhoneNumberRegisterLayout = findViewById(R.id.etPhoneNumberRegisterLayout);
        etRoleRegisterLayout = findViewById(R.id.etRoleRegisterLayout);
        etPasswordRegisterLayout = findViewById(R.id.etPasswordRegisterLayout);
        etConfirmPasswordRegisterLayout = findViewById(R.id.etConfirmPasswordRegisterLayout);
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
        String email = etEmailRegister.getText().toString();
        String phoneNumber = etPhoneNumberRegister.getText().toString();
        String role = Objects.requireNonNull(etRoleRegisterLayout.getEditText()).getText().toString();
        String password = etPasswordRegister.getText().toString();
        String confirmPassword = etConfirmPasswordRegister.getText().toString();
        return new User(fullName, email, phoneNumber, role, password, confirmPassword);
    }

    private boolean validateUser(User user) {
        boolean isValidName = AuthValidation.validateName(etFullNameRegisterLayout, user.getFullName());
        boolean isValidEmail = AuthValidation.validateEmail(etEmailRegisterLayout, user.getEmail());
        boolean isValidPhoneNumber = AuthValidation.validatePhoneNumber(etPhoneNumberRegisterLayout, user.getPhoneNumber());
        boolean isValidRole = AuthValidation.validateRole(etRoleRegisterLayout, user.getRole());
        boolean isValidPassword = AuthValidation.validatePassword(etPasswordRegisterLayout, user.getPassword(), false);
        boolean isValidConfirmPassword = AuthValidation.validateConfirmPassword(etConfirmPasswordRegisterLayout, user.getPassword(), user.getConfirmPassword());

        return isValidName && isValidEmail && isValidPhoneNumber && isValidRole && isValidPassword && isValidConfirmPassword;
    }

    private void registerUser(User user) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("fullName", user.getFullName());
        params.put("email", user.getEmail());
        params.put("phoneNumber", user.getPhoneNumber());
        params.put("role", user.getRole());
        params.put("password", user.getPassword());
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, register_url, params, response -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            // Get the currently focused view
            View currentFocus = getCurrentFocus();
            // Hide the keyboard if a view is focused
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
            finish();

            loadingDialog.dismiss();

        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(getApplicationContext(), findViewById(android.R.id.content)).onErrorResponse(error);
        });
        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }
}