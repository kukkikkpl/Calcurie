package com.calcurie.calcurie;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.calcurie.calcurie.model.User;
import com.calcurie.calcurie.util.AppUtils;
import com.calcurie.calcurie.util.DBHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class LoginFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        loginBtn();
        registerBtn();
    }

    void loginBtn() {
        Button loginBtn = (Button) getView().findViewById(R.id.login_login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) getView().findViewById(R.id.login_username);
                EditText password = (EditText) getView().findViewById(R.id.login_password);
                String usernameStr = username.getText().toString();
                String passwordStr = password.getText().toString();
                if (usernameStr.isEmpty() || passwordStr.isEmpty()) {
                    Toast.makeText(
                            getActivity(),
                            "กรุณาระบุ Username หรือ Password",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    signIn(usernameStr, passwordStr);
                }
            }
        });
    }

    void registerBtn() {
        TextView _registerBtn = (TextView) getView().findViewById(R.id.login_register_btn);
        _registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("USER", "GO TO REGISTER");
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new RegisterFragment())
                        .addToBackStack(null).commit();
            }
        });
    }

    private void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user.isEmailVerified()) {
                    Log.d("USER", "GO TO MENU");
                    SharedPreferences setting = getContext().getSharedPreferences(AppUtils.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putBoolean("hasLoggedIn", true);
                    editor.putString("id", user.getUid());
                    editor.commit();
                    firestore.collection("Users").document(user.getUid())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Log.d("USER", "GET USER FROM FIRESTORE");
                            DocumentSnapshot documentSnapshot = task.getResult();
                            User user = documentSnapshot.toObject(User.class);
                            dbHelper = new DBHelper(getActivity());
                            dbHelper.addUser(user);
                            getActivity()
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.main_view, new MenuFragment())
                                    .disallowAddToBackStack()
                                    .commit();
                        }
                    });
                } else {
                    Toast.makeText(
                            getActivity(),
                            "User นี้ยังไม่ได้ทำการ Confirm Email",
                            Toast.LENGTH_SHORT
                    ).show();
                    Log.d("USER", "EMAIL IS NOT VERIFIED");
                    firebaseAuth.signOut();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(
                        getActivity(),
                        "ชื่อ User หรือรหัสผ่านไม่ถูกต้อง",
                        Toast.LENGTH_SHORT
                ).show();
                Log.d("USER", "ERROR IN SIGNIN");
            }
        });
    }

}
