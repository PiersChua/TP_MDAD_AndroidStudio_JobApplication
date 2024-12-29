package com.example.jobapplicationmdad.fragments.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.AdminAgencyCardAdapter;
import com.example.jobapplicationmdad.adapters.AgencyAdminAgentCardAdapter;
import com.example.jobapplicationmdad.fragments.agencyadmin.agent.AgencyAdminAgentsFragment;
import com.example.jobapplicationmdad.fragments.agent.job.AgentJobsFragment;
import com.example.jobapplicationmdad.fragments.profile.EditProfileFragment;
import com.example.jobapplicationmdad.model.Agency;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminAgenciesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminAgenciesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String get_agencies_url = MainActivity.root_url + "/api/admin/get-agencies.php";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    List<Agency> agencyList;
    View dialogView;
    AlertDialog loadingDialog;
    AdminAgencyCardAdapter adminAgencyCardAdapter;
    SwipeRefreshLayout srlAdminAgency;
    FloatingActionButton fabCreateAgency;
    private long mLastClickTime;
    MaterialToolbar topAppBar;
    SharedPreferences sp;

    public AdminAgenciesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminAgenciesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminAgenciesFragment newInstance(String param1, String param2) {
        AdminAgenciesFragment fragment = new AdminAgenciesFragment();
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
        return inflater.inflate(R.layout.fragment_admin_agencies, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        topAppBar = view.findViewById(R.id.topAppbarAdminAgencies);
        fabCreateAgency = view.findViewById(R.id.fabAdminCreateAgency);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        agencyList = new ArrayList<>();
        getAgencies();
        srlAdminAgency = view.findViewById(R.id.srlAdminAgency);
        recyclerView = view.findViewById(R.id.rvAdminAgencyCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adminAgencyCardAdapter = new AdminAgencyCardAdapter(agencyList, new AdminAgencyCardAdapter.OnJobClickListener() {
            @Override
            public void onManageAgency(String userId) {
                FragmentManager fragmentManager = getChildFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
                    fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flAdminAgencies, AdminManageAgencyFragment.newInstance(userId)).addToBackStack(null).commit();

            }

        });
        recyclerView.setAdapter(adminAgencyCardAdapter);
        srlAdminAgency.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAgencies();
                srlAdminAgency.setRefreshing(false);
            }
        });
        fabCreateAgency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (System.currentTimeMillis() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = System.currentTimeMillis();
            }
        });

    }

    private void getAgencies() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        String url = UrlUtil.constructUrl(get_agencies_url, params);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray agenciesArray = response.getJSONArray("data");
                    for (int i = 0; i < agenciesArray.length(); i++) {
                        JSONObject agencyObject = agenciesArray.getJSONObject(i);
                        Agency agency = new Agency();
                        agency.setAgencyId(agencyObject.getString("agencyId"));
                        agency.setUserId(agencyObject.getString("userId"));
                        agency.setName(agencyObject.getString("name"));
                        agency.setEmail(agencyObject.getString("email"));
                        agency.setPhoneNumber(agencyObject.getString("phoneNumber"));
                        agency.setAgentCount(agencyObject.getInt("agentCount"));
                        agencyList.add(agency);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                // toggle the visibility of loader
                loadingDialog.dismiss();
                recyclerView.setVisibility(View.VISIBLE);
            }
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(requireContext()).onErrorResponse(error);
        });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void refreshAgencies() {

    }
}