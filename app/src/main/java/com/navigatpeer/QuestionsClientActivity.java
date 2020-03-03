package com.navigatpeer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.navigatpeer.models.Topics;

public class QuestionsClientActivity extends AppCompatActivity {

    private RecyclerView mTopicsRv;
    private LinearLayout mProgressLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference mTopicsRef;

    private TextView mErrorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultatan_request);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tool = (TextView) toolbar.findViewById(R.id.title);
        tool.setText("Questions Asked");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        mTopicsRef = FirebaseDatabase.getInstance().getReference().child("Consultation");
        mProgressLayout = findViewById(R.id.layout_topics_progress);
        mTopicsRv = findViewById(R.id.rv_topics_list);
        mTopicsRv.setLayoutManager(new LinearLayoutManager(this));

        mErrorTv = findViewById(R.id.tv_topics_error);


        if (!isNetworkAvailable()) {
            // Create an Alert Dialog
            mProgressLayout.setVisibility(View.GONE);

            Toast.makeText(this, "Please Connect Your Internet", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Set the Alert Dialog Message
            builder.setMessage("Internet Connection Required");
            builder.setCancelable(false);

            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(QuestionsClientActivity.this, Dashboard.class));
                            finishAffinity();
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishAffinity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Consultation");
        String fire = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query dbRefFirstTimeCheck = databaseReference.orderByChild("starter").equalTo(fire);

        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    loadData();
                }
                else {
                    Intent topicsIntent = new Intent(QuestionsClientActivity.this, Dashboard.class);
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


    private void loadData() {

        mProgressLayout.setVisibility(View.VISIBLE);

        final String fire = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = mTopicsRef.orderByChild("starter").equalTo(fire);

        FirebaseRecyclerOptions<Topics> options =
                new FirebaseRecyclerOptions.Builder<Topics>()
                        .setQuery(query, Topics.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter<Topics, TopicsViewHolder> adapter = new FirebaseRecyclerAdapter<Topics, TopicsViewHolder>(options) {
            @Override

            protected void onBindViewHolder(@NonNull final TopicsViewHolder holder, final int position, @NonNull final Topics model) {

                holder.mTopicNameTv.setText(model.getForumType());
                holder.mDateCreatedTv.setText(model.getDateCreated());

                FirebaseDatabase.getInstance().getReference().child("Users").child(model.getStarter()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.child("userName").getValue(String.class);
                        holder.mStarterTv.setText("Question Asked By You");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                final String fire = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Query dbRefFirstTimeCheck = mTopicsRef.orderByChild("starter").equalTo(fire);
                dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsClientActivity.this);
                            // Set the Alert Dialog Message
                            builder.setTitle(Html.fromHtml("<font color = '#008080'>Confirmation!!</font>"));

                            builder.setMessage("Proceed to Consultation Room?");
                            builder.setCancelable(false);

                            builder.setNegativeButton(Html.fromHtml("<font color = '#e20719'> Cancel</font>"),
                                    (dialog, id) -> {
                                        startActivity(new Intent(QuestionsClientActivity.this, Dashboard.class));
                                        finishAffinity();
                                    });
                            builder.setPositiveButton(Html.fromHtml("<font color = '#118626'> Proceed</font>"),
                                    (dialog, id) -> {
                                        // Restart the Activity
                                        Intent topicsIntent = new Intent(QuestionsClientActivity.this, PrivateActivity.class);
                                        topicsIntent.putExtra("forumKey", getIntent().getStringExtra("forumKey"));
//                                        topicsIntent.putExtra("topicKey", getRef(position).getKey());
//                                        topicsIntent.putExtra("topicName", model.getTopicName());
                                        startActivity(topicsIntent);

                                    });

                            AlertDialog alert = builder.create();

                            if (!(QuestionsClientActivity.this).isFinishing()) {

                                alert.show();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException(); // don't ignore errors
                    }
                });


                mProgressLayout.setVisibility(View.GONE);

            }

            @NonNull
            @Override
            public TopicsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new TopicsViewHolder(LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.item_topic_row, parent, false));
            }
        };

        adapter.startListening();
        mTopicsRv.setAdapter(adapter);

    }

    private class TopicsViewHolder extends RecyclerView.ViewHolder {

        private TextView mTopicNameTv, mStarterTv, mDateCreatedTv;
        private View mView;

        public TopicsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mTopicNameTv = itemView.findViewById(R.id.tv_topic_name);
            mStarterTv = itemView.findViewById(R.id.tv_topic_starter);
            mDateCreatedTv = itemView.findViewById(R.id.tv_date_created);

        }
    }

}
