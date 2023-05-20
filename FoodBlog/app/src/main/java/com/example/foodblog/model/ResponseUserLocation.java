package com.example.foodblog.model;

public class ResponseUserLocation extends ResponseObject{
    private UserLocation data;

    public ResponseUserLocation(boolean success, String message, UserLocation data) {
        super(success, message);
        this.data = data;
    }

    public UserLocation getData() {
        return data;
    }
}
