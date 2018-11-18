package com.calcurie.calcurie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.calcurie.calcurie.model.Food;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectFoodFragment extends Fragment {
    private List<Food> foods;
    private FirebaseFirestore fsDB;
    private FirebaseAuth fsAuth;

    public SelectFoodFragment() {
        this.fsDB = FirebaseFirestore.getInstance();
        this.fsAuth = FirebaseAuth.getInstance();
        this.foods = new ArrayList<Food>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_food, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initMenuBtn();
        initAddFoodBtn();
        initBackBtn();
        initAcceptFoodBtn();
        getValueDB();
    }

    private void initConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("ยืนยันรายการที่จะบันทึกหรือไม่?");
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d("SelectFood", "User Accept");

                java.util.Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf_2 = new SimpleDateFormat("hh:mm:ss");

                String dateNow = sdf_1.format(currentTime);
                String timeNow = sdf_2.format(currentTime);

//                Log.d("SelectFood", "dateNow = " + dateNow + " timeNow = " + timeNow);

                String dateTimeNow = dateNow + " " + timeNow;
                ArrayList<Food> mealList = new ArrayList<>();
                Map<String, Food> mealList2 = new HashMap<>();

                int qtyCheck = 0;
                int TCalPerMeal = 0;
                for (Food item: foods) {
                    if (item.getQty() > 0) {
                        qtyCheck += item.getQty();
                        item.setDate(dateNow);
                        item.setTime(timeNow);
                        int temp = item.getCalories() * item.getQty();
                        TCalPerMeal += temp;
                        mealList2.put(item.getId(), item);
                        mealList.add(item);
                    }
                }
                if (qtyCheck < 1) {
                    Toast.makeText(getContext(), "กรุณาเพิ่มจำนวณอาหาร", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, ArrayList> diary = new HashMap<>();
//                Map<String, ArrayList> diary = new HashMap<>();

                diary.put(timeNow, mealList);
//                diary.put("meal", mealList2);
//                diary.put("date", dateNow);
//                diary.put("total_cal_per_meal", TCalPerMeal);

                fsDB.collection("Users")
                        .document(fsAuth.getUid())
                        .collection("Diaries") //2018-12-12 dateNow
                        .document(dateTimeNow)   //12:12:12 timeNow
                        .set(diary)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("SelectFood", "DocumentSnapshot successfully written!");
                                Toast.makeText(getContext(), "บันทึกสำเร็จ", Toast.LENGTH_LONG).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("SelectFood", "Error writing document", e);
                                Toast.makeText(getContext(), "บันทึกล้มเหลว", Toast.LENGTH_SHORT).show();
                            }
                        });

                Map<String, Object> diary2 = new HashMap<>();
                diary2.put("date",dateNow);
                diary2.put("total",TCalPerMeal);

                fsDB.collection("Users")
                        .document(fsAuth.getUid())
                        .collection("Diaries") //2018-12-12 dateNow
                        .document(dateTimeNow)   //12:12:12 timeNow
                        .update(diary2)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("SelectFood", "DocumentSnapshot successfully written!");
                                Toast.makeText(getContext(), "บันทึกสำเร็จ", Toast.LENGTH_LONG).show();
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.main_view, new DiaryFragment())
                                        .commit();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("SelectFood", "Error writing document", e);
                                Toast.makeText(getContext(), "บันทึกล้มเหลว", Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });
        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("SelectFood", "User Decline");
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void getValueDB() {
        final ListView foodList = (ListView) getView().findViewById(R.id.fragment_select_food_list);

        final FoodAdapter foodAdapter = new FoodAdapter(getContext(), R.layout.fragment_select_food_item, foods);

        foodList.setAdapter(foodAdapter);
        foods.clear();

        fsDB.collection("Foods").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("SelectFood", document.getId() + " => " + document.getData());
                        String id = document.getId();
                        String foodName = document.get("name").toString();
                        String detail = document.get("detail").toString();
                        String calories = document.get("calories").toString();
                        int caloriesInt = Integer.parseInt(calories);
                        String img_url = document.get("image_url").toString();
                        Food food = new Food(id, foodName, detail, caloriesInt, img_url);
                        foods.add(food);
                        foodAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d("SelectFood", "Error getting documents: ", task.getException());
                }
            }
        });
    }
    /*
    private void initMenuBtn() {
        Button menuBtn = getView().findViewById(R.id.fragment_select_food_menu_btn);

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
    */

    private void initAcceptFoodBtn() {
        Button mButtonDialog = (Button) getView().findViewById(R.id.fragment_select_food_accept_btn);

        mButtonDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initConfirmDialog();
            }
        });
    }

    private void initAddFoodBtn() {
        Button mButtonDialog = (Button) getView().findViewById(R.id.fragment_select_food_new_food_btn);

        mButtonDialog.setOnClickListener(new View.OnClickListener() {
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

    private void initBackBtn() {
        Button mButtonDialog = (Button) getView().findViewById(R.id.fragment_select_food_back_btn);

        mButtonDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new DiaryFragment())
                        .commit();
            }
        });
    }

}
