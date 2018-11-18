package com.calcurie.calcurie;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddMenuFragment extends Fragment {

    final int OPEN_CAMERA = 1;
    private FirebaseFirestore db;
    Uri selectedImage;
    String path;
    FirebaseStorage fbStorage = FirebaseStorage.getInstance();
    StorageReference storageRef = fbStorage.getReference();
    ImageView foodImage;
    StorageReference foodsRef = storageRef.child("Foods/hello.jpg");

    public AddMenuFragment() {
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_menu, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initBackButton();
        initOpenCamera();
        initAddFoodButton();
    }

    public void initBackButton(){
        Button backButton = (Button) getView().findViewById(R.id.add_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    public void initOpenCamera(){
        ImageButton cameraButton = (ImageButton) getView().findViewById(R.id.add_camera_btn);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent.createChooser(intent, "Choose Camera"),OPEN_CAMERA);
                Log.d("add","Open Camera");
            }
        });

        ImageView image = (ImageView) getView().findViewById(R.id.food_img);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent.createChooser(intent, "Choose Camera"),OPEN_CAMERA);
                Log.d("add","Open Camera");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(resultCode == Activity.RESULT_OK){
            if(requestCode == OPEN_CAMERA){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                foodImage = (ImageView) getView().findViewById(R.id.food_img);
                foodImage.setImageBitmap(photo);


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
            if (cursor != null){
                cursor.close();
            }
        }
    }

    public void uploadPhoto(){
        java.util.Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf_2 = new SimpleDateFormat("hh:mm:ss");

        String dateNow = sdf_1.format(currentTime);
        String timeNow = sdf_2.format(currentTime);

        String dateTimeNow = dateNow + " " + timeNow;

        final StorageReference foodsRef = storageRef.child("Foods/" + dateTimeNow + ".jpg");

        foodImage = (ImageView) getView().findViewById(R.id.food_img);
        foodImage.setDrawingCacheEnabled(true);
        foodImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) foodImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = foodsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("add","Fail"+e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("add", "Upload Success"+taskSnapshot.getMetadata());
            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return foodsRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    Log.d("add",""+downloadUri);
                    addFoodToDB(downloadUri.toString());
                }else{
                    Log.d("add","fail");
                }
            }
        });
    }

    public void initAddFoodButton(){
        Button addFoodBtn = getView().findViewById(R.id.add_food_btn);
        addFoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto();
            }
        });
    }

    public void addFoodToDB(String url) {
        EditText foodNameText = getView().findViewById(R.id.fragment_add_menu_food_name);
        EditText foodDetailText = getView().findViewById(R.id.fragment_add_menu_food_detail);
        EditText foodCaloriesText = getView().findViewById(R.id.fragment_add_menu_food_calories);
        String foodNameStr = foodNameText.getText().toString();
        String foodDetailStr = foodDetailText.getText().toString();
        String foodCaloriesStr = foodCaloriesText.getText().toString();
        Map<String, String> data = new HashMap<>();
        data.put("name", foodNameStr);
        data.put("detail", foodDetailStr);
        data.put("calories", foodCaloriesStr);
        data.put("image_url", url);
        db.collection("Foods").document().set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("add", "DocumentSnapshot successfully written!");
                Toast.makeText(getContext(), "บันทึกสำเร็จ", Toast.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new SelectFoodFragment())
                        .commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("add", "Error writing document", e);
                Toast.makeText(getContext(), "บันทึกล้มเหลว", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
