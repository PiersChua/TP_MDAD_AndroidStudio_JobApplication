package com.example.jobapplicationmdad.fragments.jobseeker.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.LoginActivity;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.ProfileAdapter;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.StringUtil;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JobSeekerProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobSeekerProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String get_user_url = MainActivity.root_url + "/api/auth/get-user-details.php";
    MaterialToolbar topAppBar;
    TextView tvName;
    RecyclerView recyclerView;
    ProfileAdapter profileAdapter;
    ArrayList<HashMap<String, String>> profileItems;
    Button btnEditProfile;

    public JobSeekerProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JobSeekerProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobSeekerProfileFragment newInstance(String param1, String param2) {
        JobSeekerProfileFragment fragment = new JobSeekerProfileFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_seeker_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getUserDetails();
        topAppBar = view.findViewById(R.id.topAppBarJobSeekerProfile);
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.job_seeker_profile_item_1) {
                    return true;
                } else if (id == R.id.job_seeker_profile_item_2) {
                    Toast.makeText(getContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.job_seeker_profile_item_3) {
                    // clear shared preferences
                    SharedPreferences sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    sp.edit().clear().apply();
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                    return true;
                }
                return false;

            }
        });
        tvName = view.findViewById(R.id.tvJobSeekerProfileName);
        recyclerView = view.findViewById(R.id.rvJobSeekerProfile);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // If the position is the last item and the list size is odd, take up 2 columns
                if (position == profileItems.size() - 1 && profileItems.size() % 2 != 0) {
                    return 2;
                }
                return 1;
            }
        });
        profileItems = new ArrayList<>();
        btnEditProfile = view.findViewById(R.id.btnEditJobSeekerProfile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().replace(R.id.flMain, EditJobSeekerProfileFragment.newInstance(name, null)).commit();
            }
        });


    }

    private void getUserDetails() {
        SharedPreferences sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(Request.Method.POST, get_user_url, params, headers, response -> {
            try {
                if (response.getString("type").equals("Success")) {
                    // retrieve user details
                    tvName.setText(StringUtil.getNameInitials(response.getString("fullName")));
                    profileItems.clear();
                    User user = new User();
                    addProfileItem("Full Name", response.getString("fullName"));
                    addProfileItem("Email Address", response.getString("email"));
                    addProfileItem("Date of Birth", response.getString("dateOfBirth"));
                    addProfileItem("Phone Number", response.getString("phoneNumber"));
                    addProfileItem("Race", response.getString("race"));
                    addProfileItem("Nationality", response.getString("nationality"));
                    addProfileItem("Gender", response.getString("gender"));

                    // Set the adapter
                    profileAdapter = new ProfileAdapter(profileItems);
                    recyclerView.setAdapter(profileAdapter);

                } else if (response.getString("type").equals("Error")) {
                    Toast.makeText(requireContext().getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, VolleyErrorHandler.newErrorListener(requireContext().getApplicationContext()));
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void addProfileItem(String label, String value) {
        HashMap<String, String> item = new HashMap<>();
        item.put("label", label);
        item.put("value", value);
        profileItems.add(item);
    }


}