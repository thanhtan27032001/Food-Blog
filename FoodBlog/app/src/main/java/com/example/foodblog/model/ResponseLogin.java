package com.example.foodblog.model;

public class ResponseLogin extends ResponseObject{
    private String data;

    public ResponseLogin() {
    }

    public ResponseLogin(boolean success, String message, String data) {
        super(success, message);
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
