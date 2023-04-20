package com.example.foodblog.model;

public class ResponseOtp extends ResponseObject{
    private String data; // time

    public ResponseOtp(boolean success, String message, String data) {
        super(success, message);
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
