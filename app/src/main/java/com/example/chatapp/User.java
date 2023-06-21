package com.example.chatapp;

public class User {
    String userName;
    String image;

    public User(String userName, String image, String id) {
        this.userName = userName;
        this.image = image;
    }

    public User() {
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
