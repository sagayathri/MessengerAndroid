package com.gayathriarumugam.messengerandroid.Repos;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.gayathriarumugam.messengerandroid.Model.Message;
import com.gayathriarumugam.messengerandroid.Model.Topic;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirebaseRepository {

    static String TAG = "FirebaseRepository";

    private static FirebaseRepository sInstance;
    private ArrayList<Topic> topicsList = new ArrayList<>();
    private ArrayList<Message> messagesList = new ArrayList<>();

    private MutableLiveData<ArrayList<Topic>> topicMutableLiveData;
    private MutableLiveData<ArrayList<Message>> messagesMutableLiveData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference topicReference;
    private CollectionReference messageReference;

    private StorageReference mStorageRef, imageRef;

    private Topic topic;
    private Message message;

    public FirebaseRepository() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public static FirebaseRepository getInstance() {
        if (sInstance == null) {
            sInstance = new FirebaseRepository();
        }
        return sInstance;
    }

    public MutableLiveData<ArrayList<Topic>> getAllTopics() {
        if (topicReference == null) {
            topicReference = db.collection("topics");
        }
        topicMutableLiveData = new MutableLiveData<>();
        loadAllTopics();
        return topicMutableLiveData;
    }

    public MutableLiveData<ArrayList<Message>> getAllMessages(Topic topic) {
        if (topic != null) {
            messageReference = db.collection("topics/" + topic.getId() + "/thread/");
        }
        messagesMutableLiveData = new MutableLiveData<>();
        loadAllMessages();
        return messagesMutableLiveData;
    }

    public void addTopic(String topicName) {
        createTopic(topicName);
    }

    public void addMessage(String sender, String message) {
        pushMessage(sender, message);
    }

    public void addMessageImage(Context context, Topic topic, String senderName, Uri uri) {
        uploadImageToStorage(context, topic, senderName, uri);
    }

    private void loadAllTopics() {
        topicReference
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Error: Message Listen failed.", e);
                    return;
                }
                //Listens database, updates the UI if new database get updated
                if (snapshot != null) {
                    topicsList.clear();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        Topic topic = new Topic((String) document.getId(), (String) document.get("name"));
                        topicsList.add(topic);
                    }
                    topicMutableLiveData.postValue(topicsList);
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    private void loadAllMessages() {
        messageReference
                .orderBy("created")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Error: Message Listen failed.", e);
                    return;
                }

                //Listens database, updates the UI if new database get updated
                if (snapshot != null) {
                    messagesList.clear();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        if (document.get("content") != null) {
                            message = new Message((String) document.get("content"));
                        }
                        else {
                            message = new Message(null);
                        }
                        message.setId(document.get("senderID").toString());
                        message.setSender(document.get("senderName").toString());
                        message.setCreated((Timestamp) document.get("created"));
                        message.setDownloadURL((String) document.get("url"));
                        messagesList.add(message);
                    }
                    messagesMutableLiveData.postValue(messagesList);
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    private void createTopic(String topicName) {
        //Checks for topicTV is empty
        if (!topicName.isEmpty()){
            topic = new Topic(null, topicName);
            // Create a new topic
            Map<String, Object> data = new HashMap<>();
            if (topic.getId() != null) {
                data.put("id", topic.getId());
            }
            data.put("name", topicName);

            // Add a new document with a generated ID
            topicReference.add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            topicsList.clear();
                            topic = null;
                        }
                    });
        }
    }

    private void pushMessage(String sender, String messageText) {

        //Get current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = mAuth.getCurrentUser().getUid();

        Timestamp created = Timestamp.now();
        //Checks for topicTV is empty
        if (!messageText.isEmpty()) {
            message = new Message(messageText, sender, created,null);
            // Create a new topic
            Map<String, Object> data = new HashMap<>();
            if (currentUserID != null) {
                data.put("senderID", currentUserID);
            }
            data.put("content", messageText);
            data.put("senderName", sender);
            data.put("created", created);

            // Add a new document with a generated ID
            messageReference.add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            messagesList.clear();
                            message = null;
                        }
                    });
        }
    }

    private void pushImage(String sender, String downloadUri) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = mAuth.getCurrentUser().getUid();

        Timestamp created = Timestamp.now();
        //Checks for topicTV is empty
        message = new Message(null, sender, created,downloadUri);
        // Create a new topic
        Map<String, Object> data = new HashMap<>();
        if (currentUserID != null) {
            data.put("senderID", currentUserID);
        }
        data.put("senderName", sender);
        data.put("created", created);
        data.put("url", downloadUri);

        // Add a new document with a generated ID
        messageReference.add(data);
    }

    private void uploadImageToStorage(final Context context, Topic topic, final String senderName, Uri uri) {

        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();

        String imageName = UUID.randomUUID().toString()+Timestamp.now();
        imageRef = mStorageRef.child(topic.getId()).child(imageName);

        UploadTask uploadTask = imageRef.putFile(uri, metadata);
        Task<Uri> urlTask = uploadTask
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        String downloadUri = String.valueOf(task.getResult());
                        pushImage(senderName, downloadUri);

                    } else {
                        // Handle failures
                        Log.d(TAG, "Unable to get downloadURL");
                        Toast.makeText(context, "Unable to send photo", Toast.LENGTH_SHORT).show();;
                    }
                }
            });
    }
}
