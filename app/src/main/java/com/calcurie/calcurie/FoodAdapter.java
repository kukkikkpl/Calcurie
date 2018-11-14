package com.calcurie.calcurie;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends ArrayAdapter<Food> {
    private List<Food> foods;
    private ArrayList<Food> foodHolder;
    private Context context;
    private View foodItem;
    private Bundle bundle;

    public FoodAdapter(@NonNull Context context,
                       int resource,
                       @NonNull List<Food> objects) {
        super(context, resource, objects);
        this.foods = objects;
        this.context = context;
        this.foodHolder = new ArrayList<>();
        this.bundle = new Bundle();
    }

    @NonNull
    @Override
    public View getView(int position,
                        @Nullable View convertView,
                        @NonNull ViewGroup parent) {

        foodItem = LayoutInflater.from(context).inflate(
                R.layout.fragment_select_food_item,
                parent,
                false);

        ImageView foodImg = (ImageView) foodItem.findViewById(R.id.fragment_food_item_food_image);
        TextView nameText = (TextView) foodItem.findViewById(R.id.fragment_food_item_name);
        TextView detailText = (TextView) foodItem.findViewById(R.id.fragment_food_item_detail);
        TextView caloriesText = (TextView) foodItem.findViewById(R.id.fragment_food_item_calories);
        EditText qtyText = (EditText) foodItem.findViewById(R.id.fragment_food_item_qty);

        Food food = foods.get(position);

        Log.d("adapter", "set a food in adapter");

        Picasso.get().load(food.getImg_url()).into(foodImg);
        nameText.setText(food.getName());
        detailText.setText(food.getDetail());
        caloriesText.setText(Integer.toString(food.getCalories()) + " kcal");

//        initDecreaseBtn();
        initIncreaseBtn();
//        initAddFoodBtn(position);
//        initSelectedFoodBtn(parent);

        return foodItem;
    }

    private void initDecreaseBtn() {
        Button dcBtn = foodItem.findViewById(R.id.fragment_food_item_decrease);
        final EditText qtyText = (EditText) foodItem.findViewById(R.id.fragment_food_item_qty);
        dcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SelectFood", "Decrease Button was clicked");
                int qtyInt = Integer.parseInt(qtyText.getText().toString());
                if (qtyInt != 0) {
                    qtyInt--;
                    qtyText.setText(Integer.toString(qtyInt));
                }
            }
        });
    }

    private void initIncreaseBtn() {
        Button icBtn = foodItem.findViewById(R.id.fragment_food_item_increase);
        final EditText qtyText = (EditText) foodItem.findViewById(R.id.fragment_food_item_qty);
        icBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qtyInt = Integer.parseInt(qtyText.getText().toString());
                qtyInt++;
                qtyText.setText(Integer.toString(qtyInt));
            }
        });
    }

    private void initAddFoodBtn(final int position) {
        Button addFoodBtn = foodItem.findViewById(R.id.fragment_food_item_add);
        addFoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText qtyText = v.findViewById(R.id.fragment_food_item_qty);
                int qtyInt = Integer.parseInt(qtyText.toString());
                if (qtyInt < 1) {
                    Toast.makeText(context, "Qty must be more than 0", Toast.LENGTH_SHORT).show();;
                } else {
                    Food food = foods.get(position);
                    food.setQty(qtyInt);
                    foodHolder.add(food);
                    String foodName = food.getName();
                    Toast.makeText(context, qtyText + " " + foodName + " was added", Toast.LENGTH_SHORT).show();
                }
                qtyText.setText("0");
            }
        });
    }

    private void initSelectedFoodBtn(ViewGroup parent) {
//        Button selectedFoodBtn = LayoutInflater.from(context).inflate(R.layout.fragment_select_food, parent)
//                .findViewById(R.id.fragment_select_food_selected_food_btn);
//
//        selectedFoodBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bundle.putSerializable("selected food list", foodHolder);
//                Fragment addSlectedFoodFragment = new SelectedFoodFrgment();
//                addSlectedFoodFragment.setArguments(bundle);
//                getActivity()
//                        .getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.activity_main, new SelectedFoodFragment())
//                        .addToBackStack(null).commit();
//            }
//        });
    }
}
