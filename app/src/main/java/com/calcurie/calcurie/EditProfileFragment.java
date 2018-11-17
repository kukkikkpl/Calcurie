package com.calcurie.calcurie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.calcurie.calcurie.model.User;
import com.calcurie.calcurie.util.AppUtils;
import com.calcurie.calcurie.util.DBHelper;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Date;


public class EditProfileFragment extends Fragment {

    public static final int REQUEST_CAMERA = 2;
    ImageView profileImage;
    Uri selectedImage;
    String path;
    FirebaseStorage fbStorage = FirebaseStorage.getInstance();
    StorageReference storageRef = fbStorage.getReference();
    String strURL = "";


    private DBHelper dbHelper;
    private User user;
    private FirebaseFirestore firestore;
    private String id;
    EditText name;
    EditText weight;
    EditText height;
    EditText age;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();


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
        initOpenCamera();
        initSaveButton();
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

//    public void save() {
//        Button save = getView().findViewById(R.id.edit_profile_save);
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadPhoto();
//                Log.d("add", strURL);
//                String nameStr = name.getText().toString();
//                String ageStr = age.getText().toString();
//                String weightStr = weight.getText().toString();
//                String heightStr = height.getText().toString();
//                if (nameStr.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
//                    Toast.makeText(
//                            getActivity(),
//                            "กรุณากรอกข้อมูลให้ครบถ้วน",
//                            Toast.LENGTH_SHORT
//                    ).show();
//                } else {
//                    user.setName(nameStr);
//                    user.setHeight(Float.parseFloat(heightStr));
//                    user.setWeight(Float.parseFloat(weightStr));
//                    user.setAge(Integer.parseInt(ageStr));
//                    user.setImageUrl(strURL);
//                    dbHelper.addUser(user);
//                    firestore.collection("Users").document(id).update(
//                            "name", nameStr,
//                            "age", Integer.parseInt(ageStr),
//                            "weight", Float.parseFloat(weightStr),
//                            "height", Float.parseFloat(heightStr),
//                            "imageUrl", user.getImageUrl()
//                    );
//                    getActivity()
//                            .getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.main_view, new ProfileFragment())
//                            .disallowAddToBackStack()
//                            .commit();
//                }
//            }
//        });
//    }

    public void initSaveButton(){
        uploadPhoto();
    }

    public void save() {
        String nameStr = name.getText().toString();
        String ageStr = age.getText().toString();
        String weightStr = weight.getText().toString();
        String heightStr = height.getText().toString();

        if (nameStr.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(getActivity(), "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
        } else {
            user.setName(nameStr);
            user.setHeight(Float.parseFloat(heightStr));
            user.setWeight(Float.parseFloat(weightStr));
            user.setAge(Integer.parseInt(ageStr));
            user.setImageUrl(strURL);
            dbHelper.addUser(user);
            firestore.collection("Users").document(id).update(
                    "name", nameStr,
                    "age", Integer.parseInt(ageStr),
                    "weight", Float.parseFloat(weightStr),
                    "height", Float.parseFloat(heightStr),
                    "imageUrl", user.getImageUrl()
            );
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_view, new ProfileFragment())
                    .disallowAddToBackStack()
                    .commit();
        }
    }

    public void initOpenCamera(){
        Button cameraButton = getView().findViewById(R.id.edit_profile_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent.createChooser(intent, "Choose Camera"),REQUEST_CAMERA);
                Log.d("add","Open Camera");
            }
        });
    }

//    public void initCamera(){
//        Button openCamera = getView().findViewById(R.id.)
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent.createChooser(intent,"Choose Camera"),REQUEST_CAMERA);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_CAMERA){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profileImage = getView().findViewById(R.id.edit_profile_avatar);
                profileImage.setImageBitmap(photo);

                selectedImage = getImageUri(getActivity(),photo);
                String realPath = getRealPathFromURI(selectedImage);
                selectedImage = Uri.parse(realPath);
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),inImage,"Title",null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri contentUri){
        Cursor cursor = null;
        try{
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getActivity().getContentResolver().query(contentUri,proj,null,null,null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor!=null){
                cursor.close();
            }
        }
    }

    public void uploadPhoto(){
        final String uid = mAuth.getUid();
        java.util.Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String date = sdf.format(currentTime);

        String fileName = uid + "_" + date;

        final StorageReference profileRef = storageRef.child("Users/" + fileName + ".jpg");

        profileImage = getView().findViewById(R.id.edit_profile_avatar);
        profileImage.setDrawingCacheEnabled(true);
        profileImage.buildDrawingCache();

        Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = profileRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("add","Fail" + e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("add","Upload Sucess" + taskSnapshot.getMetadata());
            }
        });


        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return profileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    String strUrl = downloadUri.toString();
                     strURL = strUrl;
                     Log.d("add", "strURL = " + strURL);
                     save();
                }
            }
        });
    }
}
