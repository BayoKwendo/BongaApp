package com.navigatpeer;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.navigatpeer.Adapters.DiscussionMessagesAdapter;
import com.navigatpeer.models.Discussion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PrivateActivity extends AppCompatActivity {

    public static final String MyPREF = "MyPrefs";
    public static final String DATE_KEY = "DateKey";
    ImageView mimage;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    private Uri filePath;
    StorageReference storageReference;
    private FirebaseAuth mAuth;
    private DatabaseReference mDiscussionsRef;
    private RecyclerView mMessagesRv;
    private DiscussionMessagesAdapter adapter;
    private List<Discussion> discussionList = new ArrayList<>();
    private LinearLayout mProgressLayout;
    private EditText mMessageInput;
    private String text2;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button mSendBtn;
    private ProgressDialog mProgress;
    private String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private TextView mErrorTv, tool;
    private final int PICK_IMAGE_REQUEST = 71;

    ImageView imagevie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        tool = toolbar.findViewById(R.id.title);
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
        imagevie = findViewById(R.id.upload);




        mProgress = new ProgressDialog(PrivateActivity.this);
        mProgress.setMessage("Redirecting...");
        mProgress.setCancelable(false);

        mimage = findViewById(R.id.uploads);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Consultation");

        mErrorTv = findViewById(R.id.tv_discussions_error);

        mAuth = FirebaseAuth.getInstance();
        mDiscussionsRef = FirebaseDatabase.getInstance().getReference().child("ForumMessages").child(currentUid)
                .child(getIntent().getStringExtra("forumKey"));


        text2 = getIntent().getStringExtra("forumKey");
        mMessagesRv = findViewById(R.id.reyclerview_message_list);
        mMessagesRv.setLayoutManager(new LinearLayoutManager(this));
        mMessagesRv.setHasFixedSize(true);
        mAuthListener = firebaseAuth -> {
            if (currentUid != null) {
                if (currentUid.equals("pHvlmpGCIlYdavQzseg4lnAZuCh2")) {
//                        Toast.makeText(PrivateActivityDeaf.this, "Your welcome doc", Toast.LENGTH_SHORT).show();
                } else {
//                        Toast.makeText(PrivateActivityDeaf.this, "Your welcome to the consultation room", Toast.LENGTH_SHORT).show();

                }

            }
        };

        mimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(PrivateActivity.this, "Soon", Toast.LENGTH_SHORT).show();
//                chooseImage();
            }
        });

