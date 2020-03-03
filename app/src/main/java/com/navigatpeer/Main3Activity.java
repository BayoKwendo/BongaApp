package com.navigatpeer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Main3Activity extends AppCompatActivity {
    FirebaseAuth mauth;
    private static final String TAG = "MyActivity";

    String country_code = "+254";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    MaterialDialog process_dialog;



    PhoneAuthProvider.OnVerificationStateChangedCallbacks  mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        mauth = FirebaseAuth.getInstance();

        sendCode();
       }
        public void sendCode() {

            String phoneNumber = country_code.toString() + "0717629732";
            if (phoneNumber.length() > 7) {

                setUpVerificatonCallbacks();


                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        this,               // Activity (for callback binding)
                        verificationCallbacks);

                //Set processing indicators
                MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                        .title("Sending code")
                        .content("Please wait")
                        .progress(true, 0);

                process_dialog = builder.build();
                process_dialog.show();
            }else {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            }
        }



    private void setUpVerificatonCallbacks() {

        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential) {

                        Toast.makeText(Main3Activity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                        //   signoutButton.setEnabled(true);
                        //   statusText.setText("Signed In");

//                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {


                        Log.d(TAG, " BAYO" + e);


                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Toast.makeText(Main3Activity.this, "Invalid credential", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded


                            Toast.makeText(Main3Activity.this, "Limit Reached Try Again In a few Hours", Toast.LENGTH_SHORT).show();
                        }

                        process_dialog.dismiss();

                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
//
//                        phoneVerificationId = verificationId;
//                        resendToken = token;
//                        verifyButton.setVisibility(View.VISIBLE);
//                        codeText.setVisibility(View.VISIBLE);
//                        sendButton.setEnabled(false);
//                        resendButton.setVisibility(View.VISIBLE);

                        process_dialog.dismiss();
                    }
                };
    }
    }

