package com.calcurie.calcurie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DiaryAdapter extends ArrayAdapter<Food> {
    private List<Food> foods;
    private Context context;
    private View foodItem;

    public DiaryAdapter(@NonNull Context context,
                        int resource,
                        @NonNull List<Food> objects) {
        super(context, resource, objects);
        this.foods = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position,
                        @Nullable View convertView,
                        @NonNull ViewGroup parent) {

        foodItem = LayoutInflater.from(context).inflate(
                R.layout.fragment_diary_item,
                parent,
                false);

        TextView foodNameText = (TextView) foodItem.findViewById(R.id.fragment_diary_item_food_name);
        TextView caloriesText = (TextView) foodItem.findViewById(R.id.fragment_diary_item_calories);
        TextView dateText = (TextView) foodItem.findViewById(R.id.fragment_diary_item_date);

        Food food = foods.get(position);

//        String foodDate = food.getDate() + " " + food.getTime();

        foodNameText.setText(food.getName());
        caloriesText.setText(NumberFormat.getNumberInstance(Locale.US).format(food.getTotal_calories()) + " kcal");
        dateText.setText(food.getDate());

        return foodItem;
    }
}
