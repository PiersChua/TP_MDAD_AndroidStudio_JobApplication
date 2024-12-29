package com.example.jobapplicationmdad.fragments.agencyadmin.agent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.AgencyAdminAgentCardAdapter;
import com.example.jobapplicationmdad.fragments.agent.job.AgentJobsFragment;
import com.example.jobapplicationmdad.fragments.bottomSheet.AddAgentFragment;
import com.example.jobapplicationmdad.fragments.profile.EditProfileFragment;
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
 * Use the {@link AgencyAdminAgentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgencyAdminAgentsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userId";
    private static final String ARG_PARAM2 = "showAppBarIfAgentJobsFragmentClosed";
    private static final String ARG_PARAM3 = "showAppBarIfEditProfileFragmentClosed";
    private static final String get_agents_url = MainActivity.root_url + "/api/agency-admin/get-agents.php";

    // TODO: Rename and change types of parameters
    private String userId;
    private boolean showAppBarIfAgentJobsFragmentClosed;
    private boolean showAppBarIfEditProfileFragmentClosed;
    RecyclerView recyclerView;
    List<User> agentList;
    View dialogView;
    AlertDialog loadingDialog;
    AgencyAdminAgentCardAdapter agencyAdminAgentCardAdapter;
    SwipeRefreshLayout srlAgencyAdminAgent;
    MaterialToolbar topAppBar;
    FloatingActionButton fabAddAgent;
    SharedPreferences sp;

    public AgencyAdminAgentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId The userId of the agency admin
     * @return A new instance of fragment AgencyAdminAgentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AgencyAdminAgentsFragment newInstance(String userId, boolean showAppBarIfAgentJobsFragmentClosed, boolean showAppBarIfEditProfileFragmentClosed) {
        AgencyAdminAgentsFragment fragment = new AgencyAdminAgentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, userId);
        args.putBoolean(ARG_PARAM2, showAppBarIfAgentJobsFragmentClosed);
        args.putBoolean(ARG_PARAM3, showAppBarIfEditProfileFragmentClosed);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_PARAM1);
            showAppBarIfAgentJobsFragmentClosed = getArguments().getBoolean(ARG_PARAM2, true);
            showAppBarIfEditProfileFragmentClosed = getArguments().getBoolean(ARG_PARAM3, true);
        } else {
            showAppBarIfAgentJobsFragmentClosed = true;
            showAppBarIfEditProfileFragmentClosed = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialogView = inflater.inflate(R.layout.dialog_loader, container, false);
        return inflater.inflate(R.layout.fragment_agency_admin_agents, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        topAppBar = view.findViewById(R.id.topAppbarAgencyAdminAgents);
        if (userId != null) {
            topAppBar.setNavigationIcon(R.drawable.ic_arrow_back);
            topAppBar.setNavigationIconTint(ContextCompat.getColor(requireContext(), R.color.background));
            topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getParentFragmentManager().popBackStack();
                }
            });
        }
        fabAddAgent = view.findViewById(R.id.fabAgencyAdminAddAgent);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        agentList = new ArrayList<>();
        getAgents();
        srlAgencyAdminAgent = view.findViewById(R.id.srlAgencyAdminAgent);
        recyclerView = view.findViewById(R.id.rvAgencyAdminAgentCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        agencyAdminAgentCardAdapter = new AgencyAdminAgentCardAdapter(agentList, new AgencyAdminAgentCardAdapter.OnJobClickListener() {
            @Override
            public void onManageAgent(String userId) {
                FragmentManager fragmentManager = getChildFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
                    fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flAgencyAdminAgents, AgentJobsFragment.newInstance(userId, showAppBarIfAgentJobsFragmentClosed)).addToBackStack(null).commit();


            }

            @Override
            public void onEditAgent(String userId) {
                FragmentManager fragmentManager = getChildFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
                    fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flAgencyAdminAgents, EditProfileFragment.newInstance(userId, showAppBarIfEditProfileFragmentClosed)).addToBackStack(null).commit();

            }
        });
        recyclerView.setAdapter(agencyAdminAgentCardAdapter);
        srlAgencyAdminAgent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAgents();
                srlAgencyAdminAgent.setRefreshing(false);
            }
        });
        fabAddAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddAgentFragment bottomSheet = AddAgentFragment.newInstance(userId != null ? userId : sp.getString("userId", ""));
                bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isAdded()) {
                    getParentFragmentManager().popBackStack();
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userId != null) {
            ((MainActivity) requireActivity()).hideBottomNav();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (userId == null) {
            ((MainActivity) requireActivity()).showBottomNav();
        }
    }

    private void getAgents() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("agencyAdminUserId", userId != null ? userId : sp.getString("userId", ""));
        String url = UrlUtil.constructUrl(get_agents_url, params);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray usersArray = response.getJSONArray("data");
                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject userObject = usersArray.getJSONObject(i);
                        User user = new User();
                        user.setUserId(userObject.getString("userId"));
                        user.setFullName(userObject.getString("fullName"));
                        user.setEmail(userObject.getString("email"));
                        user.setPhoneNumber(userObject.getString("phoneNumber"));
                        user.setJobCount(userObject.getInt("job_count"));
                        agentList.add(user);
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

    private void refreshAgents() {
        agentList.clear();
        recyclerView.setVisibility(View.GONE);
        getAgents();
        agencyAdminAgentCardAdapter.notifyDataSetChanged();
    }
}