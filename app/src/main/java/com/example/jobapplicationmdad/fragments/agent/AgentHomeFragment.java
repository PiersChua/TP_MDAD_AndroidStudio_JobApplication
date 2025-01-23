package com.example.jobapplicationmdad.fragments.agent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.example.jobapplicationmdad.R;
import com.example.jobapplicationmdad.activities.MainActivity;
import com.example.jobapplicationmdad.network.JsonObjectRequestWithParams;
import com.example.jobapplicationmdad.network.VolleyErrorHandler;
import com.example.jobapplicationmdad.network.VolleySingleton;
import com.example.jobapplicationmdad.util.ChartUtil;
import com.example.jobapplicationmdad.util.UrlUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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
 * Use the {@link AgentHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgentHomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String get_chart_data_url = MainActivity.root_url + "/api/agent/get-chart-data.php";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    PieChart pieChart;
    BarChart barChart;
    BarChart barChart2;
    View dialogView;
    AlertDialog loadingDialog;
    SharedPreferences sp;
    ArrayList<String> appliedJobPositions = new ArrayList<>();
    ArrayList<String> favouritedJobPositions = new ArrayList<>();

    public AgentHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AgentHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AgentHomeFragment newInstance(String param1, String param2) {
        AgentHomeFragment fragment = new AgentHomeFragment();
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
        return inflater.inflate(R.layout.fragment_agent_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(dialogView).setCancelable(false);
        loadingDialog = builder.create();
        pieChart = view.findViewById(R.id.pcAgentJobApplicationProportion);
        barChart = view.findViewById(R.id.bcAgentJobAppliedProportion);
        barChart2 = view.findViewById(R.id.bcAgentJobFavouritedProportion);
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
                    ArrayList<PieEntry> jobApplicationProportion = new ArrayList<>();
                    ArrayList<BarEntry> jobAppliedProportion = new ArrayList<>();
                    ArrayList<BarEntry> jobFavouritedProportion = new ArrayList<>();
                    int totalApplications = 0;
                    JSONArray jobApplicationProportionArray = response.getJSONArray("job_application_data");
                    JSONArray jobAppliedProportionArray = response.getJSONArray("applied_job_data");
                    JSONArray jobFavouritedProportionArray = response.getJSONArray("favourited_job_data");
                    for (int i = 0; i < jobApplicationProportionArray.length(); i++) {
                        JSONObject chartObject = jobApplicationProportionArray.getJSONObject(i);
                        jobApplicationProportion.add(new PieEntry(chartObject.getInt("job_application_count"), chartObject.getString("status")));
                        totalApplications += chartObject.getInt("job_application_count");
                    }
                    appliedJobPositions.clear();
                    for (int i = 0; i < jobAppliedProportionArray.length(); i++) {
                        JSONObject chartObject = jobAppliedProportionArray.getJSONObject(i);
                        appliedJobPositions.add(chartObject.getString("position"));
                        jobAppliedProportion.add(new BarEntry(i, chartObject.getInt("job_application_count")));
                    }

                    favouritedJobPositions.clear();
                    for (int i = 0; i < jobFavouritedProportionArray.length(); i++) {
                        JSONObject chartObject = jobFavouritedProportionArray.getJSONObject(i);
                        favouritedJobPositions.add(chartObject.getString("position"));
                        jobFavouritedProportion.add(new BarEntry(i, chartObject.getInt("favourite_job_count")));
                    }

                    // Pie chart
                    PieDataSet pieDataSet = new PieDataSet(jobApplicationProportion, "Job Applications overview");
                    ChartUtil.initPieChart(pieChart, pieDataSet, String.valueOf(totalApplications), ColorTemplate.COLORFUL_COLORS);

                    // Bar chart
                    BarDataSet barDataSet = new BarDataSet(jobAppliedProportion, "Top 3 jobs (Most Applied)");
                    ChartUtil.initBarChart(barChart, barDataSet, appliedJobPositions, ColorTemplate.COLORFUL_COLORS);

                    // Bar chart 2
                    BarDataSet barDataSet2 = new BarDataSet(jobFavouritedProportion, "Top 3 jobs (Most favourited)");
                    ChartUtil.initBarChart(barChart2, barDataSet2, favouritedJobPositions, ColorTemplate.JOYFUL_COLORS);

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