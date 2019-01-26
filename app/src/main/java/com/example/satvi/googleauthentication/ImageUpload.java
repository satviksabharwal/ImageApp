package com.example.satvi.googleauthentication;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import com.google.android.gms.tasks.OnFailureListener;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageUpload extends AppCompatActivity {

    private static final int CHOOSE_IMAGE=1;
    private Button mButtonUpload,mShowUploads;
    EditText mEditTextFileName,descriptionView;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private StorageReference mStorageRef,imageReference;
    private DatabaseReference mDatabaseRef;

    SharedPreferences sharedPreferences;
    String savedName,savedEmailId,savedIdToken,savedUserId, getUri;

    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        sharedPreferences = getApplicationContext().getSharedPreferences("com.example.satvi.googleauthentication", Context.MODE_PRIVATE);
        savedName = sharedPreferences.getString("DisplayName", " ");
        savedIdToken = sharedPreferences.getString("idToken", " ");
        savedEmailId = sharedPreferences.getString("EmailId", " ");
        savedUserId = sharedPreferences.getString("UserId", " ");
        if (savedName==null && savedIdToken==null && savedEmailId==null) {
            Intent intent = new Intent(ImageUpload.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {

            descriptionView = findViewById(R.id.descriptionView);
            mButtonUpload = findViewById(R.id.button_upload);
            mShowUploads = findViewById(R.id.text_view_show_uploads);
            mEditTextFileName = findViewById(R.id.edit_text_file_name);
            mImageView = findViewById(R.id.image_view);
            mProgressBar = findViewById(R.id.progress_bar);
            mStorageRef = FirebaseStorage.getInstance().getReference("EmailId/");
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("EmailId/");
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateImg();
                }
            });

            mButtonUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    uploadImageFile();

                }
            });

            mShowUploads.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openImagesActivity();

                }
            });

        }
    }


    private void updateImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            mImageUri = data.getData();
            Picasso.get().load(mImageUri).fit().into(mImageView);

        }
    }


    private String getImageFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void uploadImageFile(){

        if (mImageUri != null) {

            imageReference = mStorageRef.child(System.currentTimeMillis()+"."+getImageFileExtension(mImageUri));
            imageReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                    mImageView.setImageDrawable(null);
                                    mEditTextFileName.getText().clear();
                                    descriptionView.getText().clear();
                                }
                            },600);
                            Toast.makeText(ImageUpload.this, "Upload Successful!!", Toast.LENGTH_SHORT).show();

                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());

                                Uri url = uri.getResult();
                                Upload upload = new Upload(mEditTextFileName.getText().toString().trim(), url.toString() ,descriptionView.getText().toString().trim());
                                String uploadId = mDatabaseRef.push().getKey();
                                mDatabaseRef.child(savedEmailId).child(uploadId).setValue(upload);




   }})
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(ImageUpload.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            mProgressBar.incrementProgressBy((int)progress);
                        }
                    });
        }else{
            Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show();
        }


    }

    private void openImagesActivity(){
        Intent intent = new Intent(getApplicationContext(),AccountActivity.class);
        startActivity(intent);

    }


    /*private void uploadImageToFirebase(InputStream rInputStream) {
        sharedPref = getPreferences(MODE_PRIVATE);
        String UserId = sharedPref.getString("firebasekey", "1");
        StorageReference storageRef =
                FirebaseStorage.getInstance().getReference();
        StorageReference mountainsRef = storageRef.child("uploads/"+UserId+System.currentTimeMillis());
        // running the task
        UploadTask uploadTask = mountainsRef.putStream(rInputStream);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // show error
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // do ui stuff
            }
        });
    }*/

}
