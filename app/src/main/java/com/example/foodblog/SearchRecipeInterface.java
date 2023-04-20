package com.example.foodblog;

import android.widget.ImageButton;
import android.widget.TextView;

import com.example.foodblog.model.Recipe;

public interface SearchRecipeInterface {
    void viewRecipeDetail(int recipeId);
    String getMyText(int resId);

    void showDialogBookmark(int recipeId);

    void changeRecipeFavorite(Recipe recipe, boolean isFavorite, ImageButton updateLikeStatus, TextView numOfLike);
}
