package com.example.jobapplicationmdad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.util.AuthValidation;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    TextView tvRedirectToRegister;
    Button btnLogin;
    EditText etEmailLogin, etPasswordLogin;
    TextInputLayout etEmailLoginLayout, etPasswordLoginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        tvRedirectToRegister = findViewById(R.id.tvRedirectToRegister);
        btnLogin = findViewById(R.id.btnLogin);

        // Form
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);

        // Form Layouts
        etEmailLoginLayout = findViewById(R.id.etEmailLoginLayout);
        etPasswordLoginLayout = findViewById(R.id.etPasswordLoginLayout);
        tvRedirectToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = getUserFromForm();
                if (validateUser(user)) {
                    loginUser(user);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.llLogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private User getUserFromForm() {
        String email = etEmailLogin.getText().toString();
        String password = etPasswordLogin.getText().toString();
        return new User(email, password);
    }

    private boolean validateUser(User user) {
        boolean isValidEmail = AuthValidation.validateEmail(etEmailLoginLayout, user.getEmail());
        boolean isValidPassword = AuthValidation.validatePassword(etPasswordLoginLayout, user.getPassword());
        return isValidEmail && isValidPassword;
    }

    private void loginUser(User user) {
        /*
         * TODO: Add login function
         * */
    }
}