package com.calcurie.calcurie;

import android.content.SharedPreferences;
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
        showCal();
    }

    public void showCal() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        dbHelper = new DBHelper(getContext());
        CollectionReference users = firestore.collection("Users").document(user.getUid()).collection("Diaries");
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd");
        final String dateNow = sdf_1.format(currentTime);

        firestore.collection("Users").document(user.getUid())
                .collection("Diaries")
                .whereEqualTo("date", dateNow)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        Map<String, Object> temp = document.getData();
                        Long totalCal = (Long) temp.get("total");
                        Log.d("home", String.valueOf(totalCal));
                        TextView recommendCal = getView().findViewById(R.id.home_recommend_cal);
                        TextView haveEatenCal = getView().findViewById(R.id.home_have_eaten_cal);
                        TextView remainCal = getView().findViewById(R.id.home_remain_cal);
                        SharedPreferences setting = getContext().getSharedPreferences(AppUtils.PREFS_NAME, 0);
                        String id = setting.getString("id", null);
                        dbHelper = new DBHelper(getContext());
                        User user = dbHelper.getUser(id);
                        int recommendCalInt = 0;
                        int haveEatenCalInt = Integer.parseInt(String.valueOf(totalCal));
                        int activityLevel = user.getActivityLevel();
                        String gender = user.getGender();
                        float weight = user.getWeight();
                        float height = user.getHeight();
                        int age = user.getAge();
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
                        int remainCalInt = recommendCalInt - haveEatenCalInt;
                        recommendCal.setText(String.valueOf(recommendCalInt));
                        haveEatenCal.setText(String.valueOf(haveEatenCalInt));
                        remainCal.setText(String.valueOf(remainCalInt));
                        PieChart chart = (PieChart) getView().findViewById(R.id.pie_chart);
                        ArrayList<PieEntry> entries = new ArrayList<>();

                        // add calories data here
                        entries.add(new PieEntry(haveEatenCalInt, "Have eaten (Calories)"));
                        entries.add(new PieEntry(remainCalInt, "Remain (Calories)"));

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
                } else {
                    Log.d("home", "Not Success");
                }
            }
        });
    }
}
