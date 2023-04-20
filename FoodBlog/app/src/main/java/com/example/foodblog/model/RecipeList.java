package com.example.foodblog.model;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RecipeList implements Serializable {
    private int recipeListId;
    private String name;
    private String date;
    private int userId;
    private String image;
    @SerializedName("file")
    private MultipartBody.Part file;

    // get recipe list
    public RecipeList(int recipeListId, String name, String image) {
        this.recipeListId = recipeListId;
        this.name = name;
        this.image = image;
    }

//    // add recipe list
//    public RecipeList(String name, MultipartBody.Part file) {
//        this.name = name;
//        this.file = file;
//    }

    public int getRecipeListId() {
        return recipeListId;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public void setName(String name) {
        this.name = name;
    }
//    public MultipartBody.Part getFile() {
//        return file;
//    }
}
