package com.example.foodblog.model;

import java.util.ArrayList;

public class ResponseRecipeList extends ResponseObject{
    private ArrayList<RecipeList> data;

    public ResponseRecipeList(boolean success, String message, ArrayList<RecipeList> data) {
        super(success, message);
        this.data = data;
    }

    public ArrayList<RecipeList> getData() {
        return data;
    }
}
