package com.example.foodblog.model;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodblog.MyFormat;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Recipe implements Serializable {

    public static final String STATUS_PRIVATE = "RT";
    public static final String STATUS_PUBLIC = "CK";
    private int recipeId;
    private String recipeName;
    private String date;
    private String status;
    private int amount;
    private int preparationTime;
    private int cookingTime;
    private int numberOfLikes;
    private String image;
    private String description;
    private boolean isFavorite;
    @SerializedName("User")
    private User user;
    @SerializedName("Steps")
    private ArrayList<RecipeStep> steps;
    @SerializedName("DetailIngredients")
    private ArrayList<IngredientDetail> ingredients;
    @SerializedName("DetailLists")
    private ArrayList<RecipeList> recipeListContain;

    public Recipe() {
    }

    // recipe preview
    public Recipe(int recipeId, String recipeName, String date, String status, int numberOfLikes, String image, boolean isFavorite, User user, ArrayList<RecipeList> recipeListContain) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.date = date;
        this.status = status;
        this.numberOfLikes = numberOfLikes;
        this.image = image;
        this.isFavorite = isFavorite;
        this.user = user;
        this.recipeListContain = recipeListContain;
    }

    // recipe detail
    public Recipe(int recipeId, String recipeName, String date, String status, int amount, int preparationTime, int cookingTime, int numberOfLikes, String image, String description, boolean isFavorite, User user, ArrayList<RecipeStep> steps, ArrayList<IngredientDetail> ingredients, ArrayList<RecipeList> recipeListContain) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.date = date;
        this.status = status;
        this.amount = amount;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.numberOfLikes = numberOfLikes;
        this.image = image;
        this.description = description;
        this.isFavorite = isFavorite;
        this.user = user;
        this.steps = steps;
        this.ingredients = ingredients;
        this.recipeListContain = recipeListContain;
    }

    // add recipe
    public Recipe(String recipeName, String description, String status, int amount, int preparationTime, int cookingTime, ArrayList<RecipeStep> steps, ArrayList<IngredientDetail> ingredients) {
        this.recipeName = recipeName;
        this.description = description;
        this.status = status;
        this.amount = amount;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.steps = steps;
        this.ingredients = ingredients;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getDate(
            CharSequence minute,
            CharSequence minutes,
            CharSequence hour,
            CharSequence hours,
            CharSequence yesterday)
    {
        return MyFormat.getPublicDate(date, minute, minutes, hour, hours, yesterday);
    }

    public String getStatus() {
        return status;
    }

    public int getAmount() {
        return amount;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public User getUser() {
        return user;
    }

    public ArrayList<RecipeStep> getSteps() {
        return steps;
    }

    public String getOriginDate() {
        return date;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public ArrayList<IngredientDetail> getIngredients() {
        return ingredients;
    }

    public ArrayList<RecipeList> getRecipeListContain() {
        return recipeListContain;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
        if (favorite){
            numberOfLikes++;
        }
        else {
            numberOfLikes--;
        }
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
