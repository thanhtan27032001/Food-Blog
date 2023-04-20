package com.example.foodblog.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BookmarkRecipeInfo {
    private ArrayList<RecipeListDetail> recipeListDetail;

    public BookmarkRecipeInfo() {
    }

    public BookmarkRecipeInfo(ArrayList<RecipeListDetail> recipeListDetail) {
        this.recipeListDetail = new ArrayList<>();
        for (RecipeListDetail detail: recipeListDetail){
            if (detail.isBookmarked()){
                this.recipeListDetail.add(detail);
            }
        }
    }

    public ArrayList<RecipeListDetail> getRecipeListDetail() {
        return recipeListDetail;
    }
}
