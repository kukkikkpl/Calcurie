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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SelectFoodFragment extends Fragment {
    private List<Food> foods;
    private Bundle bundle;
    private FirebaseFirestore fsDB;
    private FirebaseAuth fsAuth;
    private View foodItem;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            bundle.isEmpty();
        } catch (NullPointerException ne) {
            this.bundle = new Bundle();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initMenuBtn();
        initAcceptFoodBtn();
        getValueDB();
    }

    private void initConfirmDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getContext());
        builder.setMessage("ยืนยันรายการที่จะบันทึกหรือไม่?");
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getContext(),
                        "คุณตกลง", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
                        String foodName = document.get("name").toString();
                        String detail = document.get("detail").toString();
                        String calories = document.get("calories").toString();
                        int caloriesInt = Integer.parseInt(calories);
                        String img_url = document.get("image_url").toString();
                        Food food = new Food(foodName, detail, caloriesInt, img_url);
                        foods.add(food);
                        foodAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d("SelectFood", "Error getting documents: ", task.getException());
                }
            }
        });
    }

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

    private void initAcceptFoodBtn() {
        Button mButtonDialog = (Button) getView().findViewById(R.id.fragment_select_food_accept_btn);

        mButtonDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initConfirmDialog();
            }
        });
    }

}
