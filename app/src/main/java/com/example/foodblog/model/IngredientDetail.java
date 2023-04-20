package com.example.foodblog.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class IngredientDetail {
    private String ingredientId;
    private String name;
    private String amount;

    public IngredientDetail() {
        ingredientId = "";
        name = "";
        amount = "";
    }

    public IngredientDetail(String name, String amount) {
        this.name = name;
        this.amount = amount;
    }

    // add recipe
    public IngredientDetail(String ingredientId, String name, String amount) {
        this.ingredientId = ingredientId;
        this.name = name;
        this.amount = amount;
    }

    @NonNull
    @Override
    public String toString(){
        return amount + " " + name;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public String getIngredientId() {
        return ingredientId;
    }
}
