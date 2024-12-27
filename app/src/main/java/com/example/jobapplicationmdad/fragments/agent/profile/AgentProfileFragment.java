package com.example.jobapplicationmdad.fragments.agent.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.LoginActivity;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.ProfileAdapter;
import com.example.jobapplicationmdad.model.Agency;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.DateConverter;
import com.example.jobapplicationmdad.util.StringUtil;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AgentProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgentProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String get_user_url = MainActivity.root_url + "/api/auth/get-user-details.php";
    private User user;

    View dialogView;
    AlertDialog loadingDialog;
    MaterialToolbar topAppBar;
    TextView tvName;
    RecyclerView recyclerViewAgentProfile, recyclerViewAgencyProfile;
    ProfileAdapter profileAdapter;
    ProfileAdapter agencyProfileAdapter;
    List<HashMap<String, String>> profileItems;
    List<HashMap<String, String>> agencyProfileItems;
    Button btnNavigateToEditProfile;
    SharedPreferences sp;
    private long mLastClickTime;

    public AgentProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AgentProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AgentProfileFragment newInstance(String param1, String param2) {
        AgentProfileFragment fragment = new AgentProfileFragment();
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
        return inflater.inflate(R.layout.fragment_agent_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        topAppBar = view.findViewById(R.id.topAppBarAgentProfile);
        tvName = view.findViewById(R.id.tvAgentProfileName);
        btnNavigateToEditProfile = view.findViewById(R.id.btnNavigateToEditAgentProfile);
        recyclerViewAgentProfile = view.findViewById(R.id.rvAgentProfile);
        recyclerViewAgencyProfile = view.findViewById(R.id.rvAgentAgencyProfile);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        profileItems = new ArrayList<>();
        agencyProfileItems = new ArrayList<>();
        getUserDetails();


        // Set the adapter
        profileAdapter = new ProfileAdapter(profileItems);
        recyclerViewAgentProfile.setAdapter(profileAdapter);

        agencyProfileAdapter = new ProfileAdapter(agencyProfileItems);
        recyclerViewAgencyProfile.setAdapter(agencyProfileAdapter);


        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.agent_profile_item_1) {
                    return true;
                } else if (id == R.id.agent_profile_item_2) {
                    // clear shared preferences
                    sp.edit().clear().apply();
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    // Clear the fragment back stack
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    return true;
                }
                return false;

            }
        });

        GridLayoutManager gridLayoutManagerAgentProfile = new GridLayoutManager(requireContext(), 2);
        recyclerViewAgentProfile.setLayoutManager(gridLayoutManagerAgentProfile);
        gridLayoutManagerAgentProfile.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // If the position is the last item and the list size is odd, take up 2 columns
                if (position == profileItems.size() - 1 && profileItems.size() % 2 != 0) {
                    return 2;
                }
                return 1;
            }
        });

        GridLayoutManager gridLayoutManagerAgencyProfile = new GridLayoutManager(requireContext(), 2);
        recyclerViewAgencyProfile.setLayoutManager(gridLayoutManagerAgencyProfile);
        gridLayoutManagerAgencyProfile.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // If the position is the last item and the list size is odd, take up 2 columns
                if (position == agencyProfileItems.size() - 1 && agencyProfileItems.size() % 2 != 0) {
                    return 2;
                }
                return 1;
            }
        });

        btnNavigateToEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (System.currentTimeMillis() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = System.currentTimeMillis();
                // addToBackStack() allows the back button to return to the current page
                getParentFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flAgentProfile, EditAgentProfileFragment.newInstance(user)).addToBackStack(null).commit();
            }
        });
        getParentFragmentManager().setFragmentResultListener("editProfileResult", this, (requestKey, result) -> {
            boolean isUpdated = result.getBoolean("isUpdated", false);
            if (isUpdated) {
                // Refresh user details only if updated
                profileItems.clear();
                recyclerViewAgentProfile.setVisibility(View.GONE);
                profileAdapter.notifyDataSetChanged();
                getUserDetails();

            }
        });
    }

    private void getUserDetails() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        String url = UrlUtil.constructUrl(get_user_url, params);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, response -> {
            try {
                // retrieve user details
                tvName.setText(StringUtil.getNameInitials(response.getString("fullName")));
                user = new User();
                user.setFullName(response.getString("fullName"));
                user.setEmail(response.getString("email"));
                user.setDateOfBirth(DateConverter.formatDateFromSql(response.getString("dateOfBirth")));
                user.setPhoneNumber(response.getString("phoneNumber"));
                user.setRace(response.getString("race"));
                user.setNationality(response.getString("nationality"));
                user.setGender(response.getString("gender"));
                populateProfileItems(user);

                // retrieve agency details
                Agency agency = new Agency(
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
            recyclerViewAgentProfile.setVisibility(View.VISIBLE);
            recyclerViewAgencyProfile.setVisibility(View.VISIBLE);
        }, error -> {
            loadingDialog.dismiss();
            VolleyErrorHandler.newErrorListener(requireContext(), requireActivity().findViewById(android.R.id.content)).onErrorResponse(error);
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
}