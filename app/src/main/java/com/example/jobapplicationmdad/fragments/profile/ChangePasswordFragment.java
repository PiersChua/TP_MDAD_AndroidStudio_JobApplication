package com.example.jobapplicationmdad.fragments.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.AuthValidation;
import com.example.jobapplicationmdad.util.DateConverter;
import com.example.jobapplicationmdad.util.ImageUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String change_password_url = MainActivity.root_url + "/api/auth/change-password.php";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MaterialToolbar topAppBarChangePassword;
    View dialogView;
    AlertDialog loadingDialog;
    Button btnConfirmChangePassword;
    EditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    TextInputLayout etCurrentPasswordLayout, etNewPasswordLayout, etConfirmNewPasswordLayout;
    SharedPreferences sp;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangePasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangePasswordFragment newInstance(String param1, String param2) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialogView = inflater.inflate(R.layout.dialog_loader, container, false);
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        topAppBarChangePassword = view.findViewById(R.id.topAppBarChangePassword);
        btnConfirmChangePassword = view.findViewById(R.id.btnConfirmChangePassword);
        // Forms
        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmNewPassword = view.findViewById(R.id.etConfirmNewPassword);

        // Form Layouts
        etCurrentPasswordLayout = view.findViewById(R.id.etCurrentPasswordLayout);
        etNewPasswordLayout = view.findViewById(R.id.etNewPasswordLayout);
        etConfirmNewPasswordLayout = view.findViewById(R.id.etConfirmNewPasswordLayout);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();

        topAppBarChangePassword.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
        btnConfirmChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentPassword = etCurrentPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String confirmNewPassword = etConfirmNewPassword.getText().toString();
                if (validatePasswords(currentPassword, newPassword, confirmNewPassword)) {
                    loadingDialog.show();
                    changePassword(currentPassword, newPassword);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).hideBottomNav();

    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) requireActivity()).showBottomNav();
    }

    private boolean validatePasswords(String currentPassword, String newPassword, String confirmNewPassword) {
        boolean isValidCurrentPassword = AuthValidation.validateNull(etCurrentPasswordLayout, "Password", currentPassword);
        boolean isValidNewPassword = AuthValidation.validatePassword(etNewPasswordLayout, newPassword, false);
        boolean isValidConfirmPassword = AuthValidation.validateConfirmPassword(etConfirmNewPasswordLayout, newPassword, confirmNewPassword);
        return isValidCurrentPassword && isValidConfirmPassword && isValidNewPassword;
    }

    private void changePassword(String currentPassword, String newPassword) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("password", currentPassword);
        params.put("newPassword", newPassword);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));

        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, change_password_url, params, headers, response -> {
            try {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                // Get the currently focused view
                View currentFocus = requireActivity().getCurrentFocus();
                // Hide the keyboard if a view is focused
                if (currentFocus != null) {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
                Snackbar.make(requireActivity().findViewById(android.R.id.content), response.getString("message"), Snackbar.LENGTH_SHORT).setAnchorView(requireActivity().findViewById(R.id.bottom_navigation)).show();
                getParentFragmentManager().popBackStack();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, VolleyErrorHandler.newErrorListener(requireContext()));
        loadingDialog.dismiss();
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }
}