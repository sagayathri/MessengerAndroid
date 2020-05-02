package com.gayathriarumugam.messengerandroid.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import com.gayathriarumugam.messengerandroid.Adapters.MessageAdapter;
import com.gayathriarumugam.messengerandroid.Model.Message;
import com.gayathriarumugam.messengerandroid.Model.Topic;
import com.gayathriarumugam.messengerandroid.R;
import com.gayathriarumugam.messengerandroid.ViewModel.FirebaseViewModel;


import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    static final int PICK_IMAGE = 111;

    private Button btnAttach, btnSend;
    private EditText messagesEditText;

    private FirebaseViewModel firebaseViewModel;
    private RecyclerView recyclerView;
    private ArrayList<Message> messageList = new ArrayList<>();
    private MessageAdapter adapter;

    private Topic topic;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagesEditText = findViewById(R.id.messageEditText);
        btnAttach = findViewById(R.id.btnAttach);
        btnSend = findViewById(R.id.btnSend);

        topic = (Topic) getIntent().getSerializableExtra("Topic");

        //Customise the tool bar
        getSupportActionBar().setTitle(topic.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Reads user name from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(String.valueOf(R.string.PREFS_NAME), MODE_PRIVATE);
        userName = prefs.getString("name", "null");

        recyclerView = findViewById(R.id.messageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        // Get a new or existing ViewModel from the ViewModelProvider.
        firebaseViewModel = ViewModelProviders.of(ChatActivity.this).get(FirebaseViewModel.class);
        firebaseViewModel.getAllMessages(topic).observe(ChatActivity.this, new Observer<ArrayList<Message>>() {
            @Override
            public void onChanged(ArrayList<Message> messages) {
                if (!messages.isEmpty()) {
                    adapter = null;
                    messageList = messages;
                    adapter = new MessageAdapter(ChatActivity.this, messageList, userName);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size() - 2);

                    messagesEditText.setText("");
                    messagesEditText.setEnabled(true);
                    messagesEditText.setTextColor(getResources().getColor(R.color.colorBlack));
                }
            }
        });

        btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Confirms to open file explorer
                AlertDialog.Builder alert = new AlertDialog.Builder(ChatActivity.this, R.style.MyDialogTheme);
                alert.setTitle("Do you want to attach a file?");
                alert.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, ""), PICK_IMAGE);

                            //While loading message edititext will be disabled to avoid crash
                            messagesEditText.setText("File is loading please wait for a while...");
                            messagesEditText.setEnabled(false);
                            messagesEditText.setTextColor(getResources().getColor(R.color.colorLightGrey));
                        }
                    });
                alert.setNegativeButton("No", null);
                alert.show();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (messagesEditText.getText().toString().isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Please type message",Toast.LENGTH_SHORT).show();
                }
                else  {
                    //Creates a new text message
                    sendMessage(messagesEditText.getText().toString());
                }
            }
        });
    }

    private void sendMessage(String messageText) {
        messagesEditText.setText("");
        firebaseViewModel.addMessage(userName,messageText);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Opens up file explorer window
        if (requestCode == PICK_IMAGE) {
            Uri uri = data.getData();
            uploadFile(uri);
        }
    }

    private void uploadFile(Uri uri) {
        //creates new message with file
        firebaseViewModel.addMessageImage(this, topic, userName, uri);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //Closes the current activity if back on the toolbar tapped
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
