package com.example.foodblog.model;

import android.util.Log;

import com.example.foodblog.MyAuthorization;
import com.example.foodblog.api.UserApi;
import com.example.foodblog.api_instance.UserApiInstance;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class User implements Serializable {
    private int userId;
    private String fullName;
    private String dateOfBirth;
    private String address;
    private String email;
    private String introduce;
    private String avatar;
    @SerializedName("isFollow")
    private boolean isFollowed;
    @SerializedName("countRecipe")
    private int numOfRecipe;
    @SerializedName("countFollowing")
    private int numOfFollowing;
    @SerializedName("countFollowed")
    private int numOfFollower;


//    private String dateUpdatedRecipe;

    public User() {
    }

    // recipe owner's info
    public User(String fullName, String avatar) {
        this.fullName = fullName;
        this.avatar = avatar;
    }

    // recipe owner's info
    public User(int userId, String fullName, String avatar) {
        this.userId = userId;
        this.fullName = fullName;
        this.avatar = avatar;
    }

    // get user info (user wall)
    public User(int userId, String fullName, String dateOfBirth, String address, String email, String introduce, String avatar, boolean isFollowed, int numOfRecipe, int numOfFollowing, int numOfFollower) {
        this.userId = userId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.email = email;
        this.introduce = introduce;
        this.avatar = avatar;
        this.isFollowed = isFollowed;
        this.numOfRecipe = numOfRecipe;
        this.numOfFollowing = numOfFollowing;
        this.numOfFollower = numOfFollower;
    }
//    public User(int userId, String fullName, String dateOfBirth, String address, String email, String introduce, String avatar, String dateUpdatedRecipe) {
//        this.userId = userId;
//        this.fullName = fullName;
//        this.dateOfBirth = dateOfBirth;
//        this.address = address;
//        this.email = email;
//        this.introduce = introduce;
//        this.avatar = avatar;
//        this.dateUpdatedRecipe = dateUpdatedRecipe;
//    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth.substring(0, 10).replaceAll("-", "/");
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getIntroduce() {
        return introduce;
    }

    public String getAvatar() {
        return avatar;
    }

//    public String getDateUpdatedRecipe() {
//        return dateUpdatedRecipe;
//    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public int getNumOfRecipe() {
        return numOfRecipe;
    }

    public int getNumOfFollowing() {
        return numOfFollowing;
    }

    public int getNumOfFollower() {
        return numOfFollower;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
        if (followed){
            numOfFollower++;
        }
        else {
            numOfFollower--;
        }
    }

    public void setNumOfFollower(int numOfFollower) {
        this.numOfFollower = numOfFollower;
    }

    public void updateInfo(){
        Call<ResponseUser> call = UserApiInstance.getInstance().getUserInfo(
                MyAuthorization.getInstance().getToken(),
                userId
        );
        call.enqueue(new Callback<ResponseUser>() {
            @Override
            public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
                if (response.isSuccessful() || response.code() == 200){
                    fullName = response.body().getData().fullName;
                    dateOfBirth = response.body().getData().getDateOfBirth();
                    email = response.body().getData().getEmail();
                    address = response.body().getData().getAddress();
                    introduce = response.body().getData().getAddress();
                    avatar = response.body().getData().getAvatar();
                }
                else {
                    try {
                        Log.e("error", response.errorBody().string());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseUser> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }
}
