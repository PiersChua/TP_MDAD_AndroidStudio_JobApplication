package com.example.jobapplicationmdad.fragments.admin.agencies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.AdminAgencyApplicationCardAdapter;
import com.example.jobapplicationmdad.adapters.AgentJobApplicationCardAdapter;
import com.example.jobapplicationmdad.model.AgencyApplication;
import com.example.jobapplicationmdad.model.JobApplication;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminManageAgencyApplicationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminManageAgencyApplicationsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    List<AgencyApplication> agencyApplicationList;
    View dialogView;
    AlertDialog loadingDialog;
    AdminAgencyApplicationCardAdapter adminAgencyApplicationCardAdapter;
    SwipeRefreshLayout srlAdminAgencyApplication;
    MaterialToolbar topAppBar;
    SharedPreferences sp;
    FrameLayout flContent,flEmptyState;
    private static final String get_agency_applications_url = MainActivity.root_url + "/api/admin/get-agency-applications.php";
    private static final String update_agency_applications_url = MainActivity.root_url + "/api/admin/update-agency-application.php";

    public AdminManageAgencyApplicationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminManageAgencyApplicationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminManageAgencyApplicationsFragment newInstance(String param1, String param2) {
        AdminManageAgencyApplicationsFragment fragment = new AdminManageAgencyApplicationsFragment();
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
        return inflater.inflate(R.layout.fragment_admin_manage_agency_applications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        topAppBar = view.findViewById(R.id.topAppBarAdminManageAgencyApplication);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        agencyApplicationList = new ArrayList<>();
        getAgencyApplications();
        flContent = view.findViewById(R.id.flContent);
        flEmptyState = view.findViewById(R.id.flEmptyState);
        TextView emptyStateText = flEmptyState.findViewById(R.id.emptyStateText);
        emptyStateText.setText("Oops\nNo applications found");
        srlAdminAgencyApplication = view.findViewById(R.id.srlAdminAgencyApplication);
        recyclerView = view.findViewById(R.id.rvAdminAgencyApplicationCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        adminAgencyApplicationCardAdapter = new AdminAgencyApplicationCardAdapter(agencyApplicationList, new AdminAgencyApplicationCardAdapter.OnJobClickListener() {
            @Override
            public void onViewApplication(String agencyApplicationId) {

            }

            @Override
            public void onAcceptAgencyApplication(String agencyApplicationId) {
                new MaterialAlertDialogBuilder(requireContext()).setTitle("Accept Agency Application").setMessage("You are about to accept this application.\nDo you wish to proceed?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        updateAgencyApplication(agencyApplicationId, AgencyApplication.Status.ACCEPTED);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).show();
            }

            @Override
            public void onRejectAgencyApplication(String agencyApplicationId) {
                new MaterialAlertDialogBuilder(requireContext()).setTitle("Reject Agency Application").setMessage("You are about to reject this application.\nDo you wish to proceed?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        updateAgencyApplication(agencyApplicationId, AgencyApplication.Status.REJECTED);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        recyclerView.setAdapter(adminAgencyApplicationCardAdapter);
        srlAdminAgencyApplication.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshApplications();
                srlAdminAgencyApplication.setRefreshing(false);
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

    private void getAgencyApplications() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        String url = UrlUtil.constructUrl(get_agency_applications_url, params);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray agencyApplicationArray = response.getJSONArray("data");
                    for (int i = 0; i < agencyApplicationArray.length(); i++) {
                        JSONObject agencyApplicationObject = agencyApplicationArray.getJSONObject(i);
                        AgencyApplication agencyApplication = new AgencyApplication();
                        agencyApplication.setAgencyApplicationId(agencyApplicationObject.getString("agencyApplicationId"));
                        agencyApplication.setName(agencyApplicationObject.getString("name"));
                        agencyApplication.setEmail(agencyApplicationObject.getString("email"));
                        agencyApplication.setPhoneNumber(agencyApplicationObject.getString("phoneNumber"));
                        agencyApplication.setAddress(agencyApplicationObject.getString("address"));
                        agencyApplication.setStatus(AgencyApplication.Status.valueOf(agencyApplicationObject.getString("status")));
                        agencyApplication.setUpdatedAt(agencyApplicationObject.getString("updatedAt"));
                        agencyApplicationList.add(agencyApplication);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                if(!agencyApplicationList.isEmpty()){
                    recyclerView.setVisibility(View.VISIBLE);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) flContent.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    flContent.setLayoutParams(params);
                }
                else{
                    flEmptyState.setVisibility(View.VISIBLE);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) flContent.getLayoutParams();
                    params.gravity = Gravity.CENTER;
                    flContent.setLayoutParams(params);
                }
                loadingDialog.dismiss();
            }
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(requireContext()).onErrorResponse(error);
        });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void updateAgencyApplication(String agencyApplicationId, AgencyApplication.Status status) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", sp.getString("userId", ""));
        params.put("agencyApplicationId", agencyApplicationId);
        params.put("status", status.toString());
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, update_agency_applications_url, params, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), response.getString("message"), Snackbar.LENGTH_SHORT).show();
                    refreshApplications();
                    Bundle result = new Bundle();
                    result.putBoolean("isUpdated", true);
                    getParentFragmentManager().setFragmentResult("applicationResult", result);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, VolleyErrorHandler.newErrorListener(requireContext())
        );
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void refreshApplications() {
        agencyApplicationList.clear();
        recyclerView.setVisibility(View.GONE);
        getAgencyApplications();
        adminAgencyApplicationCardAdapter.notifyDataSetChanged();
    }
}