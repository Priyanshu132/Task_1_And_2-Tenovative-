package com.example.task_1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button button;
   // private Button button1;
    private TextView textView;
    private ImageView imageView;
    private EditText editText;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private Uri pdf;
    private Bitmap image;
    private ProgressDialog progressDialog;
    HashMap<String,String> hashMap = new HashMap<>();
    HashMap<String, Integer> hm = new HashMap<>();
    private static final int PICK_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.upload);

      //  button1 = findViewById(R.id.upload1);
        textView = findViewById(R.id.select);
        imageView = findViewById(R.id.image);
        editText = findViewById(R.id.text);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pdf != null && !editText.getText().toString().isEmpty() && image != null) {
                    uploadFile();
                    uploadImage();

                    Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(MainActivity.this,"Please Select a file...",Toast.LENGTH_SHORT).show();


            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                    selectFile();
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 9 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){

            selectFile();
        }
        else{
            Toast.makeText(MainActivity.this,"Provide Permission...",Toast.LENGTH_SHORT).show();
        }

    }

    private void selectFile(){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent,PICK_FILE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_FILE ) {
            if (resultCode == RESULT_OK) {

                pdf = data.getData();

                textView.setText(data.getData().getLastPathSegment());


            }
        }
        else if(requestCode == 0 && resultCode == RESULT_OK){
             image = (Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(image);
        }else{
            Toast.makeText(MainActivity.this,"Please Select a file...",Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadFile() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading...");
        progressDialog.setProgress(0);
        progressDialog.show();

        StorageReference storageReference = storage.getReference().child("Files/"+UUID.randomUUID().toString());

        storageReference.putFile(pdf).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        hashMap.put("fileLink",String.valueOf(uri));

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,e.getMessage() ,Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                int currentProgress =(int) (100 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });;

//
    }


        private void uploadImage(){
             StorageReference s = storage.getReference().child("Images/"+ UUID.randomUUID().toString());


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG,100,stream);

            byte[] b = stream.toByteArray();
            s.putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri1) {

                        Random rd = new Random();
                        hashMap.put("name",editText.getText().toString());
                        hashMap.put("ImageLink",String.valueOf(uri1));
                        hashMap.put("status","0");
                        hashMap.put("likes",String.valueOf(rd.nextInt(6)+5));
//                        hm.put("status_1",0);
//                        hm.put("likes",78);

                        DatabaseReference databaseReference = database.getReference();
                      //  DatabaseReference databaseReference1 = database.getReference();
                        databaseReference.child(editText.getText().toString()).setValue(hashMap);
                     //   databaseReference1.child(editText.getText().toString()).child("status_likes").setValue(hm);



                        Toast.makeText(MainActivity.this,"File Uploaded....",Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,e.getMessage() ,Toast.LENGTH_SHORT).show();
            }
        });;
        }


    }
