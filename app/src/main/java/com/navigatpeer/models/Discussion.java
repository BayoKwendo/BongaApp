package com.navigatpeer.models;

import android.widget.ImageView;

public class Discussion {


    String message, sender, dateSent;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDateSent() {
        return dateSent;
    }

    public void setDateSent(String dateSent) {
        this.dateSent = dateSent;
    }

    public ImageView getURL() {
        return URL;
    }

    public void setURL(ImageView URL) {
        this.URL = URL;
    }

    ImageView URL;

}
