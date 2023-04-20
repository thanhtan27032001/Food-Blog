package com.example.foodblog.model;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.File;

public class RecipeStep {
    private Integer stepId;
    private String description;
    private String image;
    private int stepIndex;
    private File imageFile;
    private Uri imageUri;

    public RecipeStep() {
    }

    // add recipe
    public RecipeStep(int stepIndex) {
        this.stepIndex = stepIndex;
        this.image = "";
    }

    public RecipeStep(int stepId, String description) {
        this.stepId = stepId;
        this.description = description;
    }

    public RecipeStep(int stepId, String description, String image) {
        this.stepId = stepId;
        this.description = description;
        this.image = image;
    }

    // show recipe detail
    public RecipeStep(int stepId, String description, String image, int stepIndex) {
        this.stepId = stepId;
        this.description = description;
        this.image = image;
        this.stepIndex = stepIndex;
    }

    public int getStepId() {
        return stepId;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public int getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(int stepIndex) {
        this.stepIndex = stepIndex;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    @NonNull
    public RecipeStep clone(){
        RecipeStep clone = new RecipeStep();
        clone.stepId = stepId;
        clone.description = description;
        clone.image = image;
        clone.stepIndex = stepIndex;
        clone.imageFile = imageFile;
        clone.imageUri = imageUri;
        return clone;
    }
}
