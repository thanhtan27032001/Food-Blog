package com.example.foodblog.model;

import java.util.ArrayList;

public class ResponseRecipeNames extends ResponseObject{
    private ArrayList<Recipe> data;

    public ResponseRecipeNames() {
    }

    public ResponseRecipeNames(boolean success, String message, ArrayList<Recipe> data) {
        super(success, message);
        this.data = data;
    }

    public ArrayList<Recipe> getData() {
        return data;
    }
}
