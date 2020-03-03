package com.navigatpeer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CheckQuestion extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;



    private DatabaseReference mForumsRef;




    View root;

    private String mKey;
    String desk;



    private FirebaseAuth.AuthStateListener mAuthListener;



    private DatabaseReference mTopicsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkactivequize);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tool = (TextView) toolbar.findViewById(R.id.title);
        tool.setText("Checking Session");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        mAuth = FirebaseAuth.getInstance();
        mTopicsRef = FirebaseDatabase.getInstance().getReference().child("Consultation");
//                .child(getIntent().getStringExtra("forumKey"));

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                if (mAuth.getCurrentUser() == null) {
//                    Toast.makeText(CheckQuestion.this, "NOTHING", Toast.LENGTH_SHORT).show();
//                }
//                else Toast.makeText(CheckQuestion.this, "You", Toast.LENGTH_SHORT).show();
            }
        };
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Checking Session \n Please wait...");
        mProgress.setCancelable(false);



        bayo();


         mKey = getIntent().getStringExtra("forumKey");


        if (!isNetworkAvailable()) {

            Toast.makeText(this, "Please Connect Your Internet", Toast.LENGTH_SHORT).show();
            // Create an Alert Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Set the Alert Dialog Message
            builder.setMessage("Internet Connection Required");
            builder.setCancelable(false);

            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            builder.setPositiveButton("Retry",
                    (dialog, id) -> {
                        // Restart the Activity
                        recreate();
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
       }


    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);



    }

    void bayo() {

        mProgress.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Consultation");
        String fire = mAuth.getCurrentUser().getUid();
        Query dbRefFirstTimeCheck = databaseReference.orderByChild("starter").equalTo(fire);
        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    mProgress.dismiss();
                    Intent topicsIntent = new Intent(CheckQuestion.this, StartConsultation.class);
                    topicsIntent.putExtra("forumKey", mKey);
                    startActivity(topicsIntent);
                }
                else {
                    user();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });

    }

    void user(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Consultation");
        String string = getIntent().getStringExtra("forumKey");
        Query dbRefFirstTimeCheck = databaseReference.orderByChild("ForumKey").equalTo(string);

//        Toast.makeText(this, "" + string, Toast.LENGTH_SHORT).show();
         dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    status();

                }else {
                    mProgress.dismiss();
                    Intent topicsIntent = new Intent(CheckQuestion.this, StartConsultation.class);
                    topicsIntent.putExtra("forumKey", mKey);
                    startActivity(topicsIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });

    }


    void status() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Consultation");
        Query dbRefFirstTimeCheck = databaseReference.orderByChild("ChatStatus").equalTo(true);
        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mProgress.dismiss();
                    Intent topicsIntent = new Intent(CheckQuestion.this, PrivateActivity.class);
                    topicsIntent.putExtra("forumKey", getIntent().getStringExtra("forumKey"));
                    startActivity(topicsIntent);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });

    }


    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());

        return currentDateandTime;
    }

    @Override
    public void onBackPressed() {
        refreshActivity();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home) {

            refreshActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void refreshActivity() {
        Intent i = new Intent(this, Dashboard.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finishAffinity();

    }









}
