package com.calcurie.calcurie;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user;

        user = mAuth.getCurrentUser();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_view, new LoginFragment())
                    .commit();
        }

            bottomNavigationView = findViewById(R.id.navigation);

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_diary:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.main_view, new DiaryFragment()).commit();
                            return true;
                        case R.id.navigation_home:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.main_view, new MenuFragment()).commit();
                            return true;
                        case R.id.navigation_profile:
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.main_view, new ProfileFragment()).commit();
                            return true;
                    }
                    return false;
                }
            });


    }
}
