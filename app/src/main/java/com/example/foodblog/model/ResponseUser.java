package com.example.foodblog.model;

public class ResponseUser extends ResponseObject{
    private User data;

    public User getData() {
        return data;
    }

    public ResponseUser() {
    }

    public ResponseUser(boolean success, String message, User data) {
        super(success, message);
        this.data = data;
    }
}
