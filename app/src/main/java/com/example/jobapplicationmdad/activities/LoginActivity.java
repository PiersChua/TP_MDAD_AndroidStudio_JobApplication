package com.example.jobapplicationmdad.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.jobapplicationmdad.fragments.auth.ForgotPasswordFragment;
import com.example.jobapplicationmdad.fragments.auth.VerifyUserFragment;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.AuthValidation;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.util.EmailSender;
import com.example.jobapplicationmdad.util.StringUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String login_url = MainActivity.root_url + "/api/auth/login.php";
    TextView tvRedirectToRegister, tvForgotPassword;
    Button btnLogin;
    EditText etEmailLogin, etPasswordLogin;
    TextInputLayout etEmailLoginLayout, etPasswordLoginLayout;
    SharedPreferences sp;
    View dialogView;
    AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        tvRedirectToRegister = findViewById(R.id.tvRedirectToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);

        boolean isPasswordReset = getIntent().getBooleanExtra("isPasswordReset", false);
        if (isPasswordReset) {
            Snackbar.make(findViewById(android.R.id.content), "Password reset successfully", Snackbar.LENGTH_SHORT).show();
        }

        dialogView = getLayoutInflater().inflate(R.layout.dialog_loader, findViewById(android.R.id.content), false);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
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
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flLogin, new ForgotPasswordFragment()).addToBackStack(null).commit();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = getUserFromForm();
                if (validateUser(user)) {
                    loadingDialog.show();
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
        String otp = StringUtil.generateOTP();
        params.put("otp", otp);
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, login_url, params, response -> {
            try {
                if (response.getString("message").equals("Account verification needed")) {
                    String recipientEmail = user.getEmail();
                    String subject = "Account verification - One time Password";
                    String messageBody = String.format("Dear valued user of SGJobMarket, \n\nYour one time password is %s", otp);

                    EmailSender emailSender = new EmailSender(recipientEmail, subject, messageBody);
                    emailSender.execute();
                    loadingDialog.dismiss();
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flLogin, VerifyUserFragment.newInstance(user.getEmail(),false)).addToBackStack(null).commit();
                    return;
                }
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

                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            loadingDialog.dismiss();
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(LoginActivity.this).onErrorResponse(error);
        });
        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }
}