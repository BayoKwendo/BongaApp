package com.navigatpeer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.navigatpeer.models.Forums;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.R.id.message;

public class Dashboard extends AppCompatActivity {
    private FirebaseAuth mAuth;
    boolean doubleBackToExitPressedOnce = false;

    private DatabaseReference mForumsRef;
    View root;
    private String userID, mkey;


    private FirebaseAuth.AuthStateListener mAuthListener;

    private RecyclerView mForumsRv;
    ImageView image;
    private ProgressDialog mProgress;
    private Parcelable recyclerViewState;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;

    private LinearLayout mProgressLayout;
    RecyclerView.AdapterDataObserver mDataObserver;
    SwipeRefreshLayout swipeRefreshLayout;
    private Parcelable mListState ;
    FirebaseRecyclerAdapter<Forums, ForumsViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tool = (TextView) toolbar.findViewById(R.id.title);
        image = findViewById(R.id.image_id);
        tool.setText("BongaApp");



        profile();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setElevation(0);
        }
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mForumsRef = FirebaseDatabase.getInstance().getReference().child("ForumsDiscussion");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //                if (mAuth.getCurrentUser() == null){
                //                    startActivity(new Intent(DashboardDeaf.this, LoginActivityDeaf.class));
                //                    finish();
                //                    Toast.makeText(DashboardDeaf.this, "You Must Login first", Toast.LENGTH_SHORT).show();
                //                }
            }
        };

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, User_Profile.class));

            }
        });
        Context context = Dashboard.this;
        mForumsRv = findViewById(R.id.rv_forum_list);
        mForumsRv.setLayoutManager(new LinearLayoutManager(context));
        mProgressLayout = findViewById(R.id.layout_progress);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Redirecting \n Please wait...");
        mProgress.setCancelable(false);

        swipeRefreshLayout = findViewById(R.id.swipe);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });


        //        if (getIntent().getBooleanExtra("EXIT", false))
        //        {
        //            Intent i = new Intent(DashboardDeaf.this, CheckQuestion.class);
        //            startActivity(i);
        //            finish();
        //        }
        loadData();


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

    private static class ForumsViewHolder extends RecyclerView.ViewHolder {

        private TextView mForumNameTv, tvIcon;

        private View mView;

        ForumsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mForumNameTv = itemView.findViewById(R.id.tv_forum_name);

            tvIcon = itemView.findViewById(R.id.tvIcon);


        }
    }

    void refreshItems() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                loadData();
            }
        }, 1000);
    }





    @Override
    protected void onPause()
    {
        super.onPause();

        mBundleRecyclerViewState = new Bundle();

        mListState = mForumsRv.getLayoutManager().onSaveInstanceState();

        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, mListState);
    }

        @Override
    protected void onResume(){
        super.onResume();

        profile();
            if (mBundleRecyclerViewState != null) {

                mListState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                mForumsRv.getLayoutManager().onRestoreInstanceState(mListState);
            }
//            else{
//                Toast.makeText(this, "NULLL", Toast.LENGTH_SHORT).show();
//            }





        }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());

        return currentDateandTime;
    }


    private void loadData(){

        mProgressLayout.setVisibility(View.VISIBLE);
        Query query = mForumsRef.limitToLast(50);
        FirebaseRecyclerOptions<Forums> options =
                new FirebaseRecyclerOptions.Builder<Forums>()
                        .setQuery(query, Forums.class)
                        .setLifecycleOwner(this)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Forums, ForumsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ForumsViewHolder holder, final int position, @NonNull Forums model) {

                holder.mForumNameTv.setText(model.getForumName());
                //                holder.mDescTv.setText(model.getDescription());

                holder.tvIcon.setText(model.getForumName().substring(0, 1));

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Consultation");
                        String fire = mAuth.getCurrentUser().getUid();
                        Query dbRefFirstTimeCheck = databaseReference.orderByChild("starter").equalTo(fire);
                        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (!dataSnapshot.exists()) {

                                    FirebaseDatabase.getInstance().getReference().child("ForumsDiscussion")
                                            .child(getRef(position).getKey()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {



                                            String desk = dataSnapshot.child("forumName").getValue(String.class);


                                            Map topicMap = new HashMap();
                                            topicMap.put("First Question", desk);
                                            topicMap.put("dateCreated", getTime());
                                            topicMap.put("ChatStatus", true);
                                            topicMap.put("forumType", desk);
                                            topicMap.put("starter", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            topicMap.put("ForumKey", getRef(position).getKey());


                                            databaseReference.push().setValue(topicMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent i = new Intent(Dashboard.this, PrivateActivity.class);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        i.putExtra("forumKey", getRef(position).getKey());
                                                        startActivity(i);
                                                        mProgress.dismiss();
                                                    } else {
                                                        mProgress.dismiss();
                                                        Toast.makeText(Dashboard.this, "Error! Please check your connection.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                }


                                else {
                                    Query dbRefFirstTimeCheck = databaseReference.orderByChild("ForumKey").equalTo(getRef(position).getKey());
                                    dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (!dataSnapshot.exists()) {
                                                FirebaseDatabase.getInstance().getReference().child("ForumsDiscussion")
                                                        .child(getRef(position).getKey()).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                                        String desk = dataSnapshot.child("forumName").getValue(String.class);


                                                        Map topicMap = new HashMap();
                                                        topicMap.put("First Question", desk);
                                                        topicMap.put("dateCreated", getTime());
                                                        topicMap.put("ChatStatus", true);
                                                        topicMap.put("forumType", desk);
                                                        topicMap.put("starter", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                        topicMap.put("ForumKey", getRef(position).getKey());


                                                        databaseReference.push().setValue(topicMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Intent i = new Intent(Dashboard.this, PrivateActivity.class);
                                                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    i.putExtra("forumKey", getRef(position).getKey());
                                                                    startActivity(i);
                                                                    mProgress.dismiss();
                                                                } else {
                                                                    mProgress.dismiss();
                                                                    Toast.makeText(Dashboard.this, "Error! Please check your connection.", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                            else{
                                                Intent i = new Intent(Dashboard.this, PrivateActivity.class);
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                i.putExtra("forumKey", getRef(position).getKey());
                                                startActivity(i);
                                            }


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                throw databaseError.toException(); // don't ignore errors
                            }
                        });

                    }
                });

                mProgressLayout.setVisibility(View.GONE);

            }

            @NonNull
            @Override
            public ForumsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                adapter.startListening();

                return new ForumsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forums_row, parent, false));
            }
        };


        mForumsRv.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();

    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        // do some stuff here
    }

    //    @Override
    //    public void onStop() {
    //        super.onStop();
    //         FirebaseAuth.getInstance().signOut();zz
    //        loadData();
    //
    //    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //        // Lofcate MenuItem with ShareActionProvider
        //        MenuItem item = menu.findItem(R.id.logout);
        //
        //
        //        // Return true to display menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as Forums specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            mAuth.signOut();
            startActivity(new Intent(Dashboard.this, LoginActivity.class));
            finishAffinity();
        }
        if (id == R.id.about) {
            startActivity(new Intent(Dashboard.this, about.class));
        }
        if (id == R.id.profile) {

            startActivity(new Intent(Dashboard.this, User_Profile.class));
        }

        else if (id == R.id.nav_twitter) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "davebulimo@gmail.com", null));
            intent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(intent, " Email Via :"));

        } else if (id == R.id.call) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:+254710252819"));
            startActivity(callIntent);
        } else if (id == R.id.nav_facebook) {
            Uri uri = Uri.parse("smsto::+254710252819");
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            startActivity(it);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();


            new Handler().postDelayed(() -> {
                finishAffinity();
                doubleBackToExitPressedOnce=false;
            }, 2000);


        }
    }


    void profile(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        Query dbRefFirstTimeCheck = databaseReference.orderByChild("UserId").equalTo(userID);
        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childdata : dataSnapshot.getChildren()) {


                        String userid = childdata.child("URL").getValue(String.class);

                        Glide.with(getApplication()).load(userid).into(image);

                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException(); // don't ignore errors
            }
        });
    }





}
