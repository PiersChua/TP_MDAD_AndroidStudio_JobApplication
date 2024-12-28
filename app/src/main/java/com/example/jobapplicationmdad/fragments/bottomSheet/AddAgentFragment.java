package com.example.jobapplicationmdad.fragments.bottomSheet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.fragments.agencyadmin.agent.AgencyAdminAgentsFragment;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.AuthValidation;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddAgentFragment extends BottomSheetDialogFragment {
    private static final String get_job_seeker_url = MainActivity.root_url + "/api/agency-admin/get-job-seeker-details.php";
    private static final String promote_job_seeker_url = MainActivity.root_url + "/api/agency-admin/promote-job-seeker.php";
    private static final String ARG_PARAM1 = "agencyId";
    EditText etEmailAddAgent;
    SharedPreferences sp;
    TextInputLayout etEmailAddAgentLayout;
    AlertDialog loadingDialog;
    AlertDialog messageDialog;
    Button btnFindUser;
    private String agencyId;

    public static AddAgentFragment newInstance(String agencyId) {
        AddAgentFragment fragment = new AddAgentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, agencyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            agencyId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_agent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        etEmailAddAgent = view.findViewById(R.id.etEmailAddAgent);
        etEmailAddAgentLayout = view.findViewById(R.id.etEmailAddAgentLayout);
        btnFindUser = view.findViewById(R.id.btnFindUser);


        btnFindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmailAddAgent.getText().toString();
                boolean isValidEmail = AuthValidation.validateEmail(etEmailAddAgentLayout, email);
                if (isValidEmail) {
                    findJobSeeker(email);
                }
            }
        });
    }

    private void findJobSeeker(String email) {
        View dialogLoader = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_loader, null);
        MaterialAlertDialogBuilder dialogLoaderBuilder = new MaterialAlertDialogBuilder(requireContext());
        dialogLoaderBuilder.setView(dialogLoader).setCancelable(false);
        loadingDialog = dialogLoaderBuilder.create();
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("email", email);
        String url = UrlUtil.constructUrl(get_job_seeker_url, params);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    User user = new User();
                    user.setFullName(response.getString("fullName"));
                    user.setEmail(response.getString("email"));

                    View dialogMessage = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_message, null);
                    MaterialAlertDialogBuilder dialogMessageBuilder = new MaterialAlertDialogBuilder(requireContext());
                    dialogMessageBuilder.setView(dialogMessage);
                    messageDialog = dialogMessageBuilder.create();
                    ImageView icon = dialogMessage.findViewById(R.id.ivDialogIcon);
                    TextView title = dialogMessage.findViewById(R.id.tvDialogTitle);
                    TextView desc = dialogMessage.findViewById(R.id.tvDialogMessage);
                    Button btnPrimary = dialogMessage.findViewById(R.id.btnDialogPrimary);
                    Button btnOutline = dialogMessage.findViewById(R.id.btnDialogOutline);
                    btnPrimary.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            promoteJobSeekerToAgent(user);
                        }
                    });
                    btnOutline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            messageDialog.dismiss();
                        }
                    });
                    icon.setVisibility(View.GONE);
                    title.setText("User found");
                    desc.setText("Name: " + user.getFullName() + "\nEmail: " + user.getEmail() + "\nWould you like to promote this user to an agent?");
                    btnPrimary.setText("OK");
                    btnOutline.setText("Cancel");
                    messageDialog.show();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                loadingDialog.dismiss();
            }
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(requireContext()).onErrorResponse(error);
        });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void promoteJobSeekerToAgent(User user) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("email", user.getEmail());
        params.put("agencyId", agencyId);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, promote_job_seeker_url, params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismiss();
                    messageDialog.dismiss();
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), response.getString("message"), Snackbar.LENGTH_SHORT).setAnchorView(requireActivity().findViewById(R.id.bottom_navigation)).show();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, error -> {
            VolleyErrorHandler.newErrorListener(requireContext()).onErrorResponse(error);
        });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }
}