//        initControls();
//        pref = getSharedPreferences(MyPREF, MODE_PRIVATE);
//
//        // Check that the shared preferences has a value
//        if(contains(DATE_KEY)){
//            editor = prefs.edit();
//            // get the current datetime.
//            editor.Long(DATE_KEY, date);
//            editor.commit();
//            // set the text of the textview android:id="@+id/date"
//            // with the current formatted date.
//        }
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                mimage.setImageResource(android.R.color.transparent);
                String message = mMessageInput.getText().toString().trim();

                if (TextUtils.isEmpty(message)) {
                    mMessageInput.setError("Type a Message");
                    return;
                }

                sendReply(message);
            }
        });

        title();


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
//        uploadImage();

        Map messageMap = new HashMap();
        messageMap.put("message", message);
        messageMap.put("sender", currentUid);
        messageMap.put("dateSent", getTime());

        startActivity(getIntent());
        finish();

        mDiscussionsRef.push().setValue(messageMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mProgressLayout.setVisibility(View.GONE);
            } else {
                mProgressLayout.setVisibility(View.GONE);
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
        if (item.getItemId() == android.R.id.home) {

            admin();

            return true;
        } else if (item.getItemId() == R.id.call) {
            selfie();
            mProgress.show();

        }


        return super.onOptionsItemSelected(item);
    }

    void selfie() {


        Query dbRefFirstTimeCheck = databaseReference.orderByChild("forumType").equalTo("Chukua Selfie");
//        Toast.makeText(this, "" + string, Toast.LENGTH_SHORT).show();
        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mProgress.dismiss();
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:+254724131547"));
                    startActivity(callIntent);

                } else {
                    rship();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });


    }

    void rship() {

        Query dbRefFirstTimeCheck = databaseReference.orderByChild("forumType").equalTo("Healthy and Unhealthy Relationship");
//        Toast.makeText(this, "" + string, Toast.LENGTH_SHORT).show();
        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mProgress.dismiss();

                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:+254727695844"));
                    startActivity(callIntent);
                } else {
                    sexual();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });

    }

    void sexual() {

        Query dbRefFirstTimeCheck = databaseReference.orderByChild("forumType").equalTo("Sexual and Gender Based Violence");
//        Toast.makeText(this, "" + string, Toast.LENGTH_SHORT).show();
        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mProgress.dismiss();

                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:+254722787485"));
                    startActivity(callIntent);
                } else {
                    drugs();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });

    }

    void drugs() {

        Query dbRefFirstTimeCheck = databaseReference.orderByChild("forumType").equalTo("Drug and Substance Abuse");
//        Toast.makeText(this, "" + string, Toast.LENGTH_SHORT).show();
        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mProgress.dismiss();
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:+254724131547"));
                    startActivity(callIntent);

                } else {
                    pep();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });

    }

    void pep() {

        Query dbRefFirstTimeCheck = databaseReference.orderByChild("forumType").equalTo("PEP and PrEP");
//        Toast.makeText(this, "" + string, Toast.LENGTH_SHORT).show();
        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mProgress.dismiss();

                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:+254727695844"));
                    startActivity(callIntent);
                } else {
                    family();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });

    }

    void family() {

        Query dbRefFirstTimeCheck = databaseReference.orderByChild("forumType").equalTo("Family Planning");
//        Toast.makeText(this, "" + string, Toast.LENGTH_SHORT).show();
        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mProgress.dismiss();
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:+254722787485"));
                    startActivity(callIntent);

                } else {
                    mProgress.dismiss();
                    Toast.makeText(PrivateActivity.this, "Contact not found", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });

    }


    void title() {
        FirebaseDatabase.getInstance().getReference().child("ForumsDiscussion")
                .child(getIntent().getStringExtra("forumKey"))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String desk = dataSnapshot.child("forumName").getValue(String.class);

                        tool.setText(desk);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseDatabase.getInstance().getReference().child("ForumMessages").child(currentUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(getIntent().getStringExtra("forumKey"))) {

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
        adapter = new DiscussionMessagesAdapter(PrivateActivity.this, discussionList);
        adapter.notifyDataSetChanged();

        mMessagesRv.setAdapter(adapter);
        mMessagesRv.scrollToPosition(discussionList.size() - 1);

    }

    private void loadData() {

//        imagevie.setVisibility(View.GONE);
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

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("DD MMM, YYYY HH:mm");
        String currentDateandTime = sdf.format(new Date());

        return currentDateandTime;
    }

    @Override
    public void onBackPressed() {

        admin();

    }

    void admin() {
        if (currentUid.equals("KSxKcCMVU1Xb4d7y8T4Vzr4TdCn1")) {
            finish();
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Set the Alert Dialog Message
            builder.setTitle(Html.fromHtml("<font color = '#008080'> Confirm!!</font>"));
            builder.setMessage("Your about to exit chatroom");
            builder.setCancelable(false);

            builder.setNegativeButton(Html.fromHtml("<font color = '#e20719'> Cancel</font>"),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            builder.setPositiveButton(Html.fromHtml("<font color = '#118626'> Continue</font>"),
                    (dialog, which) -> {
                        dialog.dismiss();
                        refreshActivity();


                    });
            AlertDialog alert = builder.create();

            alert.show();
        }
    }

    public void refreshActivity() {
//        Intent mStartActivity = new Intent(this, DashboardDeaf.class);
//        int mPendingIntentId = 123456;
//        PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmManager mgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);

        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(0);
//
        Intent i = new Intent(this, Dashboard.class);
        startActivity(i);
//finish();


    }


    void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Saving...Please Wait");
            progressDialog.show();

            StorageReference ref = storageReference.child("CoverPage/" + UUID.randomUUID().toString());

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
//                Toast.makeText(PoliceDashBoard.this, "" + sdownload_url, Toast.LENGTH_SHORT).show();
//
//
                    Query query =mDiscussionsRef.orderByChild("sender").equalTo(currentUid)
                            .limitToLast(1);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childdata : dataSnapshot.getChildren()) {
                                childdata.getRef().child("URL").setValue(sdownload_url);
                            }

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

    void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        mimage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mimage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
