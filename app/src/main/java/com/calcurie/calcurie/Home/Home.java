package com.calcurie.calcurie.Home;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.calcurie.calcurie.R;
import com.txusballesteros.widgets.FitChart;
import com.txusballesteros.widgets.FitChartValue;

import java.util.ArrayList;
import java.util.Collection;

public class Home extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Chart();
        addBtn();
    }

    public void Chart(){
        final FitChart fitChart = (FitChart)getView().findViewById(R.id.fitChart);
        fitChart.setMinValue(0f);
        fitChart.setMaxValue(100f);
        Resources resources = getResources();
        Collection<FitChartValue> values = new ArrayList<>();

        int sumCal = 0,BFCal=0,LCal=0,DNCal=0,Snack=0,maxCalCanEat=0;
        sumCal = BFCal + LCal + DNCal + Snack;
        float percentChartCal = (sumCal*100)/maxCalCanEat;
        if (percentChartCal>100){
            values.add(new FitChartValue(percentChartCal, R.color.over));
        }else {
            values.add(new FitChartValue(percentChartCal, R.color.fit));
        }
        fitChart.setValues(values);
    }

    public void addBtn() {
        Button addBtn = (Button) getView().findViewById(R.id.add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HOME", "GO ADD FOOD");
                /* getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_main, new DiaryFragment())
                        .addToBackStack(null).commit(); */
            }
        });
    }
}
