package com.example.jobapplicationmdad.fragments.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.adapters.AdminUserCardAdapter;
import com.example.jobapplicationmdad.adapters.AgencyAdminAgentCardAdapter;
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
 * Use the {@link AdminUsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminUsersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String get_users_url = MainActivity.root_url + "/api/admin/get-users.php";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    List<User> userList;
    View dialogView;
    AlertDialog loadingDialog;
    AdminUserCardAdapter adminUserCardAdapter;
    SwipeRefreshLayout srlAdminUser;
    MaterialToolbar topAppBar;
    SharedPreferences sp;
    SearchView searchView;
    FrameLayout flContent, flEmptyState;

    public AdminUsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminUsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminUsersFragment newInstance(String param1, String param2) {
        AdminUsersFragment fragment = new AdminUsersFragment();
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
        return inflater.inflate(R.layout.fragment_admin_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        topAppBar = view.findViewById(R.id.topAppbarAdminUsers);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        userList = new ArrayList<>();
        getUsers();
        flContent = view.findViewById(R.id.flContent);
        flEmptyState = view.findViewById(R.id.flEmptyState);
        TextView emptyStateText = flEmptyState.findViewById(R.id.emptyStateText);
        emptyStateText.setText("Oops\nNo users found");

        MenuItem searchMenuItem = topAppBar.getMenu().findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint("Search for users...");
        EditText etSearch = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        etSearch.setTextColor(requireContext().getColor(R.color.background));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                filterUsers(query);
                return true;
            }
        });

        srlAdminUser = view.findViewById(R.id.srlAdminUser);
        recyclerView = view.findViewById(R.id.rvAdminUserCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adminUserCardAdapter = new AdminUserCardAdapter(userList, new AdminUserCardAdapter.OnJobClickListener() {
            @Override
            public void onEditUser(String userId) {
                FragmentManager fragmentManager = getChildFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
                    fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left, R.anim.slide_left_to_right, R.anim.exit_left_to_right).replace(R.id.flAdminUsers, EditProfileFragment.newInstance(userId, true, false)).addToBackStack(null).commit();
            }
        });
        recyclerView.setAdapter(adminUserCardAdapter);
        srlAdminUser.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshUsers();
                srlAdminUser.setRefreshing(false);
            }
        });
        getChildFragmentManager().setFragmentResultListener("editProfileResult", this, (requestKey, result) -> {
            boolean isUpdated = result.getBoolean("isUpdated", false);
            if (isUpdated) {
                // Refresh user details only if updated
                refreshUsers();

            }
        });
    }
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        searchView.setIconified(true);
        searchView.setIconified(true);
    }

    private void getUsers() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        String url = UrlUtil.constructUrl(get_users_url, params);
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
                        user.setRole(userObject.getString("role"));
                        userList.add(user);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                if (!userList.isEmpty()) {
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
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

    private void refreshUsers() {
        userList.clear();
        recyclerView.setVisibility(View.GONE);
        getUsers();
        adminUserCardAdapter.notifyDataSetChanged();
    }

    private void filterUsers(String query) {
        if (query.isEmpty()) {
            adminUserCardAdapter.filterList(userList);
            flEmptyState.setVisibility(View.GONE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) flContent.getLayoutParams();
            params.gravity = Gravity.TOP;
            flContent.setLayoutParams(params);
            return;
        }
        List<User> filteredList = new ArrayList<>();
        for (User user : userList) {
            if (user.getFullName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        if (!filteredList.isEmpty()) {
            flEmptyState.setVisibility(View.GONE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) flContent.getLayoutParams();
            params.gravity = Gravity.TOP;
            flContent.setLayoutParams(params);
        } else {
            flEmptyState.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) flContent.getLayoutParams();
            params.gravity = Gravity.CENTER;
            flContent.setLayoutParams(params);
        }
        adminUserCardAdapter.filterList(filteredList);
    }
}