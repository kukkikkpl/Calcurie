package com.calcurie.calcurie;

import android.content.Context;
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

import com.calcurie.calcurie.model.Food;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodAdapter extends ArrayAdapter<Food> {
    private List<Food> foods;
    private Context context;
    private View foodItem;

    public FoodAdapter(@NonNull Context context,
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
        qtyText.setText(Integer.toString(food.getQty()));

        initDecreaseBtn(food);
        initIncreaseBtn(food);
        initResetQtyBtn(food);

        return foodItem;
    }

    private void initResetQtyBtn(final Food food) {
        Button resetBtn = foodItem.findViewById(R.id.fragment_food_item_reset_btn);
        final EditText qtyText = (EditText) foodItem.findViewById(R.id.fragment_food_item_qty);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SelectFood", "Reset Button was clicked");
                qtyText.setText("0");
                food.setQty(0);
            }
        });
    }

    private void initDecreaseBtn(final Food food) {
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
                    food.setQty(qtyInt);
                }
            }
        });
    }

    private void initIncreaseBtn(final Food food) {
        Button icBtn = foodItem.findViewById(R.id.fragment_food_item_increase);
        final EditText qtyText = (EditText) foodItem.findViewById(R.id.fragment_food_item_qty);
        icBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SelectFood", "Increase Button was clicked");
                int qtyInt = Integer.parseInt(qtyText.getText().toString());
                qtyInt++;
                qtyText.setText(Integer.toString(qtyInt));
                food.setQty(qtyInt);
            }
        });
    }

    public List<Food> getFoods() {
        return foods;
    }
}
