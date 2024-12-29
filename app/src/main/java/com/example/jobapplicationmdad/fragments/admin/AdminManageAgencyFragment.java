package com.example.jobapplicationmdad.fragments.admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.LoginActivity;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.ProfileAdapter;
import com.example.jobapplicationmdad.fragments.agencyadmin.agent.AgencyAdminAgentsFragment;
import com.example.jobapplicationmdad.fragments.agencyadmin.profile.EditAgencyAdminAgencyProfileFragment;
import com.example.jobapplicationmdad.fragments.profile.EditProfileFragment;
import com.example.jobapplicationmdad.model.Agency;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.DateConverter;
import com.example.jobapplicationmdad.util.StringUtil;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminManageAgencyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminManageAgencyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userId";
    private static final String get_user_url = MainActivity.root_url + "/api/agency-admin/get-user-details.php";
    private static final String delete_user_url = MainActivity.root_url + "/api/auth/delete-user.php";


    // TODO: Rename and change types of parameters
    private String userId;
    private Agency agency;
    private User user;
    View dialogView;
    AlertDialog loadingDialog;
    MaterialToolbar topAppBar;
    RecyclerView recyclerViewAgencyAdminProfile, recyclerViewAgencyProfile;
    ProfileAdapter profileAdapter;
    ProfileAdapter agencyProfileAdapter;
    List<HashMap<String, String>> profileItems;
    List<HashMap<String, String>> agencyProfileItems;
    Button btnNavigateToEditAgencyAdminProfile, btnNavigateToEditAgencyProfile;
    SharedPreferences sp;
    BottomAppBar bottomAppBar;
    Button btnManageAgents, btnDeleteUser;
    private long mLastClickTime;

    public AdminManageAgencyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId Parameter 1.
     * @return A new instance of fragment AdminManageAgencyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminManageAgencyFragment newInstance(String userId) {
        AdminManageAgencyFragment fragment = new AdminManageAgencyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialogView = inflater.inflate(R.layout.dialog_loader, container, false);
        return inflater.inflate(R.layout.fragment_admin_manage_agency, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        topAppBar = view.findViewById(R.id.topAppbarAdminManageAgency);
        bottomAppBar = view.findViewById(R.id.bottomAppBarManageAgency);
        bottomAppBar.setPadding(0, 0, 0, 0);
        bottomAppBar.setOnApplyWindowInsetsListener(null);
        btnManageAgents = view.findViewById(R.id.btnManageAgents);
        btnDeleteUser = view.findViewById(R.id.btnDeleteUser);
        btnNavigateToEditAgencyAdminProfile = view.findViewById(R.id.btnNavigateToEditAgencyAdminProfile);
        btnNavigateToEditAgencyProfile = view.findViewById(R.id.btnNavigateToEditAgencyProfile);
        recyclerViewAgencyAdminProfile = view.findViewById(R.id.rvAdminManageAgencyAgencyAdminProfile);
        recyclerViewAgencyProfile = view.findViewById(R.id.rvAdminManageAgencyAgencyProfile);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        profileItems = new ArrayList<>();
        agencyProfileItems = new ArrayList<>();
        getUserDetails();

        // Set the adapter
        profileAdapter = new ProfileAdapter(profileItems);
        recyclerViewAgencyAdminProfile.setAdapter(profileAdapter);

        agencyProfileAdapter = new ProfileAdapter(agencyProfileItems);
        recyclerViewAgencyProfile.setAdapter(agencyProfileAdapter);

        GridLayoutManager gridLayoutManagerAgencyAdminProfile = new GridLayoutManager(requireContext(), 2);
        recyclerViewAgencyAdminProfile.setLayoutManager(gridLayoutManagerAgencyAdminProfile);
        gridLayoutManagerAgencyAdminProfile.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // If the position is the last item and the list size is odd, take up 2 columns
                if (position == profileItems.size() - 1 && profileItems.size() % 2 != 0) {
                    return 2;
                }
                return 1;
            }
        });
        recyclerViewAgencyProfile.setLayoutManager(new LinearLayoutManager(requireContext()));

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
        btnManageAgents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (System.currentTimeMillis() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = System.currentTimeMillis();
                // addToBackStack() allows the back button to return to the current page
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flAdminManageAgency, AgencyAdminAgentsFragment.newInstance(userId, false, false)).addToBackStack(null).commit();
            }
        });
        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(requireContext()).setTitle("Delete User").setMessage("You are about to delete the agency. This will delete ALL AGENTS, JOBS that are linked to this agency. \nDo you wish to proceed?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        deleteUser();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        btnNavigateToEditAgencyAdminProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (System.currentTimeMillis() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = System.currentTimeMillis();
                // addToBackStack() allows the back button to return to the current page
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flAdminManageAgency, EditProfileFragment.newInstance(userId, false,true)).addToBackStack(null).commit();
            }
        });
        btnNavigateToEditAgencyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (System.currentTimeMillis() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = System.currentTimeMillis();
                // addToBackStack() allows the back button to return to the current page
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flAdminManageAgency, EditAgencyAdminAgencyProfileFragment.newInstance(agency, userId)).addToBackStack(null).commit();
            }
        });

        getChildFragmentManager().setFragmentResultListener("editProfileResult", this, (requestKey, result) -> {
            boolean isUpdated = result.getBoolean("isUpdated", false);
            if (isUpdated) {
                // Refresh user details only if updated
                profileItems.clear();
                recyclerViewAgencyAdminProfile.setVisibility(View.GONE);
                profileAdapter.notifyDataSetChanged();
                getUserDetails();

            }
        });
        getChildFragmentManager().setFragmentResultListener("editAgencyResult", this, (requestKey, result) -> {
            boolean isUpdated = result.getBoolean("isUpdated", false);
            if (isUpdated) {
                // Refresh user details only if updated
                agencyProfileItems.clear();
                recyclerViewAgencyProfile.setVisibility(View.GONE);
                getUserDetails();
                agencyProfileAdapter.notifyDataSetChanged();

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
        ((MainActivity) requireActivity()).hideBottomNav();


    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) requireActivity()).showBottomNav();

    }

    private void getUserDetails() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("userIdToGet", userId);
        String url = UrlUtil.constructUrl(get_user_url, params);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, response -> {
            try {
                // retrieve user details
                user = new User();
                user.setUserId(response.getString("userId"));
                user.setFullName(response.getString("fullName"));
                user.setEmail(response.getString("email"));
                user.setDateOfBirth(DateConverter.formatDateFromSql(response.getString("dateOfBirth")));
                user.setPhoneNumber(response.getString("phoneNumber"));
                user.setRace(response.getString("race"));
                user.setNationality(response.getString("nationality"));
                user.setGender(response.getString("gender"));
                populateProfileItems(user);

                // retrieve agency details
                agency = new Agency(
                        response.getString("agency_name"),
                        response.getString("agency_email"),
                        response.getString("agency_phone_number"),
                        response.getString("agency_address")
                );

                populateAgencyItems(agency);


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            // toggle the visibility of loader
            loadingDialog.dismiss();
            recyclerViewAgencyProfile.setVisibility(View.VISIBLE);
            recyclerViewAgencyAdminProfile.setVisibility(View.VISIBLE);
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(requireContext()).onErrorResponse(error);
        });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void addProfileItem(String label, String value) {
        HashMap<String, String> item = new HashMap<>();
        item.put("label", label);
        item.put("value", value);
        profileItems.add(item);
    }

    private void addAgencyProfileItem(String label, String value) {
        HashMap<String, String> item = new HashMap<>();
        item.put("label", label);
        item.put("value", value);
        agencyProfileItems.add(item);
    }

    private void populateProfileItems(User user) {
        profileItems.clear();
        addProfileItem("Full Name", user.getFullName());
        addProfileItem("Email Address", user.getEmail());
        addProfileItem("Date of Birth", user.getDateOfBirth());
        addProfileItem("Phone Number", user.getPhoneNumber());
        addProfileItem("Race", user.getRace());
        addProfileItem("Nationality", user.getNationality());
        addProfileItem("Gender", user.getGender());
    }

    private void populateAgencyItems(Agency agency) {
        agencyProfileItems.clear();
        addAgencyProfileItem("Name", agency.getName());
        addAgencyProfileItem("Email Address", agency.getEmail());
        addAgencyProfileItem("Phone Number", agency.getPhoneNumber());
        addAgencyProfileItem("Address", agency.getAddress());
    }

    private void deleteUser() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("userIdToBeDeleted", userId);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, delete_user_url, params, headers, response -> {
            try {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), response.getString("message"), Snackbar.LENGTH_SHORT).setAnchorView(requireActivity().findViewById(R.id.bottom_navigation)).show();
                Bundle result = new Bundle();
                result.putBoolean("isUpdated", true);
                getParentFragmentManager().setFragmentResult("deleteAgencyResult", result);
                getParentFragmentManager().popBackStack();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            // toggle the visibility of loader
            loadingDialog.dismiss();
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(requireContext()).onErrorResponse(error);
        });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }
}