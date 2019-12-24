package com.example.appwithdeepak;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    ImageView imageArea;
    Button compressBtn;
    EditText percentage;
    TextView sizeView;

    Bitmap orignalImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting reference frome activity_main.xml file
        imageArea = findViewById(R.id.imagedisplay);
        compressBtn = findViewById(R.id.compressbutton);
        percentage = findViewById(R.id.percent);
        sizeView = findViewById(R.id.sizedisplay);

        // getting an butterfly.jpg image
        orignalImage = BitmapFactory.decodeResource(getResources(),R.drawable.butterfly);


        // setting onClicListener
        compressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Bitmap bitmap = orignalImage;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, Integer.parseInt(percentage.getText().toString()), stream);
                    byte[] imageInByte = stream.toByteArray();
                    long lengthbmp = imageInByte.length;
                    sizeView.setText((lengthbmp/ (long)8192)+" KB");

                    Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(imageInByte));
                    imageArea.setImageBitmap(decoded);
                    Toast.makeText(MainActivity.this, "Compressed good job!", Toast.LENGTH_LONG).show();

                }catch (Exception e){

                }
            }
        });
    }
}
