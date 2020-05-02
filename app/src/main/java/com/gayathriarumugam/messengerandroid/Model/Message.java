package com.gayathriarumugam.messengerandroid.Model;

import com.google.firebase.Timestamp;
import java.io.Serializable;

public class Message implements Serializable {

    String id;
    String content;
    String sender;
    Timestamp created;
    String downloadURL;

    public Message(String content) {
        this.content = content;
    }

    public Message(String content, String sender, Timestamp created, String downloadURL) {
        this.content = content;
        this.sender = sender;
        this.created = created;
        this.downloadURL = downloadURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getContent() {
        return content;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }
}
