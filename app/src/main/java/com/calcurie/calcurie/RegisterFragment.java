package com.calcurie.calcurie;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.calcurie.calcurie.model.User;
import com.calcurie.calcurie.util.DBHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterFragment extends Fragment {

    private DBHelper dbHelper;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private User user = new User();
    private String[] activityList = {"Low", "Medium", "High"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        initRegisterNewAccountBtn();
        initSpinner();
    }

    public void initSpinner() {
        Spinner spinner = (Spinner) getView().findViewById(R.id.reg_activity);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.activity_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),
                        "Select : " + activityList[position],
                        Toast.LENGTH_SHORT).show();
                user.setActivityLevel(position+1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void initRegisterNewAccountBtn() {
        Button _registerBtn = (Button) getView().findViewById(R.id.reg_register_btn);
        _registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    public void register() {
        EditText email = (EditText) getView().findViewById(R.id.reg_email);
        EditText password = (EditText) getView().findViewById(R.id.reg_password);
        EditText rePassword = (EditText) getView().findViewById(R.id.reg_re_password);
        EditText name = (EditText) getView().findViewById(R.id.reg_name);
        EditText age = (EditText) getView().findViewById(R.id.reg_age);
        EditText weight = (EditText) getView().findViewById(R.id.reg_weight);
        EditText height = (EditText) getView().findViewById(R.id.reg_height);
        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();
        String rePasswordStr = rePassword.getText().toString();
        String nameStr = name.getText().toString();
        String ageStr = age.getText().toString();
        String weightStr = weight.getText().toString();
        String heightStr = height.getText().toString();

        RadioGroup gender = (RadioGroup) getView().findViewById(R.id.reg_gender);
        int selectedId = gender.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) getView().findViewById(selectedId);
        String genderStr = radioButton.getText().toString();

        Log.d("REGISTER", genderStr);

        if (emailStr.isEmpty() || passwordStr.isEmpty() || rePasswordStr.isEmpty() ||
                nameStr.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty() ||
                heightStr.isEmpty() || genderStr.isEmpty()) {
            Toast.makeText(
                    getActivity(),
                    "กรุณากรอกข้อมูลให้ครบถ้วน",
                    Toast.LENGTH_SHORT
            ).show();
            Log.d("REGISTER", "SOME FIELDS ARE EMPTY");
        } else if (passwordStr.length() < 8) {
            Toast.makeText(
                    getActivity(),
                    "รหัสผ่านต้องมีความยาวตั้งแต่ 6 ตัวอักษรขึ้นไป",
                    Toast.LENGTH_SHORT
            ).show();
            Log.d("REGISTER", "PASSWORD IS TOO SHORT");
        } else if (!passwordStr.equals(rePasswordStr)) {
            Toast.makeText(
                    getActivity(),
                    "รหัสผ่านไม่ตรงกัน",
                    Toast.LENGTH_SHORT
            ).show();
            Log.d("REGISTER", "PASSWORD AND RE-PASSWORD ARE NOT EQUAL");
        } else {
            user.setName(nameStr);
            user.setHeight(Float.parseFloat(heightStr));
            user.setWeight(Float.parseFloat(weightStr));
            user.setAge(Integer.parseInt(ageStr));
            user.setGender(genderStr);
            firebaseAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    sendVerifiedEmail(authResult.getUser());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "อีเมลไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendVerifiedEmail(FirebaseUser _user) {
        _user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("REGISTER", "GO TO LOG IN");
                String id = firebaseAuth.getCurrentUser().getUid();
                user.setId(id);
                dbHelper = new DBHelper(getContext());
                dbHelper.addUser(user);
                addUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "อีเมลไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addUser() {
        firestore.collection("Users")
                .document(user.getId())
                .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_main, new LoginFragment())
                        .addToBackStack(null).commit();
            }
        });
    }

}
