package com.calcurie.calcurie.Diary;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.calcurie.calcurie.R;

import java.util.ArrayList;

public class DiaryAdapter extends ArrayAdapter {
    Context context;
    ArrayList<Diary> diarys;

    public DiaryAdapter(Context context, int resource, ArrayList<Diary> objects){
        super(context,resource,objects);
        this.context = context;
        this.diarys = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View diaryList = LayoutInflater.from(context).inflate(R.layout.diary_menu,parent,false);

        Diary diary = diarys.get(position);

        TextView foodName = diaryList.findViewById(R.id.food_nameobj);
        TextView foodCal = diaryList.findViewById(R.id.food_calobj);

        foodName.setText(diary.getName());

        final String foodCalText = String.format("%d kCal",diary.getCalories());
        foodCal.setText(foodCalText);

        return diaryList;
    }
}