package com.example.foodblog;

public interface RecipeManagerInterface {
    void setSelectIngredient(int position);
    void showFrameEditIngredient(int ingredientPosition);
    void toastDeleteIngredient();

    int getStepIndex();
    void setStepIndex(int index);
    void showDialogChooseImage(int imageViewRequest);
    void focusRvStepsEdittext(int position);
    String getMyText(int resId);
    void toastDeleteStep();
}
