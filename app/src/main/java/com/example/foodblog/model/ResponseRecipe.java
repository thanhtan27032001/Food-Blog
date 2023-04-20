package com.example.foodblog.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

//class ResponseRecipeData{
//    private Recipe recipe;
//    @SerializedName("ingredient")
//    private ArrayList<IngredientDetail> ingredients;
//
//    public Recipe getRecipe() {
//        return recipe;
//    }
//
//    public ArrayList<IngredientDetail> getIngredients() {
//        return ingredients;
//    }
//}

public class ResponseRecipe extends ResponseObject{
    private Recipe data;

    public ResponseRecipe() {
    }

    public ResponseRecipe(boolean success, String message, Recipe data) {
        super(success, message);
        this.data = data;
    }

    public Recipe getData() {
        return data;
    }

//    public Recipe getRecipe() {
//        return data.getRecipe();
//    }

//    public ArrayList<IngredientDetail> getIngredients() {
//        return data.getIngredients() != null ? data.getIngredients() : new ArrayList<IngredientDetail>();
//    }

}
