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

public class RegisterActivity extends AppCompatActivity {
    TextView tvRedirectToLogin;
    Button btnRegister;
    EditText etFullNameRegister, etEmailRegister, etPhoneNumberRegister, etPasswordRegister, etConfirmPasswordRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        tvRedirectToLogin = findViewById(R.id.tvRedirectToLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvRedirectToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etFullNameRegister = findViewById(R.id.etFullNameRegister);
                etEmailRegister = findViewById(R.id.etEmailRegister);
                etPhoneNumberRegister = findViewById(R.id.etPhoneNumberRegister);
                etPasswordRegister = findViewById(R.id.etPasswordRegister);
                etConfirmPasswordRegister = findViewById(R.id.etConfirmPasswordRegister);


            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.llRegister), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}