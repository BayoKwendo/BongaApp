package com.navigatpeer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navigatpeer.models.Forums;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartConsultation extends AppCompatActivity {

    private EditText mTitleInput;
    private Button mPostBtn;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference mForumsRef;
    View root;
    private TextView textView3;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mTopicsRef;
    private ConstraintLayout layout;
    private List<Forums> forum = new ArrayList<>();
    private  String desk;
    private String Forumuid;
    private String mProfileImageUrl;
    private DatabaseReference mCourierDatabase;
    private String userID;
    private String mName;
    private TextView mNameField;
    private ImageView mProfileImage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startconsultation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tool = (TextView) toolbar.findViewById(R.id.title);
        tool.setText("Start your query");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        mAuth = FirebaseAuth.getInstance();
        mTopicsRef = FirebaseDatabase.getInstance().getReference().child("Consultation");

        Forumuid = getIntent().getStringExtra("forumKey");



        userID = mAuth.getCurrentUser().getUid();
        mCourierDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        layout = findViewById(R.id.cons);
        mPostBtn = findViewById(R.id.btn_submit_topic);
        mTitleInput = findViewById(R.id.input_topic_title);

        mNameField = (TextView) findViewById(R.id.user_name);
        mProfileImage = (ImageView) findViewById(R.id.profileImage);

        mProgress = new ProgressDialog(StartConsultation.this);
        mProgress.setMessage("Processing...");
        mProgress.setCancelable(false);
        mPostBtn.setOnClickListener(v -> {
            String title = mTitleInput.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                mTitleInput.setError("Enter a question first");
            }
            else {
                mProgress.show();
                postTopic(title);
            }

        });

        mCourierDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("userName") != null) {
                        mName = map.get("userName").toString();
                        mNameField.setText(mName);
                    }
                    if(map.get("profileImageUrl")!=null){



                        mProfileImageUrl = map.get("profileImageUrl").toString();



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
    public void onStart() {
        super.onStart();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            back();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());

        return currentDateandTime;
    }


    private void postTopic(String title) {

        FirebaseDatabase.getInstance().getReference().child("ForumsDiscussion").child(Forumuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String desk = dataSnapshot.child("forumName").getValue(String.class);


                Map topicMap = new HashMap();
                topicMap.put("First Question", title);
                topicMap.put("dateCreated", getTime());
                topicMap.put("ChatStatus", true);
                topicMap.put("forumType", desk);
                topicMap.put("starter", FirebaseAuth.getInstance().getCurrentUser().getUid());
                topicMap.put("ForumKey", getIntent().getStringExtra("forumKey"));



                mTopicsRef.push().setValue(topicMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent topicsIntent = new Intent(StartConsultation.this, PrivateActivity.class);
                            topicsIntent.putExtra("forumKey", getIntent().getStringExtra("forumKey"));
                            startActivity(topicsIntent);
                            finishAffinity();
                            mProgress.dismiss();
                        } else {
                            mProgress.dismiss();
                            Toast.makeText(StartConsultation.this, "Error! Please check your connection.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        back();
    }

    void back() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(StartConsultation.this);
//        // Set the Alert Dialog Message
//        builder.setTitle(Html.fromHtml("<font color = '#e20719'>Confirm!</font>"));
//
//        builder.setMessage("Are you sure you want to exit without asking any question??");
//        builder.setCancelable(false);
//
//        builder.setNegativeButton(Html.fromHtml("<font color = '#e20719'>CANCEL</font>"),
//                (dialog, id) -> dialog.dismiss());
//        builder.setPositiveButton(Html.fromHtml("<font color = '#118626'>YES</font>"),
//                (dialog, id) -> {
//                    // Restart the Activity
//                    Intent topicsIntent = new Intent(StartConsultation.this, DashboardDeaf.class);
//                    startActivity(topicsIntent);
//                    finishAffinity();
//
//                });
//
//        AlertDialog alert = builder.create();
//
//        alert.show();

        Intent topicsIntent = new Intent(StartConsultation.this, Dashboard.class);
        startActivity(topicsIntent);


    }
}
