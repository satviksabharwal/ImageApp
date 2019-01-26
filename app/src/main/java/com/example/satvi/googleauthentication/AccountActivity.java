package com.example.satvi.googleauthentication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AccountActivity extends AppCompatActivity {
    private Button logoutButton;
    TextView  title,description;
    ImageView picture;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabaseReference;
    private ListView listView;
    FirebaseListAdapter adapter;
    SharedPreferences sharedPreferences;
    String savedName,savedEmailId,savedIdToken,savedUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        sharedPreferences = getApplicationContext().getSharedPreferences("com.example.satvi.googleauthentication", Context.MODE_PRIVATE);
        savedName = sharedPreferences.getString("DisplayName", " ");
        savedIdToken = sharedPreferences.getString("idToken", " ");
        savedEmailId = sharedPreferences.getString("EmailId", " ");
        savedUserId = sharedPreferences.getString("UserId", " ");


        logoutButton = (Button) findViewById(R.id.logoutButton);
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(AccountActivity.this,MainActivity.class));
                    finish();

                }
            }
        };
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });

        // adding element in list
        listView = findViewById(R.id.listView);
        Query query = FirebaseDatabase.getInstance().getReference("EmailId").child(savedEmailId);
        FirebaseListOptions<Upload> options = new FirebaseListOptions.Builder<Upload>()
                .setLayout(R.layout.itemrow)
                .setQuery(query, Upload.class)
                .build();

        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(View v, final Object model, int position) {

                title = v.findViewById(R.id.titleTextView);
                picture = v.findViewById(R.id.pictureImageView);
                description = v.findViewById(R.id.descriptionTextView);


                Upload downloadData = (Upload) model;
                title.setText(downloadData.getTitle());
                Picasso.get().load(downloadData.getImageUrl()).fit().into(picture);
                description.setText(downloadData.getDescription());


            }
        };
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final Upload up = (Upload) parent.getItemAtPosition(position);
                final String j =  up.getUploadId();

                new AlertDialog.Builder(AccountActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(AccountActivity.this,ImageUpload.class);

                                intent.putExtra("title",up.getTitle());
                                intent.putExtra("imageUrl",up.getImageUrl());
                                intent.putExtra("description",up.getDescription());
                                intent.putExtra("uploadid",up.getUploadId());
                                startActivity(intent);
                                finish();
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("EmailId");
                                Query deleteQuery = ref.child(savedEmailId).child(up.getUploadId()).equalTo(j);

                                deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        dataSnapshot.getRef().child("title").removeValue();
                                        dataSnapshot.getRef().child("imageUrl").removeValue();
                                        dataSnapshot.getRef().child("description").removeValue();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("AccountActivity", "onCancelled", databaseError.toException());
                                    }
                                });

                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("EmailId");
                                Query deleteQuery = ref.child(savedEmailId).child(up.getUploadId()).equalTo(j);

                                deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        dataSnapshot.getRef().removeValue();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("AccountActivity", "onCancelled", databaseError.toException());
                                    }
                                });

                            }
                        }).show();


                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
        adapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }

}
