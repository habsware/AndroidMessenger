package com.habsware.messenger;

public class UserInfo {

    String id;
    String userName;

    public UserInfo(String id, String userName, String fullName, String phoneNum, String statusMode) {
        this.id = id;
        this.userName = userName;
        this.fullName = fullName;
        this.phoneNum = phoneNum;
        this.statusMode = statusMode;
    }

    String fullName;
    String phoneNum;
    String statusMode;

    public UserInfo() {
    }

    public String getId() {
        return id;
    }

    public String getUserName(){
        return userName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getStatusMode() {
        return statusMode;
    }
}
