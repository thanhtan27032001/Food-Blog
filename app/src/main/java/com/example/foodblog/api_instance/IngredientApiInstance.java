package com.example.foodblog.api_instance;

import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.api.IngredientApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IngredientApiInstance {
    private static IngredientApi instance;
    public static IngredientApi getInstance(){
        if (instance == null){
            instance = new Retrofit.Builder()
                    .baseUrl(ApiUrl.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(IngredientApi.class);
        }
        return instance;
    }
}
