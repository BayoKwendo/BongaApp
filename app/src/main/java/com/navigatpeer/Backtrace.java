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
import com.navigatpeer.models.Topics;

public class Backtrace extends AppCompatActivity {

//    private RecyclerView mTopicsRv;
//    private LinearLayout mProgressLayout;
//    private DatabaseReference mTopicsRef;
//
//    private TextView mErrorTv;

    String text2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultatan_request);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tool = (TextView) toolbar.findViewById(R.id.title);
        tool.setText("Sure?");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        text2 = getIntent().getStringExtra("forumKey");


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Set the Alert Dialog Message
            builder.setTitle(Html.fromHtml("<font color = '#008080'> Confirm!!</font>"));
            builder.setMessage("Exit Chat Room? You can still come back to continue with this session");
            builder.setCancelable(false);

            builder.setNegativeButton(Html.fromHtml("<font color = '#e20719'> Cancel</font>"),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                            Intent topicsIntent = new Intent(Backtrace.this, PrivateActivity.class);
                            topicsIntent.putExtra("forumKey", text2);
                            startActivity(topicsIntent);



                        }
                    });
            builder.setPositiveButton(Html.fromHtml("<font color = '#118626'> Yes</font>"),
                    (dialog, which) -> {


                        Intent topicsIntent = new Intent(Backtrace.this, Dashboard.class);
                        startActivity(topicsIntent);

                    });
                    AlertDialog alert = builder.create();

            alert.show();


    }
}
