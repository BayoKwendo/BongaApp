package com.navigatpeer.deaf;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.navigatpeer.R;
import com.navigatpeer.models.Topics;

import java.util.HashMap;
import java.util.Map;

public class QuestionsActivityDeaf extends AppCompatActivity {

    private RecyclerView mTopicsRv;
    private LinearLayout mProgressLayout;
    private DatabaseReference mTopicsRef;

    private TextView mErrorTv;
    String forumKey;
    String mName, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultatan_request);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tool = (TextView) toolbar.findViewById(R.id.title);
        tool.setText("Consultation Requests");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setElevation(0);
        }

        mTopicsRef = FirebaseDatabase.getInstance().getReference().child("DeafConsultation");
        mProgressLayout = findViewById(R.id.layout_topics_progress);
        mTopicsRv = findViewById(R.id.rv_topics_list);
        mTopicsRv.setLayoutManager(new LinearLayoutManager(this));

        mErrorTv = findViewById(R.id.tv_topics_error);
        mErrorTv.setVisibility(View.GONE);

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
        if (!isNetworkAvailable()) {
            // Create an Alert Dialog
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            admin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();


        Query dbRefFirstTimeCheck = mTopicsRef.orderByChild("ChatStatus").equalTo(true);

        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mProgressLayout.setVisibility(View.GONE);
                    mErrorTv.setVisibility(View.VISIBLE);
                }else {
                    loadData();
                    mErrorTv.setVisibility(View.INVISIBLE);

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

        Query query = mTopicsRef.orderByChild("ChatStatus").equalTo(true);

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

                FirebaseDatabase.getInstance().getReference().child("UsersDeaf").child(model.getStarter()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.child("userName").getValue(String.class);
                        holder.mStarterTv.setText("Asked by: " + username);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String pkey = getRef(position).getKey();

//                        Toast.makeText(QuestionsActivityDeaf.this, "" + pkey, Toast.LENGTH_SHORT).show();
//
                        final DatabaseReference deleteChatRef = mTopicsRef.child(pkey);


                        deleteChatRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0) {
                                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                    if (map.get("ForumKey") != null) {
                                        mName = map.get("ForumKey").toString();
                                    }
                                    if (map.get("starter") != null) {
                                        userID = map.get("starter").toString();
                                    }
                                }
                                Map<String, Object> userInfo = new HashMap<>();
                                deleteChatRef.updateChildren(userInfo);
                                Intent topicsIntent = new Intent(QuestionsActivityDeaf.this, PrivateActivityDoctorsDeaf.class);
                                topicsIntent.putExtra("forumKey", mName);
                                topicsIntent.putExtra("starter", userID);
                                startActivity(topicsIntent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {


                            }
                        });
                    }

                });


                holder.mView.setOnLongClickListener(new View.OnLongClickListener() {


                    final DatabaseReference pos = getRef(position);

                    final String Key = pos.getKey();

                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsActivityDeaf.this);
                        // Set the Alert Dialog Message
                        builder.setTitle(Html.fromHtml("<font color = '#008080'> End Session!!</font>"));

                        builder.setMessage("Are you sure you to end this consultation session???");
                        builder.setCancelable(false);

                        builder.setNegativeButton(Html.fromHtml("<font color = '#e20719'> Cancel</font>"),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.setPositiveButton(Html.fromHtml("<font color = '#118626'> Yes</font>"),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        String pkey = getRef(position).getKey();


                                        final DatabaseReference deleteChatRef = mTopicsRef.child(pkey);


                                        Query dbRefFirstTimeCheck = mTopicsRef.orderByChild("ChatStatus").equalTo(true);

                                        dbRefFirstTimeCheck.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                deleteChatRef.removeValue();


                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                throw databaseError.toException(); // don't ignore errors
                                            }
                                        });


                                    }
                                });

                        AlertDialog alert = builder.create();

                        alert.show();


                        return true;
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

    @Override
    public void onBackPressed() {
        admin();

    }

    void admin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the Alert Dialog Message
        builder.setTitle(Html.fromHtml("<font color = '#008080'> Leave!!</font>"));

        builder.setMessage("Are You Sure to Exit Consultation Room");
        builder.setCancelable(false);

        builder.setNegativeButton(Html.fromHtml("<font color = '#e20719'> Cancel</font>"),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.setPositiveButton(Html.fromHtml("<font color = '#118626'> Yes</font>"),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        Intent mainIntent = new Intent(QuestionsActivityDeaf.this, LoginActivityDeaf.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainIntent);
                        finish();


                    }
                });

        AlertDialog alert = builder.create();

        alert.show();
    }


}
