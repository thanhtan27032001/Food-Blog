package com.example.foodblog.model;

public class RecipeListDetail {
    private int recipeListId;
    private String name;
    private boolean isBookmarked;

    public RecipeListDetail(int recipeListId, String name, boolean isBookmarked) {
        this.recipeListId = recipeListId;
        this.name = name;
        this.isBookmarked = isBookmarked;
    }

    public int getRecipeListId() {
        return recipeListId;
    }

    public void setRecipeListId(int recipeListId) {
        this.recipeListId = recipeListId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }
}
