package com.example.foodblog.api_instance;

import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.api.AuthorizationApi;
import com.example.foodblog.api.CommentApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentApiInstance {
    private static CommentApi instance;
    public static CommentApi getInstance(){
        if (instance == null){
            instance = new Retrofit.Builder()
                    .baseUrl(ApiUrl.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(CommentApi.class);
        }
        return instance;
    }
}
