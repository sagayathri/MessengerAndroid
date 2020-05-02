package com.gayathriarumugam.messengerandroid.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gayathriarumugam.messengerandroid.Adapters.TopicAdapter;
import com.gayathriarumugam.messengerandroid.Model.Topic;
import com.gayathriarumugam.messengerandroid.R;
import com.gayathriarumugam.messengerandroid.ViewModel.FirebaseViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class TopicsActivity extends AppCompatActivity implements TopicAdapter.ItemClickListener  {

    private Button btnLogout, btnAddTopic;
    private TextView userNameTV;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private TopicAdapter adapter;
    private ArrayList<Topic> topicsList = new ArrayList<>();

    private FirebaseViewModel firebaseViewModel;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);

        userNameTV = findViewById(R.id.userNAmeTV);
        btnLogout = findViewById(R.id.btnLogOut);
        btnAddTopic = findViewById(R.id.btnAddTopic);

        recyclerView = findViewById(R.id.topicsRecyclerView);

        //Reads user name from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(String.valueOf(R.string.PREFS_NAME), MODE_PRIVATE);
        currentUser = prefs.getString("name", "null");
        userNameTV.setText(currentUser);

        mAuth = FirebaseAuth.getInstance();

        // Get a new or existing ViewModel from the ViewModelProvider.
        firebaseViewModel = ViewModelProviders.of(TopicsActivity.this).get(FirebaseViewModel.class);
        firebaseViewModel.getAllTopics().observe(TopicsActivity.this, new Observer<ArrayList<Topic>>() {
            @Override
            public void onChanged(ArrayList<Topic> topics) {
                if (!topics.isEmpty()) {
                    topicsList = topics;
                    recyclerView.setLayoutManager(new LinearLayoutManager(TopicsActivity.this));
                    adapter = new TopicAdapter(TopicsActivity.this, topicsList);
                    adapter.setClickListener(TopicsActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(TopicsActivity.this, R.style.MyDialogTheme);
                alert.setTitle("Alert")
                        .setMessage("Are you sure to logout?")
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        logOut();
                                    }
                                })

                        //Dismiss the dialog and take no further action.
                        .setNegativeButton("No", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        btnAddTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(TopicsActivity.this, R.style.MyDialogTheme);
                final EditText edittext = new EditText(getApplicationContext());
                alert.setView(edittext);
                alert.setTitle("Create a new Topic")
                        .setPositiveButton(
                                "Create",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if(firebaseViewModel != null) {
                                            edittext.setInputType(0);
                                            firebaseViewModel.addTopic(edittext.getText().toString());
                                        }
                                    }
                                })

                        //Dismiss the dialog and take no further action.
                        .setNegativeButton("No", null)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //If current user's session expired the app needs to logs in again
        if(currentUser.equals("null")) {
            AlertDialog.Builder alert = new AlertDialog.Builder(TopicsActivity.this, R.style.MyDialogTheme);
            alert.setTitle("Session Expired")
                    .setMessage("Please login with new name to continue")
                    .setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    logOut();
                                }
                            })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void logOut() {
        //Logs out the current user
        mAuth.signOut();
        Intent intent = new Intent(TopicsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onItemClick(View view, int position) {
        //Navigates to chat screen
        Intent intent = new Intent(TopicsActivity.this, ChatActivity.class);
        intent.putExtra("Topic", topicsList.get(position));
        startActivity(intent);
    }
}
