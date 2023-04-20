package com.example.foodblog.api;

import com.example.foodblog.model.ResponseIngredients;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface IngredientApi {
    @GET("/api/v1/ingredient/getIngredientBySeason")
    Call<ResponseIngredients> getIngredientBySeason(@Header("Authorization") String token);

    @GET("https://food-blog-services.onrender.com/api/v1/ingredient/search")
    Call<ResponseIngredients> searchIngredient(@Header("Authorization") String token, @Query("q") String searchKey);

}
