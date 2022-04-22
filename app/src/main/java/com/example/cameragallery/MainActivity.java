package com.example.cameragallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    Button btnCamera,btnGallery,btnSave;
    TextInputEditText imageDescription;
    ImageView imageView;
    Uri imagePath;

    public static final int CAMERA_REQUEST_CODE=100;
    public static final int STORAGE_REQUEST_CODE=200;
    public static final int IMAGE_GALLERY_REQUEST_CODE=300;
    public static final int IMAGE_CAMERA_REQUEST_CODE=400;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCamera =findViewById(R.id.btnCamera);
        btnGallery =findViewById(R.id.btnGallery);
        btnSave =findViewById(R.id.btnSave);
        imageDescription =findViewById(R.id.imageDescription);
        imageView=findViewById(R.id.imageView);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkGalleryPermission()){
                    pickFromGallery();
                }
                else{
                    requestGalleryPermission();
                }
            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCameraPermission()){
                    pickFromCamera();
                }
                else{
                    requestCameraPermission();
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooser();
            }
        });
    }

    private void showChooser() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        String [] options={"From Camera","From Gallery"};
        builder.setTitle("Pick Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    //
                    if (checkCameraPermission()){
                        pickFromCamera();

                    }
                    else {
                        requestCameraPermission();
                    }
                }
                else{
                    if (checkGalleryPermission()){
                        pickFromGallery();

                    }
                    else {
                        requestCameraPermission();
                    }

                }
            }
        });
        builder.show();
    }

    private void requestCameraPermission() {
        String [] cameraPersmissions={Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(MainActivity.this,cameraPersmissions,CAMERA_REQUEST_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_image title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_image descriptoion");

        imagePath=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imagePath);
        startActivityForResult(intent,IMAGE_CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        String cameraPermission=Manifest.permission.CAMERA;
        String storagePermission=Manifest.permission.WRITE_EXTERNAL_STORAGE;
        return ContextCompat.checkSelfPermission(this,cameraPermission)==PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this,storagePermission)==PackageManager.PERMISSION_GRANTED;
    }

    private void requestGalleryPermission() {
        String[] galleryPermissions={Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this,galleryPermissions,STORAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK && data!=null){
            if (requestCode==IMAGE_GALLERY_REQUEST_CODE){
                imagePath=data.getData();
                imageView.setImageURI(imagePath);
            }
            else if (requestCode==IMAGE_CAMERA_REQUEST_CODE){
                imageView.setImageURI(imagePath);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean storagePermAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storagePermAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Storage permission is required",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            case CAMERA_REQUEST_CODE:
                if (requestCode == CAMERA_REQUEST_CODE && grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Storage permission is required",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    private void pickFromGallery() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_GALLERY_REQUEST_CODE);

    }

    private boolean checkGalleryPermission() {
        String gallerryPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        return ContextCompat.checkSelfPermission(this,
                gallerryPermission) == PackageManager.PERMISSION_GRANTED;
    }

}