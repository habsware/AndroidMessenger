package com.habsware.messenger;


public class Message {

    public String messageText;
    public String userName;
    public String date;
    public String recipient;
    public String sender;
    public String image;

    public Message(String messageText, String userName, String date) {
        this.messageText = messageText;
        this.userName = userName;
        this.date = date;
    }

    public Message(String messageText, String sender, String recipient, String date){
        this.messageText = messageText;
        this.sender = sender;
        this.recipient = recipient;
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

    public String getRecipient() {
        return recipient;
    }

    public String getSender() {
        return sender;
    }

    public String getImage() {
        return image;
    }
}
