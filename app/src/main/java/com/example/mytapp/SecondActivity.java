package com.example.mytapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    ImageView imageView;
    Button btn2;
    Button btn3;
    TextView tv;
    private File photo;
    Bitmap bitmap;
    Bitmap imageCam;
    private GraphicOverlay mGraphicOverlay;
    // Max width (portrait mode)
    private Integer mImageMaxWidth;
    // Max height (portrait mode)
    private Integer mImageMaxHeight;
    Uri uri;


    private String KEY_IMAGE = "image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);


        mGraphicOverlay = findViewById(R.id.graphic_overlay);
        imageView = findViewById(R.id.imageView);

        bitmap = MainActivity.bitmap;
        //getScaledBitmap(, 360, 360);
        imageView.setImageBitmap(bitmap);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imageCam = (Bitmap) extras.get("image");
            if (imageCam != null) {
                imageView.setImageBitmap(imageCam);
            }
        }


        tv = findViewById(R.id.textView);
        //btn1 = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);

        //photo = getPhotoFile();


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runTextRecognitionCam();

            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runTextRecognitionGallery();

            }
        });

    }

    private void runTextRecognitionCam() {
        if (imageView.getDrawable() != null) {
            FirebaseVisionImage imageCamera = FirebaseVisionImage.fromBitmap(imageCam);
            FirebaseVisionTextRecognizer recognizerCamera = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            btn2.setEnabled(false);
            recognizerCamera.processImage(imageCamera)
                    .addOnSuccessListener(
                            new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText texts) {
                                    btn2.setEnabled(true);
                                    processTextRecognitionResult(texts);
                                    btn3.setEnabled(false);
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    btn2.setEnabled(true);
                                    e.printStackTrace();
                                }
                            });
        }
    }

    private void runTextRecognitionGallery() {

        FirebaseVisionImage imageGallery = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer recognizerGallery = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        recognizerGallery.processImage(imageGallery)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                btn3.setEnabled(true);
                                processTextRecognitionResult(texts);
                                btn2.setEnabled(false);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                btn3.setEnabled(true);
                                e.printStackTrace();
                            }
                        });

    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            showToast("No text found");
            return;
        }
        mGraphicOverlay.clear();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                    mGraphicOverlay.add(textGraphic);

                }
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private String getPhotoName(){
        return "IMG_" + SystemClock.currentThreadTimeMillis() + ".jpg";
    }


    private Bitmap getScaledBitmap(String path, int destWidth, int destHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, options);
    }


    /*private Bitmap getScaledBitmap(String path){
        Point size = new Point();

        getWindowManager().getDefaultDisplay()
                .getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }*/



    //get the scaled bitmap image with the new width and height showed on the screen
    /*private Bitmap getScaledBitmap (Bitmap bitmapImage){

        //width and height of original image
        final int imageWidth = bitmapImage.getWidth();
        final int imageHeight = bitmapImage.getHeight();

        //width and height of the imageView
        final int imageViewWidth  = imageView.getMeasuredWidth();
        final int imageViewHeight = imageView.getMeasuredHeight();

        final int scaledWidth , scaledHeight;


        if (imageWidth*imageViewHeight <= imageHeight*imageViewWidth) {

            //rescaled width and height of image within ImageView
            scaledWidth = (imageWidth*imageViewHeight)/imageHeight;
            scaledHeight = imageViewHeight;
        }
        else {
            //rescaled width and height of image within ImageView
            scaledWidth = imageViewWidth;
            scaledHeight = (imageHeight*imageViewWidth)/imageWidth;
        }


        return Bitmap.createScaledBitmap(bitmapImage, scaledWidth, scaledHeight, true);
    }*/
}