package com.navigatpeer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.navigatpeer.deaf.TextViewDatePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User_Profile extends AppCompatActivity {

    private EditText mNameField, mPhoneField, mNumberField;

    private Button mBack, mConfirm;

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mCourierDatabase;

    private String userID;
    private String mName;

    private ProgressDialog mProgressDialog;
    private DatabaseReference mCustomerDatabase;

    private String mPhone;
    private String mNumber;
    private String mService;
    private String mProfileImageUrl;

    private Uri resultUri;
    FirebaseStorage storage;
    StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tool = (TextView) toolbar.findViewById(R.id.title);
        tool.setText("Profile");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mNumberField = (EditText) findViewById(R.id.number);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        TextViewDatePicker editTextDatePicker = new TextViewDatePicker(this, mNumberField);


        mProfileImage = (ImageView) findViewById(R.id.profileImage);


        mBack = (Button) findViewById(R.id.back);
        mConfirm = (Button) findViewById(R.id.confirm);
        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mCourierDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
    }

    private void getUserInfo(){
        mCourierDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("userName")!=null){
                        mName = map.get("userName").toString();
                        mNameField.setText(mName);
                    }
                    if(map.get("phoneNumber")!=null){
                        mPhone = map.get("phoneNumber").toString();
                        mPhoneField.setText(mPhone);
                    }
                    if(map.get("Date of Birth")!=null){
                        mNumber = map.get("Date of Birth").toString();

                        mNumberField.setText(mNumber);
                    }
                    if(map.get("URL")!=null){



                        mProfileImageUrl = map.get("URL").toString();



                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            refreshActivity();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    private void saveUserInformation() {

        mName = mNameField.getText().toString();
        mPhone = mPhoneField.getText().toString();
        mNumber = mNumberField.getText().toString();


        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userName", mName);
        userInfo.put("phoneNumber", mPhone);
        userInfo.put("Date of Birth", mNumber);
        mCourierDatabase.updateChildren(userInfo);


        if(resultUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Saving...Please Wait");
            progressDialog.show();

            StorageReference ref = storageReference.child("CoverPage/"+ UUID.randomUUID().toString());

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
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
                }
            });

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }

    public void refreshActivity() {
        Intent i = new Intent(this, Dashboard.class);
        i.putExtra("restart", "backtrace");
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        refreshActivity();
    }
}
