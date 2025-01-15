package com.example.jobapplicationmdad.fragments.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.model.User;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminHomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String get_admin_chart_data_url = MainActivity.root_url + "/api/admin/get-chart-data.php";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    PieChart pieChart;
    View dialogView;
    AlertDialog loadingDialog;
    SharedPreferences sp;

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminHomeFragment newInstance(String param1, String param2) {
        AdminHomeFragment fragment = new AdminHomeFragment();
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
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        pieChart = view.findViewById(R.id.pcAdminUserRoles);
        getChartData();


    }

    private void getChartData() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", sp.getString("userId", ""));
        String url = UrlUtil.constructUrl(get_admin_chart_data_url, params);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<PieEntry> entries = new ArrayList<>();

                    JSONArray usersArray = response.getJSONArray("data");
                    int totalUsers = 0;
                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject userObject = usersArray.getJSONObject(i);
                        entries.add(new PieEntry(userObject.getInt("user_count"), userObject.getString("nationality")));
                        totalUsers += (userObject.getInt("user_count"));
                    }
                    PieDataSet dataSet = new PieDataSet(entries, "Proportion of users by nationality");
                    dataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            return String.valueOf((int) value);
                        }

                    });
                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    dataSet.setValueTextSize(32);
                    PieData data = new PieData(dataSet);
                    pieChart.setData(data);
                    pieChart.getDescription().setEnabled(false);
                    pieChart.setCenterText(String.valueOf(totalUsers));
                    pieChart.setCenterTextSize(20f);
                    pieChart.invalidate();
                    pieChart.animateY(1000, Easing.EaseInOutQuad);

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
}