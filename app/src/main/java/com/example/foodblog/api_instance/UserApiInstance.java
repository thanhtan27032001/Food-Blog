package com.example.foodblog.api_instance;

import com.example.foodblog.api.ApiUrl;
import com.example.foodblog.api.AuthorizationApi;
import com.example.foodblog.api.UserApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserApiInstance {
    private static UserApi instance;
    public static UserApi getInstance(){
        if (instance == null){
            instance = new Retrofit.Builder()
                    .baseUrl(ApiUrl.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(UserApi.class);
        }
        return instance;
    }
}
