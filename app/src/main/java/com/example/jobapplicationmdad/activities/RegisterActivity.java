package com.example.jobapplicationmdad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jobapplicationmdad.R;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    private static String register_url = MainActivity.root_url + "/api/auth/signup.php";
    TextView tvRedirectToLogin;
    Button btnRegister;
    EditText etFullNameRegister, etEmailRegister, etPhoneNumberRegister, etPasswordRegister, etConfirmPasswordRegister;

    TextInputLayout etFullNameRegisterLayout, etEmailRegisterLayout, etPhoneNumberRegisterLayout, etPasswordRegisterLayout, etConfirmPasswordRegisterLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        tvRedirectToLogin = findViewById(R.id.tvRedirectToLogin);
        btnRegister = findViewById(R.id.btnRegister);

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
                String fullName = etFullNameRegister.getText().toString();
                String email = etEmailRegister.getText().toString();
                String phoneNumber = etPhoneNumberRegister.getText().toString();
                String password = etPasswordRegister.getText().toString();
                String confirmPassword = etConfirmPasswordRegister.getText().toString();
            }
        });
        etPasswordRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String password = editable.toString();
                String confirmPassword = etConfirmPasswordRegister.getText().toString();
                if (!confirmPassword.isEmpty() && !confirmPassword.equals(password)) {
                    etConfirmPasswordRegisterLayout.setError("Passwords do not match");
                } else {
                    etConfirmPasswordRegisterLayout.setError(null);
                }
            }
        });
        etConfirmPasswordRegister.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String password = etPasswordRegister.getText().toString();
                String confirmPassword = editable.toString();
                if (!confirmPassword.equals(password)) {
                    etConfirmPasswordRegisterLayout.setError("Passwords do not match");
                } else {
                    etConfirmPasswordRegisterLayout.setError(null);
                }
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.llRegister), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}