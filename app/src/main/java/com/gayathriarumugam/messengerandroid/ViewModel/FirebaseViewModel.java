package com.gayathriarumugam.messengerandroid.ViewModel;

import android.app.Application;
import android.content.Context;
import android.media.Image;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gayathriarumugam.messengerandroid.Model.Message;
import com.gayathriarumugam.messengerandroid.Model.Topic;
import com.gayathriarumugam.messengerandroid.Repos.FirebaseRepository;

import java.util.ArrayList;

public class FirebaseViewModel extends AndroidViewModel {

    private FirebaseRepository  repo;

    public MutableLiveData<ArrayList<Topic>> topicMutableLiveData;
    public MutableLiveData<ArrayList<Message>> messageMutableLiveData;

    public FirebaseViewModel(Application application) {
        super(application);
        repo = FirebaseRepository.getInstance();
    }

    public MutableLiveData<ArrayList<Topic>> getAllTopics() {
        if (topicMutableLiveData == null) {
            topicMutableLiveData = repo.getAllTopics();
        }
        return topicMutableLiveData;
    }

    public MutableLiveData<ArrayList<Message>> getAllMessages(Topic topic) {
        if (messageMutableLiveData == null) {
            messageMutableLiveData = repo.getAllMessages(topic);
        }
        return messageMutableLiveData;
    }

    public void addTopic(String topicName) {
        repo.addTopic(topicName);
    }

    public void addMessage(String userName, String messageText) {
        repo.addMessage(userName, messageText);
    }

    public void addMessageImage(Context context, Topic topic, String senderName, Uri uri) {
        repo.addMessageImage(context, topic, senderName, uri);
    }
}

