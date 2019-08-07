package com.habsware.messenger;

public class Notification {

    public String sender;
    public String notificationType;

    public Notification() {}

    public Notification(String sender, String notificationType) {
        this.sender = sender;
        this.notificationType = notificationType;
    }

}
