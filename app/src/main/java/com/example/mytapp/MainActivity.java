package com.example.mytapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 100;
    //private static final int STORAGE_PERMISSION_CODE = 1;
    public static Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    Button btnCamera;
    Button btnGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamera = findViewById(R.id.buttonCam);
        btnGallery = findViewById(R.id.buttonGallery);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK
                if (view == btnGallery) {
                    showFileChooser();
                }
            }
        });
    }

    private void showFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra("image", thumbnail);
            startActivity(intent);

            /*// CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), thumbnail);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(tempUri));*/

        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();


            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            } catch (IOException e) {
                e.printStackTrace();

            }
        }

    }
}