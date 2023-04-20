package com.example.foodblog.api_instance;

import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.api.RecipeApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeApiInstance {
    private static RecipeApi instance;
    public static RecipeApi getInstance(){
        if (instance == null){
            instance = new Retrofit.Builder()
                    .baseUrl(ApiUrl.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(RecipeApi.class);
        }
        return instance;
    }
}
