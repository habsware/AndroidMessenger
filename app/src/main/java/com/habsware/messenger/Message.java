package com.habsware.messenger;


public class Message {

    public String messageText;
    public String userName;
    public String date;

    public Message(String messageText, String userName, String date) {
        this.messageText = messageText;
        this.userName = userName;
        this.date = date;
    }

    public Message() {
    }

    public String getMessageText() {
        return messageText;
    }

    public String getUserName() {
        return userName;
    }

    public String getDate() {
        return date;
    }
}
