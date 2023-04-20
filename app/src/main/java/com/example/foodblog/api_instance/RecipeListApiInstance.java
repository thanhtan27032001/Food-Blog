package com.example.foodblog.api_instance;

import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.api.RecipeApi;
import com.example.foodblog.api.RecipeListApi;
import com.example.foodblog.model.RecipeList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeListApiInstance {
    private static RecipeListApi instance;
    public static RecipeListApi getInstance(){
        if (instance == null){
            instance = new Retrofit.Builder()
                    .baseUrl(ApiUrl.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(RecipeListApi.class);
        }
        return instance;
    }
}
