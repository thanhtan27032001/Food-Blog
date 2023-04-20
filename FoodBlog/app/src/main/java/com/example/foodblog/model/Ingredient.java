package com.example.foodblog.model;

public class Ingredient {
    private String ingredientId;
    private String name;
    private String image;

    public Ingredient() {
    }

    public Ingredient(String name) {
        this.name = name;
    }

    // get ingredient
    public Ingredient(String ingredientId, String name, String image) {
        this.ingredientId = ingredientId;
        this.name = name;
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public String getIngredientId() {
        return ingredientId;
    }

    public String getImage() {
        return image;
    }
}
