package com.example.jobapplicationmdad.fragments.agent.job;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.ProfileAdapter;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.DateConverter;
import com.example.jobapplicationmdad.util.ImageUtil;
import com.example.jobapplicationmdad.util.StringUtil;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AgentApplicantDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgentApplicantDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userId";
    private static final String get_applicant_details_url = MainActivity.root_url + "/api/agent/get-applicant-details.php";

    // TODO: Rename and change types of parameters
    private String userId;
    View dialogView;
    AlertDialog loadingDialog;
    MaterialToolbar topAppBar;
    TextView tvName;
    RecyclerView recyclerView;
    ProfileAdapter profileAdapter;
    ArrayList<HashMap<String, String>> profileItems;
    SharedPreferences sp;
    ImageView ivAgentApplicantProfileImage;
    private User user;

    public AgentApplicantDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId The applicant's userId
     * @return A new instance of fragment AgentApplicantDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AgentApplicantDetailsFragment newInstance(String userId) {
        AgentApplicantDetailsFragment fragment = new AgentApplicantDetailsFragment();
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
        return inflater.inflate(R.layout.fragment_agent_applicant_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        topAppBar = view.findViewById(R.id.topAppBarAgentApplicantProfile);
        tvName = view.findViewById(R.id.tvAgentApplicantProfileName);
        ivAgentApplicantProfileImage = view.findViewById(R.id.ivAgentApplicantProfileImage);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        recyclerView = view.findViewById(R.id.rvAgentApplicantProfile);

        profileItems = new ArrayList<>();
        getApplicantDetails();

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
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
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

    private void getApplicantDetails() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("applicantUserId",userId);
        String url = UrlUtil.constructUrl(get_applicant_details_url, params);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, response -> {
            try {
                // retrieve user details
                user = new User();
                user.setFullName(response.getString("fullName"));
                user.setEmail(response.getString("email"));
                user.setDateOfBirth(DateConverter.formatDateFromSql(response.getString("dateOfBirth")));
                user.setPhoneNumber(response.getString("phoneNumber"));
                user.setRace(response.getString("race"));
                user.setNationality(response.getString("nationality"));
                user.setGender(response.getString("gender"));
                user.setImage(ImageUtil.decodeBase64(response.getString("image")));
                populateProfileItems(user);

                // Set the adapter
                profileAdapter = new ProfileAdapter(profileItems);
                recyclerView.setAdapter(profileAdapter);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            // toggle the visibility of loader
            loadingDialog.dismiss();
            recyclerView.setVisibility(View.VISIBLE);
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

    private void populateProfileItems(User user) {
        if (user.getImage() != null) {
            ivAgentApplicantProfileImage.setVisibility(View.VISIBLE);
            ivAgentApplicantProfileImage.setImageBitmap(user.getImage());
            tvName.setVisibility(View.GONE);
        }
        else{
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(StringUtil.getNameInitials(user.getFullName()));
            ivAgentApplicantProfileImage.setVisibility(View.GONE);
        }
        profileItems.clear();
        addProfileItem("Full Name", user.getFullName());
        addProfileItem("Email Address", user.getEmail());
        addProfileItem("Date of Birth", user.getDateOfBirth());
        addProfileItem("Phone Number", user.getPhoneNumber());
        addProfileItem("Race", user.getRace());
        addProfileItem("Nationality", user.getNationality());
        addProfileItem("Gender", user.getGender());
    }
}