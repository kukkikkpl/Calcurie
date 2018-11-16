package com.calcurie.calcurie;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.calcurie.calcurie.model.User;
import com.calcurie.calcurie.util.AppUtils;
import com.calcurie.calcurie.util.DBHelper;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileFragment extends Fragment {
    private DBHelper dbHelper;
    private User user;
    private FirebaseFirestore firestore;
    private String id;
    EditText name;
    EditText weight;
    EditText height;
    EditText age;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        showEditProfile();
        save();
    }

    public void showEditProfile() {
        SharedPreferences setting = getContext().getSharedPreferences(AppUtils.PREFS_NAME, 0);
        id = setting.getString("id", null);
        name = getView().findViewById(R.id.edit_profile_name);
        weight = getView().findViewById(R.id.edit_profile_weight);
        height = getView().findViewById(R.id.edit_profile_height);
        age = getView().findViewById(R.id.edit_profile_age);
        dbHelper = new DBHelper(getContext());
        user = dbHelper.getUser(id);
        name.setText(user.getName());
        age.setText(String.valueOf(user.getAge()));
        weight.setText(String.valueOf(user.getWeight()));
        height.setText(String.valueOf(user.getHeight()));
    }

    public void save() {
        Button save = getView().findViewById(R.id.edit_profile_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = name.getText().toString();
                String ageStr = age.getText().toString();
                String weightStr = weight.getText().toString();
                String heightStr = height.getText().toString();
                if (nameStr.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
                    Toast.makeText(
                            getActivity(),
                            "กรุณากรอกข้อมูลให้ครบถ้วน",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    user.setName(nameStr);
                    user.setHeight(Float.parseFloat(heightStr));
                    user.setWeight(Float.parseFloat(weightStr));
                    user.setAge(Integer.parseInt(ageStr));
                    dbHelper.addUser(user);
                    firestore.collection("Users").document(id).update(
                            "name", nameStr,
                            "age", Integer.parseInt(ageStr),
                            "weight", Float.parseFloat(weightStr),
                            "height", Float.parseFloat(heightStr)
                    );
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_view, new ProfileFragment())
                            .disallowAddToBackStack()
                            .commit();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }
}
