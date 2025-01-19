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
import com.example.jobapplicationmdad.util.ChartUtil;
import com.example.jobapplicationmdad.util.DateConverter;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
    private static final String get_chart_data_url = MainActivity.root_url + "/api/admin/get-chart-data.php";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    PieChart pieChart;
    BarChart barChart;
    BarChart barChart2;
    View dialogView;
    AlertDialog loadingDialog;
    SharedPreferences sp;
    ArrayList<String> roles = new ArrayList<>();
    ArrayList<String> agencyNames = new ArrayList<>();

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
        pieChart = view.findViewById(R.id.pcAdminUserNationalityProportion);
        barChart = view.findViewById(R.id.bcAdminUserRoleProportion);
        barChart2 = view.findViewById(R.id.bcAdminAgencyJobProportion);
        getChartData();


    }

    private void getChartData() {
        loadingDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("userId", sp.getString("userId", ""));
        String url = UrlUtil.constructUrl(get_chart_data_url, params);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sp.getString("token", ""));
        JsonObjectRequestWithParams req = new JsonObjectRequestWithParams(url, headers, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<PieEntry> nationalityProportion = new ArrayList<>();
                    ArrayList<BarEntry> roleProportion = new ArrayList<>();
                    ArrayList<BarEntry> jobProportion = new ArrayList<>();
                    int totalUsers = 0;
                    JSONArray nationalityProportionArray = response.getJSONArray("nationality_data");
                    JSONArray roleProportionArray = response.getJSONArray("role_data");
                    JSONArray jobProportionArray = response.getJSONArray("job_data");
                    for (int i = 0; i < nationalityProportionArray.length(); i++) {
                        JSONObject chartObject = nationalityProportionArray.getJSONObject(i);
                        nationalityProportion.add(new PieEntry(chartObject.getInt("nationality_user_count"), chartObject.getString("nationality")));
                        totalUsers += (chartObject.getInt("nationality_user_count"));
                    }

                    roles.clear();
                    for (int i = 0; i < roleProportionArray.length(); i++) {
                        JSONObject chartObject = roleProportionArray.getJSONObject(i);
                        roles.add(chartObject.getString("role"));
                        roleProportion.add(new BarEntry(i, chartObject.getInt("role_user_count")));
                    }

                    agencyNames.clear();
                    for (int i = 0; i < jobProportionArray.length(); i++) {
                        JSONObject chartObject = jobProportionArray.getJSONObject(i);
                        agencyNames.add(chartObject.getString("name"));
                        jobProportion.add(new BarEntry(i, chartObject.getInt("agency_job_count")));
                    }

                    // Pie chart

                    PieDataSet pieDataSet = new PieDataSet(nationalityProportion, "Proportion of users by nationality");
                    ChartUtil.initPieChart(pieChart, pieDataSet, String.valueOf(totalUsers), ColorTemplate.COLORFUL_COLORS);

                    // Bar chart
                    BarDataSet barDataSet = new BarDataSet(roleProportion, "User role distribution");
                    ChartUtil.initBarChart(barChart, barDataSet, roles, ColorTemplate.COLORFUL_COLORS);


                    // Bar chart 2
                    BarDataSet barDataSet2 = new BarDataSet(jobProportion, "Top 3 agencies with the most jobs");
                    ChartUtil.initBarChart(barChart2, barDataSet2, agencyNames, ColorTemplate.JOYFUL_COLORS);

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