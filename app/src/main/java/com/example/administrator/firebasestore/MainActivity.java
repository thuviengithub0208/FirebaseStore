package com.example.administrator.firebasestore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.R.attr.bitmap;

public class MainActivity extends AppCompatActivity {

    Button btnchonhinh, btnupload;
    ImageView imgphoto;
    int Request_code_photo = 123;
    Uri uri ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anahxa();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        final StorageReference storageRef = storage.getReference();
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path=uri.getPath();
                String filename=path.substring(path.lastIndexOf("/")+1);
                StorageReference mountainsRef = storageRef.child("photo" +filename );
                // Get the data from an ImageView as bytes
//                imgphoto.setDrawingCacheEnabled(true);
//                imgphoto.buildDrawingCache();
//                Bitmap bitmap = imgphoto.getDrawingCache();
                BitmapDrawable bitmapDrawable = (BitmapDrawable) imgphoto.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(MainActivity.this, "Upload Fail", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.d("BBB","Link dow load " + downloadUrl);
                        Toast.makeText(MainActivity.this, "Upload success", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        btnchonhinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Request_code_photo);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Request_code_photo && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                imgphoto.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void anahxa() {
        btnchonhinh = (Button) findViewById(R.id.buttonchonhinh);
        btnupload = (Button) findViewById(R.id.buttonupload);
        imgphoto = (ImageView) findViewById(R.id.imageviewhinh);
    }
}
