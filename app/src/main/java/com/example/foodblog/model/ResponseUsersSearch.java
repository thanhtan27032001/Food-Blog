package com.example.foodblog.model;

import java.util.ArrayList;

public class ResponseUsersSearch extends ResponseObject{
    private ArrayList<User> data;

    public ResponseUsersSearch(boolean success, String message, ArrayList<User> data) {
        super(success, message);
        this.data = data;
    }

    public ArrayList<User> getData() {
        return data;
    }
}
