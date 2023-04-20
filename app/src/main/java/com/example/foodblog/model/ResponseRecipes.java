package com.example.foodblog.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseRecipes extends ResponseObject{
    @SerializedName("data")
    private ArrayList<Recipe> data;

    public ResponseRecipes() {
    }

    public ResponseRecipes(boolean success, String message, ArrayList<Recipe> data) {
        super(success, message);
        this.data = data;
    }

    public ArrayList<Recipe> getData() {
        return data;
    }
}
