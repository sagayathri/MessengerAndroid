package com.gayathriarumugam.messengerandroid.View;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gayathriarumugam.messengerandroid.Adapters.MessageAdapter;
import com.gayathriarumugam.messengerandroid.Model.Message;
import com.gayathriarumugam.messengerandroid.Model.Topic;
import com.gayathriarumugam.messengerandroid.R;
import com.gayathriarumugam.messengerandroid.ViewModel.FirebaseViewModel;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat);

        messagesEditText = findViewById(R.id.messageEditText);
        btnAttach = findViewById(R.id.btnAttach);
        btnSend = findViewById(R.id.btnSend);

        recyclerView = findViewById(R.id.messageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));

        adapter = new MessageAdapter(getApplication(), messageList, userName);
        recyclerView.setAdapter(adapter);

        topic = (Topic) getIntent().getSerializableExtra("Topic");

        //Customise the tool bar
        getSupportActionBar().setTitle(topic.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
