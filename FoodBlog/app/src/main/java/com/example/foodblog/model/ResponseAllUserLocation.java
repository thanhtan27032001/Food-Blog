package com.example.foodblog.model;

import java.util.ArrayList;

public class ResponseAllUserLocation extends ResponseObject{
    private ArrayList<UserLocation> data;

    public ResponseAllUserLocation(boolean success, String message, ArrayList<UserLocation> data) {
        super(success, message);
        this.data = data;
    }

    public ArrayList<UserLocation> getData() {
        return data;
    }
}
