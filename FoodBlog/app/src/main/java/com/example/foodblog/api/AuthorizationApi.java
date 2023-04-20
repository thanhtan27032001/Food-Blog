package com.example.foodblog.api;

import com.example.foodblog.model.ChangePasswordInfo;
import com.example.foodblog.model.LoginInfo;
import com.example.foodblog.model.RegisterInfo;
import com.example.foodblog.model.ResponseLogin;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseOtp;
import com.example.foodblog.model.ResponseUser;
import com.example.foodblog.model.SendOtpInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface AuthorizationApi {
    @POST("/api/v1/auth/login/")
    Call<ResponseLogin> login(@Body LoginInfo loginInfo);

    @GET("/api/v1/auth/")
    Call<ResponseUser> getUserInfo(@Header("Authorization") String token);

    @POST("/api/v1/auth/register")
    Call<ResponseObject> register(@Body RegisterInfo registerInfo);

    @POST("/api/v1/auth/sendOtp")
    Call<ResponseOtp> sendOtp(@Body SendOtpInfo info);

    @PUT("/api/v1/auth/changePassword")
    Call<ResponseObject> changePassword(@Body ChangePasswordInfo info);
}
