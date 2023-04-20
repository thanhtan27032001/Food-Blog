package com.example.foodblog.model;

import com.example.foodblog.MyFormat;
import com.google.gson.annotations.SerializedName;

public class Comment {
    private String userId;
    private String recipeId;
    private String date;
    private String comment;
    @SerializedName("User")
    private User user;

    public Comment() {
    }

    public Comment(String comment) {
        this.comment = comment;
    }

    public Comment(String userId, String recipeId, String date, String comment, User user) {
        this.userId = userId;
        this.recipeId = recipeId;
        this.date = date;
        this.comment = comment;
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public String getDate(CharSequence minute, CharSequence minutes, CharSequence hour, CharSequence hours, CharSequence yesterday) {
        return MyFormat.getPublicDate(date, minute, minutes, hour, hours, yesterday);
    }

    public String getComment() {
        return comment;
    }

    public User getUser() {
        return user;
    }
}
