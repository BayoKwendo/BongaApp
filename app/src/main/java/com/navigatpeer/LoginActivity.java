package com.navigatpeer;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.navigatpeer.deaf.AdminLoginDeaf;
import com.navigatpeer.deaf.LoginActivityDeaf;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailInput, mPasswordInput, mEmailInput1, mPasswordInput1;
    private TextView mSignUpTv, mReset, Admin, Noramal, Deaf;
    private Button mLoginBtn, mLoginBtn1;
    private FirebaseAuth mAuth;

    private ProgressDialog mProgress;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ConstraintLayout layout1, layout2;

    private static final String TAG = LoginActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    requestWindowFeature(Window.FEATURE_NO_TITLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setContentView(R.layout.activity_login);
                    // Restore instance state
                    if (savedInstanceState != null) {
                        onRestoreInstanceState(savedInstanceState);
                    }

                    mAuth = FirebaseAuth.getInstance();

                    mAuthListener = firebaseAuth -> {
                        if (mAuth.getCurrentUser() != null) {
//                    startActivity(new Intent(LoginActivityDeaf.this, DashboardDeaf.class));
//                    finish();
            }
        };


        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Authenticating...");
        mProgress.setCancelable(true);
        mEmailInput = findViewById(R.id.input_login_email);
        mPasswordInput = findViewById(R.id.input_login_password);
        mSignUpTv = findViewById(R.id.tv_sign_up);
        mReset = findViewById(R.id.tv_forgot_password);
        Admin = findViewById(R.id.admin);

        Noramal = findViewById(R.id.admin_normal);

        Deaf = findViewById(R.id.admin_deaf);


        mLoginBtn = findViewById(R.id.btn_login);


        layout1 = findViewById(R.id.layout1);


        mLoginBtn = findViewById(R.id.btn_login);

        mLoginBtn = findViewById(R.id.btn_login);
        mSignUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });


        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmailInput.getText().toString().trim();
                String password = mPasswordInput.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (TextUtils.isEmpty(email)) {
                    mEmailInput.setError("Email Required");
                    return;
                }
                if (!(email.matches(emailPattern))){

                    mEmailInput.setError("Email not Valid");

                }


                if (TextUtils.isEmpty(password)) {
                    mPasswordInput.setError("Password Required");
                    return;
                }

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    login(email, password);
                }
            }
        });

        Admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             startActivity(new Intent(LoginActivity.this, LoginActivityDeaf.class));
            }
        });

        Deaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(LoginActivity.this, AdminLoginDeaf.class));
            }
        });

        Noramal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(LoginActivity.this, AdminLogin.class));
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

     }


    private void login(String email, String password) {

        mProgress.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mProgress.dismiss();
                    startActivity(new Intent(LoginActivity.this, Dashboard.class));
                    finish();

                } else {
                    mProgress.dismiss();
                    String s = "Sign In Failed" + task.getException();
                    Toast.makeText(LoginActivity.this, s,
                            Toast.LENGTH_SHORT).show();
                }
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
                        LoginActivity.super.onBackPressed();

                        finishAffinity();
                    }
                }).create().show();

    }
}
