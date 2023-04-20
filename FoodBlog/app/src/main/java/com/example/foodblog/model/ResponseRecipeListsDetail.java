package com.example.foodblog.model;

import java.util.ArrayList;

public class ResponseRecipeListsDetail extends ResponseObject{
    ArrayList<RecipeListDetail> data;

    public ResponseRecipeListsDetail() {
    }

    public ResponseRecipeListsDetail(boolean success, String message, ArrayList<RecipeListDetail> data) {
        super(success, message);
        this.data = data;
    }

    public ArrayList<RecipeListDetail> getData() {
        return data;
    }
}
