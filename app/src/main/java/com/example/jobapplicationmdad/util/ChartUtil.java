package com.example.jobapplicationmdad.util;

import com.example.jobapplicationmdad.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class ChartUtil {
    public static void initPieChart(PieChart pieChart, PieDataSet pieDataSet, String centerText, int[] colors) {
        pieDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }

        });
        pieDataSet.setValueTextSize(14);
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setEntryLabelColor(R.color.black);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setCenterText(centerText);
        pieChart.setCenterTextSize(20f);
        pieChart.invalidate();
        pieChart.animateY(1000, Easing.EaseInOutQuad);
    }

    public static void initBarChart(BarChart barChart, BarDataSet barDataSet, ArrayList<String> xAxisLabels, int[] colors) {
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        barDataSet.setColors(colors);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        XAxis barChartXAxis = barChart.getXAxis();
        YAxis barChartLeftAxis = barChart.getAxisLeft();
        YAxis barChartRightAxis = barChart.getAxisRight();
        barChartXAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if ((int) value < xAxisLabels.size()) {
                    return xAxisLabels.get((int) value);
                }
                return "";
            }
        });
        barChart.getLegend().setEnabled(false);
        barChartXAxis.setDrawGridLines(false);
        barChartLeftAxis.setDrawGridLines(false);
        barChartRightAxis.setDrawGridLines(false);
        barChartXAxis.setGranularity(1);
        barChartLeftAxis.setGranularity(1);
        barChartRightAxis.setGranularity(1);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000, Easing.EaseInOutQuad);
    }

}
