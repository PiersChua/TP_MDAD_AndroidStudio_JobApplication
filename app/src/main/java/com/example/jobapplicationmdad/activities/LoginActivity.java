package com.example.jobapplicationmdad.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.android.volley.Request;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.AuthValidation;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String login_url = MainActivity.root_url + "/api/auth/login.php";
    TextView tvRedirectToRegister;
    Button btnLogin;
    EditText etEmailLogin, etPasswordLogin;
    TextInputLayout etEmailLoginLayout, etPasswordLoginLayout;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        tvRedirectToRegister = findViewById(R.id.tvRedirectToRegister);
        btnLogin = findViewById(R.id.btnLogin);

        sp = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

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
        boolean isValidPassword = AuthValidation.validatePassword(etPasswordLoginLayout, user.getPassword(), true);
        return isValidEmail && isValidPassword;
    }

    private void loginUser(User user) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", user.getEmail());
        params.put("password", user.getPassword());
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, login_url, params, response -> {
            try {
                if (response.getString("type").equals("Success")) {
                    // retrieve user details and token from response
                    String name = response.getString("fullName");
                    String userId = response.getString("userId");
                    String role = response.getString("role");
                    String token = response.getString("token");

                    // store the details in shared preferences
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("name", name);
                    editor.putString("userId", userId);
                    editor.putString("role", role);
                    editor.putString("token", token);
                    editor.apply(); // use apply to write update asynchronously, alternatively can use commit()
                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else if (response.getString("type").equals("Error")) {
                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, VolleyErrorHandler.newErrorListener(getApplicationContext()));
        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }
}