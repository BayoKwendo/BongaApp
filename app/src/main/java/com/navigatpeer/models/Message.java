package com.navigatpeer.models;

public class Message {
    String message;
    String sender;
    String dateSent;

    public Message() {
    }

    public Message(String message, String sender, String dateSent) {
        this.message = message;
        this.sender = sender;
        this.dateSent = dateSent;
    }

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
}