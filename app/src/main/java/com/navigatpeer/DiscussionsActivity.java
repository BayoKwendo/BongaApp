package com.navigatpeer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navigatpeer.Adapters.DiscussionMessagesAdapter;
import com.navigatpeer.models.Discussion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.id.message;

public class DiscussionsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDiscussionsRef;

    private RecyclerView mMessagesRv;
    private DiscussionMessagesAdapter adapter;
    private List<Discussion> discussionList = new ArrayList<>();

    private LinearLayout mProgressLayout;
    private EditText mMessageInput;
    private Button mSendBtn;

    private TextView mErrorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tool = (TextView) toolbar.findViewById(R.id.title);
        tool.setText("Interaction Portal");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        mProgressLayout = findViewById(R.id.layout_discussions_progress);
        mMessageInput = findViewById(R.id.input_response);
        mSendBtn = findViewById(R.id.btn_send_reply);

        mErrorTv = findViewById(R.id.tv_discussions_error);

        String forumKey = getIntent().getStringExtra("forumKey");

        mAuth = FirebaseAuth.getInstance();

        mDiscussionsRef = FirebaseDatabase.getInstance().getReference().child("ForumMessages")
                .child(getIntent().getStringExtra("forumKey"));

        mMessagesRv = findViewById(R.id.reyclerview_message_list);
        mMessagesRv.setLayoutManager(new LinearLayoutManager(this));
        mMessagesRv.setHasFixedSize(true);

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = mMessageInput.getText().toString().trim();

                if (TextUtils.isEmpty(message)){
                    mMessageInput.setError("Type a Message");
                    return;
                }

                sendReply(message);
            }
        });


        if (!isNetworkAvailable()) {
            // Create an Alert Dialog
            mProgressLayout.setVisibility(View.GONE);
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
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int id) {
                            // Restart the Activity
                            recreate();
                        }
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


    private void sendReply(String message) {

        mProgressLayout.setVisibility(View.VISIBLE);
        mMessageInput.setText("");

        Map messageMap = new HashMap();
        messageMap.put("message", message);
        messageMap.put("sender", mAuth.getCurrentUser().getUid());
        messageMap.put("dateSent", getTime());

        mDiscussionsRef.push().setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mProgressLayout.setVisibility(View.GONE);
                } else {
                    mProgressLayout.setVisibility(View.GONE);
                }
            }
        });

     }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
//        // Lofcate MenuItem with ShareActionProvider
//        MenuItem item = menu.findItem(R.id.logout);
//
//
//        // Return true to display menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home) {
            finish();
            return true;
        }
        else if (item.getItemId() == R.id.call) {

            Uri uri = Uri.parse("smsto:+254745933298");
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            startActivity(it);


        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseDatabase.getInstance().getReference().child("ForumMessages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(getIntent().getStringExtra("forumKey"))){

                    mProgressLayout.setVisibility(View.GONE);
                    mErrorTv.setVisibility(View.VISIBLE);

                } else {


                    mErrorTv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        discussionList.clear();

        loadData();
        adapter = new DiscussionMessagesAdapter(DiscussionsActivity.this, discussionList);
        adapter.notifyDataSetChanged();

        mMessagesRv.setAdapter(adapter);
        mMessagesRv.scrollToPosition(discussionList.size() - 1);

    }

    private void loadData(){

        mProgressLayout.setVisibility(View.VISIBLE);



        mDiscussionsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Discussion discussion = dataSnapshot.getValue(Discussion.class);
                discussionList.add(discussion);
                adapter.notifyDataSetChanged();
                mMessagesRv.scrollToPosition(discussionList.size() - 1);
                mProgressLayout.setVisibility(View.GONE);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentDateandTime = sdf.format(new Date());

        return currentDateandTime;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



}
