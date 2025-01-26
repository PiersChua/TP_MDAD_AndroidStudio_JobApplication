package com.example.jobapplicationmdad.fragments.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.LoginActivity;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.AuthValidation;
import com.example.jobapplicationmdad.util.EmailSender;
import com.example.jobapplicationmdad.util.StringUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewPasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "email";
    private static final String new_password_url = MainActivity.root_url + "/api/auth/new-password.php";

    // TODO: Rename and change types of parameters
    private String email;
    EditText etPassword;
    TextInputLayout etPasswordLayout;
    Button btnConfirmNewPassword;
    View dialogView;
    AlertDialog loadingDialog;

    public NewPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment NewPasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewPasswordFragment newInstance(String email) {
        NewPasswordFragment fragment = new NewPasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialogView = inflater.inflate(R.layout.dialog_loader, container, false);
        return inflater.inflate(R.layout.fragment_new_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etPasswordLayout = view.findViewById(R.id.etPasswordLayout);
        etPassword = view.findViewById(R.id.etPassword);
        btnConfirmNewPassword = view.findViewById(R.id.btnConfirmNewPassword);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        btnConfirmNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = etPassword.getText().toString();
                boolean isValidPassword = AuthValidation.validatePassword(etPasswordLayout, password, false);
                if (isValidPassword) {
                    loadingDialog.show();
                    changePassword(password);
                }
            }
        });
    }

    private void changePassword(String password) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);
        String otp = StringUtil.generateOTP();
        params.put("otp", otp);
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, new_password_url, params, response -> {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            // Get the currently focused view
            View currentFocus = requireActivity().getCurrentFocus();
            // Hide the keyboard if a view is focused
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
            loadingDialog.dismiss();
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(requireContext()).onErrorResponse(error);
        });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }
}