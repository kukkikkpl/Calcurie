package com.calcurie.calcurie;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TestDiaryFragment extends Fragment {
    private FirebaseFirestore fsDB;
    private FirebaseAuth fsAuth;

    public TestDiaryFragment() {
        this.fsDB = FirebaseFirestore.getInstance();
        this.fsAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_diary, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getValueDB();
    }
    
    private void getValueDB() {

        java.util.Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 1; i < 31; i++) {
            java.util.Date newDate = subtractDays(currentTime, i);
            String dateCol = sdf_1.format(newDate);
            Log.d("diary", "Diary No." + i);
            fsDB.collection("Users")
                    .document(fsAuth.getUid())
                    .collection(dateCol)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("diary", document.getId() + " => " + document.getData());
//                                    String id = document.getId();
//                                    String foodName = document.get("name").toString();
//                                    String detail = document.get("detail").toString();
//                                    String calories = document.get("calories").toString();
//                                    int caloriesInt = Integer.parseInt(calories);
//                                    String img_url = document.get("image_url").toString();
//                                    Log.d("diary",
//                                            "foodName = " + foodName +
//                                                    "\ndetail = " + detail +
//                                                    "\ncalories = " + calories
//                                            );
                                }
                            } else {
                                Log.d("diary", "Error getting documents: ", task.getException());
                            }
                            Log.d("SelectFood", "DocumentSnapshot successfully written!");
                            Toast.makeText(getContext(), "บันทึกสำเร็จ", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("SelectFood", "Error writing document", e);
                    Toast.makeText(getContext(), "บันทึกล้มเหลว", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static java.util.Date subtractDays(java.util.Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);
        return cal.getTime();
    }

}
