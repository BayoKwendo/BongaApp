package com.navigatpeer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.navigatpeer.R;
import com.navigatpeer.deaf.TextViewDatePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignupActivity extends AppCompatActivity {

    private EditText mUsernameInput, mEmailInput, mPasswordInput, mdate, mConfPasswordInput, mPhoneInput;
    private Button mCreateBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressDialog mProgress, selectCheck;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    private StorageReference refstorage;
    private final int PICK_IMAGE_REQUEST = 71;

    String username, email, password, confPass ,phone ,date ;

    ImageView image;

       LinearLayout mlinear1, mlinear2;



    private static final int RC_SIGN_IN = 123;

    Button choosebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        mUsernameInput = findViewById(R.id.input_username);
        mEmailInput = findViewById(R.id.input_sign_up_email_address);
        mPasswordInput = findViewById(R.id.input_sign_up_password);
        mPhoneInput = findViewById(R.id.input_phone_number);
        mlinear1 = findViewById(R.id.linear1);
        mdate = findViewById(R.id.indate);

        image = findViewById(R.id.imgView);


        choosebtn = findViewById(R.id.btnChoose);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        TextViewDatePicker editTextDatePicker = new TextViewDatePicker(this, mdate);


        mConfPasswordInput = findViewById(R.id.input_confirm_password);
        mCreateBtn = findViewById(R.id.btn_sign_up);
        TextView back_to_login = findViewById(R.id.login);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Signing up...");
        mProgress.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() != null) {

                    mProgress.dismiss();

//                    startActivity(new Intent(LoginActivityDeaf.this, DashboardDeaf.class));
//                    finish();
                }
            }
        };
        mAuth = FirebaseAuth.getInstance();
        back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        choosebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 username = mUsernameInput.getText().toString().trim();
                 email = mEmailInput.getText().toString().trim();
                 password = mPasswordInput.getText().toString().trim();
                 confPass = mConfPasswordInput.getText().toString().trim();
                 phone = mPhoneInput.getText().toString().trim();
                 date = mdate.getText().toString().trim();
                 String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                image = findViewById(R.id.imgView);

                if (TextUtils.isEmpty(username)) {
                    mUsernameInput.setError("This field is required");
                    return;
                }
                if (username.length() <= 4) {
                    mUsernameInput.setError("Username atleast four character ");
                    return;
                }

                if (image.getVisibility() == View.GONE) {
                    Toast.makeText(SignupActivity.this, "Please add your profile picture", Toast.LENGTH_SHORT).show();
                    // Its visible
                }
                if (TextUtils.isEmpty(email)) {
                    mEmailInput.setError("This field is required");
                    return;
                }
                if (TextUtils.isEmpty(date)) {
                    mEmailInput.setError("This field is required");
                    return;
                }
                if (!(email.matches(emailPattern))) {

                    mEmailInput.setError("Email not Valid");


                }


                if (TextUtils.isEmpty(password)) {
                    mPasswordInput.setError("This field is required");
                    return;
                }

                if (TextUtils.isEmpty(confPass)) {
                    mConfPasswordInput.setError("This field is required");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    mPhoneInput.setError("This field is required");
                    return;
                }

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(phone) && image.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(password)
                        && !TextUtils.isEmpty(confPass)) {
                    if (!confPass.equals(password)) {
                        Toast.makeText(SignupActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
                    } else {

                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setAvailableProviders(
                                                Arrays.asList(
                                                        new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
                                                ))
                                        .build(),
                                RC_SIGN_IN);

                    }
                }

            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                createUser(username, email,date, phone, password);
                Toast.makeText(this, "OTP verification success", Toast.LENGTH_SHORT).show();
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.e("Login","Login canceled by User");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.e("Login","No Internet Connection");
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Log.e("Login","Unknown Error");
                    return;
                }
            }
            Log.e("Login","Unknown sign in response");
        }
        if(requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null ) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void createUser(final String username, String email,String date, final String phone, String password) {

        mProgress.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username).build();

                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                String fire = mAuth.getCurrentUser().getUid();



                                Map userMap = new HashMap();
                                uploadImage();
                                userMap.put("phoneNumber", phone);
                                userMap.put("userName", username);
                                userMap.put("Date of Birth", date);
                                userMap.put("UserId", fire);

                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(mAuth.getCurrentUser().getUid()).setValue(userMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    Toast.makeText(SignupActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    mProgress.dismiss();
                                                    Toast.makeText(SignupActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            } else {
                                mProgress.dismiss();
                                Toast.makeText(SignupActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    String s = "Sign up Failed" + task.getException();
                    Toast.makeText(SignupActivity.this, s,
                            Toast.LENGTH_SHORT).show();


                    mProgress.dismiss();
//                    Toast.makeText(SignupActivityDeaf.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }




    void uploadImage(){

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Saving...Please Wait");
            progressDialog.show();

            StorageReference ref = storageReference.child("CoverPage/"+ UUID.randomUUID().toString());

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = ref.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //and you can convert it to string like this:
                    final String sdownload_url = downloadUrl.toString();



//                Log.d(TAG, "onSuccess:" + sdownload_url);
//                Toast.makeText(MainActivity.this, "" + sdownload_url, Toast.LENGTH_SHORT).show();
//
//
                    String fire = mAuth.getCurrentUser().getUid();
                    Query query =  FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("UserId").equalTo(fire);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childdata : dataSnapshot.getChildren()) {
                                childdata.getRef().child("URL").setValue(sdownload_url);
                            }

                            Intent mainIntent = new Intent(SignupActivity.this, Dashboard.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            finish();


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            throw databaseError.toException(); // never ignore errors
                        }
                    });
                    return;
                }
            });

        }

    }

    void chooseImage(){
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
        image.setVisibility(View.VISIBLE);
    }


    void selectCheck() {
        // Create an Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the Alert Dialog Message
        builder.setMessage("Please Select Your Picture");
        builder.setCancelable(true);


        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {

                        dialog.dismiss();
                        // Restart the Activity
                        chooseImage();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}


//package com.navigatpeer;
//
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import com.afollestad.materialdialogs.MaterialDialog;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.FirebaseException;
//import com.google.firebase.FirebaseTooManyRequestsException;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.PhoneAuthCredential;
//import com.google.firebase.auth.PhoneAuthProvider;
//import com.google.firebase.auth.UserProfileChangeRequest;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//import com.navigatpeer.deaf.TextViewDatePicker;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//public class SignupActivity extends AppCompatActivity {
//
//    private static final String TAG = "MyActivity";
//    private final int PICK_IMAGE_REQUEST = 71;
//    String country_code = "+254";
//    MaterialDialog process_dialog;
//    FirebaseStorage storage;
//    StorageReference storageReference;
//    ImageView image;
//    PhoneAuthProvider phoneprovider;
//    TextView mBack;
//    Button choosebtn;
//    LinearLayout mlinear1, mlinear2;
//    private EditText mUsernameInput, mEmailInput, mPasswordInput, mdate, mConfPasswordInput, mPhoneInput, mVerifyInput;
//    private Button mCreateBtn, mCode, btnResend;
//    private FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
//    private ProgressDialog mProgress, selectCheck;
//    private Uri filePath;
//    private StorageReference refstorage;
//    private String phoneVerificationId;
//    private PhoneAuthProvider.ForceResendingToken resendToken;
//    String username ;
//    String email ;
//    String password ;
//    String confPass ;
//    String phone ;
//    String date;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_signup);
//
//        mUsernameInput = findViewById(R.id.input_username);
//        mEmailInput = findViewById(R.id.input_sign_up_email_address);
//        mPasswordInput = findViewById(R.id.input_sign_up_password);
//        mCode = findViewById(R.id.btn_code);
//        mVerifyInput = findViewById(R.id.phone_code);
//        mBack = findViewById(R.id.back);
//        btnResend = findViewById(R.id.btn_resend);
//        mlinear1 = findViewById(R.id.linear1);
//        mlinear2 = findViewById(R.id.linear2);
//        mPhoneInput = findViewById(R.id.input_phone_number);
//        mdate = findViewById(R.id.indate);
//        image = findViewById(R.id.imgView);
//        choosebtn = findViewById(R.id.btnChoose);
//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();
//        TextViewDatePicker editTextDatePicker = new TextViewDatePicker(this, mdate);
//        mConfPasswordInput = findViewById(R.id.input_confirm_password);
//        mCreateBtn = findViewById(R.id.btn_sign_up);
//        TextView back_to_login = findViewById(R.id.login);
//        mProgress = new ProgressDialog(this);
//        mProgress.setMessage("Signing up...");
//        mProgress.setCancelable(false);
//        mAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                if (mAuth.getCurrentUser() != null) {
//
//                    mProgress.dismiss();
//
////                    startActivity(new Intent(LoginActivityDeaf.this, DashboardDeaf.class));
////                    finish();
//                }
//            }
//        };
//
//
//        mBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mlinear1.setVisibility(View.VISIBLE);
//                mlinear2.setVisibility(View.GONE);
//            }
//        });
//
//
//        mAuth = FirebaseAuth.getInstance();
//        back_to_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        choosebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectCheck();
//            }
//        });
//        mCreateBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                      username = mUsernameInput.getText().toString().trim();
//                      email = mEmailInput.getText().toString().trim();
//                      password = mPasswordInput.getText().toString().trim();
//                      confPass = mConfPasswordInput.getText().toString().trim();
//                      phone = mPhoneInput.getText().toString().trim();
//                      date = mdate.getText().toString().trim();
//                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//                image = findViewById(R.id.imgView);
//
//                if (TextUtils.isEmpty(username)) {
//                    mUsernameInput.setError("This field is required");
//                    return;
//                }
//                if (username.length() <= 4) {
//                    mUsernameInput.setError("Username atleast four character ");
//                    return;
//                }
//
//                if (image.getVisibility() == View.GONE) {
//                    Toast.makeText(SignupActivity.this, "Please add your profile picture", Toast.LENGTH_SHORT).show();
//                    // Its visible
//                }
//                if (TextUtils.isEmpty(email)) {
//                    mEmailInput.setError("This field is required");
//                    return;
//                }
//                if (TextUtils.isEmpty(date)) {
//                    mEmailInput.setError("This field is required");
//                    return;
//                }
//                if (!(email.matches(emailPattern))) {
//
//                    mEmailInput.setError("Email not Valid");
//
//
//                }
//
//
//                if (TextUtils.isEmpty(password)) {
//                    mPasswordInput.setError("This field is required");
//                    return;
//                }
//
//                if (TextUtils.isEmpty(confPass)) {
//                    mConfPasswordInput.setError("This field is required");
//                    return;
//                }
//
//                if (TextUtils.isEmpty(phone)) {
//                    mPhoneInput.setError("This field is required");
//                    return;
//                }
//
//                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(phone) && image.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(password)
//                        && !TextUtils.isEmpty(confPass)) {
//                    if (!confPass.equals(password)) {
//                        Toast.makeText(SignupActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
//                    } else {
//                        mlinear1.setVisibility(View.GONE);
//                        mlinear2.setVisibility(View.VISIBLE);
//                        sendCode();
//
//                        //createUser(username, email,date, phone, password);
//                    }
//                }
//
//            }
//        });
//
//
//        mCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Soon
//
//               String mverifyInput = mVerifyInput.getText().toString().trim();
//                if (TextUtils.isEmpty(mverifyInput)) {
//                    mVerifyInput.setFocusable(true);
//                    mVerifyInput.setError("This field is required");
//                    return;
//                }else {
//                    verifyCode();
//                }
//            }
//        });
//
//        btnResend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                resendCode();
//                //Soon
//            }
//        });
//
//    }
//
//
//    public void sendCode() {
//
//        String phoneNumber = country_code + mPhoneInput.getText().toString();
//        if (phoneNumber.length() > 7) {
//
//            setUpVerificatonCallbacks();
//
//
//            PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                    phoneNumber,        // Phone number to verify
//                    60,                 // Timeout duration
//                    TimeUnit.SECONDS,   // Unit of timeout
//                    this,               // Activity (for callback binding)
//                    verificationCallbacks);
//
//            //Set processing indicators
//            MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
//                    .title("Sending code")
//                    .content("Please wait")
//                    .progress(true, 0);
//
//            process_dialog = builder.build();
//            process_dialog.show();
//        } else {
//            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
//        }
//    }
//    public void resendCode() {
//
//        String phoneNumber =  country_code + mPhoneInput.getText().toString().trim();
//
//        setUpVerificatonCallbacks();
//
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,
//                60,
//                TimeUnit.SECONDS,
//                this,
//                verificationCallbacks,
//                resendToken);
//        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
//                .title("Sending code")
//                .content("Please wait")
//                .progress(true, 0);
//
//        process_dialog = builder.build();
//        process_dialog.show();
//    }
//
//
//
//    public void verifyCode() {
//
//        String code = mVerifyInput.getText().toString();
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, code);
//        signInWithPhoneAuthCredential(credential);
//        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
//                .title("Verifying code")
//                .content("Please wait")
//                .progress(true, 0);
//        process_dialog = builder.build();
//        process_dialog.show();
//
//    }
//    private void setUpVerificatonCallbacks() {
//
//        verificationCallbacks =
//                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//                    @Override
//                    public void onVerificationCompleted(
//                            PhoneAuthCredential credential) {
//                        signInWithPhoneAuthCredential(credential);
//
//                        mVerifyInput.setText("");
////                        Toast.makeText(SignupActivityDeaf.this, "SUCCESS", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onVerificationFailed(FirebaseException e) {
//
//                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                            // Invalid request
//                            Toast.makeText(SignupActivity.this, "Invalid credential", Toast.LENGTH_SHORT).show();
//                        } else if (e instanceof FirebaseTooManyRequestsException) {
//                            // SMS quota exceeded
//
//                            Toast.makeText(SignupActivity.this, "Limit Reached Try Again In 2 Hours time", Toast.LENGTH_SHORT).show();
//                        } else
//                            Toast.makeText(SignupActivity.this, "FAILED! Check your connection", Toast.LENGTH_SHORT).show();
//                        process_dialog.dismiss();
//
//                    }
//
//                    @Override
//                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
////
////                        phoneVerificationId = verificationId;
////                        resendToken = token;
////                        verifyButton.setVisibility(View.VISIBLE);
////                        codeText.setVisibility(View.VISIBLE);
////                        sendButton.setEnabled(false);
////                        resendButton.setVisibility(View.VISIBLE);
//
//                        process_dialog.dismiss();
//                    }
//                };
//
//
//    }
//
//
//
//    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            createUser(  username, email, date,
//                                    phone,  password);
//
//                            process_dialog.dismiss();
//
//                        } else {
//                            if (task.getException() instanceof
//                                    FirebaseAuthInvalidCredentialsException) {
//                                // The verification code entered was invalid
//                            }
//                        }
//                    }
//                });
//    }
//
//
//
//
//
//    private void createUser(final String username, String email, String date, final String phone, String password) {
//
//        mProgress.show();
//
//        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    FirebaseUser user = mAuth.getCurrentUser();
//
//                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                            .setDisplayName(username).build();
//
//                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//
//                                String fire = mAuth.getCurrentUser().getUid();
//
//                                Toast.makeText(SignupActivity.this, "" + fire, Toast.LENGTH_SHORT).show();
//
//
//                                Map userMap = new HashMap();
//                                uploadImage();
//                                userMap.put("phoneNumber", phone);
//                                userMap.put("userName", username);
//                                userMap.put("Date of Birth", date);
//                                userMap.put("UserId", fire);
//
//                                FirebaseDatabase.getInstance().getReference().child("Users")
//                                        .child(mAuth.getCurrentUser().getUid()).setValue(userMap)
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
//
//                                                    Toast.makeText(SignupActivity.this, "Successful", Toast.LENGTH_SHORT).show();
//
//                                                } else {
//                                                    mProgress.dismiss();
//                                                    Toast.makeText(SignupActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
//                                                }
//                                            }
//                                        });
//                            } else {
//                                mProgress.dismiss();
//                                Toast.makeText(SignupActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//                } else {
//                    String s = "Sign up Failed" + task.getException();
//                    Toast.makeText(SignupActivity.this, s,
//                            Toast.LENGTH_SHORT).show();
//
//
//                    mProgress.dismiss();
////                    Toast.makeText(SignupActivityDeaf.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//
//    }
//
//
//    public void bayo() {
//
//        String phone = "0717629732";
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phone,        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                    @Override
//                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//
//                        Toast.makeText(SignupActivity.this, "DONE", Toast.LENGTH_SHORT).show();
//
//                    }
//
//                    @Override
//                    public void onVerificationFailed(FirebaseException e) {
//
//                        Toast.makeText(SignupActivity.this, "FAILED", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//    }
//
//
//    void uploadImage() {
//
//        if (filePath != null) {
//            final ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setTitle("Saving...Please Wait");
//            progressDialog.show();
//
//            StorageReference ref = storageReference.child("CoverPage/" + UUID.randomUUID().toString());
//
//            Bitmap bitmap = null;
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), filePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
//            byte[] data = baos.toByteArray();
//            UploadTask uploadTask = ref.putBytes(data);
//
//            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    progressDialog.dismiss();
//                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                    //and you can convert it to string like this:
//                    final String sdownload_url = downloadUrl.toString();
//
//
////                Log.d(TAG, "onSuccess:" + sdownload_url);
////                Toast.makeText(MainActivity.this, "" + sdownload_url, Toast.LENGTH_SHORT).show();
////
////
//                    Query query = FirebaseDatabase.getInstance().getReference().child("Users").limitToLast(1);
//                    query.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            for (DataSnapshot childdata : dataSnapshot.getChildren()) {
//                                childdata.getRef().child("URL").setValue(sdownload_url);
//                            }
//
//                            Intent mainIntent = new Intent(SignupActivity.this, Dashboard.class);
//                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(mainIntent);
//                            finish();
//
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                            throw databaseError.toException(); // never ignore errors
//                        }
//                    });
//                    return;
//                }
//            });
//
//        }
//
//    }
//
//    void chooseImage() {
////        Intent intent = new Intent();
////        intent.setType("image/*");
////        intent.setAction(Intent.ACTION_GET_CONTENT);
////        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        startActivityForResult(intent, 1);
//        image.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 1 && resultCode == RESULT_OK
//                && data != null && data.getData() != null) {
//            filePath = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                image.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    void selectCheck() {
//        // Create an Alert Dialog
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        // Set the Alert Dialog Message
//        builder.setMessage("Please Select Your Picture");
//        builder.setCancelable(true);
//
//
//        builder.setPositiveButton("OK",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog,
//                                        int id) {
//
//                        dialog.dismiss();
//                        // Restart the Activity
//                        chooseImage();
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
//
//
//}
