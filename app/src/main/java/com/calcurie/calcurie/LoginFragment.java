package com.calcurie.calcurie;

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

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loginBtn();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
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

    private void signIn(String email, String password) {

    }
}
