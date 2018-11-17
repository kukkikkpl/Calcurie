package com.calcurie.calcurie;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PieChart chart = (PieChart) getView().findViewById(R.id.pie_chart);
        ArrayList<PieEntry> entries = new ArrayList<>();

        // add calories data heree
        entries.add(new PieEntry(2000, "Have eaten (Calories)"));
        entries.add(new PieEntry(1000, "Remain (Calories)"));

        PieDataSet dataset = new PieDataSet(entries, "Calories");
        dataset.setSelectionShift(10);
        dataset.setValueTextSize(14);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ColorTemplate.rgb("#B22222"));
        colors.add(ColorTemplate.rgb("#cccccc"));
        dataset.setColors(colors);
        PieData data = new PieData(dataset);
        chart.animateY(3000);
        chart.setData(data);
        chart.setHoleRadius(70);
        chart.getDescription().setEnabled(false);
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(16f);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(10f);
        dataset.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataset.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
    }
}
