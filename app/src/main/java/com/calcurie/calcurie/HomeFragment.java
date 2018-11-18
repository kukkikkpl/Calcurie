package com.calcurie.calcurie;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.calcurie.calcurie.model.User;
import com.calcurie.calcurie.util.AppUtils;
import com.calcurie.calcurie.util.DBHelper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private DBHelper dbHelper;
    private Long totalCal2 = 0L;
    int activityLevel;
    String gender;
    float weight;
    float height;
    int age;
    TextView recommendCal;
    TextView haveEatenCal;
    TextView remainCal;
    private  ArrayList<PieEntry> entries = new ArrayList<>();
    private int recommendCalInt = 0;
    private  User user = new User();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        SharedPreferences setting = getContext().getSharedPreferences(AppUtils.PREFS_NAME, 0);
        String id = setting.getString("id", null);
        dbHelper = new DBHelper(getContext());
        user = dbHelper.getUser(id);
        gender = user.getGender();
        weight = user.getWeight();
        height = user.getHeight();
        age = user.getAge();
        activityLevel = user.getActivityLevel();
        entries.add(new PieEntry(calculateRecommendCal(gender, weight, height, age, activityLevel), "Remain (Calories)"));
        recommendCal = getView().findViewById(R.id.home_recommend_cal);
        haveEatenCal = getView().findViewById(R.id.home_have_eaten_cal);
        remainCal = getView().findViewById(R.id.home_remain_cal);
        recommendCal.setText(String.valueOf(calculateRecommendCal(gender, weight, height, age, activityLevel)));
        haveEatenCal.setText(String.valueOf(0));
        remainCal.setText(String.valueOf(calculateRecommendCal(gender, weight, height, age, activityLevel)));
        drawGraph(entries);
        showCal();
        BottomNavigationView bottomNavigationView;
        bottomNavigationView = getActivity().findViewById(R.id.navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    public void showCal() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd");
        String dateNow = sdf_1.format(currentTime);

        firestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
                .collection("Diaries")
                .whereEqualTo("date", dateNow)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("home", "Success");
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        Map<String, Object> temp = document.getData();
                        Long totalCal = (Long) temp.get("total");
                        totalCal2 += totalCal;
                        Log.d("home", String.valueOf(totalCal));


                        int haveEatenCalInt = Integer.parseInt(String.valueOf(totalCal2));
                        Log.d("home", "haveEaten = " + haveEatenCalInt);

                        recommendCalInt = calculateRecommendCal(gender, weight, height, age, activityLevel);

                        int remainCalInt = recommendCalInt - haveEatenCalInt;
                        recommendCal.setText(String.valueOf(recommendCalInt));
                        haveEatenCal.setText(String.valueOf(haveEatenCalInt));
                        if (remainCalInt < 0) {
                            remainCal.setTextColor(Color.parseColor("#B22222"));
                            remainCal.setTypeface(remainCal.getTypeface(), Typeface.BOLD);
                        }
                        remainCal.setText(String.valueOf(remainCalInt));
                        Log.d("home", "Set text");

                        entries = new ArrayList<>();
                        // add calories data here
                        if (remainCalInt <= 0) {
                            entries.add(new PieEntry(haveEatenCalInt, "Have eaten (Calories)"));
                            entries.add(new PieEntry(0, "Remain (Calories)"));
                        } else {
                            entries.add(new PieEntry(haveEatenCalInt, "Have eaten (Calories)"));
                            entries.add(new PieEntry(remainCalInt, "Remain (Calories)"));
                        }

                        drawGraphWithHaveEaten(entries);
                    }
                } else {
                    Log.d("home", "Not Success");
                }
            }
        });
    }

    public int calculateRecommendCal(String gender, float weight, float height, int age ,int activityLevel) {
        double BMR;
        if (gender.equals("Male")) {
            BMR = 66.5 + (13.75 * weight) + (5.003 * height) - (6.755 * age);
        } else {
            BMR = 655.1 + (9.563 * weight) + (1.85 * height) - (4.676 * age);
        }
        switch (activityLevel) {
            case 1:
                recommendCalInt = (int) Math.ceil(BMR * 1.2);
                break;
            case 2:
                recommendCalInt = (int) Math.ceil(BMR * 1.375);
                break;
            case 3:
                recommendCalInt = (int) Math.ceil(BMR * 1.55);
                break;
            case 4:
                recommendCalInt = (int) Math.ceil(BMR * 1.725);
                break;
            case 5:
                recommendCalInt = (int) Math.ceil(BMR * 1.9);
                break;
        }
        return recommendCalInt;
    }

    public void drawGraphWithHaveEaten(ArrayList<PieEntry> entries) {
        PieChart chart = (PieChart) getView().findViewById(R.id.pie_chart);
        PieDataSet dataset = new PieDataSet(entries, "Calories");
        dataset.setSelectionShift(10);
        dataset.setValueTextSize(14);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ColorTemplate.rgb("#B22222"));
        colors.add(ColorTemplate.rgb("#cccccc"));
        dataset.setColors(colors);
        PieData data = new PieData(dataset);
        chart.animateY(800);
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

    public void drawGraph(ArrayList<PieEntry> entries) {
        PieChart chart = (PieChart) getView().findViewById(R.id.pie_chart);
        PieDataSet dataset = new PieDataSet(entries, "Calories");
        dataset.setSelectionShift(10);
        dataset.setValueTextSize(14);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(ColorTemplate.rgb("#cccccc"));
        dataset.setColors(colors);
        PieData data = new PieData(dataset);
        chart.setData(data);
        chart.setHoleRadius(70);
        chart.getDescription().setEnabled(false);
        chart.setDrawEntryLabels(false);
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(16f);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        dataset.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataset.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
    }
}
