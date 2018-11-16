package com.calcurie.calcurie;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MenuFragment extends Fragment {

    ArrayList<String> menu = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        menu.clear();

        // add new menu here
        menu.add("Food Selection");
        menu.add("Add Food");
        menu.add("Diary");
        menu.add("Sign Out");

        ListView menuList = getView().findViewById(R.id.menu_list);
        final ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                menu
        );
        menuList.setAdapter(menuAdapter);
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("MENU", "Select " + menu.get(position));
                if (menu.get(position).equals("Food Selection")) {
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_view, new SelectFoodFragment())
                            .addToBackStack(null).commit();
                } else if (menu.get(position).equals("Diary")) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_view, new DiaryFragment())
                            .addToBackStack(null)
                            .commit();
                } else if (menu.get(position).equals("Add Food")){
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_view, new AddMenuFragment())
                            .addToBackStack(null)
                            .commit();
                } else if(menu.get(position).equals("Sign Out")){
                    FirebaseAuth userAuth = FirebaseAuth.getInstance();
                    userAuth.signOut();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_view, new LoginFragment())
                            .addToBackStack(null)
                            .commit();
                }

                // add link to other fragments here

                /* else if (menu.get(position).equals("Register")) {
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.activity_main, new RegisterFragment())
                            .addToBackStack(null).commit();
                } */
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }
}
