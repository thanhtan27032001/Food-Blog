package com.example.foodblog.model;

import java.util.ArrayList;

public class ResponseIngredients extends ResponseObject{
    protected ArrayList<Ingredient> data;

    public ResponseIngredients() {
    }

    public ResponseIngredients(boolean success, String message, ArrayList<Ingredient> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    public ArrayList<Ingredient> getData() {
        return data;
    }

    public void setData(ArrayList<Ingredient> data) {
        this.data = data;
    }
}
