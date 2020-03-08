package com.navigatpeer.deaf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.navigatpeer.R;

public class DocConfirmDeaf extends AppCompatActivity {

    private EditText mPhone, mPasswordInput, mEmailInput1, mPasswordInput1;
    private Button mLoginBtn, mLoginBtn1;
    private FirebaseAuth mAuth;

    private ProgressDialog mProgress;
    String mpassword;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ConstraintLayout layout1, layout2;

    private static final String TAG = DocConfirmDeaf.class.getSimpleName();
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.doctclarification);
        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("VerifiedDocDeaf");
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() != null) {
//                    startActivity(new Intent(LoginActivityDeaf.this, DashboardDeaf.class));
//                    finish();
                }
            }
        };


        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Verifying...");
        mProgress.setCancelable(true);
        mPhone = findViewById(R.id.input_login_email);
        mPasswordInput = findViewById(R.id.input_login_password);


        mLoginBtn = findViewById(R.id.btn_login);


        layout1 = findViewById(R.id.layout1);


        mLoginBtn = findViewById(R.id.btn_login);

        mLoginBtn = findViewById(R.id.btn_login);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = mPhone.getText().toString().trim();
                String password = mPasswordInput.getText().toString().trim();

                if (TextUtils.isEmpty(phone)) {
                    mPhone.setError("Phone Required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPasswordInput.setError("Password Required");
                    return;
                }

                if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)) {
                    login(phone, password);
                }
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }


    private void login(String phone, String password) {

        mProgress.show();

        Query dbRefFirstTimeCheck = databaseReference.getRef().orderByChild("phone").equalTo(phone);

        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                     if(dataSnapshot.exists() ) {

                         for (DataSnapshot child: dataSnapshot.getChildren()){
                             //Object object = child.getKey();

                              String mpassword = child.child("password").getValue().toString();
//                             Toast.makeText(DocConfirm.this, "" +key, Toast.LENGTH_SHORT).show();

                              if (mpassword.equals(password)) {

                                  startActivity(new Intent(DocConfirmDeaf.this,  QuestionsActivityDeaf.class));
                                  mProgress.dismiss();

                                  Toast.makeText(DocConfirmDeaf.this, "Verified!", Toast.LENGTH_SHORT).show();
                             } else {
                                  mProgress.dismiss();
                                  Toast.makeText(DocConfirmDeaf.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                             }
                         }

                     }
                 else{
                         mProgress.dismiss();
                     Toast.makeText(DocConfirmDeaf.this, "Phone is not Correct", Toast.LENGTH_SHORT).show();

                 }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(DocConfirmDeaf.this, "Unsuccessful", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        DocConfirmDeaf.super.onBackPressed();

                        finishAffinity();
                    }
                }).create().show();

    }
}
