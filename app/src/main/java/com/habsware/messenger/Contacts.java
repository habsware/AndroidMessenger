package com.habsware.messenger;

public class Contacts {

    public String userName;
    public String statusMode;
    public String image;

    public Contacts(String userName, String statusMode, String image) {
        this.userName = userName;
        this.statusMode = statusMode;
        this.image = image;
    }

    public Contacts() {
    }

    public String getUserName() {
        return userName;
    }

    public String getStatusMode() {
        return statusMode;
    }

    public String getImage() {
        return image;
    }
}
