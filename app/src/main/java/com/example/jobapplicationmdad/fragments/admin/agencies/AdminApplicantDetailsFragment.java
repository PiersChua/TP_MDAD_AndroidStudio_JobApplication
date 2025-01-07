package com.example.jobapplicationmdad.fragments.admin.agencies;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.jobapplicationmdad.util.ImageUtil;
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
 * Use the {@link AdminApplicantDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminApplicantDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1= "agencyApplicationId";
    private static final String get_applicant_details_url=  MainActivity.root_url + "/api/admin/get-applicant-details.php";

    // TODO: Rename and change types of parameters
    private String agencyApplicationId;
    private User user;
    private Agency agency;

    View dialogView;
    AlertDialog loadingDialog;
    MaterialToolbar topAppBar;
    TextView tvName, tvAdminApplicantAgencyName;
    RecyclerView recyclerViewAdminApplicantProfile, recyclerViewAdminApplicantAgencyProfile;
    ProfileAdapter profileAdapter;
    ProfileAdapter agencyProfileAdapter;
    List<HashMap<String, String>> profileItems;
    List<HashMap<String, String>> agencyProfileItems;
    ImageView ivAdminApplicantAgencyImage, ivAdminApplicantProfileImage;
    SharedPreferences sp;

    public AdminApplicantDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AdminApplicantDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminApplicantDetailsFragment newInstance(String agencyApplicationId) {
        AdminApplicantDetailsFragment fragment = new AdminApplicantDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, agencyApplicationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            agencyApplicationId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialogView = inflater.inflate(R.layout.dialog_loader, container, false);
        return inflater.inflate(R.layout.fragment_admin_applicant_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        topAppBar = view.findViewById(R.id.topAppBarAdminApplicantProfile);
        tvName = view.findViewById(R.id.tvAdminApplicantProfileName);
        tvAdminApplicantAgencyName = view.findViewById(R.id.tvAdminApplicantAgencyName);
        ivAdminApplicantProfileImage = view.findViewById(R.id.ivAdminApplicantProfileImage);
        ivAdminApplicantAgencyImage = view.findViewById(R.id.ivAdminApplicantAgencyImage);
        recyclerViewAdminApplicantProfile = view.findViewById(R.id.rvAdminApplicantProfile);
        recyclerViewAdminApplicantAgencyProfile = view.findViewById(R.id.rvAdminApplicantAgencyProfile);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        profileItems = new ArrayList<>();
        agencyProfileItems = new ArrayList<>();
        getApplicantDetails();

        // Set the adapter
        profileAdapter = new ProfileAdapter(profileItems);
        recyclerViewAdminApplicantProfile.setAdapter(profileAdapter);

        agencyProfileAdapter = new ProfileAdapter(agencyProfileItems);
        recyclerViewAdminApplicantAgencyProfile.setAdapter(agencyProfileAdapter);

        GridLayoutManager gridLayoutManagerAgentProfile = new GridLayoutManager(requireContext(), 2);
        recyclerViewAdminApplicantProfile.setLayoutManager(gridLayoutManagerAgentProfile);
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
        recyclerViewAdminApplicantAgencyProfile.setLayoutManager(gridLayoutManagerAgencyProfile);
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
    private void getApplicantDetails(){
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        params.put("agencyApplicationId",agencyApplicationId);
        String url = UrlUtil.constructUrl(get_applicant_details_url, params);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, response -> {
            try {
                // retrieve user details
                user = new User();
                user.setFullName(response.getString("user_fullName"));
                user.setEmail(response.getString("user_email"));
                user.setDateOfBirth(DateConverter.formatDateFromSql(response.getString("user_dateOfBirth")));
                user.setPhoneNumber(response.getString("user_phoneNumber"));
                user.setRace(response.getString("user_race"));
                user.setNationality(response.getString("user_nationality"));
                user.setGender(response.getString("user_gender"));
                user.setImage(ImageUtil.decodeBase64(response.getString("user_image")));
                populateProfileItems(user);

                agency = new Agency();
                agency.setName(response.getString("name"));
                agency.setEmail(response.getString("email"));
                agency.setPhoneNumber(response.getString("phoneNumber"));
                agency.setAddress(response.getString("address"));
                agency.setImage(ImageUtil.decodeBase64(response.getString("image")));
                populateAgencyItems(agency);


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            // toggle the visibility of loader
            loadingDialog.dismiss();
            recyclerViewAdminApplicantProfile.setVisibility(View.VISIBLE);
            recyclerViewAdminApplicantAgencyProfile.setVisibility(View.VISIBLE);
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
        if (user.getImage() != null) {
            ivAdminApplicantProfileImage.setVisibility(View.VISIBLE);
            ivAdminApplicantProfileImage.setImageBitmap(user.getImage());
            tvName.setVisibility(View.GONE);
        }
        else{
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(StringUtil.getNameInitials(user.getFullName()));
            ivAdminApplicantProfileImage.setVisibility(View.GONE);
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

    private void populateAgencyItems(Agency agency) {
        if (agency.getImage() != null) {
            ivAdminApplicantAgencyImage.setVisibility(View.VISIBLE);
            ivAdminApplicantAgencyImage.setImageBitmap(agency.getImage());
            tvAdminApplicantAgencyName.setVisibility(View.GONE);
        }
        else{
            tvAdminApplicantAgencyName.setVisibility(View.VISIBLE);
            tvAdminApplicantAgencyName.setText(StringUtil.getNameInitials(agency.getName()));
            ivAdminApplicantAgencyImage.setVisibility(View.GONE);
        }
        agencyProfileItems.clear();
        addAgencyProfileItem("Name", agency.getName());
        addAgencyProfileItem("Email Address", agency.getEmail());
        addAgencyProfileItem("Phone Number", agency.getPhoneNumber());
        addAgencyProfileItem("Address", agency.getAddress());
    }
}