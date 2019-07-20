package com.habsware.messenger;

public class UserInfo {

    String userName;
    String fullName;
    String phoneNum;
    String statusMode;

    public UserInfo(String userName, String fullName, String phoneNum, String statusMode) {
        this.userName = userName;
        this.fullName = fullName;
        this.phoneNum = phoneNum;
        this.statusMode = statusMode;
    }
    public UserInfo() {
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
