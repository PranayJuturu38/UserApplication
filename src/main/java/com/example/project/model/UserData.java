package com.example.project.model;

public class UserData {

    private String userName;
    private String email;
    private String contactNo;

    private String uniqueId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UserData(String userName, String email, String contactNo, String uniqueId) {
        this.userName = userName;
        this.email = email;
        this.contactNo = contactNo;
        this.uniqueId = uniqueId;
    }
}
