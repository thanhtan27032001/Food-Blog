package com.example.foodblog.api;

import com.example.foodblog.model.RecipeList;
import com.example.foodblog.model.RecipeListInfo;
import com.example.foodblog.model.ResponseObject;
import com.example.foodblog.model.ResponseRecipeList;
import com.example.foodblog.model.ResponseRecipeListsDetail;
import com.example.foodblog.model.ResponseRecipes;

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

public interface RecipeListApi {
//    @Multipart
//    @POST("/api/v1/recipeList/createRecipeList/")
//    Call<ResponseObject> addRecipeList(@Header("Authorization") String token, @Body RecipeList recipeList);

    @GET("/api/v1/recipeList/getRecipeList/")
    Call<ResponseRecipeList> getRecipeList(@Header("Authorization") String token);
    @Multipart
    @POST("/api/v1/recipeList/createRecipeList/")
    Call<ResponseObject> addRecipeList(
            @Header("Authorization") String token,
            @Part("name") RequestBody name,
            @Part MultipartBody.Part file);

    @GET("/api/v1/recipeList/getBookmarkList/{recipeId}")
    Call<ResponseRecipeListsDetail> getRecipeListsDetail(@Header("Authorization") String token, @Path("recipeId") int recipeId);

    @GET("/api/v1/recipeList/getRecipe/{recipeListId}")
    Call<ResponseRecipes> getRecipeListRecipes(@Header("Authorization") String token, @Path("recipeListId") int recipeListId);

    @DELETE("/api/v1/recipeList/deleteRecipeList/{recipeListId}")
    Call<ResponseObject> deleteRecipeList(@Header("Authorization") String token, @Path("recipeListId") int recipeListId);

    @PUT("/api/v1/recipeList/updateRecipeList/{recipeListId}")
    Call<ResponseObject> changeRecipeListName(
            @Header("Authorization") String token,
            @Path("recipeListId") int recipeListId,
            @Body RecipeListInfo info);
}
