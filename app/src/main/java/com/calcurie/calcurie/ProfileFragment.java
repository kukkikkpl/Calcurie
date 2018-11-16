package com.calcurie.calcurie;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.calcurie.calcurie.model.User;
import com.calcurie.calcurie.util.AppUtils;
import com.calcurie.calcurie.util.DBHelper;

import org.w3c.dom.Text;

import java.io.InputStream;

public class ProfileFragment extends Fragment {

    private DBHelper dbHelper;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showProfile();
        signOut();
        initEditBtn();
    }

    public void initEditBtn() {
        Button edit = (Button) getView().findViewById(R.id.profile_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_main, new EditProfileFragment())
                        .commit();
            }
        });
    }

    public void signOut() {
        Button signOut = (Button) getView().findViewById(R.id.profile_sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activity_main, new MenuFragment())
                        .commit();
            }
        });
    }

    public void showProfile(){
        SharedPreferences setting = getContext().getSharedPreferences(AppUtils.PREFS_NAME, 0);
        String id = setting.getString("id", null);
        TextView name = (TextView) getView().findViewById(R.id.profile_name);
        TextView age = (TextView) getView().findViewById(R.id.profile_age);
        TextView weight = (TextView) getView().findViewById(R.id.profile_weight);
        TextView height = (TextView) getView().findViewById(R.id.profile_height);
        dbHelper = new DBHelper(getContext());
        user = dbHelper.getUser(id);
        new DownloadImageTask((ImageView) getView().findViewById(R.id.profile_avatar)).execute(user.getImageUrl());
        name.setText(user.getName());
        age.setText(String.valueOf(user.getAge()));
        weight.setText(String.valueOf(user.getWeight()));
        height.setText(String.valueOf(user.getHeight()));
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
