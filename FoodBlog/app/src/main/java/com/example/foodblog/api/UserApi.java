package com.example.foodblog.api;

import com.example.foodblog.model.ResponseAllUserLocation;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseRecipes;
import com.example.foodblog.model.ResponseUser;
import com.example.foodblog.model.ResponseUserLocation;
import com.example.foodblog.model.ResponseUsers;
import com.example.foodblog.model.ResponseUsersSearch;
import com.example.foodblog.model.UserLocation;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {
    @GET("/api/v1/user/getUser1/{userId}")
    Call<ResponseUser> getUserInfo(@Header("Authorization") String token, @Path("userId") int userId);

    @GET("/api/v1/recipe/getRecipeByUserId1/{userId}")
    Call<ResponseRecipes> getAllUserPublicRecipe(@Header("Authorization") String token, @Path("userId") int userId);

    @GET("/api/v1/user/getUserFollow/{userId}")
    Call<ResponseUsers> getAllFollowers(@Header("Authorization") String token, @Path("userId") int userId);

    @GET("/api/v1/user/getUserFollowing/{userId}")
    Call<ResponseUsers> getAllFollowings(@Header("Authorization") String token, @Path("userId") int userId);

    @POST("/api/v1/follow/create/{userId}")
    Call<ResponseObject> followOtherUser(@Header("Authorization") String token, @Path("userId") int userId);

    @DELETE("/api/v1/follow/delete/{userId}")
    Call<ResponseObject> unfollowOtherUser(@Header("Authorization") String token, @Path("userId") int userId);

    @GET("/user/searchUserByEmail")
    Call<ResponseUsersSearch> searchUserByEmail(@Header("Authorization") String token, @Query("q") String email);

    @GET("/api/v1/user/searchUserByName")
    Call<ResponseUsersSearch> searchUserByName(@Header("Authorization") String token, @Query("q") String name);

    @Multipart
    @PUT("/api/v1/user/update")
    Call<ResponseObject> updateMyInfo(
            @Header("Authorization") String token,
            @Part("fullName") RequestBody newName,
            @Part("dateOfBirth") RequestBody birthDate,
            @Part("address") RequestBody address,
            @Part("email") RequestBody email,
            @Part("introduce") RequestBody intro,
            @Part MultipartBody.Part imgAvatar);

    @GET("/api/v1/user/getCurrentLocationAllUser")
    Call<ResponseAllUserLocation> getAllUserLocations(@Header("Authorization") String token);

    @GET("/api/v1/user/getCurrentLocation")
    Call<ResponseUserLocation> getMyLocation(@Header("Authorization") String token);

    @PUT("/api/v1/user/updateCurrentLocation")
    Call<ResponseObject> updateMyCurrentLocation(
            @Header("Authorization") String token,
            @Body UserLocation userLocation
    );

    @DELETE("/api/v1/user/deleteCurrentLocation")
    Call<ResponseObject> deleteMyLocation(@Header("Authorization") String token);
}
