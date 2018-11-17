package com.calcurie.calcurie;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiaryFragment extends Fragment {
    private FirebaseFirestore fsDB;
    private FirebaseAuth fsAuth;
    private List<Food> foods;

    public DiaryFragment() {
        this.fsDB = FirebaseFirestore.getInstance();
        this.fsAuth = FirebaseAuth.getInstance();
        this.foods = new ArrayList<Food>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initMenuBtn();
        initAddFoodBtn();
        getValueDB();
    }

    private void initAddFoodBtn() {
        Button menuBtn = getView().findViewById(R.id.fragment_diary_add_food_btn);

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new AddMenuFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void initMenuBtn() {
        Button menuBtn = getView().findViewById(R.id.fragment_diary_menu_btn);

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new MenuFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void getValueDB() {

        ListView diaryList = getView().findViewById(R.id.fragment_diary_list);

        final DiaryAdapter diaryAdapter = new DiaryAdapter(
                getActivity(),
                R.layout.fragment_diary_item,
                foods
        );

        diaryList.setAdapter(diaryAdapter);
        foods.clear();

        final Map<String, HashMap> diaries = new HashMap<>();
                    fsDB.collection("Users")
                    .document(fsAuth.getUid())
                            .collection("Diaries")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    Log.d("diary", document.getId() + " => " + document.getData());
                                    Map<String, Object> diaries = document.getData();
//                                    Log.d("diary", diaries.toString());
                                    int round = 0;
                                    for (Object list: diaries.values()) {
                                        round++;
                                        Log.d("diary", Integer.toString(round) + " List => " + list.toString());
                                        if (round == 3) {
                                            ArrayList<HashMap> arrayFood = (ArrayList<HashMap>) list;
//                                        Log.d("diary", arrayFood.toString());
//                                        Log.d("diary", arrayFood.get(0).toString());
                                            for (HashMap item: arrayFood) {
                                                String foodName = (String) item.get("name");
                                                Long calories = (Long) item.get("calories");
                                                Long qty = (Long) item.get("qty");
                                                String date = (String) item.get("date");
                                                String time = (String) item.get("time");
//                                             String dateTime = date + " " + time;
//                                             Log.d("diary",
//                                             "foodName => " + foodName +
//                                             "\ncalories => " + calories +
//                                                     "\ndateTime =>" + dateTime
//                                                     );
                                                Food food = new Food(foodName, calories, qty, date, time);
                                                foods.add(food);
                                        }

                                        }
                                    }
                                }
                                diaryAdapter.notifyDataSetChanged();
                            } else {
                                Log.d("diary", "Task is not successful: ", task.getException());
                            }

                            Log.d("diary", "DocumentSnapshot successfully written!");
                            Toast.makeText(getContext(), "โหลดสำเร็จ", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("diary", "Error writing document", e);
                    Toast.makeText(getContext(), "โหลดไม่สำเร็จ", Toast.LENGTH_SHORT).show();
                }
            });

        Log.d("diary", "Diaries = " + diaries.toString());
    }

    public static java.util.Date subtractDays(java.util.Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);
        return cal.getTime();
    }

}
