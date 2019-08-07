package com.habsware.messenger;

public class UserInfo {

    String userName;
    String fullName;
    String phoneNum;
    String statusMode;
    String image;
    String deviceToken;


    public UserInfo(String deviceToken) {
        this.deviceToken = deviceToken;
    }

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
    public String getImage() {
        return image;
    }
    public String getDeviceToken() {
        return deviceToken;
    }
}
