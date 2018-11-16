package com.calcurie.calcurie.Diary;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ListView;

import com.calcurie.calcurie.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DiaryFragment extends Fragment {

    SQLiteDatabase db;
    FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String uid = mAuth.getUid();

    ArrayList<Diary> diarys;
    ListView diaryListView;
    private static DiaryAdapter diaryAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary_menu, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initBackButton();
        initShowMenuButton();
        loadData();
    }

    //ทำปุ่มย้อนกลับหน้าก่อน
    public void initBackButton(){
        Button backButton = (Button) getView().findViewById(R.id.diary_back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    //ทำปุ่มแสดงหน้าเมนู
    public void initShowMenuButton(){

    }

    public void loadData(){
        diaryListView = (ListView) getView().findViewById(R.id.diary_menu);
        diaryAdapter = new DiaryAdapter(getActivity(),R.layout.diary_menu,diarys);
        diaryListView.setAdapter(diaryAdapter);
    }

    /*public void testListView(){
        diaryAdapter = new DiaryAdapter(getActivity(),0,diarys);
        diaryListView.setAdapter(diaryAdapter);
    }*/





}
