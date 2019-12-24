package com.example.appwithdeepak;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int INTERNET_PERMISSION = 10;
    private static final int LOAD_IMAGE_INT = 1;

    ImageView imageArea;
    Button compressBtn;
    EditText percentage;
    TextView sizeView;
    TextView extensionView;
    Button getImageBtn;
    Button saveImageBtn;

    Bitmap orignalImage;
    Bitmap decoded;

    Uri sourceUri;
    String sourceUriText;
    String extension = ".jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting reference frome activity_main.xml file
        imageArea = findViewById(R.id.imagedisplay);
        compressBtn = findViewById(R.id.compressbutton);
        getImageBtn = findViewById(R.id.loadImage);
        saveImageBtn = findViewById(R.id.saveImage);
        percentage = findViewById(R.id.percent);
        sizeView = findViewById(R.id.sizedisplay);
        extensionView = findViewById(R.id.extensionDisplay);

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
                    int lengthbmp = imageInByte.length;
//                    sizeView.setText((lengthbmp/8192)+" KB");
//                    sizeView.setText((stream.size()/1024) +" KB");

                    decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(imageInByte));
                    imageArea.setImageBitmap(decoded);
                    sizeView.setText((lengthbmp/1000)+" KB");
                    Toast.makeText(MainActivity.this, "Compressed good job!", Toast.LENGTH_LONG).show();

                }catch (Exception e){

                }
            }
        });

        // getting image onClickListener from gallery
        getImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // asking permission if required
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                        == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, LOAD_IMAGE_INT);
                }else
                requestStoragePermission();

            }
        });

        saveImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                        == PackageManager.PERMISSION_GRANTED)){
                    requestStoragePermission();
                }
                if(!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                        == PackageManager.PERMISSION_GRANTED)){
                    return;
                }
                saveImageBtn.setEnabled(false);

                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsoluteFile().getAbsolutePath() + "/DeepakRaman77/");
                dir.mkdirs();

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                File file = new File(dir, timeStamp + extension);

                try {
                    FileOutputStream out = new FileOutputStream(file);

                    // Compress into png format image from 0% - 100%
                    decoded.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

//                    Toast.makeText(MainActivity.this, "File saved", Toast.LENGTH_LONG).show();
                }

                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Not saved", Toast.LENGTH_LONG).show();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    final Uri contentUri = Uri.fromFile(file);
                    scanIntent.setData(contentUri);
                    sendBroadcast(scanIntent);
                } else {
                    final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                    sendBroadcast(intent);
                }
                saveImageBtn.setEnabled(true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(MainActivity.this, "String test ", Toast.LENGTH_SHORT);
        if(requestCode == LOAD_IMAGE_INT && resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:


                    sourceUri = data.getData();
                    sourceUriText = sourceUri.toString();

                    String filePath ="";
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(sourceUri, filePathColumn, null, null, null);
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        filePath = cursor.getString(columnIndex);
                    }
                    cursor.close();

//                    Toast.makeText(MainActivity.this, "String test " + filePath.substring(filePath.lastIndexOf(".") + 1), Toast.LENGTH_LONG).show();
                    extension = "." + filePath.substring(filePath.lastIndexOf(".") + 1);
                    extensionView.setText(extension + "");
                    try {
                        orignalImage = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(sourceUri));
                        imageArea.setImageBitmap(orignalImage);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(this,"file error", Toast.LENGTH_SHORT);
            }

            }
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, INTERNET_PERMISSION);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, INTERNET_PERMISSION);
        }
    }
}
