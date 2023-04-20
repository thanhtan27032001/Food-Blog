package com.example.foodblog.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

class ResponseUsersData {
    @SerializedName("users")
    private ArrayList<User> us;
    @SerializedName("count")
    private int numOfUser;

    public ResponseUsersData(ArrayList<User> us, int numOfUser) {
        this.us = us;
        this.numOfUser = numOfUser;
    }

    public ArrayList<User> getUs() {
        return us;
    }

    public int getNumOfUser() {
        return numOfUser;
    }
}
public class ResponseUsers extends ResponseObject{
    private ResponseUsersData data;

    public ResponseUsers() {
    }

    public ResponseUsers(boolean success, String message, ResponseUsersData data) {
        super(success, message);
        this.data = data;
    }

    public ArrayList<User> getUsers() {
        return data.getUs();
    }

    public int getNumOfUser() {
        return data.getNumOfUser();
    }
}
